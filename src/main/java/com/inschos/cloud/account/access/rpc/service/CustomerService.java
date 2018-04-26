package com.inschos.cloud.account.access.rpc.service;

/**
 * Created by IceAnt on 2018/4/26.
 */
public interface CustomerService {

    int  saveCust(int type,int customerId,String accountUuid,String managerUuid);

}
