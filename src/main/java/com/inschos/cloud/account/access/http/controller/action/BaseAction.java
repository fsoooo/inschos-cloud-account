package com.inschos.cloud.account.access.http.controller.action;


import com.inschos.cloud.account.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.account.access.http.controller.bean.PageBean;
import com.inschos.cloud.account.access.http.controller.bean.ResponseMessage;
import com.inschos.cloud.account.annotation.ParamCheckAnnotation;
import com.inschos.cloud.account.assist.kit.JsonKit;
import com.inschos.cloud.account.assist.kit.L;
import com.inschos.cloud.account.assist.kit.StringKit;
import com.inschos.cloud.account.model.Page;

import java.lang.reflect.Field;
import java.util.List;

public class BaseAction {

	public <T> T requst2Bean(String body, Class<T> clazz) {
		T bean = JsonKit.json2Bean(body, clazz);
		if(bean==null){
			try {
				bean =  clazz.newInstance();
			} catch (Exception e) {
				L.log.error("request bean error : {}",e.getMessage(),e);
			}
		}
		return bean;
	}

	public String json(int code, String message, BaseResponse response) {
		if (response == null) {
			response = new BaseResponse();
		}
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.add(message);
		response.code = code;
		response.message = responseMessage.get();

		return JsonKit.bean2Json(response);
	}

	public String json(int code, ResponseMessage responseMessage, BaseResponse response) {
		if (response == null) {
			response = new BaseResponse();
		}
		response.code = code;
		response.message = responseMessage.get();

		return JsonKit.bean2Json(response);
	}

	public String json(BaseResponse response) {
		return JsonKit.bean2Json(response);
	}

	public void setResponse(int code, String message, BaseResponse response) {
		if (response != null) {
			response.code = code;
			ResponseMessage responseMessage = new ResponseMessage();
			responseMessage.add(message);
			response.message = responseMessage.get();
		}
	}

	public ResponseMessage checkParam(Object obj) {
		return checkParam(obj, null);
	}

	public ResponseMessage checkParam(Object obj, List<String> ignore) {
		ResponseMessage message = new ResponseMessage();
		if (obj == null) {
			message.add("请求参数错误");
		} else {
			Field[] fields = obj.getClass().getFields();

			for (Field field : fields) {

				String fieldName = field.getName();
				if (ignore != null && ignore.contains(fieldName)) {
					continue;
				}

				boolean isCheckEmpty = false;
				boolean isInteger = false;
				boolean isCheckNumeric = false;
				int maxLength = -1;
				int minLength = -1;

				try {

					String name = null;
					String msgTxt = null;

					out:{
						if (field.isAnnotationPresent(ParamCheckAnnotation.class)) {
							ParamCheckAnnotation annotation = field.getAnnotation(ParamCheckAnnotation.class);
							name = annotation.name();

							isCheckEmpty = annotation.isCheckEmpty();
							isInteger = annotation.isInteger();
							isCheckNumeric = annotation.isCheckNumeric();
							maxLength = annotation.isCheckMaxLength();
							minLength = annotation.isCheckMinLength();
						}

						if (isCheckEmpty) {
							Object object = field.get(obj);
							if (object == null) {
								msgTxt = "不能为空";
								break out;
							} else if (field.getType() == String.class) {
								String value = String.valueOf(object);
								if (StringKit.isEmpty(value)) {
									msgTxt = "不能为空";
									break out;
								}
							}
						}

						if (isInteger) {
							Object object = field.get(obj);
							if (object == null) {
								msgTxt = "参数类型需要整数";
								break out;
							} else {
								String value = String.valueOf(object);
								if (!StringKit.isInteger(value)) {
									msgTxt = "参数类型需要整数";
									break out;
								}
							}
						}

						if (isCheckNumeric) {
							Object object = field.get(obj);
							if (object == null) {
								msgTxt = "参数类型需要数字";
								break out;
							} else {
								String value = String.valueOf(object);
								if (!StringKit.isNumeric(value)) {
									msgTxt = "参数类型需要数字";
									break out;
								}
							}
						}

						if (maxLength > -1) {
							Object object = field.get(obj);
							if (object != null && field.getType() == String.class) {
								String value = String.valueOf(object);
								if (!StringKit.isEmpty(value)) {
									if (value.length() > maxLength) {
										msgTxt = "长度大于有效长度";
										break out;
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
										msgTxt = "长度小于有效长度";
									}
								}
							}
						}
					}

					if(msgTxt!=null){
						message.add(fieldName,name+msgTxt);
						message.code = BaseResponse.CODE_FAILURE;
					}

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
		}
		return message;
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
