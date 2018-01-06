package com.hjrh.facade.notify.exceptions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hjrh.common.exceptions.BaseException;

/**
 * 
 * 功能描述：  通知异常类
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class NotifyException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 3536909333010163563L;

    /** 保存的消息为空 **/
    public static final int NOTIFY_SYSTEM_EXCEPTION = 9001;

    private static final Log LOG = LogFactory.getLog(NotifyException.class);

    public NotifyException() {
    }

    public NotifyException(int code, String msgFormat, Object... args) {
        super(code, msgFormat, args);
    }

    public NotifyException(int code, String msg) {
        super(code, msg);
    }

    public NotifyException print() {
        LOG.info("==>BaseException, code:" + this.code + ", msg:" + this.msg);
        return this;
    }
}
