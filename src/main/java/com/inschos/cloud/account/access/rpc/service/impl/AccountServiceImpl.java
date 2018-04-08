package com.inschos.cloud.account.access.rpc.service.impl;

import com.inschos.cloud.account.access.http.controller.bean.ActionBean;
import com.inschos.cloud.account.access.rpc.service.AccountService;
import com.inschos.cloud.account.assist.kit.L;
import com.inschos.cloud.account.data.dao.AccountDao;
import com.inschos.cloud.account.model.Account;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by IceAnt on 2018/3/21.
 */
@org.springframework.stereotype.Service("accountService")
public class AccountServiceImpl implements AccountService{

    @Autowired
    private AccountDao accountDao;

    @Override
    public Account getAccount(String token) {
        L.log.debug("verifyToken is : "+token);
        ActionBean actionBean = ActionBean.parseToken(token);
        Account resultAccount = new Account();
        if(actionBean!=null){
            Account account = accountDao.findByUuid(actionBean.accountUuid);
            if(token!=null && account!=null && token.equals(account.token)){
                resultAccount = account;
            }
        }
        return resultAccount;
    }

}
