package com.inschos.cloud.account.access.rpc.bean;

/**
 * Created by IceAnt on 2018/4/26.
 */
public class PersonBean {

    /** */
    public long id;

    /** 姓名*/
    public String name;

    public String head;

    /** 证件类型，1：身份证，2：护照，3：军官证，4：其他*/
    public int cert_type;

    /** 证件号*/
    public String cert_code;

    /** 证件开始时间*/
    public long cert_start;

    /** 证件结束时间*/
    public long cert_end;

    /** 1、男 2、女*/
    public int sex;

    /** 生日*/
    public String birthday;

    /** 家庭住址*/
    public String address;

    /** 详细地址*/
    public String address_detail;

    /** 联系方式*/
    public String phone;

    /** 邮件*/
    public String email;

    /** 身份证上面*/
    public String front_key;

    /** 身份证背面*/
    public String back_key;

    /** 身份证手持*/
    public String handheld_key;

}
