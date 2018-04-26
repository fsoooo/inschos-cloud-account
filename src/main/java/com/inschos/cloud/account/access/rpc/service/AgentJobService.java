package com.inschos.cloud.account.access.rpc.service;

import com.inschos.cloud.account.access.rpc.bean.AgentJobBean;

import java.util.List;

/**
 * Created by IceAnt on 2018/4/26.
 */
public interface AgentJobService {

    List<AgentJobBean> getAgentManagers(long personId);


    AgentJobBean getAgentPersonInfo(String phone,List<String> managerUuids);
}
