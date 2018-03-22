package com.inschos.cloud.account.access.http.controller.bean;

/**
 * Created by IceAnt on 2017/6/19.
 */
public class BaseRequest {

	public String lastId;
	public String pageNum;
	public String pageSize;

	public final static String FILEID_BUILDCODE = "buildCode";
	public final static String FILEID_PLATFORM = "platform";
	public final static String FILEID_APICODE = "apiCode";
	public final static String FILEID_ACCESS_TOKEN = "token";

	public final static String PLATFORM_ANDROID = "android";
	public final static String PLATFORM_IOS = "ios";
	public final static String PLATFORM_WEB = "web";
}
