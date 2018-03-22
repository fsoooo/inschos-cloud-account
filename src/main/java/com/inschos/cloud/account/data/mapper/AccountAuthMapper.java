package com.inschos.cloud.account.data.mapper;

import com.inschos.cloud.account.model.AccountAuth;

/**
 * Created by IceAnt on 2018/3/21.
 */
public interface AccountAuthMapper {

    int insert(AccountAuth accountAuth);

    int update(AccountAuth accountAuth);

    AccountAuth findOne(int id);

}
