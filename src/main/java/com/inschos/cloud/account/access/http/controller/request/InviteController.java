package com.inschos.cloud.account.access.http.controller.request;

import com.inschos.cloud.account.access.http.controller.action.InviteAction;
import com.inschos.cloud.account.assist.kit.StringKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author   meiming_mm@163.com
 * date     2018/7/9
 * version  v1.0.0
 */
@Controller
@RequestMapping("/web/invite")
public class InviteController {

    @Autowired
    private InviteAction inviteAction;


    @RequestMapping("/*")
    public void agent(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        String last = StringKit.splitLast(uri, "/");
        String agentUrl = inviteAction.agent(last);
        if (!StringKit.isEmpty(agentUrl)) {
            try {
                response.sendRedirect(agentUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
