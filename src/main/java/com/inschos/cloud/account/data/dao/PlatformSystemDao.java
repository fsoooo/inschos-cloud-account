package com.inschos.cloud.account.data.dao;

import com.inschos.cloud.account.assist.kit.StringKit;
import com.inschos.cloud.account.data.mapper.PlatformSystemMapper;
import com.inschos.cloud.account.model.PlatformSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/4/11.
 */
@Component
public class PlatformSystemDao {

    @Autowired
    private PlatformSystemMapper platformSystemMapper;

    public PlatformSystem findDomain(String domain){
        return StringKit.isEmpty(domain)?null: platformSystemMapper.findDomain(domain);
    }

    public int insert(PlatformSystem system){

        return system!=null? platformSystemMapper.insert(system):0;
    }

}
