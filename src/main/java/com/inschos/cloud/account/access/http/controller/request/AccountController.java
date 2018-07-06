package com.inschos.cloud.account.access.http.controller.request;

import com.inschos.cloud.account.access.http.controller.action.AccountAction;
import com.inschos.cloud.account.access.http.controller.bean.ActionBean;
import com.inschos.cloud.account.annotation.GetActionBeanAnnotation;
import com.inschos.cloud.account.assist.kit.StringKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by IceAnt on 2018/3/29.
 */
@Controller
@RequestMapping("/web/account")
public class AccountController {
    @Autowired
    private AccountAction accountAction;

    @GetActionBeanAnnotation(isCheckAccess=false)
    @RequestMapping("/login/*")
    @ResponseBody
    public String login(ActionBean bean){
        String method = StringKit.splitLast(bean.url, "/");
        return  accountAction.login(bean,method);
    }

    @GetActionBeanAnnotation(isCheckAccess=false)
    @RequestMapping("/registry/*")
    @ResponseBody
    public String registry(ActionBean bean){
        String method = StringKit.splitLast(bean.url, "/");
        return  accountAction.registry(bean,method);
    }


    @GetActionBeanAnnotation(isCheckAccess=false)
    @RequestMapping("/forgetPwd/*")
    @ResponseBody
    public String forgetPassword(ActionBean bean){
        String method = StringKit.splitLast(bean.url, "/");
        return  accountAction.forgetPassword(bean,method);
    }



    @GetActionBeanAnnotation
    @RequestMapping("/resetPwd")
    @ResponseBody
    public String resetPassword(ActionBean bean){
        return  accountAction.resetPassword(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/resetPwd/*")
    @ResponseBody
    public String resetPassword1(ActionBean bean){
        return  accountAction.resetPassword(bean);
    }

    @GetActionBeanAnnotation(isCheckAccess = false)
    @RequestMapping("/sendCode/*")
    @ResponseBody
    public String sendCode(ActionBean bean){
        String method = StringKit.splitLast(bean.url, "/");
        return  accountAction.sendCode(bean,method);
    }

    @GetActionBeanAnnotation(isCheckAccess = false)
    @RequestMapping("/checkSendCode/*")
    @ResponseBody
    public String checkSendCode(ActionBean bean){
        String method = StringKit.splitLast(bean.url, "/");
        return  accountAction.checkSendCode(bean,method);
    }


    @GetActionBeanAnnotation(isCheckAccess = false)
    @RequestMapping("/jointLogin")
    @ResponseBody
    public String jointLogin(ActionBean bean){
        return  accountAction.jointLogin(bean);
    }


    @GetActionBeanAnnotation
    @RequestMapping("/chooseManager")
    @ResponseBody
    public String chooseManager(ActionBean bean){
        return  accountAction.chooseManager(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/listManager")
    @ResponseBody
    public String listManager(ActionBean bean){
        return  accountAction.listManager(bean);
    }




    @GetActionBeanAnnotation
    @RequestMapping("/modifyPwd")
    @ResponseBody
    public String modifyPassword(ActionBean bean){
        return  accountAction.modifyPassword(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/changePhone")
    @ResponseBody
    public String changePhone(ActionBean bean){
        return  accountAction.changePhone(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/changeEmail")
    @ResponseBody
    public String changeEmail(ActionBean bean){
        return  accountAction.changeEmail(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/loginOut")
    @ResponseBody
    public String loginOut(ActionBean bean){
        return  accountAction.loginOut(bean);
    }

    @GetActionBeanAnnotation(isCheckAccess = false)
    @RequestMapping("/home")
    @ResponseBody
    public String home(ActionBean bean){
        return  accountAction.home(bean);
    }






}
