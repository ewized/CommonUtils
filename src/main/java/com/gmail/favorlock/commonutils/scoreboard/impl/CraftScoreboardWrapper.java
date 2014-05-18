package com.gmail.favorlock.commonutils.scoreboard.impl;

import java.lang.reflect.InvocationHandler;
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

    private final String label;
    private final boolean main;
    /** The original, unproxied scoreboard. */
    private Scoreboard original;
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
    
    /**
     * Bypass the ScoreboardProxy that would otherwise be returned.
     * 
     * @return The original, unproxied scoreboard.
     */
    public Scoreboard bypassProxy() {
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
     * Unregister this ScoreboardWrapper.
     */
    public void unregisterWrapper() {
        if (isMain())
            throw new IllegalStateException("Cannot unregister the server main Scoreboard!");
        
        clearEntries();
        clearObjectives();
        clearTeams();
        this.board = null;
        this.original = null;
        registry.remove(label);
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
     * Clear all objective scores for the given entry.
     */
    public void clearEntry(String entry) {
        board.resetScores(entry);
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
            try {
                objective.unregister();
            } catch (Exception e) {
                // Objective was already unregistered. Somehow.
            }
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
            for (String entry : team.getEntries()) {
                team.removeEntry(entry);
            }
            
            try {
                team.unregister();
            } catch (Exception e) {
                // Team was already registered. Somehow.
            }
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
        if (player.getScoreboard().equals(original))
            return false;
        
        player.setScoreboard(original);
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
    
    /**
     * Get a set of all the Objectives in the Scoreboard represented by this object.
     * 
     * @return A set of ObjectiveWrappers for all Objectives.
     */
    public Set<ObjectiveWrapper> getObjectiveSet() {
        Set<Objective> objectives = board.getObjectives();
        Set<ObjectiveWrapper> objective_wrappers = new HashSet<>();
        
        for (Objective objective : objectives) {
            if (objective instanceof ObjectiveWrapper) {
                objective_wrappers.add((ObjectiveWrapper) objective);
            } else {
                objective_wrappers.add((ObjectiveWrapper) ObjectiveProxy.newProxy(this, objective, main));
            }
        }
        
        return objective_wrappers;
    }
    
    /**
     * Get whether or not an Objective by the given name currently exists.
     * 
     * @param name  The name of the Objective to look for.
     * @return <b>true</b> if an Objective by the given name exists,
     *         <b>false</b> otherwise.
     */
    public boolean isObjectiveRegistered(String name) {
        return getObjective(name) != null;
    }
    
    /**
     * Get whether or not a Team by the given name currently exists.
     * 
     * @param name  The name of the Team to look for.
     * @return <b>true</b> if a Team by the given name exists, <b>false</b>
     *         otherwise.
     */
    public boolean isTeamRegistered(String name) {
        return getTeam(name) != null;
    }
    
    /**
     * Get a set of all the Teams in the Scoreboard represented by this object.
     * 
     * @return A set of TeamWrappers for all Teams.
     */
    public Set<TeamWrapper> getTeamSet() {
        Set<Team> teams = board.getTeams();
        Set<TeamWrapper> team_wrappers = new HashSet<>();
        
        for (Team team : teams) {
            if (team instanceof TeamWrapper) {
                team_wrappers.add((TeamWrapper) team);
            } else {
                team_wrappers.add((TeamWrapper) TeamProxy.newProxy(this, team, main));
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
            
            if (Proxy.isProxyClass(registered.getClass())) {
                InvocationHandler ih = Proxy.getInvocationHandler(registered);
                
                if (ih instanceof ObjectiveProxy) {
                    ObjectiveProxy proxy = (ObjectiveProxy) ih;
                    
                    return proxy.getObjectiveWrapper();
                }
            }

            Bukkit.getLogger().warning("Scoreboard util- Newly registered Objective returned without being proxied!");
            return null; // return new CraftObjectiveWrapper(this, registered).setProxy(registered);
        } catch (IllegalArgumentException e) {
            return getObjectiveByName(name);
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
    public ObjectiveWrapper getObjectiveByName(String name) {
        if (name == null)
            throw new IllegalArgumentException("Objective name cannot be null!");
        
        if (board.getObjective(name) == null)
            return null;
        
        Objective objective = board.getObjective(name);
        
        if (Proxy.isProxyClass(objective.getClass())) {
            InvocationHandler ih = Proxy.getInvocationHandler(objective);
            
            if (ih instanceof ObjectiveProxy) {
                ObjectiveProxy proxy = (ObjectiveProxy) ih;
                
                return proxy.getObjectiveWrapper();
            }
            
            Bukkit.getLogger().warning("Scoreboard util- Existing Objective proxy was not a ObjectiveProxy!");
            return null; // return new CraftObjectiveWrapper(this, objective).setProxy(objective);
        } else {
            Objective proxy = ObjectiveProxy.newProxy(this, objective, main);
            
            if (Proxy.isProxyClass(proxy.getClass())) {
                InvocationHandler ih = Proxy.getInvocationHandler(proxy);
                
                if (ih instanceof ObjectiveProxy) {
                    ObjectiveProxy proxyHandler = (ObjectiveProxy) ih;
                    
                    return proxyHandler.getObjectiveWrapper();
                }
            }
            
            Bukkit.getLogger().warning("Scoreboard util- Existing Objective could not be proxied!");
            return null; // return new CraftObjectiveWrapper(this, proxy).setProxy(proxy);
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
            
            if (Proxy.isProxyClass(registered.getClass())) {
                InvocationHandler ih = Proxy.getInvocationHandler(registered);
                
                if (ih instanceof TeamProxy) {
                    TeamProxy proxy = (TeamProxy) ih;
                    
                    return proxy.getTeamWrapper();
                }
            }
            
            Bukkit.getLogger().warning("Scoreboard util- Newly registered Team returned without being proxied!");
            return null; // return new CraftTeamWrapper(this, registered).setProxy(registered);
        } catch (IllegalArgumentException e) {
            return getTeamByName(name);
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
    public TeamWrapper getTeamByName(String name) {
        if (name == null)
            throw new IllegalArgumentException("Team name cannot be null!");
        
        if (board.getTeam(name) == null)
            return null;
        
        Team team = board.getTeam(name);
        
        if (Proxy.isProxyClass(team.getClass())) {
            InvocationHandler ih = Proxy.getInvocationHandler(team);
            
            if (ih instanceof TeamProxy) {
                TeamProxy proxy = (TeamProxy) ih;
                
                return proxy.getTeamWrapper();
            }

            Bukkit.getLogger().warning("Scoreboard util- Existing Team proxy was not a TeamProxy!");
            return null; // return new CraftTeamWrapper(this, team).setProxy(team);
        } else {
            Team proxy = TeamProxy.newProxy(this, team, main);
            
            if (Proxy.isProxyClass(proxy.getClass())) {
                InvocationHandler ih = Proxy.getInvocationHandler(proxy);
                
                if (ih instanceof TeamProxy) {
                    TeamProxy proxyHandler = (TeamProxy) ih;
                    
                    return proxyHandler.getTeamWrapper();
                }
            }
            
            Bukkit.getLogger().warning("Scoreboard util- Existing Team could not be proxied!");
            return null; // return new CraftTeamWrapper(this, proxy).setProxy(proxy);
        }
    }
    
    /**
     * Get the TeamWrapper for the Team that the given Player belongs to, or
     * null if the given Player isn't present on any team.
     * 
     * @param entry The Player whose Team should be looked up.
     * @return The TeamWeapper for the Player's Team, or <b>null</b> if the
     *         entry isn't on a Team.
     */
    public TeamWrapper getTeamForPlayer(Player player) {
        return getTeamForEntry(player.getName());
    }
    
    /**
     * Get the TeamWrapper for the Team that the given entry belongs to, or null
     * if the given entry isn't present on any team.
     * 
     * @param entry The entry whose Team should be looked up.
     * @return The TeamWeapper for the entry's Team, or <b>null</b> if the entry
     *         isn't on a Team.
     */
    public TeamWrapper getTeamForEntry(String entry) {
        for (TeamWrapper team : getTeamSet()) {
            if (team.hasEntry(entry)) {
                return team;
            }
        }
        
        return null;
    }

    // Must override equals for compatibility
    public boolean equals(Object obj) {
        return board.equals(obj) || original.equals(obj);
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
     * Integrate an existing Scoreboard into the ScoreboardAPI.
     * 
     * @param scoreboard The Scoreboard to attempt to integrate.
     * @param label      The label to use if a new wrapper is to be registered.
     * @return A ScoreboardWrapper for the given Scoreboard, or <b>null</b> if
     *         an error occurred.
     */
    public static ScoreboardWrapper integrateExistingScoreboard(Scoreboard scoreboard, String label) {
        if (Proxy.isProxyClass(scoreboard.getClass())) {
            if (scoreboard instanceof ScoreboardWrapper) {
                return (ScoreboardWrapper) scoreboard;
            }
            
            Bukkit.getLogger().warning("Scoreboard util- Existing Scoreboard was proxy but not ScoreboardWrapper!");
            return null;
        }
        
        if (SERVERMAIN != null) {
            if (scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
                return SERVERMAIN;
            }
        }
        
        for (Map.Entry<String, ScoreboardWrapper> entry : registry.entrySet()) {
            ScoreboardWrapper wrapper = entry.getValue();
            
            if (wrapper == null)
                continue;
            
            if (Proxy.isProxyClass(wrapper.getClass())) {
                InvocationHandler ih = Proxy.getInvocationHandler(wrapper);
                
                if (ih instanceof ScoreboardProxy) {
                    ScoreboardProxy proxy = (ScoreboardProxy) ih;
                    
                    CraftScoreboardWrapper custom_wrapper = proxy.getProxiedScoreboardWrapper();
                    
                    if (scoreboard.equals(custom_wrapper.bypassProxy())) {
                        return wrapper;
                    }
                } else {
                    Bukkit.getLogger().warning("Scoreboard util- Custom Wrapper was a proxy but not a ScoreboardProxy!");
                }
            } else {
                Bukkit.getLogger().warning("Scoreboard util- Custom Wrapper was not null and not a proxy!");
            }
        }
        
        if (registry.containsKey(label)) {
            throw new IllegalArgumentException(String.format(
                    "The label %s is already in use!", label));
        }
        
        return wrapExistingScoreboard(scoreboard, label);
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
                ScoreboardWrapper custom_wrapper = (ScoreboardWrapper) proxy;
                registry.put(label, custom_wrapper);
                return custom_wrapper;
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
     * @deprecated There are a lot of things that can go wrong when using the
     *             scoreboard that the server saves to file; use this with
     *             caution, as this may have to be taken out in the future.
     * 
     * @return The ScoreboardWrapper that represents the server main Scoreboard.
     */
    public static ScoreboardWrapper getServerMainScoreboardWrapper() {
        return SERVERMAIN;
    }
    
    private static ScoreboardWrapper wrapExistingScoreboard(Scoreboard scoreboard, String label) {
        if (registry.containsKey(label)) {
            throw new IllegalArgumentException(String.format(
                    "The label %s is already in use!", label));
        }
        
        if (Proxy.isProxyClass(scoreboard.getClass())) {
            if (scoreboard instanceof ScoreboardWrapper) {
                return (ScoreboardWrapper) scoreboard;
            }
            
            Bukkit.getLogger().warning("Scoreboard util- Existing Scoreboard was proxy but not ScoreboardWrapper!");
        }
        
        CraftScoreboardWrapper wrapper = new CraftScoreboardWrapper(scoreboard, label);
        Scoreboard proxy = ScoreboardProxy.newProxy(wrapper, false);
        
        if (proxy instanceof ScoreboardWrapper) {
            ScoreboardWrapper custom_wrapper = (ScoreboardWrapper) proxy;
            registry.put(label, custom_wrapper);
            return custom_wrapper;
        } else {
            return null;
        }
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
