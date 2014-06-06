package com.gmail.favorlock.commonutils.network.packets;

import java.lang.reflect.Constructor;

import org.bukkit.Location;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;

public class WrapperPlayOutWorldParticles extends PacketWrapper {
    
    private static final Class<?> classPacketPlayOutWorldParticles = VersionHandler.getNMSClass("PacketPlayOutWorldParticles");
    
    private final String effect;
    private float x, y, z, dx, dy, dz, speed;
    private int amount;
    
    public WrapperPlayOutWorldParticles(String effect) {
        super(classPacketPlayOutWorldParticles);
        this.effect = effect;
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
        this.dx = 0f;
        this.dy = 0f;
        this.dz = 0f;
        this.speed = 0f;
        this.amount = 1;
    }
    
    public WrapperPlayOutWorldParticles setLocation(Location location) {
        this.x = (float) location.getX();
        this.y = (float) location.getY();
        this.z = (float) location.getZ();
        return this;
    }
    
    public WrapperPlayOutWorldParticles setDeviations(float x, float y, float z) {
        this.dx = x;
        this.dy = y;
        this.dz = z;
        return this;
    }
    
    public WrapperPlayOutWorldParticles setSpeed(float speed) {
        this.speed = speed;
        return this;
    }
    
    public WrapperPlayOutWorldParticles setAmount(int amount) {
        this.amount = amount;
        return this;
    }
    
    public Object get() {
        Constructor<?> constructorPacketPlayOutWorldParticles = CommonReflection.getConstructor(
                classPacketPlayOutWorldParticles, new Class<?>[] {
                        String.class, float.class, float.class, float.class,
                        float.class, float.class, float.class, float.class, int.class });
        return CommonReflection.constructNewInstance(constructorPacketPlayOutWorldParticles,
                new Object[] { effect, x, y, z, dx, dy, dz, speed, amount });
    }
}