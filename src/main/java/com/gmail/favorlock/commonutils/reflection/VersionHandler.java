package com.gmail.favorlock.commonutils.reflection;

import org.bukkit.Bukkit;

public class VersionHandler {

    public static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1);
        return version;
    }
    
    public static Class<?> getCraftClass(String className) {
        String clazzName = "net.minecraft.server." + getVersion() + "." + className;
        Class<?> clazz = null;

        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return clazz;
    }
    
    public static Class<?> getCraftBukkitClass(String className) {
        String clazzName = "org.bukkit.craftbukkit." + getVersion() + "." + className;
        Class<?> clazz = null;

        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return clazz;
    }
}
