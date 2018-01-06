package com.hjrh.service.notify.dao;

import org.springframework.stereotype.Repository;

import com.hjrh.common.core.biz.GenericDAO;
import com.hjrh.facade.notify.entity.NotifyRecord;
/**
 * 
 * 功能描述：通知dao
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
@Repository("notifyRecordDao")
public class NotifyRecordDao  extends GenericDAO<NotifyRecord> {

}