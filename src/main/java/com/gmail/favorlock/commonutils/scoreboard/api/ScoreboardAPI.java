package com.gmail.favorlock.commonutils.scoreboard.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ScoreboardWrapper;
import com.gmail.favorlock.commonutils.scoreboard.impl.CraftScoreboardWrapper;

/**
 * A [pretty hacky] API for dealing with Scoreboards.
 * <p/>
 * All Scoreboards and components created through this API will actually be
 * proxies of their representative Bukkit objects. This allows them to have
 * enhanced capabilities, such as giving access to new events and the ability to
 * cast directly from the Bukkit object to the API Wrapper object.
 */
public class ScoreboardAPI {

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
     * @deprecated There are a lot of things that can go wrong when using the
     *             scoreboard that the server saves to file; use this with
     *             caution, as this may have to be taken out in the future.
     * 
     * @return A ScoreboardWrapper for the server's main Scoreboard.
     */
    public static ScoreboardWrapper getServerScoreboard() {
        return CraftScoreboardWrapper.getServerMainScoreboardWrapper();
    }
    
    /**
     * Integrate an existing Scoreboard into the ScoreboardAPI. If the given
     * Scoreboard is already a ScoreboardProxy, nothing will be changed, and its
     * existing ScoreboardWrapper will be returned. If the given Scoreboard is
     * not already a proxy, it will be registered under the given label.
     * 
     * @throws IllegalArgumentException
     *             If the given label is already in use, and the given
     *             Scoreboard isn't already proxied.
     * 
     * @param scoreboard The Scoreboard to attempt to integrate.
     * @param label      The label to use if a new wrapper is to be registered.
     * @return A ScoreboardWrapper for the given Scoreboard, or <b>null</b> if
     *         an error occurred.
     */
    public static ScoreboardWrapper integrateExistingScoreboard(Scoreboard scoreboard, String label) {
        return CraftScoreboardWrapper.integrateExistingScoreboard(scoreboard, label);
    }
    
    /**
     * Test if an Objective instance is still valid. ObjectiveWrappers implement
     * this check with ScoreboardComponentWrapper#isValid.
     * 
     * @param objective The Objective to test.
     * @return <b>true</b> if the given Objective is still valid, <b>false</b>
     *         otherwise.
     */
    public static boolean checkState(Objective objective) {
        Object scoreboard_component = objective;
        return ScoreboardAPI.checkState(scoreboard_component);
    }
    
    /**
     * Test if a Team instance is still valid. TeamWrappers implement this check
     * with ScoreboardComponentWrapper#isValid.
     * 
     * @param team  The Team to test.
     * @return <b>true</b> if the given Team is still valid, <b>false</b>
     *         otherwise.
     */
    public static boolean checkState(Team team) {
        Object scoreboard_component = team;
        return ScoreboardAPI.checkState(scoreboard_component);
    }
    
    private static boolean checkState(Object scoreboard_component) {
        Class<?> classCraftScoreboardComponent = VersionHandler.getCraftBukkitClass("scoreboard.CraftScoreboardComponent");
        Method checkState = CommonReflection.getMethod(classCraftScoreboardComponent, "checkState", 0);
        
        try {
            checkState.setAccessible(true);
            checkState.invoke(scoreboard_component);
            
            return true;
        } catch (IllegalStateException e) {
            // checkState() threw an IllegalStateException because the Objective no longer has a valid Scoreboard.
            return false;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // Something actually went wrong
            e.printStackTrace();
            return false;
        }
    }
}
