package com.inschos.cloud.account.access.rpc.client;

import com.inschos.cloud.account.access.rpc.bean.CustomerBean;
import com.inschos.cloud.account.access.rpc.service.CustomerService;
import com.inschos.cloud.account.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/4/26.
 */
@Component
public class CustomerClient {

    @Value("${rpc.remote.customer.host}")
    private String host;

    private String uri = "/rpc/person";

    private CustomerService getService(){
        return new HproseHttpClient(host+uri).useService(CustomerService.class);
    }

    public int saveCust(CustomerBean bean) {
        try{
            CustomerService service = getService();
            return service.saveCust(bean.type,bean.customerId,bean.accountUuid,bean.managerUuid);
        }catch(Exception e){
            L.log.error("rpc error {}",e.getMessage(),e);
            return 0;
        }
    }


}
