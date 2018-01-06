package com.hjrh.core;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hjrh.App;
import com.hjrh.common.utils.DateUtil;
import com.hjrh.facade.notify.entity.NotifyRecord;
import com.hjrh.param.NotifyParam;

/**
 * 
 * 功能描述：
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
@Service("notifyQueue")
public class NotifyQueue implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Log LOG = LogFactory.getLog(NotifyQueue.class);

    @Autowired
    private NotifyParam notifyParam;
    

    /**
     * 
     * 功能描述：将传过来的对象进行通知次数判断，决定是否放在任务队列中
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月1日 上午11:36:03</p>
     *
     * @param notifyRecord
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public void addToNotifyTaskDelayQueue(NotifyRecord notifyRecord) {
        if (notifyRecord == null) {
            return;
        }
        LOG.info("===>addToNotifyTaskDelayQueue notify id:" + notifyRecord.getId());
        Integer notifyTimes = notifyRecord.getNotifyTimes(); // 通知次数
        Integer maxNotifyTimes = notifyRecord.getLimitNotifyTimes(); // 最大通知次数
        
        if (notifyRecord.getNotifyTimes().intValue() == 0) {
            notifyRecord.setLastNotifyTime(new Date()); // 第一次发送(取当前时间)
        }else{
        	notifyRecord.setLastNotifyTime(notifyRecord.getEditTime()); // 非第一次发送（取上一次修改时间，也是上一次发送时间）
        }
        
        if (notifyTimes < maxNotifyTimes) {
        	// 未超过最大通知次数，继续下一次通知
            LOG.info("===>notify id:" + notifyRecord.getId() + ", 上次通知时间lastNotifyTime:" + DateUtil.date2Str(notifyRecord.getLastNotifyTime(), "yyyy-MM-dd HH:mm:ss SSS"));
            App.tasks.put(new NotifyTask(notifyRecord, this, notifyParam));
        }
        
    }
}
