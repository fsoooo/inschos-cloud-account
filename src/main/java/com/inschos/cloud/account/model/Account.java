package com.inschos.cloud.account.model;

import com.inschos.cloud.account.assist.kit.MD5Kit;
import com.inschos.cloud.account.assist.kit.StringKit;

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



    public final static int ACCOUNT_FILED_USERNAME = 1;
    public final static int ACCOUNT_FILED_PHONE = 2;
    public final static int ACCOUNT_FILED_EMAIL = 3;


    /** */
    public long id;

    /** sys_id*/
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

    /** 渠道系统ID */
    public long sys_id;

    /** 1：个人账号，2：企业账号，3：业管账号，4：代理人*/
    public int user_type;

    /** 用户ID*/
    public String user_id;


    /** 创建时间*/
    public long created_at;

    /** 结束时间*/
    public long updated_at;

    /** 删除标识 0删除 1可用*/
    public int state;

    /** 盐值 */
    public String salt;

    /**
     * 搜索
     * 1 username 2 phone 3 email
     */
    public int searchAccountFiled;


    public static String generatePwd(String password,String salt){
        String enpwd = "";
        if(!StringKit.isEmpty(password)){

            enpwd =  MD5Kit.MD5Digest("cloud"+password +salt + "@inschos-#:"+salt) ;
        }
        return enpwd;
    }

    public static int getAccountType(String requestAccountType){
        int  accountType = 0;
        switch (requestAccountType){
            case "custuser":
                accountType = TYPE_CUST_USER;
                break;
            case "custcom":
                accountType = TYPE_CUST_COM;
                break;
            case "agent":
                accountType = TYPE_AGENT;
                break;
            case "company":
                accountType = TYPE_COMPANY;
                break;
        }
        return accountType;
    }


}
