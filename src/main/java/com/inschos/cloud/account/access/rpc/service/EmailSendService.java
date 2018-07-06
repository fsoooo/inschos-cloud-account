package com.inschos.cloud.account.access.rpc.service;

import com.inschos.cloud.account.access.rpc.bean.EmailInfoBean;

/**
 * author   meiming_mm@163.com
 * date     2018/7/6
 * version  v1.0.0
 */
public interface EmailSendService
{
    int addEmailInfo(EmailInfoBean emailInfoBean);
}
