package com.gmail.favorlock.commonutils.scoreboard.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ScoreboardWrapper;
import com.gmail.favorlock.commonutils.scoreboard.impl.CraftScoreboardWrapper;

/**
 * An API for dealing with Scoreboards.
 * <p/>
 * All Scoreboards and components created through this API will actually be
 * proxies of their representative Bukkit objects. This allows them to have
 * enhanced capabilities, such as giving access to new events and the ability to
 * cast directly from the Bukkit object to the API Wrapper object.
 */
public class ScoreboardAPI {

    private static final Class<?> classCraftScoreboardComponent =
            VersionHandler.getCraftBukkitClass("scoreboard.CraftScoreboardComponent");
    private static final Method checkState =
            CommonReflection.getMethod(classCraftScoreboardComponent, "checkState", 0);
    
    /**
     * Gets the ScoreboardWrapper currently registered under the given label, or
     * creates and registers one if one isn't present.
     * 
     * @param label The name to lookup / register under.
     * @return The ScoreboardWrapper that was found or created.
     */
    public static ScoreboardWrapper getCustomScoreboard(String label) {
        return CraftScoreboardWrapper.getCustomScoreboardWrapper(label);
    }
    
    /**
     * Gets whether or not there is currently a ScoreboardWrapper registered
     * under the given label.
     * 
     * @param name  The label to lookup.
     * @return <b>true</b> if there is a ScoreboardWrapper registered under the
     *         given name, <b>false</b> otherwise.
     */
    public static boolean isCustomScoreboardRegistered(String label) {
        return CraftScoreboardWrapper.isCustomScoreboardRegistered(label);
    }
    
    /**
     * Get a new ScoreboardWrapper for the server main Scoreboard.
     * 
     * @return A ScoreboardWrapper for the server's main Scoreboard.
     */
    public static ScoreboardWrapper getServerScoreboard() {
        return CraftScoreboardWrapper.getServerMainScoreboardWrapper();
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
