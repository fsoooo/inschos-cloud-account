package com.inschos.cloud.account.access.rpc.client;

import com.inschos.cloud.account.access.rpc.service.ChannelService;
import com.inschos.cloud.account.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * author   meiming_mm@163.com
 * date     2018/7/26
 * version  v1.0.0
 */
@Component
public class ChannelServiceClient {


    @Value("${rpc.remote.customer.host}")
    private String host;

    private String uri = "/rpc/channel";

    private ChannelService getService(){
        return new HproseHttpClient(host+uri).useService(ChannelService.class);
    }

    public boolean addSystemDefaultChannel(String managerUuid) {
        try{
            ChannelService service = getService();
            return service.addSystemDefaultChannel(managerUuid);
        }catch(Exception e){
            L.log.error("rpc error {}",e.getMessage(),e);
            return false;
        }
    }

}
