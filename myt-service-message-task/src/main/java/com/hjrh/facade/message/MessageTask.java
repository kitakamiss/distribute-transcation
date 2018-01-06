/**
 * MessageTask.java	  V1.0   2017年6月28日 上午10:42:38
 *
 * Copyright 2015 Hjrh Technology Co.,Ltd. All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package com.hjrh.facade.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.hjrh.facade.message.service.MessageScheduled;

/**
 * 
 * 功能描述：消息处理定时器<br/>
 * 主要分为两步： MessageStatusEnum <br/>
 * 1.处理状态为“待确认”但已超时的消息 <br/>
 * 2.处理状态为“发送中”但超时没有被成功消费确认的消息 <br/>
 * 
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class MessageTask {

	private static final Log log = LogFactory.getLog(MessageTask.class);

	private MessageTask() {

	}

	public static void main(String[] args) {
		try {
			@SuppressWarnings("resource")
			final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/spring-context.xml");
			context.start();
			log.info("定时任务开始执行>>>");
			final MessageScheduled settScheduled = (MessageScheduled) context.getBean("messageScheduled");
			ThreadPoolTaskExecutor threadPool = (ThreadPoolTaskExecutor) context.getBean("threadPool");
			// 开一个子线程处理状态为“待确认”但已超时的消息.
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					while (true) {
						log.info("执行(处理[waiting_confirm]状态的消息)任务开始");
						settScheduled.handleWaitingConfirmTimeOutMessages();
						log.info("执行(处理[waiting_confirm]状态的消息)任务结束");
						try {
							log.info("[waiting_confirm]睡眠60秒");
							Thread.sleep(60000);
						} catch (InterruptedException e) {
						}
					}
				}
			});

			// 开一个子线程处理状态为“发送中”但超时没有被成功消费确认的消息
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					while (true) {
						log.info("执行(处理[SENDING]的消息)任务开始");
						settScheduled.handleSendingTimeOutMessage();
						log.info("执行(处理[SENDING]的消息)任务结束");
						try {
							log.info("[SENDING]睡眠60秒");
							Thread.sleep(60000);
						} catch (InterruptedException e) {
						}
					}
				}
			});

		} catch (Exception e) {
			log.error("===>DubboReference context start error:", e);
		}
	}
}