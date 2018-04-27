package org.springframework.remoting.hprose;

import com.inschos.cloud.account.assist.kit.L;
import hprose.common.HproseContext;
import hprose.server.HproseHttpServiceEvent;
import hprose.server.HttpContext;

/**
 * Created by IceAnt on 2018/4/21.
 */
public class HproseHttpServiceEventExporter implements HproseHttpServiceEvent {
    @Override
    public void onSendHeader(HttpContext httpContext) {
        L.log.debug("onSendHeader");

    }

    @Override
    public void onBeforeInvoke(String s, Object[] objects, boolean b, HproseContext hproseContext) throws Throwable {
        L.log.debug("onBeforeInvoke");
    }

    @Override
    public void onAfterInvoke(String s, Object[] objects, boolean b, Object o, HproseContext hproseContext) throws Throwable {
        L.log.debug("onAfterInvoke");
    }

    @Override
    public Throwable onSendError(Throwable throwable, HproseContext hproseContext) throws Throwable {
        L.log.error("rpc hprose onSendError  ",throwable);
        return null;
    }

    @Override
    public void onServerError(Throwable throwable, HproseContext hproseContext) {
        L.log.error("rpc hprose onServerError  ",throwable);
    }
}
