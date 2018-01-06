package com.hjrh.facade.notify.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hjrh.common.core.dao.IGenericDAO;
import com.hjrh.common.core.service.BaseServiceImpl;
import com.hjrh.facade.notify.entity.NotifyRecord;
import com.hjrh.facade.notify.enums.NotifyStatusEnum;
import com.hjrh.facade.notify.enums.NotifyTypeEnum;
import com.hjrh.facade.notify.service.NotifyService;
import com.hjrh.service.notify.dao.NotifyRecordDao;

/**
 * 
 * 功能描述：通知接口实现
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
@Service("notifyService")
public class NotifyServiceImpl extends BaseServiceImpl<NotifyRecord> implements NotifyService {

    @Autowired
    private JmsTemplate notifyJmsTemplate;

    @Resource(name = "notifyRecordDao")
    private NotifyRecordDao notifyRecordDao;
    
    @Override
	protected IGenericDAO<NotifyRecord> getDao() {
		return notifyRecordDao;
	}
    
    /**
     * 发送消息通知
     *
     * @param notifyUrl       通知地址
     * @param merchantOrderNo 商户订单号
     * @param merchantNo      商户编号
     */
    public void notifySend(String notifyUrl, String merchantOrderNo, String merchantNo) {

        NotifyRecord record = new NotifyRecord();
        record.setNotifyTimes(0);
        record.setLimitNotifyTimes(5);
        record.setStatus(NotifyStatusEnum.CREATED.name());
        record.setUrl(notifyUrl);
        record.setMerchantOrderNo(merchantOrderNo);
        record.setMerchantNo(merchantNo);
        record.setNotifyType(NotifyTypeEnum.MERCHANT.name());

        Object toJSON = JSONObject.toJSON(record);
        final String str = toJSON.toString();

        notifyJmsTemplate.send(new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(str);
            }
        });
    }

    /**
     * 根据商户编号,商户订单号,通知类型获取通知记录
     *
     * @param merchantNo      商户编号
     * @param merchantOrderNo 商户订单号
     * @param notifyType      消息类型
     * @return
     */
    public NotifyRecord getNotifyByMerchantNoAndMerchantOrderNoAndNotifyType(String merchantNo, String merchantOrderNo, String notifyType) {
    	
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("merchantNo", merchantNo);
    	params.put("merchantOrderNo", merchantOrderNo);
    	params.put("notifyType", notifyType);
    	List<NotifyRecord> notifyRecords = super.getObjByListPropertys(params, NotifyRecord.class);
    	if(notifyRecords!=null && notifyRecords.size()>0){
    		return notifyRecords.get(0);
    	}else{
    		return null;
    	}
    }
}
