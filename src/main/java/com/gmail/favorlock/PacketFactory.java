package com.gmail.favorlock;

import com.gmail.favorlock.util.CommonReflection;
import org.bukkit.Location;

public class PacketFactory {

    public static Object getEntityDestroyPacket(int entityId) {
        Class<?> clazz = VersionHandler.getCraftClass("PacketPlayOutEntityDestroy");
        Object packet = null;

        try {
            packet = clazz.newInstance();
            CommonReflection.setField(clazz, packet, "a", new int[]{entityId});
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
    }

    public static Object getSpawnEntityLivingPacket(int entityId, int mobId, Location location, Object watcher) {
        Class<?> clazz = VersionHandler.getCraftClass("PacketPlayOutSpawnEntityLiving");
        Object packet = null;

        try {
            packet = clazz.newInstance();
            CommonReflection.setField(clazz, packet, "a", entityId);
            CommonReflection.setField(clazz, packet, "b", mobId);
            CommonReflection.setField(clazz, packet, "c", location.getBlockX());
            CommonReflection.setField(clazz, packet, "d", location.getBlockY());
            CommonReflection.setField(clazz, packet, "e", location.getBlockZ());
            CommonReflection.setField(clazz, packet, "f", 0);
            CommonReflection.setField(clazz, packet, "g", 0);
            CommonReflection.setField(clazz, packet, "h", 0);
            CommonReflection.setField(clazz, packet, "i", (byte) 0);
            CommonReflection.setField(clazz, packet, "j", (byte) 0);
            CommonReflection.setField(clazz, packet, "k", (byte) 0);
            CommonReflection.setField(clazz, packet, "l", watcher);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
    }

    public static Object getWorldParticlesPacket(String effect, Location location) {
        Class<?> clazz = VersionHandler.getCraftClass("PacketPlayOutWorldParticles");
        Object packet = null;

        try {
            packet = clazz.newInstance();
            CommonReflection.setField(clazz, packet, "a", effect);
            CommonReflection.setField(clazz, packet, "b", (float) location.getX());
            CommonReflection.setField(clazz, packet, "c", (float) location.getY());
            CommonReflection.setField(clazz, packet, "d", (float) location.getZ());
            CommonReflection.setField(clazz, packet, "e", 0);
            CommonReflection.setField(clazz, packet, "f", 0);
            CommonReflection.setField(clazz, packet, "g", 0);
            CommonReflection.setField(clazz, packet, "h", 0);
            CommonReflection.setField(clazz, packet, "i", 10);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
    }

}
