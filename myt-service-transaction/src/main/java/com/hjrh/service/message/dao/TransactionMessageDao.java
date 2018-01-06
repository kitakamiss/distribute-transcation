/**
 * RpTransactionMessageDao.java	  V1.0   2017年6月23日 下午3:23:03
 *
 * Copyright 2015 Hjrh Technology Co.,Ltd. All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package com.hjrh.service.message.dao;

import org.springframework.stereotype.Repository;

import com.hjrh.common.core.biz.GenericDAO;
import com.hjrh.facade.message.entity.TransactionMessage;

/**
 * 
 * 功能描述：事务
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
@Repository("transactionMessageDao")
public class TransactionMessageDao  extends GenericDAO<TransactionMessage> {

}
