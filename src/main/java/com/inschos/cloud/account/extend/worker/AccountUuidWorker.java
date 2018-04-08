package com.inschos.cloud.account.extend.worker;

/**
 * Created by IceAnt on 2018/3/31.
 */
public class AccountUuidWorker {

    private static IdWorker worker;

    public static IdWorker getWorker(long workerId, long dataCenterId) {
        if(worker==null){
            worker = new IdWorker(workerId,dataCenterId);
        }
        return worker;
    }
}
