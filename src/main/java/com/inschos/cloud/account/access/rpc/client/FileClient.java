package com.inschos.cloud.account.access.rpc.client;


import com.inschos.cloud.account.access.rpc.service.FileService;
import com.inschos.cloud.account.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/5/5.
 */
@Component
public class FileClient {
    @Value("${rpc.remote.file.host}")
    private String host;

    private final String uri = "/rpc/file";


    private FileService getService(){
        return new HproseHttpClient(host + uri).useService(FileService.class);
    }

    public String getFileUrl(String fileKey){
        try {
            FileService service = getService();
            return service!=null?service.getFileUrl(fileKey):null;

        }catch (Exception e){
            L.log.error("remote fail {}",e.getMessage(),e);
            return null;
        }
    }

    public String getFileUrl(String fileKey,int width,int height,int quality){
        try {
            FileService service = getService();
            return service!=null?service.getFileUrl(fileKey,width,height,quality):null;

        }catch (Exception e){
            L.log.error("remote fail {}",e.getMessage(),e);
            return null;
        }
    }




}
