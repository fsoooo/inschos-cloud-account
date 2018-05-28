package com.inschos.cloud.account.access.rpc.service;

import com.inschos.cloud.account.access.rpc.bean.AgentJobBean;

import java.util.List;

/**
 * Created by IceAnt on 2018/4/26.
 */
public interface AgentJobService {

    List<AgentJobBean> getAgentManagers(long personId);


    List<AgentJobBean> getAgents(String phone, List<String> managerUuids);

    AgentJobBean getAgentInfoByPersonIdManagerUuid(String managerUuid,long personId);

    int bindPerson(String phone,String managerUuid,long personId);



}
