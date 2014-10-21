package com.archeinteractive.dev.commonutils.scoreboard.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.archeinteractive.dev.commonutils.scoreboard.api.wrappers.ObjectiveWrapper;
import com.archeinteractive.dev.commonutils.scoreboard.api.wrappers.ScoreboardWrapper;

/**
 * Represents when an Objective's Score for some entry changes on a
 * ScoreboardAPI Scoreboard.
 */
public class ScoreboardScoreChangeEvent extends Event implements Cancellable {

    private static HandlerList handlers = new HandlerList();
    
    private final ObjectiveWrapper objective;
    private final String entry_name;
    private final int old_score;
    private final int new_score;
    private boolean cancelled;
    
    /**
     * Create a new ScoreboardScoreChangeEvent with the given parameters.
     */
    public ScoreboardScoreChangeEvent(ObjectiveWrapper objective, String entry_name, int oldscore, int newscore) {
        this.objective = objective;
        this.entry_name = entry_name;
        this.old_score = oldscore;
        this.new_score = newscore;
        this.cancelled = false;
    }
    
    /**
     * Get the ScoreboardWrapper that this score change took place in.
     * 
     * @return The ScoreboardWrapper whose Objective has had a score change.
     */
    public ScoreboardWrapper getScoreboard() {
        return objective.getWrapper();
    }

    /**
     * Get the ObjectiveWrapper that this score change took place in.
     * 
     * @return The ObjectiveWrapper whose score has changed.
     */
    public ObjectiveWrapper getObjective() {
        return objective;
    }

    /**
     * Get the entry for whom the score has changed.
     * 
     * @return The entry whose score has changed.
     */
    public String getEntryName() {
        return entry_name;
    }

    /**
     * Get the old score value.
     * 
     * @return The old score.
     */
    public int getOldScore() {
        return old_score;
    }

    /**
     * Get the new score value.
     * 
     * @return The new score.
     */
    public int getNewScore() {
        return new_score;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
