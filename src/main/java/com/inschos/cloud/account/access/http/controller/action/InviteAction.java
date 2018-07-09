package com.inschos.cloud.account.access.http.controller.action;

import com.inschos.cloud.account.assist.kit.StringKit;
import com.inschos.cloud.account.data.dao.PlatformSystemDao;
import com.inschos.cloud.account.model.PlatformSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * author   meiming_mm@163.com
 * date     2018/7/9
 * version  v1.0.0
 */
@Component
public class InviteAction {

    @Autowired
    private PlatformSystemDao platformSystemDao;


    public String agent(String code){

        if(StringKit.isEmpty(code)){
            return null;
        }

        PlatformSystem platformSystem = platformSystemDao.findCode(code.toUpperCase());

        if(platformSystem!=null &&platformSystem.domain!=null){
            String[] split = platformSystem.domain.split(",");
            for (int i = 0,len=split.length; i < len; i++) {
                String host = split[i];
                if(host.startsWith("agent.")){
                    return "http://"+host;
                }
            }
        }
        return null;
    }
}
