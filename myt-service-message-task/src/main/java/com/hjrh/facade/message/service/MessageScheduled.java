/**
 * MessageScheduled.java	  V1.0   2017年6月26日 下午2:30:27
 *
 * Copyright 2015 Hjrh Technology Co.,Ltd. All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package com.hjrh.facade.message.service;

/**
 * 
 * 功能描述：消息定时器接口
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public interface MessageScheduled {
	/**
	 * 处理状态为“待确认”但已超时的消息.
	 */
	public void handleWaitingConfirmTimeOutMessages();

	/**
	 * 处理状态为“发送中”但超时没有被成功消费确认的消息
	 */
	public void handleSendingTimeOutMessage();
}
