package com.archeinteractive.dev.commonutils.network.packets;

import java.lang.reflect.Constructor;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import com.archeinteractive.dev.commonutils.reflection.CommonReflection;
import com.archeinteractive.dev.commonutils.reflection.VersionHandler;

public class WrapperPlayOutScoreboardDisplayObjective extends PacketWrapper {

    private static final Class<?> classPacketPlayOutScoreboardDisplayObjective =
            VersionHandler.getNMSClass("PacketPlayOutScoreboardDisplayObjective");
    
    private final byte slot;
    private final Objective objective;
    
    public WrapperPlayOutScoreboardDisplayObjective(DisplaySlot slot, Objective objective) {
        super(classPacketPlayOutScoreboardDisplayObjective);
        
        switch (slot) {
        case PLAYER_LIST:
            this.slot = 0;
            break;
        case SIDEBAR:
            this.slot = 1;
            break;
        case BELOW_NAME:
            this.slot = 2;
            break;
        default:
            throw new IllegalArgumentException("Unsupported DisplaySlot " + slot.name() + "!");
        }
        
        this.objective = objective;
    }
    
    public Object get() {
        Class<?> classScoreboardObjective = VersionHandler.getNMSClass("ScoreboardObjective");
        Constructor<?> constructorPacketPlayOutScoreboardDisplayObjective = CommonReflection.getConstructor(
                classPacketPlayOutScoreboardDisplayObjective, new Class<?>[] { int.class, classScoreboardObjective });
        Object nmsobjective = CommonReflection.getHandle(objective);
        
        return CommonReflection.constructNewInstance(constructorPacketPlayOutScoreboardDisplayObjective,
                new Object[] { (int) slot, nmsobjective });
    }
}
