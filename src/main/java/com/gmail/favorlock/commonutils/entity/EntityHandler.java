package com.gmail.favorlock.commonutils.entity;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.MethodBuilder;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;
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

    public static void sendPacket(Player[] players, Object packet) {
        for (Player player : players)
            sendPacket(player, packet);
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object nmsPlayer = CommonReflection.getHandle(player);
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

    public static Object getWatcher(Entity entity, boolean visible, float health, String name) {
        Class<?> clazz = VersionHandler.getCraftClass("DataWatcher");
        Object watcher = null;

        try {
            Object nmsEntity = CommonReflection.getHandle(entity);
            watcher = clazz.getConstructors()[0].newInstance(nmsEntity);

            new MethodBuilder(clazz, "a", watcher, new Class<?>[]{int.class, Object.class})
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
