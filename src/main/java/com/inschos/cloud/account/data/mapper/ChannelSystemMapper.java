package com.inschos.cloud.account.data.mapper;

import com.inschos.cloud.account.model.ChannelSystem;

/**
 * Created by IceAnt on 2018/4/11.
 */
public interface ChannelSystemMapper {

    int insert(ChannelSystem system);

    ChannelSystem findOne(ChannelSystem system);

    ChannelSystem findDomain(String domain);

}
