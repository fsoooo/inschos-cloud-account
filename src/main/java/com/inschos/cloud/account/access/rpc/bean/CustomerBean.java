package com.inschos.cloud.account.access.rpc.bean;

/**
 * Created by IceAnt on 2018/4/26.
 */
public class CustomerBean {

    public static final int TYPE_CUST_USER = 1;

    public static final int TYPE_CUST_COM = 2;

    public int type;

    public int customerId;

    public String accountUuid;

    public String managerUuid;



}
