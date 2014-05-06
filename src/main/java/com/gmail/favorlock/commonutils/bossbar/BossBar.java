package com.gmail.favorlock.commonutils.bossbar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.favorlock.commonutils.CommonUtils;
import com.gmail.favorlock.commonutils.bossbar.version.v1_7.Dragon;
import com.gmail.favorlock.commonutils.entity.EntityHandler;

public class BossBar {

    private static final int DRAGON_HEIGHT_OFFSET = -512;
    // Maps player name to their current FakeDragon
    private static final Map<String, FakeDragon> playerBars = new HashMap<String, FakeDragon>();
    // Maps player name to the task ID of their ticking bar, if applicable
    private static final Map<String, Integer> tickingBars = new HashMap<String, Integer>();
    
    /**
     * Get whether or not a player has a bar currently registered.
     *
     * @param player    The Player to test.
     * @return <b>true</b> if they have a bar registered, <b>false</b>
     *         otherwise.
     */
    public static boolean hasBar(Player player) {
        return playerBars.get(player.getName()) != null;
    }
    
    /**
     * Get whether or not an offline player has a bar currently registered.
     * 
     * @param player    The OfflinePlayer to test.
     * @return <b>true</b> if they have a bar registered, <b>false</b>
     *         otherwise.
     */
    public static boolean hasBar(OfflinePlayer player) {
        return playerBars.get(player.getName()) != null;
    }
    
    /**
     * Get whether or not an entry has a bar currently registered.
     * 
     * @param entry The entry (name) to test.
     * @return <b>true</b> if they have a bar registered, <b>false</b>
     *         otherwise.
     */
    public static boolean hasBar(String entry) {
        return playerBars.get(entry) != null;
    }
    
    /**
     * Get whether or not a player has a bar currently displayed that has an active ticking timer.
     *
     * @param player Player to test.
     * @return true if they have a bar displayed with a timed task, false otherwise.
     */
    public static boolean hasTickingBar(Player player) {
        return tickingBars.containsKey(player.getName());
    }
    
    /**
     * Removes a player's bar, if they have one. If removing
     * an offline player's bar is desired, use
     * {@link BossBar#removeBar(OfflinePlayer)}.
     * 
     * @see {@link BossBar#removeBar(OfflinePlayer)}, {@link BossBar#removeBar(String)}
     * 
     * @param player Player whose bar to remove.
     * @return <b>true</b> if the player had a bar, <b>false</b> otherwise.
     */
    public static boolean removeBar(Player player) {
        if (!hasBar(player))
            return false;
        
        EntityHandler.sendPacket(player, getBarDragon(player, "").getDestroyPacket());
        playerBars.remove(player.getName());
        stopTickingBar(player.getName());
        return true;
    }
    
    /**
     * Removes a offline player's bar, if they have one.
     * 
     * @see {@link BossBar#removeBar(Player)}, {@link BossBar#removeBar(String)}
     * 
     * @param player
     * @return <b>true</b> if the player had a bar, <b>false</b> otherwise.
     */
    public static boolean removeBar(OfflinePlayer player) {
        if (!hasBar(player))
            return false;
        
        Player online_player = player.getPlayer();
        
        if (online_player != null) {
            return removeBar(online_player);
        } else {
            playerBars.remove(player.getName());
            stopTickingBar(player.getName());
            return true;
        }
    }
    
    /**
     * Remove an entry's bar, if they have one.
     * 
     * @see {@link BossBar#removeBar(Player)}, {@link BossBar#removeBar(String)}
     * 
     * @param entry The entry to remove.
     * @return <b>true</b> if the player had a bar, <b>false</b> otherwise.
     */
    public static boolean removeBar(String entry) {
        if (!hasBar(entry))
            return false;
        
        Player player = Bukkit.getPlayer(entry);
        
        if (player != null) {
            return removeBar(player);
        } else {
            playerBars.remove(entry);
            stopTickingBar(entry);;
            return true;
        }
    }
    
    /**
     * Remove all online player's bars.
     * 
     * @see {@link BossBar#removeOfflineBars()}, {@link BossBar#removeAllBars()}
     */
    public static void removeOnlineBars() {
        for (Player player : Bukkit.getServer().getOnlinePlayers())
            removeBar(player);
    }
    
    /**
     * Removes all offline player's bars.
     * 
     * @see {@link BossBar#removeOnlineBars()}, {@link BossBar#removeAllBars()}
     */
    public static void removeOfflineBars() {
        for (Map.Entry<String, FakeDragon> entry : new HashSet<>(playerBars.entrySet())) {
            if (Bukkit.getPlayer(entry.getKey()) == null) {
                removeBar(entry.getKey());
            }
        }
    }
    
    /**
     * Removes all player's bars; every player who is online will have their bar
     * cleared appropriately, and afterwards all offline players' bars will be
     * cleared as well. If clearing only online or only offline players' bars is
     * desired, use {@link BossBar#removeOnlineBars()} or {@link BossBar#removeOfflineBars()}
     * 
     * @see {@link BossBar#removeOnlineBars()}, {@link BossBar#removeOfflineBars()}
     */
    public static void removeAllBars() {
        removeOnlineBars();
        removeOfflineBars();
    }
    
    /**
     * Assign a message bar to all online players with the specified message. The bar will be full.
     *
     * @param message String message to be displayed to all players.
     */
    public static void setAllMessageBar(String message) {
        for (Player player : Bukkit.getOnlinePlayers())
            setMessageBar(player, message);
    }
    
    /**
     * Assign a bar to a player, with the specified message. The bar will be full.
     *
     * @param player  Player to assign this bar to.
     * @param message String message to be displayed on the bar.
     */
    public static void setMessageBar(Player player, String message) {
        removeBar(player);
        
        FakeDragon dragon = getBarDragon(player, message);
        dragon.setName(message);
        dragon.setHealth(FakeDragon.MAX_HEALTH);
        stopTickingBar(player.getName());
        sendBarDragon(dragon, player);
    }
    
    /**
     * Assign a bar to all players, with the specified message and fill percentage. The percentage will not change unless manually updated.
     *
     * @param message String message to be displayed on the bar.
     * @param percent Int 0-100, how full the bar should be.
     */
    public static void setAllPercentBar(String message, int percent) {
        if ((percent < 0f) || (percent > 100f))
            percent = Math.min(100, Math.max(0, percent));
        
        for (Player player : Bukkit.getServer().getOnlinePlayers())
            setPercentBar(player, message, percent);
    }
    
    /**
     * Assign a bar to a player, with the specified message and fill percentage. The percentage will not change unless manually updated.
     *
     * @param player  Player to assign this bar to.
     * @param message String message to be displayed on the bar.
     * @param percent Int 0-100, how full the bar should be.
     */
    public static void setPercentBar(Player player, String message, int percent) {
        if ((percent < 0f) || (percent > 100f))
            percent = Math.min(100, Math.max(0, percent));
        
        removeBar(player);
        
        FakeDragon dragon = getBarDragon(player, message);
        dragon.setName(message);
        dragon.setHealth(percent);
        stopTickingBar(player.getName());
        sendBarDragon(dragon, player);
    }
    
    /**
     * Assign a timer bar (decrementing) to a player, with a specified message and number of seconds.
     *
     * @param player  Player to assign this bar to.
     * @param message String message to be displayed on the bar.
     * @param seconds Number of seconds for this bar to take to fully deplete.
     */
    public static void setTimerBarDecrement(Player player, String message, int seconds) {
        setTimerBarDecrement(player, message, new int[]{seconds, 20}, null);
    }
    
    /**
     * Assign a timer bar (incrementing) to a player, with a specified message and number of seconds.
     *
     * @param player  Player to assign this bar to.
     * @param message String message to be displayed on the bar.
     * @param seconds Number of seconds for this bar to take to fully fill.
     */
    public static void setTimerBarIncrement(Player player, String message, int seconds) {
        setTimerBarIncrement(player, message, new int[]{seconds, 20}, null);
    }
    
    /**
     * Assign a timer bar (decrementing) to a player, with a specified message, number of intervals and message to display upon completion.
     *
     * @param player          Player to assign this bar to.
     * @param message         String message to be displayed on the bar.
     * @param seconds         Number of seconds for this bar to take to fully deplete.
     * @param completeMessage String message to be displayed when the timer finishes.
     */
    public static void setTimerBarDecrement(Player player, String message, int seconds, String completeMessage) {
        setTimerBarDecrement(player, message, new int[]{seconds, 20}, completeMessage);
    }
    
    /**
     * Assign a timer bar (incrementing) to a player, with a specified message, number of intervals and message to display upon completion.
     *
     * @param player          Player to assign this bar to.
     * @param message         String message to be displayed on the bar.
     * @param seconds         Number of seconds for this bar to take to fully fill.
     * @param completeMessage String message to be displayed when the timer finishes.
     */
    public static void setTimerBarIncrement(Player player, String message, int seconds, String completeMessage) {
        setTimerBarIncrement(player, message, new int[]{seconds, 20}, completeMessage);
    }
    
    /**
     * Assign a timer bar (decrementing) to a player, with a specified message, number of intervals and length of interval.
     *
     * @param player       Player to assign this bar to.
     * @param message      String message to be displayed on the bar.
     * @param count        Number of intervals for this bar to take to fully deplete.
     * @param tickinterval Length of each interval, in ticks.
     */
    public static void setTimerBarDecrement(Player player, String message, int count, int tickinterval) {
        setTimerBarDecrement(player, message, new int[]{count, tickinterval}, null);
    }
    
    /**
     * Assign a timer bar (incrementing) to a player, with a specified message, number of intervals and length of interval.
     *
     * @param player       Player to assign this bar to.
     * @param message      String message to be displayed on the bar.
     * @param count        Number of intervals for this bar to take to fully fill.
     * @param tickinterval Length of each interval, in ticks.
     */
    public static void setTimerBarIncrement(Player player, String message, int count, int tickinterval) {
        setTimerBarIncrement(player, message, new int[]{count, tickinterval}, null);
    }
    
    /**
     * Assign a timer bar (decrementing) to a player, with a specified message, number of intervals, length of interval and a message to display upon completion.
     *
     * @param player          Player to assign this bar to.
     * @param message         String message to be displayed on the bar.
     * @param count           Number of intervals for this bar to take to fully deplete.
     * @param tickinterval    Length of each interval, in ticks.
     * @param completeMessage String message to be displayed when the timer finishes.
     */
    public static void setTimerBarDecrement(Player player, String message, int count, int tickinterval, String completeMessage) {
        setTimerBarDecrement(player, message, new int[]{count, tickinterval}, completeMessage);
    }
    
    /**
     * Assign a timer bar (incrementing) to a player, with a specified message, number of intervals, length of interval and a message to display upon completion.
     *
     * @param player          Player to assign this bar to.
     * @param message         String message to be displayed on the bar.
     * @param count           Number of intervals for this bar to take to fully fill.
     * @param tickinterval    Length of each interval, in ticks.
     * @param completeMessage String message to be displayed when the timer finishes.
     */
    public static void setTimerBarIncrement(Player player, String message, int count, int tickinterval, String completeMessage) {
        setTimerBarIncrement(player, message, new int[]{count, tickinterval}, completeMessage);
    }
    
    private static void setTimerBarDecrement(final Player player, String message, int[] interval, final String completeMessage) {
        setTimerBar(player, message, interval, completeMessage, true);
    }
    
    private static void setTimerBarIncrement(final Player player, String message, int[] interval, final String completeMessage) {
        setTimerBar(player, message, interval, completeMessage, false);
    }
    
    private static void setTimerBar(final Player player, String message, int[] interval, final String completeMessage, final boolean decrement) {
        if (interval[0] < 0)
            return;
        
        if (interval.length < 2)
            interval = new int[]{interval[0], 20};
        
        removeBar(player);
        
        FakeDragon dragon = getBarDragon(player, message);
        dragon.setName(message);
        dragon.setHealth(decrement ? FakeDragon.MAX_HEALTH : 0);
        final float healthChange = FakeDragon.MAX_HEALTH / interval[0];
        stopTickingBar(player.getName());
        
        tickingBars.put(player.getName(), Bukkit.getScheduler().runTaskTimer(CommonUtils.getPlugin(), new BukkitRunnable() {
            public void run() {
                FakeDragon dragon = getBarDragon(player, "");
                dragon.setHealth(dragon.getHealth() + (decrement ? -healthChange : healthChange));
                if (decrement ? dragon.getHealth() < 0 : dragon.getHealth() > FakeDragon.MAX_HEALTH) {
                    removeBar(player);
                    stopTickingBar(player.getName());
                } else if (dragon.getHealth() == (decrement ? 0 : FakeDragon.MAX_HEALTH)) {
                    if (completeMessage != null) {
                        dragon.setHealth(decrement ? 0 : FakeDragon.MAX_HEALTH);
                        dragon.setName(completeMessage);
                        sendBarDragon(dragon, player);
                    }
                    sendBarDragon(dragon, player);
                } else {
                    sendBarDragon(dragon, player);
                }
            }
        }, (long) interval[1], (long) interval[1]).getTaskId());
        sendBarDragon(dragon, player);
    }
    
    private static FakeDragon addBarDragon(Player player, String message) {
        FakeDragon dragon = newBarDragon(message, player.getLocation().add(0, BossBar.DRAGON_HEIGHT_OFFSET, 0));
        EntityHandler.sendPacket(player, dragon.getSpawnPacket(player.getWorld()));
        playerBars.put(player.getName(), dragon);
        return dragon;
    }
    
    private static FakeDragon addBarDragon(Player player, Location loc, String message) {
        FakeDragon dragon = newBarDragon(message, loc.add(0, BossBar.DRAGON_HEIGHT_OFFSET, 0));
        EntityHandler.sendPacket(player, dragon.getSpawnPacket(player.getWorld()));
        playerBars.put(player.getName(), dragon);
        return dragon;
    }
    
    private static FakeDragon getBarDragon(Player player, String message) {
        if (hasBar(player))
            return playerBars.get(player.getName());
        else
            return addBarDragon(player, message);
    }
    
    private static FakeDragon newBarDragon(String message, Location loc) {
        FakeDragon barDragon = new Dragon(message, loc);
        return barDragon;
    }
    
    private static void sendBarDragon(FakeDragon barDragon, Player player) {
        EntityHandler.sendPacket(player, barDragon.getMetaPacket(barDragon.getWatcher()));
        EntityHandler.sendPacket(player, barDragon.getTeleportPacket(player.getLocation().add(0, BossBar.DRAGON_HEIGHT_OFFSET, 0)));
    }
    
    private static void stopTickingBar(String entry) {
        Integer timerID = tickingBars.remove(entry);
        
        if (timerID != null)
            Bukkit.getScheduler().cancelTask(timerID);
    }
    
    /**
     * This method should be called from methods that respond to events like PlayerTeleportEvent or PlayerRespawnEvent.
     * It adjusts a player's bar to their new location, if applicable.
     *
     * @param player Player who is teleporting.
     * @param loc    Location that the player is teleporting to.
     */
    public static void adjustBossBarTeleport(final Player player, final Location loc) {
        if (!hasBar(player))
            return;
        
        Bukkit.getScheduler().runTask(CommonUtils.getPlugin(), new Runnable() {
            public void run() {
                if (!hasBar(player))
                    return;
                FakeDragon oldBarDragon = getBarDragon(player, "");
                float oldHealth = oldBarDragon.getHealth();
                String message = oldBarDragon.getName();
                EntityHandler.sendPacket(player, getBarDragon(player, "").getDestroyPacket());
                playerBars.remove(player.getName());
                
                FakeDragon newBarDragon = addBarDragon(player, loc, message);
                newBarDragon.setHealth(oldHealth);
                sendBarDragon(newBarDragon, player);
            }
        });
    }
}
