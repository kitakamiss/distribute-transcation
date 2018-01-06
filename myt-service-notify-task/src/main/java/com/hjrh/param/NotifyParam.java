package com.hjrh.param;

import java.util.Map;

/**
 * 
 * 功能描述：通知参数（通知规则），从XML中加载.
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class NotifyParam {

	/**
	 * 通知参数（通知规则Map）
	 */
    private Map<Integer, Integer> notifyParams;
    
    /**
     * 通知后用于判断是否成功的返回值（成功标识）,由HttpResponse获取
     */
    private String successValue;

    public Map<Integer, Integer> getNotifyParams() {
        return notifyParams;
    }

    public void setNotifyParams(Map<Integer, Integer> notifyParams) {
        this.notifyParams = notifyParams;
    }

    public String getSuccessValue() {
        return successValue;
    }

    public void setSuccessValue(String successValue) {
        this.successValue = successValue;
    }

    /**
     * 最大通知次数限制.
     * @return
     */
    public Integer getMaxNotifyTimes() {
        return notifyParams == null ? 0 : notifyParams.size();
    }

}
