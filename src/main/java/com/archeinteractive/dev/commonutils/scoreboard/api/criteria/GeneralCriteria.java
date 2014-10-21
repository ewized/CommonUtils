package com.archeinteractive.dev.commonutils.scoreboard.api.criteria;


/**
 * Represents the original Scoreboard criteria.
 */
public enum GeneralCriteria implements ScoreboardCriterion {

    DEATHS("deathCount"),
    KILLS_ENTITY("totalKillCount"),
    KILLS_PLAYER("playerKillCount"),
    HEALTH("health"),
    TRIGGER("trigger"),
    DUMMY("dummy"),
    ;
    
    private final String criteria;
    
    private GeneralCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getCriterionString() {
        return criteria;
    }
}
