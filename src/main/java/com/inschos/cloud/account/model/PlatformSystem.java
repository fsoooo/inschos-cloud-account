package com.inschos.cloud.account.model;

/**
 * Created by IceAnt on 2018/4/11.
 */
public class PlatformSystem {

    public final static int STATUS_NO = 0;

    public final static int STATUS_OK = 1;

    /** 主键*/
    public long id;

    public String code;

    /** 名称*/
    public String name;

    /** 子域名*/
    public String domain;

    /**  0不可用 1可用*/
    public int status;

    /** 创建时间*/
    public long created_at;

    /** 结束时间*/
    public long updated_at;

    /** 删除标识 0删除 1可用*/
    public int state;


}
