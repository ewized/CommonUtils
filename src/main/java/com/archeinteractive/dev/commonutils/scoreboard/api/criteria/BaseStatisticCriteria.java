package com.archeinteractive.dev.commonutils.scoreboard.api.criteria;


/**
 * Represents Scoreboard criteria that are tracked by the server, and are
 * controlled by their respective Statistics.
 */
public enum BaseStatisticCriteria implements ScoreboardCriterion {

    ANIMALS_BRED("animalsBred"),
    BOAT_ONE_CM("boat_ONE_CM"),
    CLIMB_ONE_CM("climb_ONE_CM"),
    CROUCH_ONE_CM("crouch_ONE_CM"),
    DAMAGE_DEALT("damageDealt"),
    DAMAGE_TAKEN("damageTaken"),
    DEATHS("deaths"),
    DIVE_ONE_CM("dive_ONE_CM"),
    DROP("drop"),
    FALL_ONE_CM("fall_ONE_CM"),
    FISH_CAUGHT("fishCaught"),
    FLY_ONE_CM("fly_ONE_CM"),
    HORSE_ONE_CM("horse_ONE_CM"),
    JUMP("jump"),
    JUNK_FISHED("junkFished"),
    LEAVE_GAME("leaveGame"),
    MINECART_ONE_CM("minecart_ONE_CM"),
    MOB_KILLS("mobKills"),
    PIG_ONE_CM("pig_ONE_CM"),
    PLAYER_KILLS("playerKills"),
    PLAY_ONE_MINUTE("playOneMinute"),
    SPRINT_ONE_CM("sprint_ONE_CM"),
    SWIM_ONE_CM("swim_ONE_CM"),
    TIME_SINCE_DEATH("timeSinceDeath"),
    TREASURE_FISHED("treasureFished"),
    WALK_ONE_CM("walk_ONE_CM"),
    ;
    
    private static final String base = "stat.";
    
    private final String criterion;
    
    private BaseStatisticCriteria(String criterion) {
        this.criterion = base + criterion;
    }
    
    public String getCriterionString() {
        return criterion;
    }
}
