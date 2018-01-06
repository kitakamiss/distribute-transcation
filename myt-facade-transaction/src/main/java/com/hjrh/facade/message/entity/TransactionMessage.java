package com.hjrh.facade.message.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.hjrh.common.entity.IdEntity;


/**
 * 
 * 功能描述：消息
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "smt_transaction_message")
public class TransactionMessage extends IdEntity {

	private static final long serialVersionUID = 1757377457814546156L;
	
	@Column(columnDefinition = "int default 0")
	@Version
	private int version;
	
	/**
	 * 状态  PublicStatusEnum
	 */
	private String status;
		
	/**
	 * 创建人
	 */
	private String creater;
	
	/**
	 * 创建时间.
	 */
	private Date createTime = new Date();
	
	/**
	 * 修改人.
	 */
	private String editor;
	
	/**
	 * 修改时间.
	 */
	private Date editTime;
	/**
	 * 描述
	 */
	private String remark;

	
	/**
	 * 消息内容
	 */
	@Lob
	@Column(columnDefinition = "LongText")
	private String messageBody;

	/**
	 * 消息数据类型
	 */
	private String messageDataType;

	/**
	 * 消费队列
	 */
	private String consumerQueue;

	/**
	 * 消息重发次数
	 */
	private Integer messageSendTimes;

	/**
	 * 是否死亡
	 */
	private String areadlyDead;
	
	/**
	 * 扩展字段
	 */
	private String field1;

	private String field2;

	private String field3;

	public TransactionMessage() {
		super();
	}


	public TransactionMessage(String messageId, String messageBody, String consumerQueue) {
		super();
		this.messageId = messageId;
		this.messageBody = messageBody;
		this.consumerQueue = consumerQueue;
	}


	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String getMessageDataType() {
		return messageDataType;
	}

	public void setMessageDataType(String messageDataType) {
		this.messageDataType = messageDataType;
	}

	public String getConsumerQueue() {
		return consumerQueue;
	}

	public void setConsumerQueue(String consumerQueue) {
		this.consumerQueue = consumerQueue;
	}

	public Integer getMessageSendTimes() {
		return messageSendTimes;
	}

	public void setMessageSendTimes(Integer messageSendTimes) {
		this.messageSendTimes = messageSendTimes;
	}

	public String getAreadlyDead() {
		return areadlyDead;
	}

	public void setAreadlyDead(String areadlyDead) {
		this.areadlyDead = areadlyDead;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	public void addSendTimes() {
		messageSendTimes++;
	}


	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getCreater() {
		return creater;
	}


	public void setCreater(String creater) {
		this.creater = creater;
	}


	public Date getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	public String getEditor() {
		return editor;
	}


	public void setEditor(String editor) {
		this.editor = editor;
	}


	public Date getEditTime() {
		return editTime;
	}


	public void setEditTime(Date editTime) {
		this.editTime = editTime;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}

}