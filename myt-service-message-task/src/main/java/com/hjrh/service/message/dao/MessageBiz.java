/**
 * MessageScheduled.java	  V1.0   2017年6月26日 下午2:30:27
 *
 * Copyright 2015 Hjrh Technology Co.,Ltd. All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package com.hjrh.service.message.dao;

import java.text.SimpleDateFormat;
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
import com.hjrh.common.utils.CommUtil;
import com.hjrh.facade.discount.entity.DiscountUser;
import com.hjrh.facade.discount.service.DiscountUserService;
import com.hjrh.facade.message.entity.TransactionMessage;
import com.hjrh.facade.message.service.TransactionMessageService;
import com.hjrh.facade.order.entity.OrderForm;
import com.hjrh.facade.order.enums.OrderStatusEnum;
import com.hjrh.facade.order.service.OrderFormService;

/**
 * 
 * 功能描述：message业务处理类
 *
 * @author 吴俊明
 *
 *         <p>
 *         修改历史：(修改人，修改时间，修改原因/内容)
 *         </p>
 */
@Component("messageBiz")
public class MessageBiz {

	private static final Log log = LogFactory.getLog(MessageBiz.class);

	@Autowired
	private TransactionMessageService transactionMessageService;

	@Autowired
	private OrderFormService orderFormService;

	@Autowired
	private DiscountUserService discountUserService;

	/**
	 * 
	 * 功能描述：处理待确认状态消息
	 *
	 * @author 吴俊明
	 *         <p>
	 *         创建日期 ：2017年6月26日 下午3:58:12
	 *         </p>
	 *
	 * @param messages
	 *
	 *            <p>
	 *            修改历史 ：(修改人，修改时间，修改原因/内容)
	 *            </p>
	 */
	public void handleWaitingConfirmTimeOutMessages(List<Map> messages) {
		log.debug("开始处理[waiting_confirm]状态的消息,总条数[" + messages.size() + "]");
		// 单条消息处理（目前该状态的消息，消费队列全部是订单，如果后期有业务扩充，需做队列判断，做对应的业务处理。）
		for (Map message : messages) {
			try {
				log.debug("开始处理[waiting_confirm]消息ID为[" + message.get("id") + "]的消息");
				String orderNo = CommUtil.null2String(message.get("field1"));
				OrderForm orderForm = orderFormService.getObjByProperty("order_id", orderNo, OrderForm.class);
				// 如果订单成功，把消息改为待处理，并发送消息
				if (orderForm != null) {
					if (OrderStatusEnum.ORDER_YFK.name().equals(orderForm.getOrder_status())) {
						// 确认并发送消息
						transactionMessageService.confirmAndSendMessage(CommUtil.null2String(message.get("messageId")));
					} else if (OrderStatusEnum.ORDER_DFK.name().equals(orderForm.getOrder_status())) {
						// 订单状态是等待支付，把消息改为待处理，并发送消息
						transactionMessageService.confirmAndSendMessage(CommUtil.null2String(message.get("messageId")));
					} else if (OrderStatusEnum.ORDER_YQX.name().equals(orderForm.getOrder_status())) {
						// 订单状态取消，可以直接删除数据
						log.debug("订单状态取消,删除[waiting_confirm]消息id[" + CommUtil.null2String(message.get("messageId"))
								+ "]的消息");
						transactionMessageService
								.deleteMessageByMessageId(CommUtil.null2String(message.get("messageId")));
					}
				}
				log.debug("结束处理[waiting_confirm]消息ID为[" + message.get("id") + "]的消息");
			} catch (Exception e) {
				log.error("处理[waiting_confirm]消息ID为[" + message.get("id") + "]的消息异常：", e);
			}
		}
	}

	/**
	 * 
	 * 功能描述：处理发送中状态的消息
	 *
	 * @author 吴俊明
	 *         <p>
	 *         创建日期 ：2017年6月26日 下午2:40:15
	 *         </p>
	 *
	 * @param messageMap
	 *
	 *            <p>
	 *            修改历史 ：(修改人，修改时间，修改原因/内容)
	 *            </p>
	 */
	public void handleSendingTimeOutMessage(List<Map> messages) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		log.debug("开始处理[SENDING]状态的消息,总条数[" + messages.size() + "]");

		// 根据配置获取通知间隔时间
		Map<Integer, Integer> notifyParam = getSendTime();

		// 单条消息处理
		for (Map message : messages) {
			try {
				log.debug("开始处理[SENDING]消息ID为[" + message.get("id") + "]的消息");
				// 判断发送次数
				int maxTimes = CommUtil.null2Int(PublicConfig.PUBLIC_MQ.get("message.max.send.times"));
				int reSendTimes = CommUtil.null2Int(message.get("messageSendTimes"));
				log.debug("[SENDING]消息ID为[" + message.get("id") + "]的消息,已经重新发送的次数[" + message.get("messageSendTimes")
						+ "]");

				// 如果超过最大发送次数直接退出
				if (maxTimes < reSendTimes) {
					// 标记为死亡
					transactionMessageService.setMessageToAreadlyDead(CommUtil.null2String(message.get("messageId")));
					continue;
				}
				// 判断是否达到发送消息的时间间隔条件

				int times = notifyParam.get(reSendTimes == 0 ? 1 : reSendTimes);

				long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
				long needTime = currentTimeInMillis - times * 60 * 1000;

				long hasTime = CommUtil.formatDate(CommUtil.null2String(message.get("editTime")), "yyyy-MM-dd HH:mm:ss")
						.getTime();
				// 判断是否达到了可以再次发送的时间条件
				if (hasTime > needTime) {
					log.debug("currentTime[" + sdf.format(new Date()) + "],[SENDING]消息上次发送时间["
							+ CommUtil.null2String(message.get("editTime")) + "],必须过了[" + times + "]分钟才可以再发送。");
					continue;
				}
				TransactionMessage rpTransactionMessage = (TransactionMessage) CommUtil
						.convertMap(TransactionMessage.class, message);
				// 重新发送消息
				transactionMessageService.reSendMessage(rpTransactionMessage);

				log.debug("结束处理[SENDING]消息ID为[" + message.get("id") + "]的消息");
			} catch (Exception e) {
				log.error("处理[SENDING]消息ID为[" + message.get("id") + "]的消息异常：", e);
			}
		}
	}

	/**
	 * 根据配置获取通知间隔时间
	 * 
	 * @return
	 */
	private Map<Integer, Integer> getSendTime() {
		Map<Integer, Integer> notifyParam = new HashMap<Integer, Integer>();

		notifyParam.put(1, CommUtil.null2Int(PublicConfig.PUBLIC_MQ.get("message.send.1.time")));
		notifyParam.put(2, CommUtil.null2Int(PublicConfig.PUBLIC_MQ.get("message.send.2.time")));
		notifyParam.put(3, CommUtil.null2Int(PublicConfig.PUBLIC_MQ.get("message.send.3.time")));
		notifyParam.put(4, CommUtil.null2Int(PublicConfig.PUBLIC_MQ.get("message.send.4.time")));
		notifyParam.put(5, CommUtil.null2Int(PublicConfig.PUBLIC_MQ.get("message.send.5.time")));
		return notifyParam;
	}
}
