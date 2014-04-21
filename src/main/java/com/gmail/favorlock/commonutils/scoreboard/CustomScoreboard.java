package com.gmail.favorlock.commonutils.scoreboard;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

public class CustomScoreboard extends ScoreboardWrapper {

    private static Map<String, CustomScoreboard> registry = new HashMap<>();
    
    private CustomScoreboard(String label) {
        super(Bukkit.getScoreboardManager().getNewScoreboard(), label, false);
    }
    
    
    /**
     * Gets the CustomScoreboard currently registered under the given label, or
     * creates and registers one if one isn't present.
     * 
     * @param label  The name to lookup / register under.
     * @return The CustomScoreboard that was found or created.
     */
    public static CustomScoreboard forLabel(String label) {
        CustomScoreboard scoreboard = registry.get(label);
        
        if (scoreboard == null) {
            scoreboard = new CustomScoreboard(label);
            registry.put(label, scoreboard);
        }
        
        return scoreboard;
    }
    
    /**
     * Gets whether or not there is currently a CustomScoreboard registered
     * under the given label.
     * 
     * @param name  The label to lookup.
     * @return <b>true</b> if there is a CustomScoreboard registered under the
     *         given name, <b>false</b> otherwise.
     */
    public static boolean isCustomScoreboardRegistered(String label) {
        return registry.containsKey(label);
    }
}
