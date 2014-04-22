package com.gmail.favorlock.commonutils.scoreboard.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ObjectiveWrapper;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ScoreboardWrapper;

public class ScoreboardScoreChangeEvent extends Event {

    private static HandlerList handlers = new HandlerList();
    
    private final ScoreboardWrapper scoreboard;
    private final ObjectiveWrapper objective;
    private final String entry_name;
    private final int old_score;
    private final int new_score;

    public ScoreboardScoreChangeEvent(ScoreboardWrapper scoreboard, ObjectiveWrapper objective, String entry_name, int oldscore, int newscore) {
        this.scoreboard = scoreboard;
        this.objective = objective;
        this.entry_name = entry_name;
        this.old_score = oldscore;
        this.new_score = newscore;
    }
    
    public ScoreboardWrapper getScoreboard() {
        return scoreboard;
    }
    
    public ObjectiveWrapper getObjective() {
        return objective;
    }
    
    public String getEntryName() {
        return entry_name;
    }
    
    public int getOldScore() {
        return old_score;
    }
    
    public int getNewScore() {
        return new_score;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
