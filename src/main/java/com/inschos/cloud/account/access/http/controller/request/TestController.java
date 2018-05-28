package com.inschos.cloud.account.access.http.controller.request;

import com.inschos.cloud.account.access.http.controller.bean.ActionBean;
import com.inschos.cloud.account.access.rpc.bean.AgentJobBean;
import com.inschos.cloud.account.access.rpc.bean.CompanyBean;
import com.inschos.cloud.account.access.rpc.bean.CustomerBean;
import com.inschos.cloud.account.access.rpc.bean.PersonBean;
import com.inschos.cloud.account.access.rpc.client.AgentJobClient;
import com.inschos.cloud.account.access.rpc.client.CompanyClient;
import com.inschos.cloud.account.access.rpc.client.CustomerClient;
import com.inschos.cloud.account.access.rpc.client.PersonClient;
import com.inschos.cloud.account.assist.kit.L;
import com.inschos.cloud.account.assist.kit.StringKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IceAnt on 2018/4/19.
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private CompanyClient companyClient;
    @Autowired
    private PersonClient personClient;
    @Autowired
    private CustomerClient customerClient;
    @Autowired
    private AgentJobClient agentJobClient;


    @RequestMapping("/do")
    @ResponseBody
    public String login(ActionBean bean){
        String method = StringKit.splitLast(bean.url, "/");
        CompanyBean bean1 = new CompanyBean();
        bean1.name="233333333333哈哈哈111哈";
        bean1.email="xxxx@164.co";
        PersonBean account = new PersonBean();
        account.phone = "15101691357";
        int info = personClient.saveInfo(account);

        CustomerBean customerBean = new CustomerBean();
        customerBean.type = 1;
        customerBean.managerUuid = "2";
        customerBean.accountUuid = "1";
        customerBean.customerId = 13;
//        int saveCust = customerClient.saveCust(customerBean);

        ArrayList<String> list = new ArrayList<>();
        list.add("2");

        List<AgentJobBean> agents = agentJobClient.getAgents(2);


//        companyClient.getI();
        try{
        }catch (Exception e){
            System.out.println("=======================");
            L.log.error("error {} msg",e.getMessage(),e);
        }

        return String.valueOf(info);
    }

}
