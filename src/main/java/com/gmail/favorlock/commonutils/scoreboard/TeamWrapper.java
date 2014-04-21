package com.gmail.favorlock.commonutils.scoreboard;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.gmail.favorlock.commonutils.scoreboard.api.ScoreboardAPI;

/**
 * A wrapper for a Bukkit scoreboard Team, providing API methods for dealing
 * with Scoreboard Teams.
 */
public class TeamWrapper {

    private final ScoreboardWrapper wrapper;
    private final Team team;
    
    protected TeamWrapper(ScoreboardWrapper wrapper, Team team) {
        this.wrapper = wrapper;
        this.team = team;
    }
    
    // API methods
    /**
     * Get the Bukkit Team that this object represents.
     * 
     * @return The Bukkit Team object.
     */
    protected Team getTeam() {
        return team;
    }

    /**
     * Get the ScoreboardWrapper associated with this TeamWrapper.
     * 
     * @return The ScoreboardWrapper.
     */
    public ScoreboardWrapper getWrapper() {
        return wrapper;
    }
    
    /**
     * Get whether or not the Team represented by this class is still attached
     * to a valid Scoreboard.
     * 
     * @return <b>true</b> if the Team is still valid, <b>false</b> otherwise.
     */
    public boolean isValid() {
        return ScoreboardAPI.checkState(team);
    }

    /**
     * Clear the Team represented by this object. All entries associated with
     * the Team will have their team reset.
     */
    public void clear() {
        for (String entry : getEntries()) {
            team.removeEntry(entry);
        }
    }

    /**
     * Get the amount of players on this Team who are currently online.
     * 
     * @return The number of players currently online from this Team.
     */
    @SuppressWarnings("deprecation")
    public int getOnlineSize() {
        int size = 0;
        
        for (String entry : getEntries()) {
            if (Bukkit.getPlayerExact(entry) != null)
                size++;
        }
        
        return size;
    }

    /**
     * Get the sum of this Team's scores for the given objective name.
     * 
     * @param objective_name    The name of the Objective to use.
     * @return The sum of this Team's scores, or <code>Integer.MIN_VALUE</code>
     *         if no Objective is currently registered under the given name.
     */
    public int getTotalScores(String objective_name) {
        Set<String> entries = getEntries();
        ObjectiveWrapper objective = wrapper.getObjective(objective_name);
        
        if (objective == null)
            return Integer.MIN_VALUE;
        
        int total = 0;
        
        for (String teammate : entries) {
            int score = objective.getScore(teammate);
            
            if (score == Integer.MIN_VALUE)
                continue;
            
            total += score;
        }
        
        return total;
    }

    /**
     * Get the higher scoring Team of this team and another team. If the scores
     * are tied, null will be returned.
     * 
     * @param other_team        The TeamWrapper of the other Team.
     * @param objective_name    The name of the Objective to use.
     * @return The Team with the higher scores sum for the given Objective, or
     *         <b>null</b> if the Teams are tied.
     */
    public TeamWrapper getHigherScoringTeam(TeamWrapper other_team, String objective_name) {
        if (isHigherScoringThan(other_team, objective_name)) {
            return this;
        } else if (isEqualScoreWith(other_team, objective_name)) {
            return null;
        } else {
            return other_team;
        }
    }

    /**
     * Get if this TeamWrapper's Team has a higher total score for the given
     * Objective.
     * 
     * @param other_team        The TeamWrapper of the other Team.
     * @param objective_name    The name of the Objective to use.
     * @return <b>true</b> if this Team's score is higher than the other Team's,
     *         <b>false</b> otherwise.
     */
    public boolean isHigherScoringThan(TeamWrapper other_team, String objective_name) {
        int this_score = getTotalScores(objective_name);
        int other_score = other_team.getTotalScores(objective_name);
        
        if (this_score > other_score)
            return true;
        
        return false;
    }

    /**
     * Get if this TeamWrapper's Team has the exact same score for the given
     * Objective.
     * 
     * @param other_team        The TeamWrapper of the other Team.
     * @param objective_name    The name of the Objective to use.
     * @return <b>true</b> if this Team's score is equal to the other Team's,
     *         <b>false</b> otherwise.
     */
    public boolean isEqualScoreWith(TeamWrapper other_team, String objective_name) {
        int this_score = getTotalScores(objective_name);
        int other_score = other_team.getTotalScores(objective_name);
        
        if (this_score == other_score)
            return true;
        
        return false;
    }

    // Wrapper methods
    /**
     * Adds the given Player to the Team.
     * 
     * @param player    The Player who should be added.
     */
    public void addPlayer(Player player) {
        addEntry(player.getName());
    }

    /**
     * Adds the given entry to the Team.
     * 
     * @param entry     The entry that should be added.
     */
    public void addEntry(String entry) {
        team.addEntry(entry);
    }

    /**
     * Get whether or not the given Player is on the Team.
     * 
     * @param player    The Player to test.
     * @return <b>true</b> if the Player is on the Team, <b>false</b> otherwise.
     */
    public boolean hasPlayer(Player player) {
        return hasEntry(player.getName());
    }

    /**
     * Get whether or not the given entry is on the Team.
     * 
     * @param entry     The entry to test.
     * @return <b>true</b> if the Player is on the Team, <b>false</b> otherwise.
     */
    public boolean hasEntry(String entry) {
        return team.hasEntry(entry);
    }

    /**
     * Remove the given Player from the Team.
     * 
     * @param player    The Player to remove.
     * @return <b>true</b> if the Team has been changed as a result of this
     *         call, <b>false</b> otherwise.
     */
    public boolean removePlayer(Player player) {
        return removeEntry(player.getName());
    }

    /**
     * Remove the given entry from the Team.
     * 
     * @param entry     The entry to remove.
     * @return <b>true</b> if the Team has been changed as a result of this
     *         call, <b>false</b> otherwise.
     */
    public boolean removeEntry(String entry) {
        if (team.hasEntry(entry)) {
            team.removeEntry(entry);
            return true;
        }
        
        return false;
    }

    /**
     * Get all of the entries currently associated with the Team.
     * 
     * @return A Set containing all entries on the Team.
     */
    public Set<String> getEntries() {
        return team.getEntries();
    }

    /**
     * Get the size of the Team.
     * 
     * @return The size of the Team.
     */
    public int getSize() {
        return team.getSize();
    }

    /**
     * Set this Team's allow friendly fire flag.
     * 
     * @param set   The value to set.
     */
    public void setAllowFriendlyFire(boolean set) {
        team.setAllowFriendlyFire(set);
    }

    /**
     * Set this Team's see friendly invisibles flag.
     * 
     * @param set   The value to set.
     */
    public void setCanSeeInvisibles(boolean set) {
        team.setCanSeeFriendlyInvisibles(set);
    }

    /**
     * Set this Team's display prefix.
     * 
     * @param prefix    The prefix to set.
     */
    public void setDisplayPrefix(String prefix) {
        team.setPrefix(prefix);
    }

    /**
     * Set this Team's display suffix.
     * 
     * @param suffix    The suffix to set.
     */
    public void setDisplaySuffix(String suffix) {
        team.setSuffix(suffix);
    }

    /**
     * Get the Team's name, as it was registered with the Scoreboard. This is
     * not the name that Players see in-game.
     * 
     * @return The code name for the Team.
     */
    public String getCodeName() {
        return team.getName();
    }

    /**
     * Get the Team's display name; this is the name that Players see in-game.
     * 
     * @return The display name for the Team.
     */
    public String getDisplayName() {
        return team.getDisplayName();
    }
    
    /**
     * Set the Team's display name; this is the name that Players see in-game.
     * 
     * @param name  The display name to set for the Team.
     */
    public void setDisplayName(String name) {
        team.setDisplayName(name);
    }
    
    /**
     * Unregister the Team from its Scoreboard. This TeamWrapper and it's Team
     * will no longer be valid after this call returns.
     */
    public void unregister() {
        team.unregister();
    }
}
