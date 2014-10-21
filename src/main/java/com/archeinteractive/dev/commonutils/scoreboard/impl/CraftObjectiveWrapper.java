package com.archeinteractive.dev.commonutils.scoreboard.impl;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.archeinteractive.dev.commonutils.scoreboard.api.ScoreboardAPI;
import com.archeinteractive.dev.commonutils.scoreboard.api.wrappers.ObjectiveWrapper;
import com.archeinteractive.dev.commonutils.scoreboard.api.wrappers.ScoreboardWrapper;
import com.archeinteractive.dev.commonutils.scoreboard.api.wrappers.TeamWrapper;

/**
 * A wrapper for a Bukkit scoreboard Objective, providing API methods for
 * dealing with Scoreboard Objectives.
 */
public class CraftObjectiveWrapper implements ObjectiveWrapper, Objective {

    private final ScoreboardWrapper wrapper;
    private Objective objective;
    /** @deprecated NEVER call methods on this reference, will cause recursion. */
    private Objective proxy;
    
    protected CraftObjectiveWrapper(ScoreboardWrapper wrapper, Objective objective) {
        if (wrapper == null)
            throw new IllegalArgumentException("Wrapper cannot be null!");
        
        if (objective == null)
            throw new IllegalArgumentException("Objective cannot be null!");
        
        this.wrapper = wrapper;
        this.objective = objective;
        this.proxy = null;
    }
    
    // API methods
    /**
     * Get the Bukkit Objective that this object represents.
     * 
     * @return The Bukkit Objective object.
     */
    public Objective getObjective() {
        return proxy;
    }
    
    protected CraftObjectiveWrapper setProxy(Objective proxy) {
        this.proxy = proxy;
        return this;
    }

    /**
     * Get the ScoreboardWrapper associated with this ObjectiveWrapper.
     * 
     * @return The ScoreboardWrapper.
     */
    public ScoreboardWrapper getWrapper() {
        return wrapper;
    }
    
    /**
     * Get whether or not the Objective represented by this class is still
     * attached to a valid Scoreboard.
     * 
     * @return <b>true</b> if the Objective is still valid, <b>false</b>
     *         otherwise.
     */
    public boolean isValid() {
        return ScoreboardAPI.checkState(objective);
    }
    
    /**
     * Format an extended Scoreboard entry, using a team's prefix and suffix
     * ability. This can allow for a Scoreboard entry to have a maximum of 48
     * characters.
     * 
     * @param prefix_16 The prefix, can be up to 16 characters.
     * @param entry_16  The main entry, 16 char limit; this is also the
     *                  name of the team that will be changed by this operation.
     * @param suffix_16 The suffix, can be up to 16 characters.
     * @param score     The score to set this entry as.
     */
    public void formatExtended(String prefix_16, String entry_16, String suffix_16, int score) {
        formatExtended(entry_16, prefix_16, entry_16, suffix_16, score);
    }
    
    /**
     * Format an extended Scoreboard entry, using a team's prefix and suffix
     * ability. This can allow for a Scoreboard entry to have a maximum of 48
     * characters.
     * 
     * @param team_name The name of the team that should be used.
     * @param prefix_16 The prefix, can be up to 16 characters.
     * @param entry_16  The main entry, 16 char limit.
     * @param suffix_16 The suffix, can be up to 16 characters.
     * @param score     The score to set this entry as.
     */
    public void formatExtended(String team_name, String prefix_16, String entry_16, String suffix_16, int score) {
        if (prefix_16.length() > 16 || suffix_16.length() > 16 || entry_16.length() > 16)
            throw new IllegalArgumentException("Cannot use a string longer than 16 characters!");
        
        TeamWrapper team = wrapper.registerTeam(team_name);
        team.addEntry(entry_16);
        team.setDisplayPrefix(prefix_16);
        team.setDisplaySuffix(suffix_16);
        objective.getScore(entry_16).setScore(score);
    }
    
    /**
     * Sort through all of the entries (in O(n) time) currently associated with
     * the Objective and compile a Set with the entries that have the highest
     * score.
     * <p/>
     * The returned set is likely to have size 1, however if there is a tie it
     * can be larger, and if there are no entries currently associated with the
     * Scoreboard, it will have a size of 0.
     * 
     * @return The entries with the highest score.
     */
    public Set<String> getHighestScore() {
        return getHighestScore(wrapper.getEntrySet());
    }
    
    /**
     * Sort through the given entries (in O(n) time) and compile a Set with the
     * entries that have the highest score.
     * <p/>
     * The returned set is likely to have size 1, however if there is a tie it
     * can be larger, and if there are no entries currently associated with the
     * Scoreboard, it will have a size of 0.
     * 
     * @return The entries with the highest score.
     */
    public Set<String> getHighestScore(Set<String> entries) {
        Set<String> winning = new HashSet<>();
        int highest = Integer.MIN_VALUE;
        
        for (String entry : entries) {
            int score = getScoreFor(entry);
            if (score > highest) {
                highest = score;
                winning.clear();
                winning.add(entry);
            } else if (score == highest) {
                winning.add(entry);
            }
        }
        
        return winning;
    }

    /**
     * Increment the given Player's score by one for the Objective represented
     * by this object, returning their previous value.
     * 
     * @param player    The Player whose score should be incremented.
     * @return The previous score associated with this Player.
     */
    public int incrementScore(Player player) {
        return deltaScore(player, 1);
    }

    /**
     * Increment the given entry's score by one for the Objective represented
     * by this object, returning its previous value.
     * 
     * @param entry     The entry whose score should be incremented.
     * @return The previous score associated with this entry.
     */
    public int incrementScore(String entry) {
        return deltaScore(entry, 1);
    }

    /**
     * Decrement the given Player's score by one for the Objective represented
     * by this object, returning their previous value.
     * 
     * @param player    The Player whose score should be decremented.
     * @return The previous score associated with this Player.
     */
    public int decrementScore(Player player) {
        return deltaScore(player, -1);
    }

    /**
     * Decrement the given entry's score by one for the Objective represented
     * by this object, returning its previous value.
     * 
     * @param entry     The entry whose score should be decremented.
     * @return The previous score associated with this entry.
     */
    public int decrementScore(String entry) {
        return deltaScore(entry, -1);
    }

    /**
     * Change the given Player's score by the given amount for the Objective
     * represented by this object, returning its previous value.
     * 
     * @param player    The Player whose score should be changed.
     * @param amount    The amount that their score should be changed by.
     * @return The previous score associated with this Player.
     */
    public int deltaScore(Player player, int amount) {
        return deltaScore(player.getName(), 1);
    }

    /**
     * Change the given entry's score by the given amount for the Objective
     * represented by this object, returning its previous value.
     * 
     * @param entry     The entry whose score should be changed.
     * @param amount    The amount that their score should be changed by.
     * @return The previous score associated with this entry.
     */
    public int deltaScore(String entry, int amount) {
        int oldscore = getScoreFor(entry);
        int newscore = oldscore == Integer.MIN_VALUE ? amount : oldscore + amount;
        
        return setScoreFor(entry, newscore);
    }
    
    // Wrapper methods
    /**
     * Get the String value of the criterion that the Objective uses.
     * 
     * @return  The value of the criterion tracked by the Objective.
     */
    public String getCriterion() {
        return objective.getCriteria();
    }
    
    /**
     * Get the Objective's name, as it was registered with the Scoreboard. This
     * is not the name that Players see in-game.
     * 
     * @return The code name for the Objective.
     */
    public String getCodeName() {
        return objective.getName();
    }
    
    /**
     * Get the display name of the Objective represented by this object.
     * 
     * @return  The current display name of the Objective.
     */
    public String getDisplay() {
        return objective.getDisplayName();
    }
    
    /**
     * Set the display name of the Objective represented by this object.
     * 
     * @param name  The new display name to set.
     */
    public void setDisplay(String name) {
        objective.setDisplayName(name);
    }
    
    /**
     * Get the score associated with the given Player for the Objective
     * represented by this object.
     * 
     * @param player    The Player whose score should be returned.
     * @return The score that was found, or <code>Integer.MIN_VALUE</code> if
     *         an error occurred.
     */
    public int getScoreFor(Player player) {
        return getScoreFor(player.getName());
    }
    
    /**
     * Get the score associated with the given entry for the Objective
     * represented by this object.
     * 
     * @param entry The entry whose score should be returned.
     * @return The score that was found, or <code>Integer.MIN_VALUE</code> if
     *         an error occurred.
     */
    public int getScoreFor(String entry) {
        try {
            return objective.getScore(entry).getScore();
        } catch (IllegalStateException e) {
            return Integer.MIN_VALUE;
        }
    }
    
    /**
     * Set a Player's score to the given value, returning their previous score.
     * 
     * @param player    The Player whose score should be changed.
     * @param value     The value that the Player's score should be set to.
     * @return The previous score associated with this Player.
     */
    public int setScoreFor(Player player, int value) {
        return setScoreFor(player.getName(), value);
    }

    /**
     * Set an entry's score to the given value, returning its previous value.
     * 
     * @param entry     The entry whose score should be changed.
     * @param value     The value that the entry's score should be set to.
     * @return The previous score associated with this entry.
     */
    public int setScoreFor(String entry, int value) {
        int prev = getScoreFor(entry);
        
        objective.getScore(entry).setScore(value);
        return prev;
    }
    
    /**
     * Set the Objective's display slot to the side bar slot.
     */
    public void setDisplaySlotSideBar() {
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Set the Objective's display slot to the below name slot.
     */
    public void setDisplaySlotBelowName() {
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    /**
     * Set the Objective's display slot to the player list slot.
     */
    public void setDisplaySlotPlayerList() {
        objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }
    
    /**
     * Unregister the Objective from its Scoreboard. This ObjectiveWrapper and
     * it's Objective will no longer be valid after this call returns.
     */
    public void unregisterComponent() {
        objective.unregister();
    }
    
    // Must override equals for compatibility
    public boolean equals(Object obj) {
        return objective.equals(obj);
    }
    
    // Bukkit Objective delegate methods
    public String getCriteria() throws IllegalStateException {
        return objective.getCriteria();
    }

    public String getDisplayName() throws IllegalStateException {
        return objective.getDisplayName();
    }

    public DisplaySlot getDisplaySlot() throws IllegalStateException {
        return objective.getDisplaySlot();
    }

    public String getName() throws IllegalStateException {
        return objective.getName();
    }

    @Deprecated
    public Score getScore(OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
        return objective.getScore(player);
    }

    public Score getScore(String entry) throws IllegalArgumentException, IllegalStateException {
        return objective.getScore(entry);
    }

    public Scoreboard getScoreboard() {
        return objective.getScoreboard();
    }

    public boolean isModifiable() throws IllegalStateException {
        return objective.isModifiable();
    }

    public void setDisplayName(String name) throws IllegalStateException, IllegalArgumentException {
        objective.setDisplayName(name);
    }

    public void setDisplaySlot(DisplaySlot slot) throws IllegalStateException {
        objective.setDisplaySlot(slot);
    }

    public void unregister() throws IllegalStateException {
        objective.unregister();
    }
}
