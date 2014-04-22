package com.gmail.favorlock.commonutils.scoreboard.impl;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.gmail.favorlock.commonutils.scoreboard.api.criteria.GeneralCriteria;
import com.gmail.favorlock.commonutils.scoreboard.api.criteria.ScoreboardCriterion;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ObjectiveWrapper;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ScoreboardWrapper;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.TeamWrapper;

/**
 * A wrapper for a Bukkit Scoreboard, providing API methods for dealing with
 * Scoreboards.
 */
public class CraftScoreboardWrapper implements ScoreboardWrapper, Scoreboard {

    private static final ScoreboardWrapper SERVERMAIN = initServerMainScoreboardWrapper();
    private static final Map<String, ScoreboardWrapper> registry = new HashMap<>();

    private final Scoreboard original;
    private final String label;
    private final boolean main;
    private Scoreboard board;
    
    private CraftScoreboardWrapper(Scoreboard board, String label) {
        if (board == null)
            throw new IllegalArgumentException("Scoreboard cannot be null!");
        
        this.original = board;
        this.board = board;
        this.label = label;
        this.main = label == null;
    }
    
    /**
     * Get the Bukkit Scoreboard that this object represents.
     * 
     * @return The Bukkit Scoreboard.
     */
    public Scoreboard getScoreboard() {
        return board;
    }
    
    protected Scoreboard bypassProxy() {
        return original;
    }
    
    protected void setProxy(Scoreboard proxy) {
        this.board = proxy;
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
     * Clear all objective scores for every entry of the Scoreboard represented
     * by this object.
     * 
     * @return <b>true</b> if the Scoreboard has changed as a result of this
     *         call, <b>false</b> otherwise.
     */
    public boolean clearEntries() {
        Set<String> entries = getEntrySet();
        
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
    public Set<String> getEntrySet() {
        return board.getEntries();
    }
    
    public Set<ObjectiveWrapper> getObjectiveSet() {
        Set<Objective> objectives = board.getObjectives();
        Set<ObjectiveWrapper> objective_wrappers = new HashSet<>();
        
        for (Objective objective : objectives) {
            if (objective instanceof ObjectiveWrapper) {
                objective_wrappers.add((ObjectiveWrapper) objective);
            } else {
                objective_wrappers.add((ObjectiveWrapper) ObjectiveProxy.newProxy(this, objective));
            }
        }
        
        return objective_wrappers;
    }
    
    public Set<TeamWrapper> getTeamSet() {
        Set<Team> teams = board.getTeams();
        Set<TeamWrapper> team_wrappers = new HashSet<>();
        
        for (Team team : teams) {
            if (team instanceof TeamWrapper) {
                team_wrappers.add((TeamWrapper) team);
            } else {
                team_wrappers.add((TeamWrapper) TeamProxy.newProxy(this, team));
            }
        }
        
        return team_wrappers;
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
        
        try { // Newly registered objective should have been intercepted and proxied
            Objective registered = board.registerNewObjective(name, criterion.getCriterionString());
//            Objective proxy = ObjectiveProxy.newProxy(this, registered);
            
            return new CraftObjectiveWrapper(this, registered).setProxy(registered);
        } catch (IllegalArgumentException e) {
            return getObjectiveFor(name);
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
    public ObjectiveWrapper getObjectiveFor(String name) {
        if (name == null)
            throw new IllegalArgumentException("Objective name cannot be null!");
        
        if (board.getObjective(name) == null)
            return null;
        
        Objective objective = board.getObjective(name);
        
        if (Proxy.isProxyClass(objective.getClass())) {
            return new CraftObjectiveWrapper(this, objective).setProxy(objective);
        } else {
            Objective proxy = ObjectiveProxy.newProxy(this, objective);
            return new CraftObjectiveWrapper(this, proxy).setProxy(proxy);
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
        
        try { // Newly registered team should have been intercepted and proxied
            Team registered = board.registerNewTeam(name);
//            Team proxy = TeamProxy.newProxy(this, registered);
            
            return new CraftTeamWrapper(this, registered).setProxy(registered);
        } catch (IllegalArgumentException e) {
            return getTeamFor(name);
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
    public TeamWrapper getTeamFor(String name) {
        if (name == null)
            throw new IllegalArgumentException("Team name cannot be null!");
        
        if (board.getTeam(name) == null)
            return null;
        
        Team team = board.getTeam(name);
        
        if (Proxy.isProxyClass(team.getClass())) {
            return new CraftTeamWrapper(this, team).setProxy(team);
        } else {
            Team proxy = TeamProxy.newProxy(this, team);
            return new CraftTeamWrapper(this, proxy).setProxy(proxy);
        }
    }

    // Must override equals for compatibility
    public boolean equals(Object obj) {
        return board.equals(obj);
    }
    
    // Bukkit Scoreboard delegate methods
    public void clearSlot(DisplaySlot slot) throws IllegalArgumentException {
        board.clearSlot(slot);
    }

    public Set<String> getEntries() {
        return board.getEntries();
    }

    public Objective getObjective(String name) throws IllegalArgumentException {
        return board.getObjective(name);
    }

    public Objective getObjective(DisplaySlot slot) throws IllegalArgumentException {
        return board.getObjective(slot);
    }

    public Set<Objective> getObjectives() {
        return board.getObjectives();
    }

    public Set<Objective> getObjectivesByCriteria(String criteria) throws IllegalArgumentException {
        return board.getObjectivesByCriteria(criteria);
    }

    public Team getPlayerTeam(OfflinePlayer player) throws IllegalArgumentException {
        return board.getPlayerTeam(player);
    }

    @Deprecated
    public Set<OfflinePlayer> getPlayers() {
        return board.getPlayers();
    }

    @Deprecated
    public Set<Score> getScores(OfflinePlayer player) throws IllegalArgumentException {
        return board.getScores(player);
    }

    public Set<Score> getScores(String entry) throws IllegalArgumentException {
        return board.getScores(entry);
    }

    public Team getTeam(String name) throws IllegalArgumentException {
        return board.getTeam(name);
    }

    public Set<Team> getTeams() {
        return board.getTeams();
    }

    public Objective registerNewObjective(String name, String criteria) throws IllegalArgumentException {
        return board.registerNewObjective(name, criteria);
    }

    public Team registerNewTeam(String name) throws IllegalArgumentException {
        return board.registerNewTeam(name);
    }

    @Deprecated
    public void resetScores(OfflinePlayer player) throws IllegalArgumentException {
        board.resetScores(player);
    }

    public void resetScores(String entry) throws IllegalArgumentException {
        board.resetScores(entry);
    }
    
    
    /**
     * Get the ScoreboardWrapper currently registered under the given label, or
     * create one if none is currently registered.
     * 
     * @param label The label to lookup / register under.
     * @return The ScoreboardWrapper for the given label.
     */
    public static ScoreboardWrapper getCustomScoreboardWrapper(String label) {
        ScoreboardWrapper custom = registry.get(label);
        
        if (custom == null) {
            CraftScoreboardWrapper wrapper = new CraftScoreboardWrapper(Bukkit.getScoreboardManager().getNewScoreboard(), label);
            Scoreboard proxy = ScoreboardProxy.newProxy(wrapper, false);
            
            if (proxy instanceof ScoreboardWrapper) {
                return (ScoreboardWrapper) proxy;
            } else {
                return null;
            }
        } else {
            return custom;
        }
    }
    
    /**
     * Get whether or not there is currently a ScoreboardWrapper registered
     * under the given label.
     * 
     * @param label The label to lookup.
     * @return <b>true</b> if the given Objective is still valid, <b>false</b>
     *         otherwise.
     */
    public static boolean isCustomScoreboardRegistered(String label) {
        return registry.containsKey(label);
    }
    
    /**
     * Get the ScoreboardWrapper for the server main Scoreboard.
     * 
     * @return The ScoreboardWrapper that represents the server main Scoreboard.
     */
    public static ScoreboardWrapper getServerMainScoreboardWrapper() {
        return SERVERMAIN;
    }
    
    private static ScoreboardWrapper initServerMainScoreboardWrapper() {
        CraftScoreboardWrapper main = new CraftScoreboardWrapper(Bukkit.getScoreboardManager().getMainScoreboard(), null);
        Scoreboard proxy = ScoreboardProxy.newProxy(main, true);
        
        if (proxy instanceof ScoreboardWrapper) {
            return (ScoreboardWrapper) proxy;
        } else {
            return null;
        }
    }
}
