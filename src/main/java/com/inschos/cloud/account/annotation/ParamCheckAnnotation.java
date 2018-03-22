package com.inschos.cloud.account.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamCheckAnnotation {
	/**
	 * 字段文案
	 *
	 * @return
	 */
	String name() default "";

	/**
	 * 是否验证不能为空
	 *
	 * @return
	 */
	boolean isCheckEmpty() default false;

	/**
	 * 是否验证为整数
	 *
	 * @return
	 */
	boolean isInteger() default false;

	/**
	 * 是否验证为数字，包括整数和浮点数
	 *
	 * @return
	 */
	boolean isCheckNumeric() default false;

	/**
	 * 验证字符串最大长度
	 *
	 * @return
	 */
	int isCheckMaxLength() default -1;

	/**
	 * 验证字符串最小长度
	 *
	 * @return
	 */
	int isCheckMinLength() default -1;
}