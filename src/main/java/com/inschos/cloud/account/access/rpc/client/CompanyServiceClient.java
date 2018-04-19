package com.inschos.cloud.account.access.rpc.client;

import com.inschos.cloud.account.access.rpc.bean.CompanyBean;
import com.inschos.cloud.account.access.rpc.service.CompanyService;
import com.inschos.cloud.account.assist.kit.L;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/4/19.
 */
@Component
public class CompanyServiceClient extends BaseServiceClient<CompanyService>  implements CompanyService{

    @Value("${rpc.remote.customer.host}")
    private String host;

    private String uri = "/rpc/company";

    @Override
    public int addCompany(CompanyBean params) {
        try{
            CompanyService service = getService(host+uri);
            return service.addCompany(params);
        }catch(Exception e){
            L.log.error("rpc error {}",e.getMessage(),e);
            return 0;
        }
    }
}
