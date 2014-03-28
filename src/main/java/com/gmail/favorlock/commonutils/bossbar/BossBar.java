package com.gmail.favorlock.commonutils.bossbar;

import java.util.HashMap;
import java.util.Map;

import com.gmail.favorlock.commonutils.entity.EntityHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.favorlock.commonutils.CommonUtils;

public class BossBar {
	// Maps player name to their current BossBarDragon
	private static final Map<String, BossBarDragon> playerBars = new HashMap<String, BossBarDragon>();
	// Maps player name to the task ID of their ticking bar, if applicable
	private static final Map<String, Integer> tickingBars = new HashMap<String, Integer>(); 
	
	/**Get whether or not a player has a bar currently displayed.
	 * @param player Player to test.
	 * @return true if they have a bar displayed, false otherwise.
	 */
	public static boolean hasBar(Player player){
		return playerBars.get(player.getName()) != null;
	}
	/**Get whether or not a player has a bar currently displayed that has an active ticking timer.
	 * @param player Player to test.
	 * @return true if they have a bar displayed with a timed task, false otherwise.
	 */
	public static boolean hasTickingBar(Player player){
		return tickingBars.containsKey(player.getName());
	}
	
	/**Removes a player's bar, if they have one.
	 * @param player Player whose bar to remove.
	 * @return true if the player had a bar, false otherwise.
	 */
	public static boolean removeBar(Player player){
		if(!hasBar(player))
			return false;
		EntityHandler.sendPacket(player, getBarDragon(player, "").getDragonDestroyPacket());
		playerBars.remove(player.getName());
		stopTickingBar(player);
		return true;
	}
	/**Removes all player's bars.
	 */
	public static void removeAllBars(){
		for(Player player : Bukkit.getServer().getOnlinePlayers())
			removeBar(player);
	}
	/**Assign a message bar to all online players with the specified message. The bar will be full.
	 * @param message String message to be displayed to all players.
	 */
	public static void setAllMessageBar(String message){
		for(Player player : Bukkit.getOnlinePlayers())
			setMessageBar(player, message);
	}
	/**Assign a bar to a player, with the specified message. The bar will be full.
	 * @param player Player to assign this bar to.
	 * @param message String message to be displayed on the bar.
	 */
	public static void setMessageBar(Player player, String message){
		BossBarDragon dragon = getBarDragon(player, message);
		dragon.setDisplayedName(trim(message));
		dragon.setHealthDirectly(BossBarDragon.FULL_HEALTH);
		stopTickingBar(player);
		sendBarDragon(dragon, player);
	}
	/**Assign a bar to all players, with the specified message and fill percentage. The percentage will not change unless manually updated.
	 * @param message String message to be displayed on the bar.
	 * @param percent Int 0-100, how full the bar should be.
	 */
	public static void setAllPercentBar(String message, int percent){
		if((percent < 0f) || (percent > 100f))
			return;
		for(Player player : Bukkit.getServer().getOnlinePlayers())
			setPercentBar(player, message, percent);
	}
	/**Assign a bar to a player, with the specified message and fill percentage. The percentage will not change unless manually updated.
	 * @param player Player to assign this bar to.
	 * @param message String message to be displayed on the bar.
	 * @param percent Int 0-100, how full the bar should be.
	 */
	public static void setPercentBar(Player player, String message, int percent){
		if((percent < 0f) || (percent > 100f))
			return;
		BossBarDragon dragon = getBarDragon(player, message);
		dragon.setDisplayedName(trim(message));
		dragon.setHealthPercent(percent);
		stopTickingBar(player);
		sendBarDragon(dragon, player);
	}
	/**Assign a timer bar (decrementing) to a player, with a specified message and number of seconds.
	 * @param player Player to assign this bar to.
	 * @param message String message to be displayed on the bar.
	 * @param seconds Number of seconds for this bar to take to fully deplete.
	 */
	public static void setTimerBarDecrement(Player player, String message, int seconds){
		setTimerBarDecrement(player, message, new int[]{seconds, 20}, null);
	}
	/**Assign a timer bar (incrementing) to a player, with a specified message and number of seconds.
	 * @param player Player to assign this bar to.
	 * @param message String message to be displayed on the bar.
	 * @param seconds Number of seconds for this bar to take to fully fill.
	 */
	public static void setTimerBarIncrement(Player player, String message, int seconds){
		setTimerBarIncrement(player, message, new int[]{seconds, 20}, null);
	}
	/**Assign a timer bar (decrementing) to a player, with a specified message, number of intervals and message to display upon completion.
	 * @param player Player to assign this bar to.
	 * @param message String message to be displayed on the bar.
	 * @param seconds Number of seconds for this bar to take to fully deplete.
	 * @param completeMessage String message to be displayed when the timer finishes.
	 */
	public static void setTimerBarDecrement(Player player, String message, int seconds, String completeMessage){
		setTimerBarDecrement(player, message, new int[]{seconds, 20}, completeMessage);
	}
	/**Assign a timer bar (incrementing) to a player, with a specified message, number of intervals and message to display upon completion.
	 * @param player Player to assign this bar to.
	 * @param message String message to be displayed on the bar.
	 * @param seconds Number of seconds for this bar to take to fully fill.
	 * @param completeMessage String message to be displayed when the timer finishes.
	 */
	public static void setTimerBarIncrement(Player player, String message, int seconds, String completeMessage){
		setTimerBarIncrement(player, message, new int[]{seconds, 20}, completeMessage);
	}
	/**Assign a timer bar (decrementing) to a player, with a specified message, number of intervals and length of interval.
	 * @param player Player to assign this bar to.
	 * @param message String message to be displayed on the bar.
	 * @param count Number of intervals for this bar to take to fully deplete.
	 * @param tickinterval Length of each interval, in ticks.
	 */
	public static void setTimerBarDecrement(Player player, String message, int count, int tickinterval){
		setTimerBarDecrement(player, message, new int[]{count, tickinterval}, null);
	}
	/**Assign a timer bar (incrementing) to a player, with a specified message, number of intervals and length of interval.
	 * @param player Player to assign this bar to.
	 * @param message String message to be displayed on the bar.
	 * @param count Number of intervals for this bar to take to fully fill.
	 * @param tickinterval Length of each interval, in ticks.
	 */
	public static void setTimerBarIncrement(Player player, String message, int count, int tickinterval){
		setTimerBarIncrement(player, message, new int[]{count, tickinterval}, null);
	}
	/**Assign a timer bar (decrementing) to a player, with a specified message, number of intervals, length of interval and a message to display upon completion.
	 * @param player Player to assign this bar to.
	 * @param message String message to be displayed on the bar.
	 * @param count Number of intervals for this bar to take to fully deplete.
	 * @param tickinterval Length of each interval, in ticks.
	 * @param completeMessage String message to be displayed when the timer finishes.
	 */
	public static void setTimerBarDecrement(Player player, String message, int count, int tickinterval, String completeMessage){
		setTimerBarDecrement(player, message, new int[]{count, tickinterval}, completeMessage);
	}
	/**Assign a timer bar (incrementing) to a player, with a specified message, number of intervals, length of interval and a message to display upon completion.
	 * @param player Player to assign this bar to.
	 * @param message String message to be displayed on the bar.
	 * @param count Number of intervals for this bar to take to fully fill.
	 * @param tickinterval Length of each interval, in ticks.
	 * @param completeMessage String message to be displayed when the timer finishes.
	 */
	public static void setTimerBarIncrement(Player player, String message, int count, int tickinterval, String completeMessage){
		setTimerBarIncrement(player, message, new int[]{count, tickinterval}, completeMessage);
	}
	
	
	private static void setTimerBarDecrement(final Player player, String message, int[] interval, final String completeMessage){
		setTimerBar(player, message, interval, completeMessage, true);
	}
	private static void setTimerBarIncrement(final Player player, String message, int[] interval, final String completeMessage){
		setTimerBar(player, message, interval, completeMessage, false);
	}
	private static void setTimerBar(final Player player, String message, int[] interval, final String completeMessage, final boolean decrement){
		if(interval[0] < 0)
			return;
		if(interval.length < 2)
			interval = new int[]{interval[0], 20};
		BossBarDragon dragon = getBarDragon(player, message);
		dragon.setDisplayedName(trim(message));
		dragon.setHealthDirectly(decrement ? BossBarDragon.FULL_HEALTH : 0);
		final float healthChange = BossBarDragon.FULL_HEALTH / interval[0];
		stopTickingBar(player);
		tickingBars.put(player.getName(), Bukkit.getScheduler().runTaskTimer(CommonUtils.getPlugin(), new BukkitRunnable(){
							public void run(){
								BossBarDragon dragon = getBarDragon(player, "");
								dragon.setHealthDirectly(dragon.getHealth() + (decrement ? -healthChange : healthChange));
								if(decrement ? dragon.getHealth() < 0 : dragon.getHealth() > BossBarDragon.FULL_HEALTH){
									removeBar(player);
									stopTickingBar(player);
								}else if(dragon.getHealth() == (decrement ? 0 : BossBarDragon.FULL_HEALTH)){
									if(completeMessage != null){
										dragon.setHealthDirectly(decrement ? 0 : BossBarDragon.FULL_HEALTH);
										dragon.setDisplayedName(trim(completeMessage));
										sendBarDragon(dragon, player);
									}
									sendBarDragon(dragon, player);
								}else{
									sendBarDragon(dragon, player);
								}
							}
						}, (long) interval[1], (long) interval[1]).getTaskId());
		sendBarDragon(dragon, player);
	}
	
	private static BossBarDragon addBarDragon(Player player, String message){
		BossBarDragon dragon = newBarDragon(message, player.getLocation().add(0, -384, 0));
		EntityHandler.sendPacket(player, dragon.getDragonSpawnPacket());
		playerBars.put(player.getName(), dragon);
		return dragon;
	}
	private static BossBarDragon addBarDragon(Player player, Location loc, String message){
		BossBarDragon dragon = newBarDragon(message, loc.add(0, -384, 0));
		EntityHandler.sendPacket(player, dragon.getDragonSpawnPacket());
		playerBars.put(player.getName(), dragon);
		return dragon;
	}
	
	private static BossBarDragon getBarDragon(Player player, String message){
		if(hasBar(player))
			return playerBars.get(player.getName());
		else
			return addBarDragon(player, trim(message));
	}
	
	private static BossBarDragon newBarDragon(String message, Location loc){
		BossBarDragon barDragon = new BossBarDragon(message, loc);
		return barDragon;
	}
	
	private static void sendBarDragon(BossBarDragon barDragon, Player player){
		EntityHandler.sendPacket(player, barDragon.getMetaPacket(barDragon.getWatcher()));
		EntityHandler.sendPacket(player, barDragon.getTeleportPacket(player.getLocation().add(0, -384, 0)));
	}
	
	private static void stopTickingBar(Player player){
		Integer timerID = tickingBars.remove(player.getName());
		if(timerID != null)
			Bukkit.getScheduler().cancelTask(timerID);
	}
	
	// This is no longer a limitation, apparently
	private static String trim(String message){
		// if(message.length() > 64) // Prevents client crash from sending strings longer than 64
			// message = message.substring(0, 63);
		return message;
	}
	/** This method should be called from methods that respond to events like PlayerTeleportEvent or PlayerRespawnEvent.
	 * It adjusts a player's bar to their new location, if applicable.
	 * @param player Player who is teleporting.
	 * @param loc Location that the player is teleporting to.
	 */
	public static void adjustBossBarTeleport(final Player player, final Location loc){
		if (!hasBar(player))
			return;
		Bukkit.getScheduler().runTask(CommonUtils.getPlugin(), new Runnable(){
			public void run(){
				if (!hasBar(player))
					return;
				BossBarDragon oldBarDragon = getBarDragon(player, "");
				float oldHealth = oldBarDragon.getHealth();
				String message = oldBarDragon.getDisplayedName();
				EntityHandler.sendPacket(player, getBarDragon(player, "").getDragonDestroyPacket());
				playerBars.remove(player.getName());
				
				BossBarDragon newBarDragon = addBarDragon(player, loc, message);
				newBarDragon.setHealthDirectly(oldHealth);
				sendBarDragon(newBarDragon, player);
			}
		});
	}
}
