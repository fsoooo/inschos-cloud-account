package com.inschos.cloud.account.access.rpc.service.impl;

import com.inschos.cloud.account.access.http.controller.bean.ActionBean;
import com.inschos.cloud.account.access.rpc.bean.AccountBean;
import com.inschos.cloud.account.access.rpc.service.AccountService;
import com.inschos.cloud.account.assist.kit.L;
import com.inschos.cloud.account.assist.kit.StringKit;
import com.inschos.cloud.account.data.dao.AccountDao;
import com.inschos.cloud.account.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by IceAnt on 2018/3/21.
 */
@Service
public class AccountServiceImpl implements AccountService{

    @Autowired
    private AccountDao accountDao;

    @Override
    public AccountBean getAccount(String token) {

        L.log.debug("verifyToken is : "+token);
        ActionBean actionBean = ActionBean.parseToken(token);
        AccountBean accountBean = null;
        if(actionBean!=null){
            Account account = accountDao.findByUuid(actionBean.accountUuid);
            if(token!=null && account!=null && ActionBean.getSalt(account.salt).equals(actionBean.salt)){
                accountBean = new AccountBean();
                accountBean.managerUuid = actionBean.managerUuid;
                accountBean.accountUuid = actionBean.accountUuid;
                accountBean.userId = account.user_id;
                accountBean.userType = account.user_type;
                accountBean.username = account.username;
                accountBean.phone = account.phone;
                accountBean.email = account.email;
            }
        }
        return accountBean;
    }

    @Override
    public AccountBean findByUuid(String uuid) {
        AccountBean bean = null;
        if(!StringKit.isEmpty(uuid)){
            Account account = accountDao.findByUuid(uuid);
            if(account!=null){
                bean = new AccountBean();
                bean.userId = account.user_id;
                bean.userType = account.user_type;
                bean.username = account.username;
                bean.phone = account.phone;
                bean.email = account.email;
                bean.accountUuid = uuid;
            }
        }
        return bean;
    }

}
