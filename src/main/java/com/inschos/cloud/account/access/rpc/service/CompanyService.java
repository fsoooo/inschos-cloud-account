package com.inschos.cloud.account.access.rpc.service;

import com.inschos.cloud.account.access.rpc.bean.CompanyBean;

/**
 * Created by IceAnt on 2018/4/19.
 */
public interface CompanyService {

    long addCompany(CompanyBean params,String managerUuid);

    CompanyBean getCompanyById(long id) ;


}
