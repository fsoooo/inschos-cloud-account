package com.inschos.cloud.account.access.http.controller.request;

import com.inschos.cloud.account.access.http.controller.bean.ActionBean;
import com.inschos.cloud.account.access.rpc.bean.CompanyBean;
import com.inschos.cloud.account.access.rpc.client.CompanyClient;
import com.inschos.cloud.account.assist.kit.StringKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by IceAnt on 2018/4/19.
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private CompanyClient companyClient;


    @RequestMapping("/do")
    @ResponseBody
    public String login(ActionBean bean){
        String method = StringKit.splitLast(bean.url, "/");
        CompanyBean bean1 = new CompanyBean();
        bean1.name="233333333333哈哈哈111哈";
        bean1.email="xxxx@164.co";
        Object i = companyClient.addCompany(bean1);
        return String.valueOf(i);
    }

}
