package com.inschos.cloud.account.data.dao;

import com.inschos.cloud.account.assist.kit.StringKit;
import com.inschos.cloud.account.assist.kit.TimeKit;
import com.inschos.cloud.account.data.mapper.AccountDefaultMapper;
import com.inschos.cloud.account.data.mapper.AccountMapper;
import com.inschos.cloud.account.model.Account;
import com.inschos.cloud.account.model.AccountDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by IceAnt on 2018/3/23.
 */
@Component
public class AccountDao {


    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountDefaultMapper accountDefaultMapper;

    /**
     *
     * @param accountName  账号
     * @param accountType 账号类型 user_type
     * @param searchAccountFiled  检索对应账号字段  1 username 2 phone 3 email
     * @return
     */
    public Account findByAccount(long sysId,String  accountName,int accountType,int searchAccountFiled){
        Account search = new Account();
        search.searchAccountFiled = searchAccountFiled;
        search.user_type = accountType;
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

    public Account findOneBySysType(Account search){
        return search!=null?accountMapper.findOneBySysType(search):null;
    }

    public List<Account> findListBySysType(Account search){
        return search!=null?accountMapper.findListBySysType(search):null;
    }

    public Account findByUser(Account search){
        return search!=null?accountMapper.findByUser(search):null;
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

    public AccountDefault findAccountDefault(String accountUuid){
        return StringKit.isEmpty(accountUuid)?null:accountDefaultMapper.selectOneByAccount(accountUuid);
    }

    public int addOrUpdate(AccountDefault accountDefault){
        int flag = 0;
        if(accountDefault!=null){
            AccountDefault oldDefault = accountDefaultMapper.selectOneByAccount(accountDefault.account_uuid);
            if(oldDefault!=null){
                oldDefault.manager_uuid = accountDefault.manager_uuid;
                oldDefault.updated_at = accountDefault.updated_at;
                flag = accountDefaultMapper.update(oldDefault);
            }else{
                accountDefault.created_at = TimeKit.currentTimeMillis();
                flag = accountDefaultMapper.insert(accountDefault);
            }
        }
        return flag;
    }

}
