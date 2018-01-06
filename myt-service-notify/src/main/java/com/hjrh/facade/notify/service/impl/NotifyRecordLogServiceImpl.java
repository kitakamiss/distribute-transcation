package com.hjrh.facade.notify.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hjrh.common.core.dao.IGenericDAO;
import com.hjrh.common.core.service.BaseServiceImpl;
import com.hjrh.facade.notify.entity.NotifyRecordLog;
import com.hjrh.facade.notify.service.NotifyRecordLogService;
import com.hjrh.service.notify.dao.NotifyRecordLogDao;

/**
 * 
 * 功能描述：通知接口实现
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
@Service("notifyRecordLogService")
public class NotifyRecordLogServiceImpl extends BaseServiceImpl<NotifyRecordLog> implements NotifyRecordLogService {

	@Resource(name = "notifyRecordLogDao")
    private NotifyRecordLogDao notifyRecordLogDao;
    
    @Override
	protected IGenericDAO<NotifyRecordLog> getDao() {
		return notifyRecordLogDao;
	}

}
