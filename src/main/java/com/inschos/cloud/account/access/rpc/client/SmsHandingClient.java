package com.inschos.cloud.account.access.rpc.client;

import com.inschos.cloud.account.access.rpc.service.SmsHandingService;
import com.inschos.cloud.account.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/4/20.
 */
@Component
public class SmsHandingClient {

    @Value("${rpc.remote.msghanding.host}")
    private String host;

    private String uri = "/rpc/sms";

    private SmsHandingService getService(){
        return new HproseHttpClient(host+uri).useService(SmsHandingService.class);
    }


    public boolean sendVerifyCode( String phone, String code){
        boolean flag =false;
        try {
            flag = getService().sendVerifyCode("ACCOUNT", phone, code);

        }catch (Exception e){
            L.log.error("remote failed ",e);
        }
        return flag;
    }


}
