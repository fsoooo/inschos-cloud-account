package com.inschos.cloud.account.access.rpc.client;

import com.inschos.cloud.account.access.rpc.bean.CompanyBean;
import com.inschos.cloud.account.access.rpc.service.CompanyService;
import com.inschos.cloud.account.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/4/19.
 */
@Component
public class CompanyClient {

    @Value("${rpc.remote.customer.host}")
    private String host;

    private String uri = "/rpc/company";

    private CompanyService getService(){
        return new HproseHttpClient(host+uri).useService(CompanyService.class);
    }

    public long addCompany(CompanyBean bean) {
        try{
            CompanyService service = getService();
            return service.addCompany(bean);
        }catch(Exception e){
            L.log.error("rpc error {}",e.getMessage(),e);
            return 0;
        }
    }

    public CompanyBean getCompanyById(long id) {
        try{
            CompanyService service = getService();
            return service.getCompanyById(id);
        }catch(Exception e){
            L.log.error("rpc error {}",e.getMessage(),e);
            return null;
        }
    }
}
