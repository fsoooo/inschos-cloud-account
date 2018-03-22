package com.inschos.cloud.account.access.rpc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.inschos.cloud.account.access.rpc.service.AccountService;
import com.inschos.cloud.account.assist.kit.L;

/**
 * Created by IceAnt on 2018/3/21.
 */
@Service
public class AccountServiceImpl implements AccountService{

    @Override
    public boolean isLogin(String token) {
        L.log.debug("token is : "+token);
        return true;
    }

}
