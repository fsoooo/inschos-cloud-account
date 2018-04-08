package com.inschos.cloud.account.access.http.controller.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IceAnt on 2018/3/29.
 */
public class ResponseMessage {


    private Map<String ,Object> message = new HashMap<>();

    private final String DEFAULT_FIELD = "default";

    public int code;

    public void add(String content){
        message.put(DEFAULT_FIELD,content);
    }

    public void add(String field, String content){

        message.put(field,content);
    }

    public <T> void add(String field,Class<T> tClass,String content){

    }

    public Map<String ,Object> get(){
        return this.message;
    }

    public boolean hasError(){
        return code!=BaseResponse.CODE_SUCCESS;
    }


}
