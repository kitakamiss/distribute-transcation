package com.hjrh.listener;

import java.util.Date;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjrh.common.exceptions.BaseException;
import com.hjrh.core.NotifyPersist;
import com.hjrh.core.NotifyQueue;
import com.hjrh.facade.notify.entity.NotifyRecord;
import com.hjrh.facade.notify.enums.NotifyStatusEnum;
import com.hjrh.param.NotifyParam;


/**
 * 
 * 功能描述： 通知监听类
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
    private NotifyPersist notifyPersist;
    
    @Autowired
    private NotifyParam notifyParam;

    /**
     * 监听消费MQ队列中的消息.
     */
    public void onMessage(Message message) {
        try {
            ActiveMQTextMessage msg = (ActiveMQTextMessage) message;
            final String msgText = msg.getText();
            log.info("== receive message:" + msgText);

            JSON json = (JSON) JSONObject.parse(msgText);
            NotifyRecord notifyRecord = JSONObject.toJavaObject(json, NotifyRecord.class);
            if (notifyRecord == null) {
                return;
            }
            notifyRecord.setStatus(NotifyStatusEnum.CREATED.name());
            notifyRecord.setCreateTime(new Date());
            notifyRecord.setEditTime(new Date());
            notifyRecord.setLastNotifyTime(new Date());
            notifyRecord.setNotifyTimes(0); // 初始化通知0次
            notifyRecord.setLimitNotifyTimes(notifyParam.getMaxNotifyTimes()); // 最大通知次数
            Map<Integer, Integer> notifyParams = notifyParam.getNotifyParams();
            notifyRecord.setNotifyRule(JSONObject.toJSONString(notifyParams)); // 保存JSON

            try {
                notifyPersist.saveNotifyRecord(notifyRecord); // 将获取到的通知先保存到数据库中
                notifyQueue.addToNotifyTaskDelayQueue(notifyRecord); // 添加到通知队列(第一次通知)
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
