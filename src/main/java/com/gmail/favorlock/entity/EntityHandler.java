package com.gmail.favorlock.entity;

import com.gmail.favorlock.VersionHandler;
import com.gmail.favorlock.util.reflection.CommonReflection;
import com.gmail.favorlock.util.reflection.MethodBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EntityHandler {

    public static void sendPacketToAll(Object packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(player, packet);
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object nmsPlayer = getHandle(player);
            Field playerConnection = nmsPlayer.getClass().getField("playerConnection");
            Object connection = playerConnection.get(nmsPlayer);
            Method sendPacket = CommonReflection.getMethod(connection.getClass(), "sendPacket");
            sendPacket.invoke(connection, packet);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

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

    public static Object getWatcher(Entity entity, boolean visible, float health, String name) {
        Class<?> clazz = VersionHandler.getCraftClass("DataWatcher");
        Object watcher = null;

        try {
            Object nmsEntity = getHandle(entity);
            watcher = clazz.getConstructors()[0].newInstance(nmsEntity);

            new MethodBuilder(clazz, "a", new Class<?>[] {int.class, Object.class})
                    .invoke(0, visible ? (byte) 0 : (byte) 0x20)
                    .invoke(6, (Float) health)
                    .invoke(7, (Integer) 0)
                    .invoke(8, (Byte) (byte) 0)
                    .invoke(10, name)
                    .invoke(11, (Byte) (byte) 1);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return watcher;
    }

}
