package com.inschos.cloud.account.data.mapper;

import com.inschos.cloud.account.model.Account;

import java.util.List;

/**
 * Created by IceAnt on 2018/3/20.
 */
public interface AccountMapper {

    int insert(Account account);

    int insertRegistry(Account account);

    int update(Account account);

    int updatePasswordByUuid(Account account);

    int updatePasswordTokenByUuid(Account account);

    int updateTokenByUuid(Account account);

    int updatePhoneByUuid(Account account);

    int updateEmailByUuid(Account account);

    Account findOne(Account id);

    public Account findByAccount(Account search);

    public Account findByUuid(String accountUuid);

    public Account findOneBySysType(Account search);

    public List<Account> findListBySysType(Account search);

}
