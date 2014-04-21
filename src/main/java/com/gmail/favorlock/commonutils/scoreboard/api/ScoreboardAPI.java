package com.gmail.favorlock.commonutils.scoreboard.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;
import com.gmail.favorlock.commonutils.scoreboard.CustomScoreboard;
import com.gmail.favorlock.commonutils.scoreboard.ServerScoreboard;

public class ScoreboardAPI {

    private static final Class<?> classCraftScoreboardComponent =
            VersionHandler.getCraftBukkitClass("scoreboard.CraftScoreboardComponent");
    private static final Method checkState =
            CommonReflection.getMethod(classCraftScoreboardComponent, "checkState", 0);
    
    /**
     * Get a new CustomScoreboard. If one exists under the given label, it will
     * be returned.
     * 
     * @param label The label to register the Scoreboard under.
     * @return The CustomScoreboard.
     */
    public static CustomScoreboard newCustomScoreboard(String label) {
        return CustomScoreboard.forLabel(label);
    }
    
    /**
     * Get a new ScoreboardWrapper for the server main Scoreboard.
     * 
     * @return  A ServerScoreboard.
     */
    public static ServerScoreboard newServerScoreboard() {
        return new ServerScoreboard();
    }
    
    /**
     * Test if an Objective instance is still valid.
     * 
     * @param objective The Objective to test.
     * @return <b>true</b> if the given Objective is still valid, <b>false</b>
     *         otherwise.
     */
    public static boolean checkState(Objective objective) {
        try {
            checkState.setAccessible(true);
            checkState.invoke(objective);
            
            return true;
        } catch (IllegalStateException e) {
            // checkState() threw an IllegalStateException because the Objective no longer has a valid Scoreboard.
            return false;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace(); // Something actually went wrong
            return false;
        }
    }
    
    /**
     * Test if a Team instance is still valid.
     * 
     * @param team  The Team to test.
     * @return <b>true</b> if the given Team is still valid, <b>false</b>
     *         otherwise.
     */
    public static boolean checkState(Team team) {
        try {
            checkState.setAccessible(true);
            checkState.invoke(team);
            
            return true;
        } catch (IllegalStateException e) {
            // checkState() threw an IllegalStateException because the Team no longer has a valid Scoreboard.
            return false;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace(); // Something actually went wrong
            return false;
        }
    }
}
