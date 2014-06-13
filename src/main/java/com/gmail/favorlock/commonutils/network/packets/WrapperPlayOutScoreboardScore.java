package com.gmail.favorlock.commonutils.network.packets;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.scoreboard.Score;

import com.gmail.favorlock.commonutils.CommonUtils;
import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;

public class WrapperPlayOutScoreboardScore extends PacketWrapper {

    private static final Class<?> classPacketPlayOutScoreboardScore =
            VersionHandler.getNMSClass("PacketPlayOutScoreboardScore");
    private static final Class<?> classScoreboardScore =
            VersionHandler.getNMSClass("ScoreboardScore");
    
    private final Score score;
    private final int type;
    
    public WrapperPlayOutScoreboardScore(Score score, UpdateType type) {
        super(classPacketPlayOutScoreboardScore);
        this.score = score;
        this.type = type.type;
    }
    
    public static enum UpdateType {
        CREATE_UPDATE(0),
        REMOVE(1);
        
        private final int type;
        
        private UpdateType(int type) {
            this.type = type;
        }
    }
    
    public Object get() {
        Constructor<?> constructorPacketPlayOutScoreboardScore = CommonReflection.getConstructor(
                classPacketPlayOutScoreboardScore, new Class<?>[] { classScoreboardScore, int.class });
        return CommonReflection.constructNewInstance(constructorPacketPlayOutScoreboardScore,
                new Object[] { getNMSScore(score), type }); // TODO Get ScoreboardScore for the score
    }
    
    private Object getNMSScore(Score score) {
        Class<?> classCraftScore = VersionHandler.getOBCClass("scoreboard.CraftScore");
        
        Field entryField = CommonReflection.getField(classCraftScore, "entry");
        Field objectiveField = CommonReflection.getField(classCraftScore, "objective");
        Method checkstate = CommonReflection.getMethod(
                VersionHandler.getOBCClass("scoreboard.CraftScoreboardComponent"), "checkState", 0);
        Field boardField = CommonReflection.getField(
                VersionHandler.getOBCClass("scoreboard.CraftScoreboard"), "board");
        Method getPlayerObjectives = CommonReflection.getMethod(
                VersionHandler.getNMSClass("Scoreboard"), "getPlayerObjectives", new Class<?>[] { String.class });
        Method getHandle = CommonReflection.getMethod(
                VersionHandler.getOBCClass("scoreboard.CraftObjective"), "getHandle", 0);
        
        entryField.setAccessible(true);
        objectiveField.setAccessible(true);
        checkstate.setAccessible(true);
        boardField.setAccessible(true);
        getPlayerObjectives.setAccessible(true);
        getHandle.setAccessible(true);
        
        try {
            String entry = (String) entryField.get(score);
            Object objective = objectiveField.get(score);
            Object nmsScoreboard = boardField.get(checkstate.invoke(objective));
            @SuppressWarnings("unchecked")
            Map<String, ?> scores = (Map<String, ?>) getPlayerObjectives.invoke(nmsScoreboard, entry);
            return scores.get(getHandle.invoke(objective));
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalStateException e) {
            CommonUtils.getPlugin().getLogger().warning(
                    "Found an invalid Scoreboard when attempting to resolve a ScoreboardScore.");
            e.printStackTrace();
            return null;
        }
    }
}
