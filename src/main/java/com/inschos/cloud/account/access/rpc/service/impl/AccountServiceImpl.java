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
        String message;
        int code;
        if(actionBean!=null){

            Account account = accountDao.findByUuid(actionBean.accountUuid);
            if(token!=null && account!=null && ActionBean.getSalt(account.salt).equals(actionBean.salt)){
                accountBean = toBean(account);
                accountBean.managerUuid = actionBean.managerUuid;
                if(accountBean.userType==Account.TYPE_AGENT && StringKit.isEmpty(accountBean.managerUuid)){
                    code = 504;
                    message = "未选择业管";
                }else{
                    code = 200;
                    message = "登录验证成功";
                }
            }else{
                code = 502;
                message = "登录验证失败";
            }
        }else{
            code = 502;
            message = "登录验证失败";
        }
        if(accountBean==null){
            accountBean = new AccountBean();
        }
        accountBean.code = code;
        accountBean.message = message;
        return accountBean;
    }

    @Override
    public AccountBean findByUuid(String uuid) {
        AccountBean bean = null;
        if(!StringKit.isEmpty(uuid)){
            Account account = accountDao.findByUuid(uuid);
            if(account!=null){
                bean = toBean(account);
            }
        }
        return bean;
    }

    @Override
    public AccountBean findByAgentPhone(long sysId, String phone) {
        AccountBean bean = null;
        if(!StringKit.isEmpty(phone)){
            Account account = accountDao.findByAccount(sysId,phone,Account.TYPE_AGENT,Account.ACCOUNT_FILED_PHONE);
            bean = toBean(account);
        }
        return bean;
    }

    private AccountBean toBean(Account account){
        AccountBean bean = null;
        if(account!=null){
            bean = new AccountBean();
            bean.userId = account.user_id;
            bean.userType = account.user_type;
            bean.username = account.username;
            bean.phone = account.phone;
            bean.email = account.email;
            bean.accountUuid = account.account_uuid;
            bean.sysId = account.sys_id;
        }
        return bean;
    }

}
