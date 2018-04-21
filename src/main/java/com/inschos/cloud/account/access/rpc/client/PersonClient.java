package com.inschos.cloud.account.access.rpc.client;

import com.inschos.cloud.account.access.rpc.service.PersonService;
import com.inschos.cloud.account.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/4/20.
 */
@Component
public class PersonClient {

    @Value("${rpc.remote.customer.host}")
    private String host;

    private String uri = "/rpc/person";

    private PersonService getService(){
        return new HproseHttpClient(host+uri).useService(PersonService.class);
    }


    public int  saveInfo(){

        try {
            int resultId = getService().saveInfo();
            return resultId;
        }catch (Exception e){
            L.log.error("rpc error {}",e.getMessage(),e);
            return 0;
        }
    }


}
