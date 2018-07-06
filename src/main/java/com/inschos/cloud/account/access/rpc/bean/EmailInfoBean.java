package com.inschos.cloud.account.access.rpc.bean;

import java.util.List;

/**
 * author   meiming_mm@163.com
 * date     2018/7/6
 * version  v1.0.0
 */
public class EmailInfoBean {

    //等待发送的邮件
    public String bekongs;  //邮件所属
    public String source_code;  //邮件来源标识
    public int send_type;       //发送方式  1即时 2定时
    public String merge_code;   //邮件合并标识 同标识合并
    public int type;            //邮件类型 0消息 1验证 2理赔 99其他
    public String title;        //邮件标题
    public String html;         //邮件内容
    public List<String> to_email;     //收件地址 （json）
}
