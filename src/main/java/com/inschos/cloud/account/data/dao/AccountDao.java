package com.inschos.cloud.account.data.dao;

import com.inschos.cloud.account.assist.kit.StringKit;
import com.inschos.cloud.account.data.mapper.AccountMapper;
import com.inschos.cloud.account.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/3/23.
 */
@Component
public class AccountDao {


    @Autowired
    private AccountMapper accountMapper;

    /**
     *
     * @param accountName  账号
     * @param accountType 账号类型 type
     * @param searchAccountFiled  检索对应账号字段  1 username 2 phone 3 email
     * @return
     */
    public Account findByAccount(long sysId,String  accountName,int accountType,int searchAccountFiled){
        Account search = new Account();
        search.searchAccountFiled = searchAccountFiled;
        search.type = accountType;
        search.sys_id = sysId;
        switch (searchAccountFiled){
            case Account.ACCOUNT_FILED_USERNAME:
                search.username = accountName;
                break;
            case Account.ACCOUNT_FILED_PHONE:
                search.phone = accountName;
                break;
            case Account.ACCOUNT_FILED_EMAIL:
                search.email = accountName;
                break;
        }
        return accountMapper.findByAccount(search);

    }

    public Account findOneChannelSystem(Account search){
        return search!=null?accountMapper.findOneChannelSystem(search):null;
    }

    public int registry(Account account){
        return account!=null?accountMapper.insertRegistry(account):0;
    }

    public int updatePassword(Account account){
        return account!=null?accountMapper.updatePasswordByUuid(account):0;
    }

    public int updatePasswordTokenByUuid(Account account){
        return account!=null?accountMapper.updatePasswordTokenByUuid(account):0;
    }

    public int updateTokenByUuid(Account account){
        return account!=null?accountMapper.updateTokenByUuid(account):0;
    }

    public int updatePhoneByUuid(Account account){
        return account!=null?accountMapper.updatePhoneByUuid(account):0;
    }

    public int updateEmailByUuid(Account account){
        return account!=null?accountMapper.updateEmailByUuid(account):0;
    }

    public Account findByUuid(String accountUuid){
        return !StringKit.isEmpty(accountUuid)?accountMapper.findByUuid(accountUuid):null;
    }


}
