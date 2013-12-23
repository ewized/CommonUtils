package com.gmail.favorlock;

import org.bukkit.Bukkit;

public class ServerHandler {

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

}
