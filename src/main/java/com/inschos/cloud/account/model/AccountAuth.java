package com.inschos.cloud.account.model;

import java.io.Serializable;

/**
 * Created by IceAnt on 2018/3/21.
 */
public class AccountAuth implements Serializable {

    private static final long serialVersionUID = 5693717983109796431L;

    public static final int AUTH_TYPE_QQ = 1;
    public static final int AUTH_TYPE_WX = 2;
    public static final int AUTH_TYPE_WEIBO = 3;

    /** */
    public long id;

    /** 主账号ID*/
    public String account_uuid;

    /** 身份类型: 1 qq 2 微信 3新浪微博*/
    public int auth_type;

    /** 第三方账号*/
    public String auth_username;

    /** 第三方账号ID*/
    public String auth_id;

    /** token*/
    public String auth_access_token;

    /** 账号有效时间*/
    public long auth_expires;

    /** 1：绑定，2解绑*/
    public int status;

    /** 创建时间*/
    public long created_at;

    /** 结束时间*/
    public long updated_at;


}
