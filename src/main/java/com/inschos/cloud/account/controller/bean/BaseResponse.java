package com.inschos.cloud.account.controller.bean;

public class BaseResponse {
	public int code;
	public String message = "";

	public Object data;
	public PageBean page;

	public static final int CODE_SUCCESS = 200;
	public static final int CODE_FAILURE = 500;
	public static final int CODE_VERSION_FAILURE = 501;
	public static final int CODE_ACCESS_FAILURE = 502;

}
