package com.inschos.cloud.account.access.http.controller.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IceAnt on 2018/3/29.
 */
public class ResponseMessage {

    private List<ResMessageKV> resMessageKVList = new ArrayList<>();

    private final String DEFAULT_FIELD = "default";

    public int code = BaseResponse.CODE_SUCCESS;

    public void add(String content){
        resMessageKVList.add(new ResMessageKV(DEFAULT_FIELD, content));
    }

    public void add(String field, String content){

        resMessageKVList.add(new ResMessageKV(field,content));
    }

    public List<ResMessageKV> get(){
        return this.resMessageKVList;
    }

    public boolean hasError(){
        return code!=BaseResponse.CODE_SUCCESS;
    }

    public static class ResMessageKV{
        public String key;
        public String value;

        public ResMessageKV(String k,String v){
            this.key = k;
            this.value = v;
        }
    }


}
