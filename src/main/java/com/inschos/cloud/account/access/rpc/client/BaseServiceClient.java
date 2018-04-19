package com.inschos.cloud.account.access.rpc.client;

import hprose.client.HproseHttpClient;

import java.lang.reflect.ParameterizedType;

/**
 * Created by IceAnt on 2018/4/16.
 */
public class BaseServiceClient<T> {

    private T instance;

    public T getService(String url){

        if(instance==null){
            instance = new HproseHttpClient(url).useService(getTClass());
        }
        return instance;

    }

    private Class<T> getTClass()
    {
        Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;

    }
}
