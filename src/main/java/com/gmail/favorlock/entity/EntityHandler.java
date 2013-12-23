package com.gmail.favorlock.entity;

import com.gmail.favorlock.util.CommonReflection;
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

}
