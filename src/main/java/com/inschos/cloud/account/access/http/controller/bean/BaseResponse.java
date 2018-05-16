package com.inschos.cloud.account.access.http.controller.bean;

import java.util.List;

public class BaseResponse {
	public int code;
	public List<ResponseMessage.ResMessageKV> message ;

	public Object data;
	public PageBean page;

	public static final int CODE_SUCCESS = 200;
	public static final int CODE_FAILURE = 500;
	public static final int CODE_VERSION_FAILURE = 501;
	public static final int CODE_ACCESS_FAILURE = 502;
	public static final int CODE_AGENT_ACCOUNT_INVALID = 503;

}
