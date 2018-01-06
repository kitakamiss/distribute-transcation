package com.hjrh.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hjrh.common.page.PageBean;
import com.hjrh.common.page.PageParam;
import com.hjrh.common.utils.CommUtil;
import com.hjrh.facade.notify.entity.NotifyRecord;
import com.hjrh.facade.notify.entity.NotifyRecordLog;
import com.hjrh.facade.notify.service.NotifyRecordLogService;
import com.hjrh.facade.notify.service.NotifyService;
import com.hjrh.param.NotifyParam;

/**
 * 
 * 功能描述：通知记录持久化类
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
@Service("notifyPersist")
public class NotifyPersist {

	private static final Log LOG = LogFactory.getLog(NotifyPersist.class);

	@Autowired
	private NotifyService notifyService;

	@Autowired
	private NotifyRecordLogService notifyRecordLogService;

	@Autowired
	private NotifyParam notifyParam;

	@Autowired
	private NotifyQueue notifyQueue;

	/**
	 * 
	 * 功能描述：创建商户通知记录
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年7月1日 上午11:37:57</p>
	 *
	 * @param notifyRecord
	 * @return
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public Long saveNotifyRecord(NotifyRecord notifyRecord) {
		try {
			notifyService.save(notifyRecord);
			return 1L;
		} catch (Exception e) {
			return 0L;
		}
	}

	/**
	 * 
	 * 功能描述：更新商户通知记录
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年7月1日 上午11:38:09</p>
	 *
	 * @param id 
	 * @param notifyTimes  通知次数
	 * @param status  通知状态
	 * @param editTime 更新时间
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public void updateNotifyRord(Long id, int notifyTimes, String status, Date editTime) {
		NotifyRecord notifyRecord = notifyService.getObjById(id);
		notifyRecord.setNotifyTimes(notifyTimes);
		notifyRecord.setStatus(status);
		notifyRecord.setEditTime(editTime);
		notifyRecord.setLastNotifyTime(editTime);
		notifyService.update(notifyRecord);
	}

	/**
	 * 
	 * 功能描述：创建商户通知日志记录
	 *
	 * @author 吴俊明
	 *         <p>
	 * 		创建日期 ：2017年7月1日 上午11:09:10
	 *         </p>
	 *
	 * @param notifyId
	 *            通知记录ID
	 * @param merchantNo
	 *            商户编号
	 * @param merchantOrderNo
	 *            商户订单号
	 * @param request
	 *            请求信息
	 * @param response
	 *            返回信息
	 * @param httpStatus
	 *            通知状态(HTTP状态)
	 * @return
	 *
	 *         <p>
	 * 		修改历史 ：(修改人，修改时间，修改原因/内容)
	 *         </p>
	 */
	public long saveNotifyRecordLogs(Long notifyId, String merchantNo, String merchantOrderNo, String request,
			String response, int httpStatus) {
		NotifyRecordLog notifyRecordLog = new NotifyRecordLog();
		notifyRecordLog.setNotifyId(notifyId);
		notifyRecordLog.setMerchantNo(merchantNo);
		notifyRecordLog.setMerchantOrderNo(merchantOrderNo);
		notifyRecordLog.setRequest(request);
		notifyRecordLog.setResponse(response);
		notifyRecordLog.setHttpStatus(httpStatus);
		notifyRecordLog.setCreateTime(new Date());
		notifyRecordLog.setEditTime(new Date());
		try {
			notifyRecordLogService.save(notifyRecordLog);
			return 1L;
		} catch (Exception e) {
			return 0L;
		}
	}

	/**
	 * 
	 * 功能描述：从数据库中取一次数据用来当系统启动时初始化
	 *
	 * @author 吴俊明
	 *         <p>
	 * 		创建日期 ：2017年7月1日 上午11:11:02
	 *         </p>
	 *
	 *
	 *         <p>
	 * 		修改历史 ：(修改人，修改时间，修改原因/内容)
	 *         </p>
	 */
	public void initNotifyDataFromDB() {
		LOG.info("===>init get notify data from database");

		int pageNum = 1; // 当前页
		int numPerPage = 100; // 每页记录数

		PageParam pageParam = new PageParam(pageNum, numPerPage);

		List<Map> recordList = new ArrayList<Map>(); // 每次拿到的结果集

		// 组装查询条件，通知状态不成功，并且还没有超过最大通知次数的
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("statusNotSuccess", "statusNotSuccess"); // 不是成功状态
		paramMap.put("statusNotFailed", "statusNotFailed"); // 不是失败状态
		paramMap.put("maxNotifyTimes", notifyParam.getMaxNotifyTimes());

		// >>>>>>>>>> 查第一页开始 >>>>>>>>>>
		LOG.info("==>pageNum:" + pageNum + ", numPerPage:" + numPerPage);

		StringBuffer sql = new StringBuffer();
		List<Object> objs = new ArrayList<Object>();
		sql.append("select * from smt_notify_record where 1=1 ");

		PageBean pageBean = notifyService.queryForListPage(sql.toString(), pageParam, objs.toArray());
		recordList = pageBean.getRecordList();
		if (recordList == null || recordList.isEmpty()) {
			LOG.info("==>recordList is empty");
			return;
		}
		LOG.info("==>now page size:" + recordList.size());

		int totalPage = pageBean.getTotalCount(); // 总页数
		int totalCount = pageBean.getTotalCount(); // 总记录数
		LOG.info("===>totalPage:" + totalPage);
		LOG.info("===>totalCount:" + totalCount);

		for (Map notifymap : recordList) {
			NotifyRecord notifyRecord = null;
			try {
				notifyRecord = (NotifyRecord) CommUtil.convertMap(NotifyRecord.class, notifymap);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			notifyQueue.addToNotifyTaskDelayQueue(notifyRecord);
		}
		// 如果有第2页或以上页
		for (pageNum = 2; pageNum <= totalPage; pageNum++) {
			LOG.info("==>pageNum:" + pageNum + ", numPerPage:" + numPerPage);
			pageParam = new PageParam(pageNum, numPerPage);
			pageBean = notifyService.queryForListPage(sql.toString(), pageParam, objs.toArray());
			recordList = pageBean.getRecordList();
			if (recordList == null || recordList.isEmpty()) {
				LOG.info("==>recordList is empty");
				return;
			}
			LOG.info("==>now page size:" + recordList.size());

			for (Map notifymap : recordList) {
				NotifyRecord notifyRecord = null;
				try {
					notifyRecord = (NotifyRecord) CommUtil.convertMap(NotifyRecord.class, notifymap);
				} catch (Exception e) {
					e.printStackTrace();
				} 
				notifyQueue.addToNotifyTaskDelayQueue(notifyRecord);
			}
		}
	}

}
