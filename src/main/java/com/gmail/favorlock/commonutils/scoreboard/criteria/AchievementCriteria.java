package com.gmail.favorlock.commonutils.scoreboard.criteria;


public enum AchievementCriteria implements ScoreboardCriterion {

    ACQUIRE_HARDWARE("acquireIron"),
    THE_LIE("bakeCake"),
    INTO_FIRE("blazeRod"),
    LIBRARIAN("bookcase"),
    REPOPULATION("breedCow"),
    GETTING_AN_UPGRADE("buildBetterPickaxe"),
    HOT_TOPIC("buildFurnace"),
    TIME_TO_FARM("buildHoe"),
    TIME_TO_MINE("buildPickaxe"),
    TIME_TO_STRIKE("buildSword"),
    BENCHMARKING("buildWorkBench"),
    DELICIOUS_FISH("cookFish"),
    DIAMONDS("diamonds"),
    DIAMONDS_TO_YOU("diamondsToYou"),
    ENCHANTER("enchantments"),
    ADVENTURING_TIME("exploreAllBiomes"),
    WHEN_PIGS_FLY("flyPig"),
    BEACONATOR("fullBeacon"),
    RETURN_TO_SENDER("ghast"),
    COW_TIPPER("killCow"),
    MONSTER_HUNTER("killEnemy"),
    THE_BEGINNING2("killWither"),
    BAKE_BREAD("makeBread"),
    GETTING_WOOD("mineWood"),
    ON_A_RAIL("onARail"),
    TAKING_INVENTORY("openInventory"),
    OVERKILL("overkill"),
    OVERPOWERED("overpowered"),
    WE_NEED_TO_GO_DEEPER("portal"),
    LOCAL_BREWERY("potion"),
    SNIPER_DUEL("snipeSkeleton"),
    THE_BEGINNING1("spawnWither"),
    THE_END1("theEnd"),
    THE_END2("theEnd2"),
    ;
    
    private static final String base = "achievement.";
    
    private final String criterion;
    
    private AchievementCriteria(String criterion) {
        this.criterion = base + criterion;
    }
    
    public String getCriterionString() {
        return criterion;
    }
}
