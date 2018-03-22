package com.inschos.cloud.account.access.http.controller.request;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IceAnt on 2018/3/14.
 */
@Controller
@RequestMapping("/test/")
public class TestController {



    @RequestMapping("/do")
    @ResponseBody
    public String doOne(HttpServletRequest request){

        return "test data :";
    }
}
