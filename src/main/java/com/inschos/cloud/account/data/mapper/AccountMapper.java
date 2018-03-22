package com.inschos.cloud.account.data.mapper;

import com.inschos.cloud.account.model.Account;

/**
 * Created by IceAnt on 2018/3/20.
 */
public interface AccountMapper {

    int insert(Account account);

    int update(Account account);

    Account findOne(Account id);

}
