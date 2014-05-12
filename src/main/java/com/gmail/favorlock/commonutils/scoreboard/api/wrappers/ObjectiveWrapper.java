package com.gmail.favorlock.commonutils.scoreboard.api.wrappers;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

/**
 * An interface implemented by ObjectiveProxy wrappers.
 */
public interface ObjectiveWrapper extends ScoreboardComponentWrapper {

    // Full API Methods
    /**
     * Get the Bukkit Objective that this object represents.
     * 
     * @return The Bukkit Objective object.
     */
    public Objective getObjective();
    
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
    public Set<String> getHighestScore();
    
    /**
     * Format an extended Scoreboard entry, using a team's prefix and suffix
     * ability. This can allow for a Scoreboard entry to have a maximum of 80
     * characters.
     * 
     * @param prefix_32 The prefix, can be up to 32 characters.
     * @param entry_16  The main entry, 16 char limit; this is also the
     *                  name of the team that will be changed by this operation.
     * @param suffix_32 The suffix, can be up to 32 characters.
     * @param score     The score to set this entry as.
     */
    public void formatExtended(String prefix_32, String entry_16, String suffix_32, int score);
    
    /**
     * Format an extended Scoreboard entry, using a team's prefix and suffix
     * ability. This can allow for a Scoreboard entry to have a maximum of 80
     * characters.
     * 
     * @param team_name The name of the team that should be used.
     * @param prefix_32 The prefix, can be up to 32 characters.
     * @param entry_16  The main entry, 16 char limit.
     * @param suffix_32 The suffix, can be up to 32 characters.
     * @param score     The score to set this entry as.
     */
    public void formatExtended(String team_name, String prefix_32, String entry_16, String suffix_32, int score);
    
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
    public Set<String> getHighestScore(Set<String> entries);
    
    /**
     * Increment the given Player's score by one for the Objective represented
     * by this object, returning their previous value.
     * 
     * @param player    The Player whose score should be incremented.
     * @return The previous score associated with this Player.
     */
    public int incrementScore(Player player);
    
    /**
     * Increment the given entry's score by one for the Objective represented
     * by this object, returning its previous value.
     * 
     * @param entry     The entry whose score should be incremented.
     * @return The previous score associated with this entry.
     */
    public int incrementScore(String entry);
    
    /**
     * Decrement the given Player's score by one for the Objective represented
     * by this object, returning their previous value.
     * 
     * @param player    The Player whose score should be decremented.
     * @return The previous score associated with this Player.
     */
    public int decrementScore(Player player);
    
    /**
     * Decrement the given entry's score by one for the Objective represented
     * by this object, returning its previous value.
     * 
     * @param entry     The entry whose score should be decremented.
     * @return The previous score associated with this entry.
     */
    public int decrementScore(String entry);
    
    /**
     * Change the given Player's score by the given amount for the Objective
     * represented by this object, returning its previous value.
     * 
     * @param player    The Player whose score should be changed.
     * @param amount    The amount that their score should be changed by.
     * @return The previous score associated with this Player.
     */
    public int deltaScore(Player player, int amount);
    
    /**
     * Change the given entry's score by the given amount for the Objective
     * represented by this object, returning its previous value.
     * 
     * @param entry     The entry whose score should be changed.
     * @param amount    The amount that their score should be changed by.
     * @return The previous score associated with this entry.
     */
    public int deltaScore(String entry, int amount);
    
    // Wrapper Methods
    /**
     * Get the String value of the criterion that the Objective uses.
     * 
     * @return  The value of the criterion tracked by the Objective.
     */
    public String getCriterion();
    
    /**
     * Get the score associated with the given Player for the Objective
     * represented by this object.
     * 
     * @param player    The Player whose score should be returned.
     * @return The score that was found, or <code>Integer.MIN_VALUE</code> if
     *         an error occurred.
     */
    public int getScoreFor(Player player);
    
    /**
     * Get the score associated with the given entry for the Objective
     * represented by this object.
     * 
     * @param entry The entry whose score should be returned.
     * @return The score that was found, or <code>Integer.MIN_VALUE</code> if
     *         an error occurred.
     */
    public int getScoreFor(String entry);
    
    /**
     * Set a Player's score to the given value, returning their previous score.
     * 
     * @param player    The Player whose score should be changed.
     * @param value     The value that the Player's score should be set to.
     * @return The previous score associated with this Player.
     */
    public int setScoreFor(Player player, int value);
    
    /**
     * Set an entry's score to the given value, returning its previous value.
     * 
     * @param entry     The entry whose score should be changed.
     * @param value     The value that the entry's score should be set to.
     * @return The previous score associated with this entry.
     */
    public int setScoreFor(String entry, int value);
    
    /**
     * Set the Objective's display slot to the side bar slot.
     */
    public void setDisplaySlotSideBar();
    
    /**
     * Set the Objective's display slot to the below name slot.
     */
    public void setDisplaySlotBelowName();
    
    /**
     * Set the Objective's display slot to the player list slot.
     */
    public void setDisplaySlotPlayerList();
}
