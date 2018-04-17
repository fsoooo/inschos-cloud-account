package com.inschos.cloud.account.data.mapper;

import com.inschos.cloud.account.model.PlatformSystem;

/**
 * Created by IceAnt on 2018/4/11.
 */
public interface PlatformSystemMapper {

    int insert(PlatformSystem system);

    PlatformSystem findOne(PlatformSystem system);

    PlatformSystem findDomain(String domain);

}
