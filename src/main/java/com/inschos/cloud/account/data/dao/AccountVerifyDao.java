package com.inschos.cloud.account.data.dao;

import com.inschos.cloud.account.data.mapper.AccountVerifyMapper;
import com.inschos.cloud.account.model.AccountVerify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/3/28.
 */
@Component
public class AccountVerifyDao {
    @Autowired
    private AccountVerifyMapper accountVerifyMapper;

    public int insert(AccountVerify record){
        return record!=null?accountVerifyMapper.insert(record):0;
    }

    /**
     * 获取最新的验证信息
     * @param verifyName  验证主体号
     * @param fromAccountType 验证账号来源
     * @return
     */
    public AccountVerify findLatestByFromVerify(String verifyName,int fromAccountType,long sysId){
        AccountVerify search = new AccountVerify();
        search.verify_name = verifyName;
        search.from_type = fromAccountType;
        search.sys_id = sysId;
        return accountVerifyMapper.findLatestByFromVerify(search);
    }

    public int updateCodeTime(AccountVerify updateRecord){
        return updateRecord!=null?accountVerifyMapper.updateCodeTime(updateRecord):0;
    }
    public int updateStatus(AccountVerify updateRecord){
        return updateRecord!=null?accountVerifyMapper.updateStatus(updateRecord):0;
    }

}
