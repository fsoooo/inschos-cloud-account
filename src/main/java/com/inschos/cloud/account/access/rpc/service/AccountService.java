package com.inschos.cloud.account.access.rpc.service;

import com.inschos.cloud.account.access.rpc.bean.AccountBean;
import com.inschos.cloud.account.model.PlatformSystem;

/**
 * Created by IceAnt on 2018/3/21.
 */

public interface AccountService {

    /**通过token，获取账号信息*/
    public AccountBean getAccount(String token);

    /** 通过账号uuid 获取账号信息*/
    public AccountBean findByUuid(String uuid);

    public AccountBean findByAgentPhone(long sysId,String phone);

    public AccountBean findByUser(long sysId,int userType,String userId);

    PlatformSystem getSystem(long sId);

}
