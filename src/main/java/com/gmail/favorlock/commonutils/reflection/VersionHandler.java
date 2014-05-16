package com.gmail.favorlock.commonutils.reflection;

import org.bukkit.Bukkit;

public class VersionHandler {

    /**
     * Get the version of the currently running Bukkit server. This is the
     * version that is found in the dynamic package of the NMS and OBC code.
     * 
     * @return A String that represents the version of this server.
     */
    public static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1);
        return version;
    }
    
    /** @deprecated in favor of {@link VersionHandler#getNMSClass(String)} */
    public static Class<?> getCraftClass(String className) {
        return getNMSClass(className);
    }
    
    /**
     * Get a Class from the net.minecraft.server package of the given name.
     * 
     * @param className The name of the Class to look for
     * @return The Class that was found, or <b>null</b> if none.
     */
    public static Class<?> getNMSClass(String className) {
        String clazzName = "net.minecraft.server." + getVersion() + "." + className;
        Class<?> clazz = null;

        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return clazz;
    }
    
    /** @deprecated in favor of {@link VersionHandler#getOBCClass(String)} */
    public static Class<?> getCraftBukkitClass(String className) {
        return getOBCClass(className);
    }
    
    /**
     * Get a Class from the org.bukkit.craftbukkit package of the given name.
     * 
     * @param className The path to the class, from org.bukkit.craftbukkit onward.
     * @return The Class that was found, or <b>null</b> if none.
     */
    public static Class<?> getOBCClass(String className) {
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
