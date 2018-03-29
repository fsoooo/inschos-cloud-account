package com.inschos.cloud.account.access.rpc.service;

import com.inschos.cloud.account.model.Account;

/**
 * Created by IceAnt on 2018/3/21.
 */

public interface AccountService {

    public Account getAccount(String token);

}
