package com.inschos.cloud.account.access.rpc.client;

import com.inschos.cloud.account.access.rpc.bean.AgentJobBean;
import com.inschos.cloud.account.access.rpc.service.AgentJobService;
import com.inschos.cloud.account.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by IceAnt on 2018/4/26.
 */
@Component
public class AgentJobClient {

    @Value("${rpc.remote.customer.host}")
    private String host;

    private String uri = "/rpc/agent";

    private AgentJobService getService(){
        return new HproseHttpClient(host+uri).useService(AgentJobService.class);
    }

    public List<AgentJobBean> getAgents(long personId) {
        try{
            AgentJobService service = getService();
            return service.getAgentManagers(personId);
        }catch(Exception e){
            L.log.error("rpc error {}",e.getMessage(),e);
            return null;
        }
    }

    public List<AgentJobBean> getInviteAgents(String phone, List<String> manageUuids) {
//        long personId = 0;
        try{
            AgentJobService service = getService();
            return service.getInviteAgents(phone,manageUuids);
        }catch(Exception e){
            L.log.error("rpc error {}",e.getMessage(),e);
        }
        return null;
    }
    public int bindPerson(String phone,String managerUuid,long personId) {
//        long personId = 0;
        try{
            AgentJobService service = getService();
            return service.bindPerson(phone,managerUuid,personId);
        }catch(Exception e){
            L.log.error("rpc error {}",e.getMessage(),e);
        }
        return 0;
    }
    public int unBindPerson(String phone,String managerUuid,long personId) {
//        long personId = 0;
        try{
            AgentJobService service = getService();
            return service.unBindPerson(phone,managerUuid,personId);
        }catch(Exception e){
            L.log.error("rpc error {}",e.getMessage(),e);
        }
        return 0;
    }
    public AgentJobBean getAgentInfoByPersonIdManagerUuid(String managerUuid,long personId) {
        try{
            AgentJobService service = getService();
            return service.getAgentInfoByPersonIdManagerUuid(managerUuid,personId);
        }catch(Exception e){
            L.log.error("rpc error {}",e.getMessage(),e);
        }
        return null;
    }

    public AgentJobBean getAgentInfoInAndOut(String managerUuid,String phone) {
        try{
            AgentJobService service = getService();
            return service.getAgentInfoInAndOut(managerUuid,phone);
        }catch(Exception e){
            L.log.error("rpc error {}",e.getMessage(),e);
        }
        return null;
    }


}
