package com.archeinteractive.dev.commonutils.scoreboard.api.wrappers;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

/**
 * An interface implemented by TeamProxy wrappers.
 */
public interface TeamWrapper extends ScoreboardComponentWrapper {

    // Full API Methods
    /**
     * Get the Bukkit Team that this object represents.
     * 
     * @return The Bukkit Team object.
     */
    public Team getTeam();
    
    /**
     * Clear the Team represented by this object. All entries associated with
     * the Team will have their team reset.
     */
    public void clear();
    
    /**
     * Get the amount of players on this Team who are currently online.
     * 
     * @return The number of players currently online from this Team.
     */
    public int getOnlineSize();
    
    /**
     * Get the sum of this Team's scores for the given objective.
     * 
     * @param objective The ObjectiveWrapper of the objective to use.
     * @return The sum of this Team's scores for the given Objective.
     */
    public int getTotalScores(ObjectiveWrapper objective);
    
    /**
     * Get the sum of this Team's scores for the given objective name.
     * 
     * @param objective_name    The name of the Objective to use.
     * @return The sum of this Team's scores, or <code>Integer.MIN_VALUE</code>
     *         if no Objective is currently registered under the given name.
     */
    public int getTotalScores(String objective_name);
    
    /**
     * Get the higher scoring Team of this team and another team. If the scores
     * are tied, null will be returned.
     * 
     * @param other_team        The TeamWrapper of the other Team.
     * @param objective_name    The name of the Objective to use.
     * @return The Team with the higher scores sum for the given Objective, or
     *         <b>null</b> if the Teams are tied.
     */
    public TeamWrapper getHigherScoringTeam(TeamWrapper other_team, String objective_name);
    
    /**
     * Get if this TeamWrapper's Team has a higher total score for the given
     * Objective.
     * 
     * @param other_team        The TeamWrapper of the other Team.
     * @param objective_name    The name of the Objective to use.
     * @return <b>true</b> if this Team's score is higher than the other Team's,
     *         <b>false</b> otherwise.
     */
    public boolean isHigherScoringThan(TeamWrapper other_team, String objective_name);
    
    /**
     * Get if this TeamWrapper's Team has the exact same score for the given
     * Objective.
     * 
     * @param other_team        The TeamWrapper of the other Team.
     * @param objective_name    The name of the Objective to use.
     * @return <b>true</b> if this Team's score is equal to the other Team's,
     *         <b>false</b> otherwise.
     */
    public boolean isEqualScoreWith(TeamWrapper other_team, String objective_name);
    // Wrapper Methods
    /**
     * Adds the given Player to the Team.
     * 
     * @param player    The Player who should be added.
     */
    public void addPlayer(Player player);
    
    /**
     * Adds the given entry to the Team.
     * 
     * @param entry     The entry that should be added.
     */
    public void addEntry(String entry);
    
    /**
     * Get whether or not the given Player is on the Team.
     * 
     * @param player    The Player to test.
     * @return <b>true</b> if the Player is on the Team, <b>false</b> otherwise.
     */
    public boolean hasPlayer(Player player);
    
    /**
     * Get whether or not the given entry is on the Team.
     * 
     * @param entry     The entry to test.
     * @return <b>true</b> if the Player is on the Team, <b>false</b> otherwise.
     */
    public boolean hasEntry(String entry);
    
    /**
     * Remove the given Player from the Team.
     * 
     * @param player    The Player to remove.
     * @return <b>true</b> if the Team has been changed as a result of this
     *         call, <b>false</b> otherwise.
     */
    public boolean removePlayer(Player player);
    
    /**
     * Remove the given entry from the Team.
     * 
     * @param entry     The entry to remove.
     * @return <b>true</b> if the Team has been changed as a result of this
     *         call, <b>false</b> otherwise.
     */
    public boolean removeEntry(String entry);
    
    /**
     * Get all of the entries currently associated with the Team.
     * 
     * @return A Set containing all entries on the Team.
     */
    public Set<String> getEntries();
    
    /**
     * Get the size of the Team.
     * 
     * @return The size of the Team.
     */
    public int getTotalSize();
    
    /**
     * Set this Team's allow friendly fire flag.
     * 
     * @param set   The value to set.
     */
    public void setDoFriendlyFire(boolean set);
    
    /**
     * Set this Team's see friendly invisibles flag.
     * 
     * @param set   The value to set.
     */
    public void setCanSeeInvisibles(boolean set);
    
    /**
     * Set this Team's display prefix.
     * 
     * @param prefix    The prefix to set.
     */
    public void setDisplayPrefix(String prefix);
    
    /**
     * Set this Team's display suffix.
     * 
     * @param suffix    The suffix to set.
     */
    public void setDisplaySuffix(String suffix);
}
