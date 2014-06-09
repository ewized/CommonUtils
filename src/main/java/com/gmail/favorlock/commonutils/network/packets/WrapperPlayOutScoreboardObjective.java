package com.gmail.favorlock.commonutils.network.packets;

import java.lang.reflect.Constructor;

import org.bukkit.scoreboard.Objective;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;

public class WrapperPlayOutScoreboardObjective extends PacketWrapper {

    private static final Class<?> classPacketPlayOutScoreboardObjective =
            VersionHandler.getNMSClass("PacketPlayOutScoreboardObjective");
    
    private final Objective objective;
    private final int type;
    
    public WrapperPlayOutScoreboardObjective(Objective objective, WrapperPlayOutScoreboardObjective.DisplayType type) {
        super(classPacketPlayOutScoreboardObjective);
        this.objective = objective;
        this.type = type.type;
    }
    
    public static enum DisplayType {
        CREATE(0),
        REMOVE(1),
        UPDATE(2);
        
        private final int type;
        
        private DisplayType(int type) {
            this.type = type;
        }
    }
    
    public Object get() {
        Class<?> classScoreboardObjective = VersionHandler.getNMSClass("ScoreboardObjective");
        Constructor<?> constructorPacketPlayOutScoreboardObjective = CommonReflection.getConstructor(
                classPacketPlayOutScoreboardObjective, new Class<?>[] { classScoreboardObjective, int.class });
        Object nmsobjective = CommonReflection.getHandle(objective);
        
        return CommonReflection.constructNewInstance(constructorPacketPlayOutScoreboardObjective,
                new Object[] { nmsobjective, type });
    }
}
