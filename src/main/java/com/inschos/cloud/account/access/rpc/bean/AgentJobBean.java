package com.inschos.cloud.account.access.rpc.bean;

/**
 * Created by IceAnt on 2018/4/26.
 */
public class AgentJobBean {

    /** 当前业管账号id*/
    public String manager_uuid;

    /** 渠道id*/
    public long channel_id;

    /** 关联person表主键*/
    public long person_id;

    /** 代理人手机号*/
    public String phone;

    /** 工号*/
    public String job_num;

    /** 职位表id*/
    public long position_id;

    /** 标签 使用英文的,隔开 关联user_tag表的主键*/
    public String user_tag_id;

    /** 备注*/
    public String note;

    /** 入职时间*/
    public long start_time;

    /** 离职时间*/
    public long end_time;


}
