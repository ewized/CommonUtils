package com.gmail.favorlock.commonutils.scoreboard.api;

import com.gmail.favorlock.commonutils.scoreboard.ObjectiveWrapper;
import com.gmail.favorlock.commonutils.scoreboard.ScoreboardWrapper;

public class ScoreboardScoreChangeEvent {

    private final ScoreboardWrapper scoreboard;
    private final ObjectiveWrapper objective;
    private final String entry_name;
    private final int previous_score;
    private final int new_score;
    
    public ScoreboardScoreChangeEvent(ScoreboardWrapper scoreboard, ObjectiveWrapper objective, String entry_name, int previous_score, int new_score) {
        this.scoreboard = scoreboard;
        this.objective = objective;
        this.entry_name = entry_name;
        this.previous_score = previous_score;
        this.new_score = new_score;
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
    
    public int getPreviousScore() {
        return previous_score;
    }
    
    public int getNewScore() {
        return new_score;
    }
}
