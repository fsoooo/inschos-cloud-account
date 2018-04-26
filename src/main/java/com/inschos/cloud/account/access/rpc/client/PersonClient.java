package com.inschos.cloud.account.access.rpc.client;

import com.inschos.cloud.account.access.rpc.bean.PersonBean;
import com.inschos.cloud.account.access.rpc.service.PersonService;
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


    public int  saveInfo(PersonBean personBean){
        int insertId = 0;
        try {
            if (personBean!=null){
                insertId = getService().saveInfo(personBean);
            }
        }catch (Exception e){
            return 0;
        }
        return insertId;
    }


}
