package com.gmail.favorlock.commonutils.scoreboard.api.wrappers;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.favorlock.commonutils.scoreboard.api.criteria.ScoreboardCriterion;

/**
 * An interface implemented by ScoreboardProxy wrappers.
 */
public interface ScoreboardWrapper {

    /**
     * Get the Bukkit Scoreboard that this object represents.
     * 
     * @return The Bukkit Scoreboard.
     */
    public Scoreboard getScoreboard();
    
    /**
     * Get the label that this ScoreboardWrapper was registered under. If this
     * ScoreboardWrapper represents the server main Scoreboard, the label will
     * be null.
     * 
     * @return The String label of this ScoreboardWrapper, or <b>null</b> if
     *         this ScoreboardWrapper represents the server main Scoreboard.
     */
    public String getLabel();
    
    /**
     * Get if the Scoreboard represented by this object is the server's main
     * scoreboard.
     * 
     * @return <b>true</b> if the Scoreboard is the server main scoreboard,
     *         <b>false</b> otherwise.
     */
    public boolean isMain();
    
    /**
     * Unregister this ScoreboardWrapper from the ScoreboardAPI. This operation
     * can only be performed if this ScoreboardWrapper is not representative of
     * the server main Scoreboard.
     * <p/>
     * Should this call be successful, the all Objectives, Scores, and Teams
     * will be cleared, and the ScoreboardWrapper will no longer have any
     * reference to the Scoreboard.
     * 
     * @throws IllegalStateException
     *             If this ScoreboardWrapper represents the server's main
     *             Scoreboard, in which case it cannot be unregistered.
     */
    public void unregisterWrapper();
    
    /**
     * Clear all objective scores for every entry of the Scoreboard represented
     * by this object.
     * 
     * @return <b>true</b> if the Scoreboard has changed as a result of this
     *         call, <b>false</b> otherwise.
     */
    public boolean clearEntries();
    
    /**
     * Clear all objective scores for the given entry.
     */
    public void clearEntry(String entry);
    
    /**
     * Clear (by unregistering) all Objectives from this Scoreboard. Note that
     * this may cause any code that utilizies these objectives directly to begin
     * throwing IllegalStateExceptions.
     * 
     * @return <b>true</b> if the Scoreboard has changed as a result of this
     *         call, <b>false</b> otherwise.
     */
    public boolean clearObjectives();
    
    /**
     * Clear (by unregistering) all Teams from this Scoreboard. Note that this
     * may cause any code that utilizies these teams directly to begin throwing
     * IllegalStateExceptions.
     * 
     * @return <b>true</b> if the Scoreboard has changed as a result of this
     *         call, <b>false</b> otherwise.
     */
    public boolean clearTeams();
    
    /**
     * Set the given players' active Scoreboards to the Scoreboard represented
     * by this object.
     * 
     * @param players   The Players to affect.
     */
    public void setFor(Player... players);
    
    /**
     * Set the given player's active Scoreboard to the Scoreboard represented by
     * this object.
     * 
     * @param player    The Player to affect.
     * @return <b>true</b> if the Player's active Scoreboard has changed as a
     *         result of this call, <b>false</b> otherwise.
     */
    public boolean setFor(Player player);
    
    /**
     * Get a set of all of the entries in the Scoreboard represented by this
     * object.
     * 
     * @return A set of all entries.
     */
    public Set<String> getEntrySet();
    
    /**
     * Get a set of all the Objectives in the Scoreboard represented by this object.
     * 
     * @return A set of ObjectiveWrappers for all Objectives.
     */
    public Set<ObjectiveWrapper> getObjectiveSet();
    
    /**
     * Get a set of all the Teams in the Scoreboard represented by this object.
     * 
     * @return A set of TeamWrappers for all Teams.
     */
    public Set<TeamWrapper> getTeamSet();
    
    /**
     * Get whether or not an Objective by the given name currently exists.
     * 
     * @param name  The name of the Objective to look for.
     * @return <b>true</b> if an Objective by the given name exists,
     *         <b>false</b> otherwise.
     */
    public boolean isObjectiveRegistered(String name);
    
    /**
     * Get whether or not a Team by the given name currently exists.
     * 
     * @param name  The name of the Team to look for.
     * @return <b>true</b> if a Team by the given name exists, <b>false</b>
     *         otherwise.
     */
    public boolean isTeamRegistered(String name);
    
    /**
     * Register an Objective under the given name and the 'dummy' criterion. If
     * such an Objective already exists, the criterion will be ignored, and that
     * Objective will be returned instead.
     * 
     * @param name  The name of the Objective to register / lookup.
     * @return The ObjectiveWrapper for the Objective that was created / found.
     */
    public ObjectiveWrapper registerObjective(String name);
    
    /**
     * Register an Objective under the given name and criterion. If such an
     * Objective already exists, the criterion will be ignored, and that
     * Objective will be returned instead.
     * 
     * @param name  The name of the Objective to register / lookup.
     * @return The ObjectiveWrapper for the Objective that was created / found.
     */
    public ObjectiveWrapper registerObjective(String name, ScoreboardCriterion criterion);
    
    /**
     * Get the ObjectiveWrapper for the Objective of the given name, if such a
     * Objective has been registered.
     * 
     * @param name  The name of the Objective to lookup.
     * @return The ObjectiveWrapper for the Objective, or <b>null</b> if the
     *         Objective isn't registered.
     */
    public ObjectiveWrapper getObjectiveByName(String name);
    
    /**
     * Register a Team under the given name. If such a Team already exists, that
     * Team will be returned instead.
     * 
     * @param name  The name of the Team to register / lookup.
     * @return The TeamWrapper for the Team that was created / found.
     */
    public TeamWrapper registerTeam(String name);
    
    /**
     * Get the TeamWrapper for the Team of the given name, if such a Team has
     * been registered.
     * 
     * @param name  The name of the Team to lookup.
     * @return The TeamWrapper for the Team, or <b>null</b> if the Team isn't
     *         registered.
     */
    public TeamWrapper getTeamByName(String name);
    
    /**
     * Get the TeamWrapper for the Team that the given Player belongs to, or
     * null if the given Player isn't present on any team.
     * 
     * @param player    The Player whose Team should be looked up.
     * @return The TeamWeapper for the Player's Team, or <b>null</b> if the
     *         Player isn't on a Team.
     */
    public TeamWrapper getTeamForPlayer(Player player);
    
    /**
     * Get the TeamWrapper for the Team that the given entry belongs to, or null
     * if the given entry isn't present on any team.
     * 
     * @param entry The entry whose Team should be looked up.
     * @return The TeamWeapper for the entry's Team, or <b>null</b> if the entry
     *         isn't on a Team.
     */
    public TeamWrapper getTeamForEntry(String entry);
}
