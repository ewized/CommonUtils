package com.gmail.favorlock.commonutils.scoreboard.api;

public enum ScoreboardEvent {

    SCORE_CHANGE(ScoreboardScoreChangeEvent.class),
    ;
    
    private Class<?> event_class;
    
    private ScoreboardEvent(Class<?> event_class) {
        this.event_class = event_class;
    }
    
    public Class<?> getEventClass() {
        return event_class;
    }
}
