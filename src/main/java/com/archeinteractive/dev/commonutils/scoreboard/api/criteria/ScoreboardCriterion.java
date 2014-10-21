package com.archeinteractive.dev.commonutils.scoreboard.api.criteria;


/**
 * An interface implemented by Scoreboard criteria
 */
public interface ScoreboardCriterion {

    /**
     * Get the String representation of this criterion, formatted the same as
     * the /scoreboard command would require it to be.
     * 
     * @return The String representation of this criterion.
     */
    public String getCriterionString();
}
