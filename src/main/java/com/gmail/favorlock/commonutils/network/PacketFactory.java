package com.gmail.favorlock.commonutils.network;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;
import org.bukkit.Location;

public class PacketFactory {

    public static Object getEntityDestroyPacket(int entityId) {
        Class<?> clazz = VersionHandler.getNMSClass("PacketPlayOutEntityDestroy");
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

    public static Object getEntityMetadataPacket(int entityId, Object watcher) {
        Class<?> clazz = VersionHandler.getNMSClass("PacketPlayOutEntityMetadata");
        Object packet = null;

        try {
            packet = clazz.newInstance();
            CommonReflection.setField(clazz, packet, "a", entityId);
            CommonReflection.setField(clazz, packet, "b",
                    CommonReflection.invokeMethodAndReturn(watcher.getClass(), "c", watcher));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
    }

    public static Object getSpawnEntityLivingPacket(int entityId, int mobId, Location location, Object watcher) {
        Class<?> clazz = VersionHandler.getNMSClass("PacketPlayOutSpawnEntityLiving");
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

    public static Object getWorldParticlesPacket(String effect, Location location, float xDeviation, float yDeviation, float zDeviation, float speed, int amount) {
        Class<?> clazz = VersionHandler.getNMSClass("PacketPlayOutWorldParticles");
        Object packet = null;

        try {
            packet = clazz.newInstance();
            CommonReflection.setField(clazz, packet, "a", effect);
            CommonReflection.setField(clazz, packet, "b", (float) location.getX());
            CommonReflection.setField(clazz, packet, "c", (float) location.getY());
            CommonReflection.setField(clazz, packet, "d", (float) location.getZ());
            CommonReflection.setField(clazz, packet, "e", xDeviation);
            CommonReflection.setField(clazz, packet, "f", yDeviation);
            CommonReflection.setField(clazz, packet, "g", zDeviation);
            CommonReflection.setField(clazz, packet, "h", speed);
            CommonReflection.setField(clazz, packet, "i", amount);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
    }

}
