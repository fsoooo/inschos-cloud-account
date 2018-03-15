package com.inschos.cloud.account.assist.kit;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IceAnt on 2017/7/11.
 */


/**
 * 反射工具类
 */
public class ReflectKit {

    /**
     * 反射调用指定构造方法创建对象
     *
     * @param clazz    对象类型
     * @param argTypes 参数类型
     * @param args     构造参数
     * @return 返回构造后的对象
     */
    public static <T> T invokeConstructor(Class<T> clazz, Class<?>[] argTypes,
                                          Object[] args) {
        Constructor<T> constructor = null;
        try {
            constructor = clazz.getConstructor(argTypes);
            return constructor.newInstance(args);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            L.log.error("reflect error", e);
        }

        return null;
    }

    /**
     * 反射调用指定对象属性的getter方法
     *
     * @param <T>       泛型
     * @param target    指定对象
     * @param fieldName 属性名
     * @return 返回调用后的值
     */
    public static <T> Object getter(T target, String fieldName) {

        Field field = null;
        try {
            Class<?> clazz = target.getClass();
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            L.log.error("reflect error", e);
        }
        return null;
    }

    /**
     * 反射调用指定对象属性的setter方法
     *
     * @param <T>       泛型
     * @param target    指定对象
     * @param fieldName 属性名
     * @param args      参数列表
     */
    public static <T> void setter(T target, String fieldName, Object args) {

        Class<?> clazz = target.getClass();

        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.set(target, args);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            L.log.error("reflect error", e);
        }
    }


}