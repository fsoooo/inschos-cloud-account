package com.inschos.cloud.account.access.http.controller.bean;


import com.inschos.cloud.account.assist.kit.*;

public class ActionBean {

	private final static String SALT_VALUE_TEST = "InschosTest";
	private final static String SALT_VALUE_ONLINE = "InschosOnLine";

	public String salt;

	public String url;
	public String body;

	public int buildCode;

	public String platform;

	public int apiCode=1;

	public String accountUuid;

	public String managerUuid;

	public long sysId;

	public int type;

	public long tokenTime;

	public String referer;

	public static final RC4Kit rc4 = new RC4Kit("Inschos@2018@verifyToken");

	public static String getSalt(int accountType) {
		if (ConstantKit.IS_PRODUCT) {
			return SALT_VALUE_ONLINE+MD5Kit.MD5Digest(String.valueOf(accountType));
		} else {
			return SALT_VALUE_TEST+MD5Kit.MD5Digest(String.valueOf(accountType));
		}
	}

	public static String packageToken(ActionBean bean) {
		if (bean != null) {
			String token = JsonKit.bean2Json(bean);
			if (!StringKit.isEmpty(token)) {
				return rc4.encry_RC4_base64(token);
			}
		}
		return "";
	}

	public static ActionBean parseToken(String token) {
		ActionBean bean = null;
		try {
			if (!StringKit.isEmpty(token)) {
				String tokenString = rc4.decry_RC4_base64(token);

				if (!StringKit.isEmpty(tokenString)) {
					bean = JsonKit.json2Bean(tokenString, ActionBean.class);
				}
			}
		} catch (Exception e) {
			L.log.debug("verifyToken parse error:{}", e);
		}
		if (bean == null) {
			bean = new ActionBean();
		}
		return bean;
	}
}
