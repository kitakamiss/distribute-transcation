/**
 * MessageScheduled.java	  V1.0   2017年6月26日 下午2:30:27
 *
 * Copyright 2015 Hjrh Technology Co.,Ltd. All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package com.hjrh.facade.message.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hjrh.common.config.PublicConfig;
import com.hjrh.common.core.enums.PublicEnum;
import com.hjrh.common.page.PageBean;
import com.hjrh.common.page.PageParam;
import com.hjrh.facade.message.enums.MessageStatusEnum;
import com.hjrh.facade.message.service.MessageScheduled;
import com.hjrh.facade.message.service.TransactionMessageService;
import com.hjrh.service.message.dao.MessageBiz;

/**
 * 
 * 功能描述：消息定时器接口实现
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
@Component("messageScheduled")
public class MessageScheduledImpl implements MessageScheduled {
	
	private static final Log log = LogFactory.getLog(MessageScheduledImpl.class);

	@Autowired
	private TransactionMessageService transactionMessageService;
	
	@Autowired
	private MessageBiz messageBiz;

	/**
	 * 
	 * 功能描述：处理状态为“待确认”但已超时的消息.
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年6月26日 下午2:33:18</p>
	 *
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public void handleWaitingConfirmTimeOutMessages() {
		try {
			
			int numPerPage = 2000; // 每页条数
			int maxHandlePageCount = 3; // 一次最多处理页数
			
			Map<String, Object> paramMap = new HashMap<String, Object>(); // 查询条件
			//paramMap.put("consumerQueue", queueName); // 队列名（可以按不同业务队列分开处理）
			// 获取配置的开始处理的时间
			String dateStr = getCreateTimeBefore();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from smt_transaction_message where 1=1 ");
			List<Object> objs = new ArrayList<Object>();
			sql.append("and createTime < ? ").append(" and status = ? ");
			objs.add(dateStr);
			//取状态为“待确认”的消息
			objs.add(MessageStatusEnum.WAITING_CONFIRM.name());
			sql.append(" order by createTime asc ");
			
			List<Map> messageMap = getMessageMap(numPerPage, maxHandlePageCount,sql.toString(), objs);

			messageBiz.handleWaitingConfirmTimeOutMessages(messageMap);
			
		} catch (Exception e) {
			log.error("处理[waiting_confirm]状态的消息异常" + e);
		}
	}
	
	/**
	 * 处理状态为发送中但超时没有被成功消费确认的消息
	 */
	public void handleSendingTimeOutMessage() {
		try {

			int numPerPage = 2000; // 每页条数
			int maxHandlePageCount = 3; // 一次最多处理页数
			
			Map<String, Object> paramMap = new HashMap<String, Object>(); // 查询条件
			//paramMap.put("consumerQueue", queueName); // 队列名（可以按不同业务队列分开处理）
			// 获取配置的开始处理的时间
			String dateStr = getCreateTimeBefore();
			
			StringBuffer sql = new StringBuffer();
			sql.append("select * from smt_transaction_message where 1=1 ");
			List<Object> objs = new ArrayList<Object>();
			sql.append("and createTime < ? ").append(" and status = ? and areadlyDead = ? ");
			objs.add(dateStr);
			//取状态为“待确认”的消息
			objs.add(MessageStatusEnum.SENDING.name());
			objs.add(PublicEnum.NO.name());
			sql.append(" order by createTime asc ");
			List<Map> messageMap = getMessageMap(numPerPage, maxHandlePageCount, sql.toString(),objs);
			
			messageBiz.handleSendingTimeOutMessage(messageMap);
		} catch (Exception e) {
			log.error("处理发送中的消息异常" + e);
		}
	}
	
	/**
	 * 根据分页参数及查询条件批量获取消息数据.
	 * @param numPerPage 每页记录数.
	 * @param maxHandlePageCount 最多获取页数.
	 * @param paramMap 查询参数.
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Map> getMessageMap(int numPerPage, int maxHandlePageCount, String sql,List<Object> params){
		
		List<Map> list = new ArrayList<Map>();
		int pageNum = 1; // 当前页
		
		List<Map> recordList = new ArrayList<Map>(); // 每次拿到的结果集
		int pageCount = 1; // 总页数
		
		PageBean pageBean = transactionMessageService.queryForListPage(sql.toString(), new PageParam(pageNum, numPerPage), params.toArray());
				
		recordList = pageBean.getRecordList();
		if (recordList == null || recordList.isEmpty()) {
			log.info("==>recordList is empty");
			return list;
		}
		log.info("==>now page size:" + recordList.size());
		list.addAll(recordList);
		
		pageCount = pageBean.getTotalCount(); // 总页数(可以通过这个值的判断来控制最多取多少页)
		log.info("==>pageCount:" + pageCount);
		if (pageCount > maxHandlePageCount){
			pageCount = maxHandlePageCount;
			log.info("==>set pageCount:" + pageCount);
		}

		for (pageNum = 2; pageNum <= pageCount; pageNum++) {
			log.info("==>pageNum:" + pageNum + ", numPerPage:" + numPerPage);
			pageBean = transactionMessageService.queryForListPage(sql.toString(), new PageParam(pageNum, numPerPage), params.toArray());
			recordList = pageBean.getRecordList();
			if (recordList == null || recordList.isEmpty()) {
				break;
			}
			log.info("==>now page size:" + recordList.size());
			list.addAll(recordList);
		}
		recordList = null;
		
		return list;
	}
	
	/**
	 * 
	 * 功能描述：获取配置的开始处理的时间
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年6月26日 下午2:51:41</p>
	 *
	 * @return
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	private String getCreateTimeBefore() {
		String duration = PublicConfig.PUBLIC_MQ.get("message.handle.duration");
		long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
		Date date = new Date(currentTimeInMillis - Integer.valueOf(duration) * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = sdf.format(date);
		return dateStr;
	}
}
