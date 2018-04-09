package com.inschos.cloud.account.data.mapper;

import com.inschos.cloud.account.model.AccountVerify;

/**
 * Created by IceAnt on 2018/3/28.
 */
public interface AccountVerifyMapper {

    int insert(AccountVerify record);

    int update(AccountVerify record);

    int updateCodeTime(AccountVerify updateRecord);

    int updateStatus(AccountVerify updateRecord);

    AccountVerify findOne(AccountVerify accountVerify);

    AccountVerify findLatestByFromVerify(AccountVerify accountVerify);

    AccountVerify findLatestByUuidFromVerify(AccountVerify accountVerify);


}
