package com.inschos.cloud.account.data.dao;

import com.inschos.cloud.account.assist.kit.StringKit;
import com.inschos.cloud.account.data.mapper.ChannelSystemMapper;
import com.inschos.cloud.account.model.ChannelSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/4/11.
 */
@Component
public class ChannelSystemDao {

    @Autowired
    private ChannelSystemMapper channelSystemMapper;

    public ChannelSystem findDomain(String domain){
        return StringKit.isEmpty(domain)?null:channelSystemMapper.findDomain(domain);
    }

    public int insert(ChannelSystem system){

        return system!=null?channelSystemMapper.insert(system):0;
    }

}
