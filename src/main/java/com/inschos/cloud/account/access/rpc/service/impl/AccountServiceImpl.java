package com.inschos.cloud.account.access.rpc.service.impl;

import com.inschos.cloud.account.access.http.controller.bean.ActionBean;
import com.inschos.cloud.account.access.rpc.bean.AccountBean;
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
    public AccountBean getAccount(String token) {
        L.log.debug("verifyToken is : "+token);
        ActionBean actionBean = ActionBean.parseToken(token);
        AccountBean accountBean = null;
        if(actionBean!=null){
            Account account = accountDao.findByUuid(actionBean.loginUuid);
            if(token!=null && account!=null && token.equals(account.token)){
                accountBean = new AccountBean();
                accountBean.accountUuid = actionBean.belongAccountUuid;
                accountBean.loginUuid = actionBean.loginUuid;
                accountBean.userId = account.user_id;
                accountBean.userType = account.type;
                accountBean.username = account.username;
                accountBean.phone = account.phone;
                accountBean.email = account.email;
            }
        }
        return accountBean;
    }

}
