package com.hjrh.message;

import java.util.Date;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjrh.common.exceptions.BaseException;
import com.hjrh.common.utils.string.StringUtil;
import com.hjrh.core.NotifyPersist;
import com.hjrh.core.NotifyQueue;
import com.hjrh.facade.notify.entity.NotifyRecord;
import com.hjrh.facade.notify.enums.NotifyStatusEnum;
import com.hjrh.facade.notify.service.NotifyService;

/**
 * 
 * 功能描述：
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class ConsumerSessionAwareMessageListener  implements MessageListener {

    private static final Log log = LogFactory.getLog(ConsumerSessionAwareMessageListener.class);

    @Autowired
    private NotifyQueue notifyQueue;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private NotifyPersist notifyPersist;

    @SuppressWarnings("static-access")
    public void onMessage(Message message) {
        try {
            ActiveMQTextMessage msg = (ActiveMQTextMessage) message;
            final String ms = msg.getText();
            log.info("== receive message:" + ms);

            JSON json = (JSON) JSONObject.parse(ms);
            NotifyRecord notifyRecord = JSONObject.toJavaObject(json, NotifyRecord.class);
            if (notifyRecord == null) {
                return;
            }
            // log.info("notifyParam:" + notifyParam);
            notifyRecord.setStatus(NotifyStatusEnum.CREATED.name());
            notifyRecord.setCreateTime(new Date());
            notifyRecord.setLastNotifyTime(new Date());

            if ( !StringUtil.isNullOrEmpty(notifyRecord.getId())){
                NotifyRecord notifyRecordById = notifyService.getObjById(notifyRecord.getId());
                if (notifyRecordById != null){
                    return;
                }
            }

            while (notifyService == null) {
                Thread.currentThread().sleep(1000); // 主动休眠，防止类Spring 未加载完成，监听服务就开启监听出现空指针异常
            }

            try {
                // 将获取到的通知先保存到数据库中
                notifyPersist.saveNotifyRecord(notifyRecord);
                notifyRecord = (NotifyRecord) notifyService.getNotifyByMerchantNoAndMerchantOrderNoAndNotifyType(notifyRecord.getMerchantNo(), notifyRecord.getMerchantOrderNo(), notifyRecord.getNotifyType());

                // 添加到通知队列
                notifyQueue.addToNotifyTaskDelayQueue(notifyRecord);
            }  catch (BaseException e) {
                log.error("BizException :", e);
            } catch (Exception e) {
                log.error(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
    }


}
