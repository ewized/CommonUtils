package com.archeinteractive.dev.commonutils.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodBuilder {

    Method method;
    Object object;

    public MethodBuilder(Class<?> clazz, String method, Object object) {
        this.method = CommonReflection.getMethod(clazz, method);
        this.method.setAccessible(true);
        this.object = object;
    }

    public MethodBuilder(Class<?> clazz, String method, Object object, Class<?>[] params) {
        this.method = CommonReflection.getMethod(clazz, method, params);
        this.method.setAccessible(true);
        this.object = object;
    }

    public MethodBuilder invoke(Object... params) {
        try {
            method.invoke(object, params);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return this;
    }

    public Object invokeReturn(Object... params) {
        try {
            return method.invoke(object, params);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

}
