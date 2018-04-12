package com.inschos.cloud.account.model;

/**
 * Created by IceAnt on 2018/3/28.
 */
public class AccountVerify {

    public final static int VERIFY_TYPE_PHONE = 1;
    public final static int VERIFY_TYPE_EMAIL = 2;

    public final static int STATUS_NOT_USE = 0;
    public final static int STATUS_USED = 1;

    /** */
    public long id;

    public String account_uuid;

    /** 系统类型*/
    public int from_type;

    /** 验证主体号 如手机|邮箱号*/
    public String verify_name;

    /** 验证类型 1手机 2邮箱*/
    public int verify_type;

    /** 验证码*/
    public String code;

    /** 验证码有效时间*/
    public long code_time;

    /** 是否使用 0 未 1已使用*/
    public int status;

    /** 创建时间*/
    public long created_at;

    /** 结束时间*/
    public long updated_at;


}
