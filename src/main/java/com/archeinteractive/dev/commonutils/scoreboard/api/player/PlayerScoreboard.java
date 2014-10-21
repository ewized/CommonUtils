package com.archeinteractive.dev.commonutils.scoreboard.api.player;

import java.lang.ref.WeakReference;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.archeinteractive.dev.commonutils.scoreboard.api.ScoreboardAPI;
import com.archeinteractive.dev.commonutils.scoreboard.api.wrappers.ScoreboardWrapper;

/**
 * A Class for representing a ScoreboardWrapper that is designed for a specific
 * Player.
 */
public class PlayerScoreboard {

    private final ScoreboardWrapper scoreboard;
    private final WeakReference<Player> player;
    private final UUID uuid;
    private final String name;
    
    /**
     * Create a new PlayerScoreboard for the given player, specifying whether or
     * not the Scoreboard should be set to the given Player.
     * 
     * @param player          The Player to create this PlayerScoreboard for.
     * @param set_immediately Whether or not to set the created scoreboard to the Player.
     */
    public PlayerScoreboard(Player player, boolean set_immediately) {
        if (player == null)
            throw new IllegalArgumentException("The given Player cannot be null!");

        this.scoreboard = ScoreboardAPI.getCustomScoreboard(player.getUniqueId().toString());
        this.player = new WeakReference<>(player);
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        
        if (set_immediately) {
            scoreboard.setFor(player);
        }
    }
    
    /**
     * Create a new PlayerScoreboard for the given player. The created
     * Scoreboard will automatically be set to the given Player.
     * 
     * @param player The Player to create this PlayerScoreboard for.
     */
    public PlayerScoreboard(Player player) {
        this(player, true);
    }
    
    /**
     * Get the ScoreboardWrapper that was created or retrieved when this
     * PlayerScoreboard was instantiated.
     * 
     * @return The ScoreboardWrapper.
     */
    public ScoreboardWrapper getScoreboard() {
        return scoreboard;
    }
    
    /**
     * Get whether or not this PlayerScoreboard's Player is still logged on. If
     * a strong reference to this Player's instance is kept by any code, this
     * method may not exhibit the expected behavior.
     * 
     * @return <b>true</b> if this Player's instance is still a valid reference,
     *         <b>false</b> otherwise.
     */
    public boolean isValid() {
        return player.get() != null;
    }
    
    /**
     * Get the Player that this PlayerScoreboard was created with. This may be
     * null, if the associated Player's instance has been dereferenced.
     * 
     * @return The Player, or <b>null</b> if their Object has been gc'ed.
     */
    public Player getPlayer() {
        return player.get();
    }
    
    /**
     * Get the UUID of the Player that this PlayerScoreboard was created with.
     * This will <b>never</b> be null, even if the associated Player's instance
     * has been dereferenced.
     * 
     * @return The Player's UUID.
     */
    public UUID getPlayerUUID() {
        return uuid;
    }
    
    /**
     * Get the name of the Player that this PlayerScoreboard was created with.
     * This will <b>never</b> be null, even if the associated Player's instance
     * has been dereferenced.
     * 
     * @return The Player's name.
     */
    public String getPlayerName() {
        return name;
    }
    
    /**
     * Set the Player's currently active Scoreboard to the Scoreboard that was
     * created or retrieved when this PlayerScoreboard was created.
     * <p>
     * If the Player is no longer a valid reference, this call does nothing.
     */
    public void set() {
        Player player;
        
        if ((player = this.player.get()) != null) {
            scoreboard.setFor(player);
        }
    }
}
