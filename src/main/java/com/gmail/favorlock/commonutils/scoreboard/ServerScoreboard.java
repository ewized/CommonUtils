package com.gmail.favorlock.commonutils.scoreboard;

import org.bukkit.Bukkit;

public class ServerScoreboard extends ScoreboardWrapper {

    /**
     * Get a new ScoreboardWrapper representative of the server's main
     * Scoreboard.
     */
    public ServerScoreboard() {
        super(Bukkit.getScoreboardManager().getMainScoreboard(), null, true);
    }
}
