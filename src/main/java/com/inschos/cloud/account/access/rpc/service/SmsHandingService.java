package com.inschos.cloud.account.access.rpc.service;

/**
 * Created by IceAnt on 2018/5/9.
 */
public interface SmsHandingService {

    boolean sendVerifyCode(String fromCode, String phone, String code);
}
