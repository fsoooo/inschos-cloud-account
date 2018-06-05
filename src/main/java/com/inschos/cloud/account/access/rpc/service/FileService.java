package com.inschos.cloud.account.access.rpc.service;

/**
 * Created by IceAnt on 2018/6/5.
 */
public interface FileService {
    String getFileUrl(String fileKey);

    /** 图片压缩 width height quality  传值为0时  不做处理*/
    String getFileUrl(String fileKey,int width,int height,int quality);
}
