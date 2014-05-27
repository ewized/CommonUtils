package com.gmail.favorlock.commonutils.network.packets;

import java.lang.reflect.Constructor;

import org.bukkit.Location;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;

public class WrapperPlayOutWorldParticles extends PacketWrapper {
    
    private static final Class<?> classPacketPlayOutWorldParticles = VersionHandler.getNMSClass("PacketPlayOutWorldParticles");
    
    private final String effect;
    private final float x, y, z;
    private float dx, dy, dz;
    private float speed;
    private int amount;
    
    public WrapperPlayOutWorldParticles(String effect, Location location) {
        super(classPacketPlayOutWorldParticles);
        this.effect = effect;
        this.x = (float) location.getX();
        this.y = (float) location.getY();
        this.z = (float) location.getZ();
        this.dx = 0f;
        this.dy = 0f;
        this.dz = 0f;
        this.speed = 0f;
        this.amount = 1;
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
                classPacketPlayOutWorldParticles, new Class<?>[] { String.class,
                        float.class, float.class, float.class,
                        float.class, float.class, float.class,
                        float.class, int.class });
        return CommonReflection.constructNewInstance(constructorPacketPlayOutWorldParticles,
                new Object[] { effect, x, y, z, dx, dy, dz, speed, amount });
    }
}