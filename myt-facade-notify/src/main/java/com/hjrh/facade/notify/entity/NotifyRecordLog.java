package com.hjrh.facade.notify.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.hjrh.common.entity.IdEntity;

/**
 * 
 * 功能描述：通知日志
 *
 * @author 吴俊明
 *
 *         <p>
 * 		修改历史：(修改人，修改时间，修改原因/内容)
 *         </p>
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "smt_notify_record_log")
public class NotifyRecordLog extends IdEntity implements Serializable {
	private static final long serialVersionUID = 459406550725396000L;

	/** 
	 * 通知记录ID 
	 */
	private Long notifyId;

	/** 
	 * 请求信息
	 */
	private String request;

	/** 
	 * 返回信息
	 */
	private String response;

	/** 
	 * 商户编号 
	 */
	private String merchantNo;

	/** 
	 * 商户订单号
	 */
	private String merchantOrderNo;

	/** 
	 * HTTP状态
	 */
	private Integer httpStatus;

	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 编辑时间
	 */
	private Date editTime;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public NotifyRecordLog() {
		super();
	}

	public NotifyRecordLog(Date createTime, Long notifyId, String request, String response, String merchantNo,
			String merchantOrderNo, Integer httpStatus) {
		super();
		this.createTime = createTime;
		this.notifyId = notifyId;
		this.request = request;
		this.response = response;
		this.merchantNo = merchantNo;
		this.merchantOrderNo = merchantOrderNo;
		this.httpStatus = httpStatus;
	}

	public Long getNotifyId() {
		return notifyId;
	}

	public void setNotifyId(Long notifyId) {
		this.notifyId = notifyId;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request == null ? null : request.trim();
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response == null ? null : response.trim();
	}

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo == null ? null : merchantNo.trim();
	}

	public String getMerchantOrderNo() {
		return merchantOrderNo;
	}

	public void setMerchantOrderNo(String merchantOrderNo) {
		this.merchantOrderNo = merchantOrderNo == null ? null : merchantOrderNo.trim();
	}

	public Integer getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(Integer httpStatus) {
		this.httpStatus = httpStatus;
	}

	public Date getEditTime() {
		return editTime;
	}

	public void setEditTime(Date editTime) {
		this.editTime = editTime;
	}

}
