package com.inschos.cloud.account.service;


import com.inschos.cloud.trading.model.Area;

/**
 * Created by IceAnt on 2018/3/13.
 */
public interface DemoService {

    String sayHello(String name);

    String doDemo(String name);

    Area findOne(Area area);

}
