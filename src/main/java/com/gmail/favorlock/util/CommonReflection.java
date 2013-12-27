package com.gmail.favorlock.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommonReflection {

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void setField(Class<?> clazz, Object object, String fieldName, Object value) {
        try {
            Field field = CommonReflection.getField(clazz, fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Method getMethod(Class<?> clazz, String method, Class<?>[] args) {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(method) && classListEqual(args, m.getParameterTypes())) {
                return m;
            }
        }

        return null;
    }

    public static Method getMethod(Class<?> clazz, String method, Integer args) {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(method) && args.equals(Integer.valueOf(m.getParameterTypes().length))) {
                return m;
            }
        }

        return null;
    }

    public static Method getMethod(Class<?> clazz, String method) {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(method)) {
                return m;
            }
        }

        return null;
    }

    public static Object invokeMethod(Class clazz, String method, Object object) {
        Object value = null;

        try {
            value = getMethod(clazz, method).invoke(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static boolean classListEqual(Class<?>[] listOne, Class<?>[] listTwo) {
        if (listOne.length != listTwo.length) {
            return false;
        }

        for (int i = 0; i < listOne.length; i++) {
            if (listOne[i] != listTwo[i]) {
                return false;
            }
        }

        return true;
    }

}
