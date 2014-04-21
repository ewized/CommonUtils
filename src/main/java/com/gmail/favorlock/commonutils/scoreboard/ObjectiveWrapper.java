package com.gmail.favorlock.commonutils.scoreboard;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import com.gmail.favorlock.commonutils.scoreboard.api.ScoreboardAPI;

/**
 * A wrapper for a Bukkit scoreboard Objective, providing API methods for
 * dealing with Scoreboard Objectives.
 */
public class ObjectiveWrapper {

    private final ScoreboardWrapper wrapper;
    private final Objective objective;
    
    protected ObjectiveWrapper(ScoreboardWrapper wrapper, Objective objective) {
        if (objective == null)
            throw new IllegalArgumentException("Objective cannot be null!");
        
        this.wrapper = wrapper;
        this.objective = objective;
    }
    
    // API methods
    /**
     * Get the Bukkit Objective that this object represents.
     * 
     * @return The Bukkit Objective object.
     */
    protected Objective getObjective() {
        return objective;
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
        return getHighestScore(wrapper.getEntries());
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
            int score = getScore(entry);
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
        int oldscore = getScore(entry);
        int newscore = oldscore == Integer.MIN_VALUE ? amount : oldscore + amount;
        
        return setScore(entry, newscore);
    }
    
    // Wrapper methods
    /**
     * Get the String value of the criterion that the Objective uses.
     * 
     * @return  The value of the criterion tracked by the Objective.
     */
    public String getCriteria() {
        return objective.getCriteria();
    }
    
    public String getCodeName() {
        return objective.getName();
    }
    
    /**
     * Get the display name of the Objective represented by this object.
     * 
     * @return  The current display name of the Objective.
     */
    public String getDisplayName() {
        return objective.getDisplayName();
    }
    
    /**
     * Set the display name of the Objective represented by this object.
     * 
     * @param name  The new display name to set.
     */
    public void setDisplayName(String name) {
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
    public int getScore(Player player) {
        return getScore(player.getName());
    }
    
    /**
     * Get the score associated with the given entry for the Objective
     * represented by this object.
     * 
     * @param entry The entry whose score should be returned.
     * @return The score that was found, or <code>Integer.MIN_VALUE</code> if
     *         an error occurred.
     */
    public int getScore(String entry) {
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
    public int setScore(Player player, int value) {
        return setScore(player.getName(), value);
    }

    /**
     * Set an entry's score to the given value, returning its previous value.
     * 
     * @param entry     The entry whose score should be changed.
     * @param value     The value that the entry's score should be set to.
     * @return The previous score associated with this entry.
     */
    public int setScore(String entry, int value) {
        int prev = getScore(entry);
        
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
    public void unregister() {
        objective.unregister();
    }
}
