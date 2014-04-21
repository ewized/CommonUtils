package com.gmail.favorlock.commonutils.scoreboard;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.gmail.favorlock.commonutils.scoreboard.api.ScoreboardEvent;
import com.gmail.favorlock.commonutils.scoreboard.api.ScoreboardHandler;
import com.gmail.favorlock.commonutils.scoreboard.api.ScoreboardScoreChangeEvent;
import com.gmail.favorlock.commonutils.scoreboard.criteria.GeneralCriteria;
import com.gmail.favorlock.commonutils.scoreboard.criteria.ScoreboardCriterion;

/**
 * A wrapper for a Bukkit Scoreboard, providing API methods for dealing with
 * Scoreboards.
 */
public abstract class ScoreboardWrapper {

    final Scoreboard board;
    final String label;
    final boolean main;
    final List<ScoreboardListener> listeners;
    
    ScoreboardWrapper(Scoreboard board, String label, boolean main) {
        if (board == null)
            throw new IllegalArgumentException("Scoreboard cannot be null!");
        
        this.board = board;
        this.label = label;
        this.main = main;
        this.listeners = new ArrayList<>();
    }
    
    private static class ScoreboardListener {
        private final Class<?> type;
        private final Listener instance;
        private final Method method;
        
        private ScoreboardListener(ScoreboardEvent type, Listener instance, Method method) {
            this.type = type.getEventClass();
            this.instance = instance;
            this.method = method;
        }
        
        private void handleEvent(Object event) {
            if (event.getClass().equals(type)) {
                try {
                    method.setAccessible(true);
                    method.invoke(instance, event);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Get the Bukkit Scoreboard that this object represents.
     * 
     * @return The Bukkit Scoreboard.
     */
    protected Scoreboard getScoreboard() {
        return board;
    }
    
    /**
     * Get the label that this ScoreboardWrapper was registered under. If this
     * ScoreboardWrapper represents the server main Scoreboard, the label will
     * be null.
     * 
     * @return The String label of this ScoreboardWrapper, or <b>null</b> if
     *         this ScoreboardWrapper represents the server main Scoreboard.
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Get if the Scoreboard represented by this object is the server's main
     * scoreboard.
     * 
     * @return <b>true</b> if the Scoreboard is the server main scoreboard,
     *         <b>false</b> otherwise.
     */
    public boolean isMain() {
        return main;
    }
    
    /**
     * Invoke all applicable registered ScoreboardListeners for a score change.
     * 
     * @param event The ScoreboardScoreChangeEvent to invoke.
     */
    protected void invokeListeners(ScoreboardScoreChangeEvent event) {
        for (ScoreboardListener listener : listeners) {
            listener.handleEvent(event);
        }
    }
    
    /**
     * Register a new listener with this ScoreboardWrapper.
     * 
     * @param listener  The ScoreboardListener to register.
     */
    public void registerListener(Listener listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(ScoreboardHandler.class)) {
                ScoreboardHandler annotation = method.getAnnotation(ScoreboardHandler.class);
                Class<?>[] params = method.getParameterTypes();
                ScoreboardEvent event = annotation.event();
                
                if (params.length == 1 && params[0].equals(event.getEventClass())) {
                    ScoreboardListener scoreboardListener = new ScoreboardListener(event, listener, method);
                    listeners.add(scoreboardListener);
                }
            }
        }
    }
    
    /**
     * Unregister the given listener, if present.
     * 
     * @param listener The listener instance that should be unregistered.
     */
    public void unregisterListener(Listener listener) {
        for (ScoreboardListener scoreboardListener : new ArrayList<>(listeners)) {
            if (scoreboardListener.instance.equals(listener))
                listeners.remove(scoreboardListener);
        }
    }
    
    /**
     * Unregister all registered listeners of the given class.
     * 
     * @param cls The Class that should have its instances unregistered.
     */
    public void unregisterListeners(Class<?> cls) {
        for (ScoreboardListener listener : new ArrayList<>(listeners)) {
            if (listener.instance.getClass().equals(cls))
                listeners.remove(listener);
        }
    }
    
    /**
     * Clear all objective scores for every entry of the Scoreboard represented
     * by this object.
     * 
     * @return <b>true</b> if the Scoreboard has changed as a result of this
     *         call, <b>false</b> otherwise.
     */
    public boolean clearEntries() {
        Set<String> entries = getEntries();
        
        if (entries.size() < 1)
            return false;
        
        for (String entry : entries) {
            board.resetScores(entry);
        }
        
        return true;
    }
    
    /**
     * Clear (by unregistering) all Objectives from this Scoreboard. Note that
     * this may cause any code that utilizies these objectives directly to begin
     * throwing IllegalStateExceptions.
     * 
     * @return <b>true</b> if the Scoreboard has changed as a result of this
     *         call, <b>false</b> otherwise.
     */
    public boolean clearObjectives() {
        Set<Objective> objectives = board.getObjectives();
        
        if (objectives.size() < 1)
            return false;
        
        for (Objective objective : objectives) {
            objective.unregister();
        }
        
        return true;
    }
    
    /**
     * Clear (by unregistering) all Teams from this Scoreboard. Note that this
     * may cause any code that utilizies these teams directly to begin throwing
     * IllegalStateExceptions.
     * 
     * @return <b>true</b> if the Scoreboard has changed as a result of this
     *         call, <b>false</b> otherwise.
     */
    public boolean clearTeams() {
        Set<Team> teams = board.getTeams();
        
        if (teams.size() < 1)
            return false;
        
        for (Team team : teams) {
            team.unregister();
        }
        
        return true;
    }
    
    /**
     * Set the given players' active Scoreboards to the Scoreboard represented
     * by this object.
     * 
     * @param players   The Players to affect.
     */
    public void setFor(Player... players) {
        for (Player player : players)
            setFor(player);
    }
    
    /**
     * Set the given player's active Scoreboard to the Scoreboard represented by
     * this object.
     * 
     * @param player    The Player to affect.
     * @return <b>true</b> if the Player's active Scoreboard has changed as a
     *         result of this call, <b>false</b> otherwise.
     */
    public boolean setFor(Player player) {
        if (player.getScoreboard().equals(board))
            return false;
        
        player.setScoreboard(board);
        return true;
    }
    
    /**
     * Get a set of all of the entries in the Scoreboard represented by this
     * object.
     * 
     * @return A set of all entries.
     */
    public Set<String> getEntries() {
        return board.getEntries();
    }
    
    /**
     * Register an Objective under the given name and the 'dummy' criterion. If
     * such an Objective already exists, the criterion will be ignored, and that
     * Objective will be returned instead.
     * 
     * @param name  The name of the Objective to register / lookup.
     * @return The ObjectiveWrapper for the Objective that was created / found.
     */
    public ObjectiveWrapper registerObjective(String name) {
        return registerObjective(name, GeneralCriteria.DUMMY);
    }

    /**
     * Register an Objective under the given name and criterion. If such an
     * Objective already exists, the criterion will be ignored, and that
     * Objective will be returned instead.
     * 
     * @param name  The name of the Objective to register / lookup.
     * @return The ObjectiveWrapper for the Objective that was created / found.
     */
    public ObjectiveWrapper registerObjective(String name, ScoreboardCriterion criterion) {
        if (name == null)
            throw new IllegalArgumentException("Objective name cannot be null!");
        
        try {
            Objective registered = board.registerNewObjective(name, criterion.getCriterionString());
            Objective proxy = ObjectiveProxy.newProxy(this, registered);
            
            return new ObjectiveWrapper(this, proxy);
        } catch (IllegalArgumentException e) {
            return getObjective(name);
        }
    }

    /**
     * Get the ObjectiveWrapper for the Objective of the given name, if such a
     * Objective has been registered.
     * 
     * @param name  The name of the Objective to lookup.
     * @return The ObjectiveWrapper for the Objective, or <b>null</b> if the
     *         Objective isn't registered.
     */
    public ObjectiveWrapper getObjective(String name) {
        if (name == null)
            throw new IllegalArgumentException("Objective name cannot be null!");
        
        if (board.getObjective(name) == null)
            return null;
        
        Objective objective = board.getObjective(name);
        
        if (Proxy.isProxyClass(objective.getClass())) {
            return new ObjectiveWrapper(this, objective);
        } else {
            Objective proxy = ObjectiveProxy.newProxy(this, objective);
            return new ObjectiveWrapper(this, proxy);
        }
    }

    /**
     * Register a Team under the given name. If such a Team already exists, that
     * Team will be returned instead.
     * 
     * @param name  The name of the Team to register / lookup.
     * @return The TeamWrapper for the Team that was created / found.
     */
    public TeamWrapper registerTeam(String name) {
        if (name == null)
            throw new IllegalArgumentException("Team name cannot be null!");
        
        try {
            Team registered = board.registerNewTeam(name);
            
            return new TeamWrapper(this, registered);
        } catch (IllegalArgumentException e) {
            return getTeam(name);
        }
    }

    /**
     * Get the TeamWrapper for the Team of the given name, if such a Team has
     * been registered.
     * 
     * @param name  The name of the Team to lookup.
     * @return The TeamWrapper for the Team, or <b>null</b> if the Team isn't
     *         registered.
     */
    public TeamWrapper getTeam(String name) {
        if (name == null)
            throw new IllegalArgumentException("Team name cannot be null!");
        
        if (board.getTeam(name) == null)
            return null;
        
        Team team = board.getTeam(name);
        
        return new TeamWrapper(this, team);
    }
}
