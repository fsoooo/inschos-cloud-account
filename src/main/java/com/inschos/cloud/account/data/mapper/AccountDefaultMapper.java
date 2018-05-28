package com.inschos.cloud.account.data.mapper;

import com.inschos.cloud.account.model.AccountDefault;

/**
 * Created by IceAnt on 2018/5/28.
 */
public interface AccountDefaultMapper {

    int insert(AccountDefault record);

    int update(AccountDefault record);

    AccountDefault selectOne(long id);

    AccountDefault selectOneByAccount(String accountUuid);



}
