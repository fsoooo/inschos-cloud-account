package com.inschos.cloud.account.access.rpc.service;

import com.inschos.cloud.account.access.rpc.bean.AccountBean;

/**
 * Created by IceAnt on 2018/3/21.
 */

public interface AccountService {

    /**通过token，获取账号信息*/
    public AccountBean getAccount(String token);

    /** 通过账号uuid 获取账号信息*/
    public AccountBean findByUuid(String uuid);
}
