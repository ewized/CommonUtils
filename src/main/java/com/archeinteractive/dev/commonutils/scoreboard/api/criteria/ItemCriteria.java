package com.archeinteractive.dev.commonutils.scoreboard.api.criteria;

import org.bukkit.Material;

/**
 * Represents Scoreboard criteria that are tracked by the server, and are
 * controlled by their respective item-related Statistics.
 */
public class ItemCriteria {

    private static final String base = "stat.";
    private static final String craft = "craftItem.";
    private static final String use = "useItem.";
    private static final String brek = "breakItem.";
    private static final String mine = "mineBlock.";
    
    private static class ItemCriterion implements ScoreboardCriterion {
    
        private final String criterion;
        
        private ItemCriterion(String super_criterion, String criterion) {
            this.criterion = base + super_criterion + criterion;
        }
        
        public String getCriterionString() {
            return criterion;
        }
    }
    
    @SuppressWarnings("deprecation")
    public static ScoreboardCriterion forCrafting(Material material) {
        return new ItemCriterion(craft, String.valueOf(material.getId()));
    }
    
    @SuppressWarnings("deprecation")
    public static ScoreboardCriterion forUsing(Material material) {
        return new ItemCriterion(use, String.valueOf(material.getId()));
    }
    
    @SuppressWarnings("deprecation")
    public static ScoreboardCriterion forBreaking(Material material) {
        return new ItemCriterion(brek, String.valueOf(material.getId()));
    }
    
    @SuppressWarnings("deprecation")
    public static ScoreboardCriterion forMining(Material material) {
        return new ItemCriterion(mine, String.valueOf(material.getId()));
    }
}
