package com.hjrh.facade.message.exceptions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hjrh.common.core.exceptions.HjrhException;



/**
 * 
 * 功能描述：消息模块业务异常类
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class MessageException extends HjrhException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3536909333010163563L;

	/** 保存的消息为空 **/
	public static final int SAVA_MESSAGE_IS_NULL = 8001;
	
	/** 消息的消费队列为空 **/
	public static final int MESSAGE_CONSUMER_QUEUE_IS_NULL = 8002;

	private static final Log LOG = LogFactory.getLog(MessageException.class);

	public MessageException() {
	}

	public MessageException(int code, String msgFormat, Object... args) {
		super(code, msgFormat, args);
	}

	public MessageException(int code, String msg) {
		super(code, msg);
	}

	public MessageException print() {
		LOG.info("==>HjrhException, code:" + this.code + ", msg:" + this.msg);
		return this;
	}
}
