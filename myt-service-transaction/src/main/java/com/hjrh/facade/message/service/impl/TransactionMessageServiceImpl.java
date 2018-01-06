package com.hjrh.facade.message.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hjrh.common.core.dao.IGenericDAO;
import com.hjrh.common.core.enums.PublicEnum;
import com.hjrh.common.core.service.BaseServiceImpl;
import com.hjrh.common.page.PageBean;
import com.hjrh.common.page.PageParam;
import com.hjrh.common.utils.ResourceUtils;
import com.hjrh.common.utils.string.StringUtil;
import com.hjrh.facade.message.entity.TransactionMessage;
import com.hjrh.facade.message.enums.MessageStatusEnum;
import com.hjrh.facade.message.exceptions.MessageException;
import com.hjrh.facade.message.service.TransactionMessageService;
import com.hjrh.service.message.dao.TransactionMessageDao;

/**
 * 
 * 功能描述：消息处理接口
 *
 * @author 吴俊明
 *
 *         <p>
 * 		修改历史：(修改人，修改时间，修改原因/内容)
 *         </p>
 */
@Service("transactionMessageService")
@Transactional
public class TransactionMessageServiceImpl extends BaseServiceImpl<TransactionMessage>
		implements TransactionMessageService {

	private static final Log log = LogFactory.getLog(TransactionMessageServiceImpl.class);

	@Resource(name = "transactionMessageDao")
	private TransactionMessageDao transactionMessageDao;

	@Autowired
	private JmsTemplate notifyJmsTemplate;
	
	@Override
	protected IGenericDAO<TransactionMessage> getDao() {
		return transactionMessageDao;
	}

	/**
	 * 
	 * 功能描述：预存储消息
	 *
	 * @author 吴俊明
	 *         <p>
	 * 		创建日期 ：2017年6月23日 下午3:10:44
	 *         </p>
	 *
	 * @param rpTransactionMessage
	 *            消息实体
	 * @return
	 * @throws MessageException
	 *
	 *             <p>
	 * 			修改历史 ：(修改人，修改时间，修改原因/内容)
	 *             </p>
	 */
	public void saveMessageWaitingConfirm(TransactionMessage message) throws MessageException{

		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, "保存的消息为空");
		}

		if (StringUtil.isNullOrEmpty(message.getConsumerQueue())) {
			throw new MessageException(MessageException.MESSAGE_CONSUMER_QUEUE_IS_NULL, "消息的消费队列不能为空 ");
		}

		message.setEditTime(new Date());
		message.setStatus(MessageStatusEnum.WAITING_CONFIRM.name());
		message.setAreadlyDead(PublicEnum.NO.name());
		message.setMessageSendTimes(0);
		transactionMessageDao.save(message);
	}

	/**
	 * 
	 * 功能描述：确认并发送消息
	 *
	 * @author 吴俊明
	 *         <p>
	 * 		创建日期 ：2017年6月23日 下午3:10:53
	 *         </p>
	 *
	 * @param messageId
	 *            消息ID
	 * @throws MessageException
	 *
	 *             <p>
	 * 			修改历史 ：(修改人，修改时间，修改原因/内容)
	 *             </p>
	 */
	public void confirmAndSendMessage(String messageId) throws MessageException{
		final TransactionMessage message = getMessageByMessageId(messageId);
		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, "根据消息id查找的消息为空");
		}

		message.setStatus(MessageStatusEnum.SENDING.name());
		message.setEditTime(new Date());
		transactionMessageDao.update(message);

		notifyJmsTemplate.setDefaultDestinationName(message.getConsumerQueue());
		notifyJmsTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message.getMessageBody());
			}
		});
	}

	/**
	 * 
	 * 功能描述：存储并发送消息
	 *
	 * @author 吴俊明
	 *         <p>
	 * 		创建日期 ：2017年6月23日 下午3:11:05
	 *         </p>
	 *
	 * @param rpTransactionMessage
	 *            消息实体
	 * @return
	 * @throws MessageException
	 *
	 *             <p>
	 * 			修改历史 ：(修改人，修改时间，修改原因/内容)
	 *             </p>
	 */
	public void saveAndSendMessage(final TransactionMessage message) throws MessageException{

		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, "保存的消息为空");
		}

		if (StringUtil.isNullOrEmpty(message.getConsumerQueue())) {
			throw new MessageException(MessageException.MESSAGE_CONSUMER_QUEUE_IS_NULL, "消息的消费队列不能为空 ");
		}

		message.setStatus(MessageStatusEnum.SENDING.name());
		message.setAreadlyDead(PublicEnum.NO.name());
		message.setMessageSendTimes(0);
		message.setEditTime(new Date());
		transactionMessageDao.save(message);

		notifyJmsTemplate.setDefaultDestinationName(message.getConsumerQueue());
		notifyJmsTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message.getMessageBody());
			}
		});
	}

	/**
	 * 
	 * 功能描述：直接发送消息
	 *
	 * @author 吴俊明
	 *         <p>
	 * 		创建日期 ：2017年6月23日 下午3:11:14
	 *         </p>
	 *
	 * @param rpTransactionMessage
	 *            消息实体
	 * @throws MessageException
	 *
	 *             <p>
	 * 			修改历史 ：(修改人，修改时间，修改原因/内容)
	 *             </p>
	 */
	public void directSendMessage(final TransactionMessage message)  throws MessageException {

		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, "保存的消息为空");
		}

		if (StringUtil.isNullOrEmpty(message.getConsumerQueue())) {
			throw new MessageException(MessageException.MESSAGE_CONSUMER_QUEUE_IS_NULL, "消息的消费队列不能为空 ");
		}

		notifyJmsTemplate.setDefaultDestinationName(message.getConsumerQueue());
		notifyJmsTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message.getMessageBody());
			}
		});
	}

	/**
	 * 
	 * 功能描述：重发消息
	 *
	 * @author 吴俊明
	 *         <p>
	 * 		创建日期 ：2017年6月23日 下午3:11:24
	 *         </p>
	 *
	 * @param rpTransactionMessage
	 *            消息实体
	 * @throws MessageException
	 *
	 *             <p>
	 * 			修改历史 ：(修改人，修改时间，修改原因/内容)
	 *             </p>
	 */
	public void reSendMessage(final TransactionMessage message) throws MessageException{

		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, "保存的消息为空");
		}

		if (StringUtil.isNullOrEmpty(message.getConsumerQueue())) {
			throw new MessageException(MessageException.MESSAGE_CONSUMER_QUEUE_IS_NULL, "消息的消费队列不能为空 ");
		}

		message.addSendTimes();
		message.setEditTime(new Date());
		transactionMessageDao.update(message);

		notifyJmsTemplate.setDefaultDestinationName(message.getConsumerQueue());
		notifyJmsTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message.getMessageBody());
			}
		});
	}

	/**
	 * 
	 * 功能描述：根据messageId重发某条消息
	 *
	 * @author 吴俊明
	 *         <p>
	 * 		创建日期 ：2017年6月23日 下午3:11:32
	 *         </p>
	 *
	 * @param messageId
	 *            消息ID
	 * @throws MessageException
	 *
	 *             <p>
	 * 			修改历史 ：(修改人，修改时间，修改原因/内容)
	 *             </p>
	 */
	public void reSendMessageByMessageId(String messageId) throws MessageException {
		final TransactionMessage message = getMessageByMessageId(messageId);
		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, "根据消息id查找的消息为空");
		}
		Map<String, String> mqconfig = (Map<String, String>) ResourceUtils.getResource("mq_config");
		int maxTimes = Integer.valueOf(mqconfig.get("message.max.send.times"));
		if (message.getMessageSendTimes() >= maxTimes) {
			message.setAreadlyDead(PublicEnum.YES.name());
		}

		message.setEditTime(new Date());
		message.setMessageSendTimes(message.getMessageSendTimes() + 1);
		transactionMessageDao.update(message);

		notifyJmsTemplate.setDefaultDestinationName(message.getConsumerQueue());
		notifyJmsTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message.getMessageBody());
			}
		});
	}

	/**
	 * 
	 * 功能描述：将消息标记为死亡消息
	 *
	 * @author 吴俊明
	 *         <p>
	 * 		创建日期 ：2017年6月23日 下午3:11:43
	 *         </p>
	 *
	 * @param messageId
	 *            消息ID
	 * @throws MessageException
	 *
	 *             <p>
	 * 			修改历史 ：(修改人，修改时间，修改原因/内容)
	 *             </p>
	 */
	public void setMessageToAreadlyDead(String messageId) throws MessageException {
		TransactionMessage message = getMessageByMessageId(messageId);
		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, "根据消息id查找的消息为空");
		}

		message.setAreadlyDead(PublicEnum.YES.name());
		message.setEditTime(new Date());
		transactionMessageDao.update(message);
	}

	/**
	 * 
	 * 功能描述：根据消息ID获取消息
	 *
	 * @author 吴俊明
	 *         <p>
	 * 		创建日期 ：2017年6月23日 下午3:11:54
	 *         </p>
	 *
	 * @param messageId
	 *            消息ID
	 * @return
	 * @throws MessageException
	 *
	 *             <p>
	 * 			修改历史 ：(修改人，修改时间，修改原因/内容)
	 *             </p>
	 */
	public TransactionMessage getMessageByMessageId(String messageId) throws MessageException {
		
		return super.getObjByProperty("messageId", messageId, TransactionMessage.class);
	}

	/**
	 * 
	 * 功能描述：根据消息ID删除消息
	 *
	 * @author 吴俊明
	 *         <p>
	 * 		创建日期 ：2017年6月23日 下午3:12:05
	 *         </p>
	 *
	 * @param messageId
	 *            消息ID
	 * @throws MessageException
	 *
	 *             <p>
	 * 			修改历史 ：(修改人，修改时间，修改原因/内容)
	 *             </p>
	 */
	public void deleteMessageByMessageId(String messageId) throws MessageException{
		TransactionMessage message = getMessageByMessageId(messageId);
		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, "根据消息id查找的消息为空");
		}
		transactionMessageDao.remove(message.getId());
	}

	/**
	 * 
	 * 功能描述：重发某个消息队列中的全部已死亡的消息
	 *
	 * @author 吴俊明
	 *         <p>
	 * 		创建日期 ：2017年6月23日 下午3:12:14
	 *         </p>
	 *
	 * @param queueName
	 *            队列名称
	 * @param batchSize
	 *            批量执行数量
	 * @throws MessageException
	 *
	 *             <p>
	 * 			修改历史 ：(修改人，修改时间，修改原因/内容)
	 *             </p>
	 */
	public void reSendAllDeadMessageByQueueName(String queueName, int batchSize) throws MessageException{
		log.info("==>reSendAllDeadMessageByQueueName");
		int numPerPage = 1000;
		if (batchSize > 0 && batchSize < 100) {
			numPerPage = 100;
		} else if (batchSize > 100 && batchSize < 5000) {
			numPerPage = batchSize;
		} else if (batchSize > 5000) {
			numPerPage = 5000;
		} else {
			numPerPage = 1000;
		}
		int pageNum = 1;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from smt_transaction_message where 1=1 ");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<Object> objs = new ArrayList<Object>();
		if (StringUtil.isNotEmpty(queueName)) {
			sql.append(" and consumerQueue = ? ");
			objs.add(queueName);
		}
		sql.append(" and areadlyDead = ? ");
		objs.add(PublicEnum.YES.name());

		sql.append(" order by listPageSortType asc ");

		Map<String, TransactionMessage> messageMap = new HashMap<String, TransactionMessage>();
		List<Map> recordList = new ArrayList<Map>();
		int pageCount = 1;

		PageBean pageBean = transactionMessageDao.listPage(sql.toString(), new PageParam(pageNum, numPerPage),
				objs.toArray());
		recordList = pageBean.getRecordList();
		if (recordList == null || recordList.isEmpty()) {
			log.info("==>recordList is empty");
			return;
		}
		pageCount = pageBean.getTotalCount();
		for (final Object obj : recordList) {
			final TransactionMessage message = (TransactionMessage) obj;
			messageMap.put(message.getMessageId(), message);
		}

		for (pageNum = 2; pageNum <= pageCount; pageNum++) {
			pageBean = transactionMessageDao.listPage(sql.toString(), new PageParam(pageNum, numPerPage),
					objs.toArray());
			recordList = pageBean.getRecordList();

			if (recordList == null || recordList.isEmpty()) {
				break;
			}

			for (final Object obj : recordList) {
				final TransactionMessage message = (TransactionMessage) obj;
				messageMap.put(message.getMessageId(), message);
			}
		}

		recordList = null;
		pageBean = null;

		for (Map.Entry<String, TransactionMessage> entry : messageMap.entrySet()) {
			final TransactionMessage message = entry.getValue();

			message.setEditTime(new Date());
			message.setMessageSendTimes(message.getMessageSendTimes() + 1);
			transactionMessageDao.update(message);

			notifyJmsTemplate.setDefaultDestinationName(message.getConsumerQueue());
			notifyJmsTemplate.send(new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(message.getMessageBody());
				}
			});
		}

	}
}
