package com.inschos.cloud.account.assist.kit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IceAnt on 2017/7/27.
 */
public class L {

    public static final String LOG_NAME_DEFAULT = "inschos";
    public static Logger log =  LoggerFactory.getILoggerFactory().getLogger(LOG_NAME_DEFAULT);
    public static Logger getLogger(String name){
        return LoggerFactory.getILoggerFactory().getLogger(name);
    }

}
