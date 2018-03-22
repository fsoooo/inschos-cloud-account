package com.inschos.cloud.account.access.http.controller.action;


import com.inschos.cloud.account.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.account.access.http.controller.bean.PageBean;
import com.inschos.cloud.account.annotation.ParamCheckAnnotation;
import com.inschos.cloud.account.assist.kit.JsonKit;
import com.inschos.cloud.account.assist.kit.StringKit;
import com.inschos.cloud.account.model.Page;

import java.lang.reflect.Field;
import java.util.List;

public class BaseAction {

	public <T> T requst2Bean(String body, Class<T> clazz) {
		return JsonKit.json2Bean(body, clazz);
	}

	public String json(int code, String message, BaseResponse response) {
		if (response == null) {
			response = new BaseResponse();
		}

		response.code = code;
		response.message = message;

		return JsonKit.bean2Json(response);
	}

	public String json(BaseResponse response) {
		return JsonKit.bean2Json(response);
	}

	public void setResponse(int code, String message, BaseResponse response) {
		if (response != null) {
			response.code = code;
			response.message = message;
		}
	}

	public String checkParam(Object obj) {
		return checkParam(obj, null);
	}

	public String checkParam(Object obj, List<String> ignore) {
		String result = "";
		if (obj == null) {
			result = "请求参数错误";
		} else {
			Field[] fields = obj.getClass().getFields();

			for (Field field : fields) {
				if (!StringKit.isEmpty(result)) {
					return result;
				}
				String name = field.getName();
				boolean isCheckEmpty = false;
				boolean isInteger = false;
				boolean isCheckNumeric = false;
				int maxLength = -1;
				int minLength = -1;

				if (ignore != null && ignore.contains(name)) {
					continue;
				}

				try {
					if (field.isAnnotationPresent(ParamCheckAnnotation.class)) {
						ParamCheckAnnotation annotation = field.getAnnotation(ParamCheckAnnotation.class);
						name = annotation.name();
						isCheckEmpty = annotation.isCheckEmpty();
						isInteger = annotation.isInteger();
						isCheckNumeric = annotation.isCheckNumeric();
						maxLength = annotation.isCheckMaxLength();
						minLength = annotation.isCheckMinLength();
					}

					if (maxLength > -1) {
						Object object = field.get(obj);
						if (object != null && field.getType() == String.class) {
							String value = String.valueOf(object);
							if (!StringKit.isEmpty(value)) {
								if (value.length() > maxLength) {
									result = name + "长度大于有效长度";
								}
							}
						}
					}

					if (minLength > -1) {
						Object object = field.get(obj);
						if (object != null && field.getType() == String.class) {
							String value = String.valueOf(object);
							if (!StringKit.isEmpty(value)) {
								if (value.length() < minLength) {
									result = name + "长度小于有效长度";
								}
							}
						}
					}

					if (isCheckEmpty) {
						Object object = field.get(obj);
						if (object == null) {
							result = name + "不能为空";
						} else if (field.getType() == String.class) {
							String value = String.valueOf(object);
							if (StringKit.isEmpty(value)) {
								result = name + "不能为空";
							}
						}
					}

					if (isInteger) {
						Object object = field.get(obj);
						if (object == null) {
							result = name + "参数类型需要整数";
						} else {
							String value = String.valueOf(object);
							if (!StringKit.isInteger(value)) {
								result = name + "参数类型需要整数";
							}
						}
					}

					if (isCheckNumeric) {
						Object object = field.get(obj);
						if (object == null) {
							result = name + "参数类型需要数字";
						} else {
							String value = String.valueOf(object);
							if (!StringKit.isNumeric(value)) {
								result = name + "参数类型需要数字";
							}
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	protected Page setPage(String lastId, String num, String size) {
		Page page = new Page();

		if (StringKit.isInteger(size)) {
			if (StringKit.isInteger(lastId)) {
				page.lastId = Long.valueOf(lastId);
				page.offset = Integer.valueOf(size);
			} else if (StringKit.isInteger(num)) {
				int pageSize = Integer.valueOf(size);
				int pageStart = (Integer.valueOf(num) - 1) * pageSize;

				page.start = pageStart;
				page.offset = pageSize;
			}
		}
		return page;
	}

	protected PageBean setPageBean(long lastId, String page_size, int total, int listSize) {
		PageBean pageBean = new PageBean();
		pageBean.lastId = String.valueOf(lastId);
		pageBean.pageSize = StringKit.isInteger(page_size) ? page_size : "20";
		pageBean.total = String.valueOf(total);
		pageBean.listSize = String.valueOf(listSize);

		return pageBean;
	}

	protected PageBean setPageBean(String page_num, String page_size, int total, int listSize) {
		PageBean pageBean = new PageBean();

		int pageTotal = 0;

		if (StringKit.isInteger(page_size)) {
			int pageSize = Integer.valueOf(page_size);
			if (pageSize > 0) {
				pageTotal = total / pageSize;

				if (total % pageSize > 0) {
					pageTotal += 1;
				}
			}
		}

		pageBean.pageNum = StringKit.isInteger(page_num) ? page_num : "1";
		pageBean.pageSize = StringKit.isInteger(page_size) ? page_size : "20";
		pageBean.pageTotal = String.valueOf(pageTotal);
		pageBean.total = String.valueOf(total);
		pageBean.listSize = String.valueOf(listSize);

		return pageBean;
	}
}
