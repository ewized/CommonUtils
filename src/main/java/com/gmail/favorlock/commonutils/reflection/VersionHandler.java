package com.gmail.favorlock.commonutils.reflection;

import org.bukkit.Bukkit;

public class VersionHandler {

    public static Class<?> getCraftClass(String className) {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1);
        String clazzName = "net.minecraft.server." + version + "." + className;
        Class<?> clazz = null;

        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return clazz;
    }

    public static Class<?> getCraftBukkitClass(String className) {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1);
        String clazzName = "org.bukkit.craftbukkit." + version + "." + className;
        Class<?> clazz = null;

        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return clazz;
    }

}