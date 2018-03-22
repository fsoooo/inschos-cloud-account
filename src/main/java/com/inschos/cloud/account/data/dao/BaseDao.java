package com.inschos.cloud.account.data.dao;

import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * Created by IceAnt on 2018/3/20.
 */
public class BaseDao {
    public void rollBack() {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
