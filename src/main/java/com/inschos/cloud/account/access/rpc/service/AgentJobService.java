package com.inschos.cloud.account.access.rpc.service;

import java.util.List;

/**
 * Created by IceAnt on 2018/4/26.
 */
public interface AgentJobService {

    Object getAgentManagers(long personId);


    Object getAgentPersonInfo(String phone,List<String> managerUuids);
}
