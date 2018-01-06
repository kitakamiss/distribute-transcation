package com.hjrh.facade.message.service;

import com.hjrh.common.core.service.BaseService;
import com.hjrh.facade.message.entity.TransactionMessage;
import com.hjrh.facade.message.exceptions.MessageException;

/**
 * 
 * 功能描述：
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public interface TransactionMessageService extends BaseService<TransactionMessage>{

	/**
	 * 
	 * 功能描述：预存储消息
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年6月23日 下午3:10:44</p>
	 *
	 * @param rpTransactionMessage
	 * @return
	 * @throws MessageException
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public void saveMessageWaitingConfirm(TransactionMessage rpTransactionMessage) throws MessageException;
	
	/**
	 * 
	 * 功能描述：确认并发送消息
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年6月23日 下午3:10:53</p>
	 *
	 * @param messageId
	 * @throws MessageException
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public void confirmAndSendMessage(String messageId) throws MessageException;

	/**
	 * 
	 * 功能描述：存储并发送消息
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年6月23日 下午3:11:05</p>
	 *
	 * @param rpTransactionMessage
	 * @return
	 * @throws MessageException
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public void saveAndSendMessage(TransactionMessage rpTransactionMessage) throws MessageException;

	
	/**
	 * 
	 * 功能描述：直接发送消息
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年6月23日 下午3:11:14</p>
	 *
	 * @param rpTransactionMessage
	 * @throws MessageException
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public void directSendMessage(TransactionMessage rpTransactionMessage) throws MessageException;
	
	/**
	 * 
	 * 功能描述：重发消息
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年6月23日 下午3:11:24</p>
	 *
	 * @param rpTransactionMessage
	 * @throws MessageException
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public void reSendMessage(TransactionMessage rpTransactionMessage) throws MessageException;
	
	/**
	 * 
	 * 功能描述：根据messageId重发某条消息
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年6月23日 下午3:11:32</p>
	 *
	 * @param messageId
	 * @throws MessageException
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public void reSendMessageByMessageId(String messageId) throws MessageException;
	
	/**
	 * 
	 * 功能描述：将消息标记为死亡消息
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年6月23日 下午3:11:43</p>
	 *
	 * @param messageId
	 * @throws MessageException
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public void setMessageToAreadlyDead(String messageId) throws MessageException;

	/**
	 * 
	 * 功能描述：根据消息ID获取消息
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年6月23日 下午3:11:54</p>
	 *
	 * @param messageId
	 * @return
	 * @throws MessageException
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public TransactionMessage getMessageByMessageId(String messageId) throws MessageException;

	/**
	 * 
	 * 功能描述：根据消息ID删除消息
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年6月23日 下午3:12:05</p>
	 *
	 * @param messageId
	 * @throws MessageException
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public void deleteMessageByMessageId(String messageId) throws MessageException;
	
	/**
	 * 
	 * 功能描述：重发某个消息队列中的全部已死亡的消息
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年6月23日 下午3:12:14</p>
	 *
	 * @param queueName
	 * @param batchSize
	 * @throws MessageException
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public void reSendAllDeadMessageByQueueName(String queueName, int batchSize) throws MessageException;

}
