package com.gmail.favorlock;

import com.gmail.favorlock.ServerHandler;
import com.gmail.favorlock.util.CommonReflection;
import org.bukkit.Location;

import java.lang.reflect.Field;

public class PacketHandler {

    public static Object getWorldParticlesPacket(String effect, Location location) {
        Class<?> clazz = ServerHandler.getCraftClass("PacketPlayOutWorldParticles");
        Object packet = null;

        try {
            packet = clazz.newInstance();
            CommonReflection.setField(clazz, packet, "a", effect);
            CommonReflection.setField(clazz, packet, "b", location.getX());
            CommonReflection.setField(clazz, packet, "c", location.getY());
            CommonReflection.setField(clazz, packet, "d", location.getZ());
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
