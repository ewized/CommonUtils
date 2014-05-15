package com.gmail.favorlock.commonutils.reflection;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommonReflection {

    public static Object getHandle(Entity entity) {
        Object nmsEntity = null;
        Method getHandle = CommonReflection.getMethod(entity.getClass(), "getHandle");

        try {
            nmsEntity = getHandle.invoke(entity);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return nmsEntity;
    }

    public static Object getHandle(World world) {
        Object nmsEntity = null;

        Method getHandle = getMethod(world.getClass(), "getHandle");
        try {
            nmsEntity = getHandle.invoke(world);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return nmsEntity;
    }

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
    
    public static <T> Constructor<T> getConstructor(Class<T> cls, Class<?>[] param_classes) {
        try {
            Constructor<T> constructor = cls.getConstructor(param_classes);
            return constructor;
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }
    
    public static <T> T constructNewInstance(Constructor<T> constructor, Object[] parameters) {
        constructor.setAccessible(true);
        
        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return null;
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
    
    public static Object invokeMethodAndReturn(Method method, Object instance) {
        Object ret;
        
        try {
            method.setAccessible(true);
            ret = method.invoke(instance);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            ret = null;
        }
        
        return ret;
    }
    
    public static Object invokeMethodAndReturn(Method method, Object instance, Object[] params) {
        Object ret;
        
        try {
            method.setAccessible(true);
            ret = method.invoke(instance, params);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            ret = null;
        }
        
        return ret;
    }

    public static Object invokeMethodAndReturn(Class<?> clazz, String method, Object object) {
        Object value = null;

        try {
            Method methodToInvoke = getMethod(clazz, method);
            methodToInvoke.setAccessible(true);

            value = methodToInvoke.invoke(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static Object invokeMethodAndReturn(Class<?> clazz, String method, Object object, Object... params) {
        Object value = null;

        Class<?>[] args = new Class<?>[params.length];
        for (int x = 0; x < params.length; x++) {
            args[x] = params[x].getClass();
        }

        try {
            Method methodToInvoke = getMethod(clazz, method, args);
            methodToInvoke.setAccessible(true);

            value = methodToInvoke.invoke(object, params);
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
            if (listOne[i] != listTwo[i] && (listTwo[i].isAssignableFrom(listOne[i]) == false)) {
                return false;
            }
        }

        return true;
    }

}
