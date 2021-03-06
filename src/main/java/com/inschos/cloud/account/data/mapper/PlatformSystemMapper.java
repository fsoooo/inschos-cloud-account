package com.inschos.cloud.account.data.mapper;

import com.inschos.cloud.account.model.PlatformSystem;

/**
 * Created by IceAnt on 2018/4/11.
 */
public interface PlatformSystemMapper {

    int insert(PlatformSystem system);

    PlatformSystem findOne(long id);

    PlatformSystem findDomain(String domain);

    PlatformSystem findCode(String code);

}
