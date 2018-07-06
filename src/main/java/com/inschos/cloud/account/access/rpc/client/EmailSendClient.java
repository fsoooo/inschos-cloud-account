package com.inschos.cloud.account.access.rpc.client;

import com.inschos.cloud.account.access.rpc.bean.EmailInfoBean;
import com.inschos.cloud.account.access.rpc.service.EmailSendService;
import com.inschos.cloud.account.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;

/**
 * author   meiming_mm@163.com
 * date     2018/7/6
 * version  v1.0.0
 */
public class EmailSendClient {

    @Value("${rpc.remote.msghanding.host}")
    private String host;

    private String uri = "/rpc/email";

    private EmailSendService getService(){
        return new HproseHttpClient(host+uri).useService(EmailSendService.class);
    }


    public long send(EmailInfoBean bean) {
        try{
            EmailSendService service = getService();
            return service.addEmailInfo(bean);
        }catch(Exception e){
            L.log.error("rpc error {}",e.getMessage(),e);
            return 0;
        }
    }
}


