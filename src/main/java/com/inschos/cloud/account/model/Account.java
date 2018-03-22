package com.inschos.cloud.account.model;

import java.io.Serializable;

/**
 * Created by IceAnt on 2018/3/20.
 */
public class Account implements Serializable{

    private static final long serialVersionUID = -1992985910633911067L;

    /**账号类型 客户个人*/
    public static final int TYPE_CUST_USER = 1;
    /**账号类型 客户企业*/
    public static final int TYPE_CUST_COM = 2;
    /**账号类型 企业 业管*/
    public static final int TYPE_COMPANY = 3;
    /**账号类型 代理人*/
    public static final int TYPE_AGENT = 4;

    public static final int STATUS_NORMAL = 1;

    public static final int STATUS_ABNORMAL = 2;

    /** */
    public long id;

    /** account_uuid*/
    public String account_uuid;

    /** 用户名*/
    public String username;

    /** 密码*/
    public String password;

    /** 个人账号手机号*/
    public String phone;

    /** 邮箱*/
    public String email;

    /** token*/
    public String token;

    /** 1：正常，2，异常*/
    public int status;

    /** 1：个人账号，2：企业账号，3：业管账号，4：代理人*/
    public int type;

    /** 用户ID*/
    public long cust_id;

    /** 验证码*/
    public String code;

    /** 验证码有效时间*/
    public long code_time;

    /** 创建时间*/
    public long created_at;

    /** 结束时间*/
    public long updated_at;

    /** 删除标识 0删除 1可用*/
    public int state;



}
