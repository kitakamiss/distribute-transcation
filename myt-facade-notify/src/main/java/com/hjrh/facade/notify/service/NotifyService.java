package com.hjrh.facade.notify.service;

import com.hjrh.common.core.service.BaseService;
import com.hjrh.facade.notify.entity.NotifyRecord;
import com.hjrh.facade.notify.exceptions.NotifyException;

/**
 * 
 * 功能描述：通知接口
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public interface NotifyService extends BaseService<NotifyRecord> {
	
    /**
     * 
     * 功能描述：发送消息通知
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月1日 上午9:33:25</p>
     *
     * @param notifyUrl 通知URL
     * @param merchantOrderNo 商户订单号
     * @param merchantNo 商户编号
     * @throws NotifyException 
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public void notifySend(String notifyUrl,String merchantOrderNo,String merchantNo) throws NotifyException;

    /**
     * 
     * 功能描述：根据商户编号,商户订单号,通知类型获取通知记录
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月1日 上午10:01:52</p>
     *
     * @param merchantNo 商户编号
     * @param merchantOrderNo 商户订单号
     * @param notifyType 消息类型
     * @return
     * @throws NotifyException
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public NotifyRecord getNotifyByMerchantNoAndMerchantOrderNoAndNotifyType(String merchantNo , String merchantOrderNo , String notifyType) throws NotifyException;
}
