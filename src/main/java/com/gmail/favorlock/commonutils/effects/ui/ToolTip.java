package com.gmail.favorlock.commonutils.effects.ui;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.favorlock.commonutils.entity.EntityHandler;
import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;

public class ToolTip implements Listener {

    @SuppressWarnings("deprecation")
    /** The MaterialData that should be used when a player has no held item to modify. */
    private static final MaterialData EMPTY_MATERIAL = new MaterialData(Material.CARPET, (byte) 15);
    /** The inventory ID that NMS code considers the player inventory. */
    private static final int PLAYER_INVENTORY_ID = 0;
    /** The slot ID that NMS code considers the first hotbar slot. */
    private static final int HOTBAR_OFFSET = 36;
    /** The default priority level for a ToolTipMessage. */
    private static final int DEFAULT_PRIORITY = 0;
    
    private final JavaPlugin plugin;
    private final Map<String, ToolTipQueue> main_queue;
    private QueueHandler task;
    
    /**
     * Create a new ToolTip handler, registered under the given
     * plugin, and automatically start the processing task.
     * 
     * @param plugin    The plugin to register under.
     */
    public ToolTip(JavaPlugin plugin) {
        this(plugin, true);
    }
    
    /**
     * Create a new ToolTip handler, registered under the given
     * plugin, specifying if the processing task should be started.
     * 
     * @param plugin    The plugin to register under.
     * @param process   Whether or not the processing task should be started.
     */
    public ToolTip(JavaPlugin plugin, boolean process) {
        this.plugin = plugin;
        this.main_queue = new HashMap<>();
        this.task = null;
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        if (process)
            startQueue();
    }
    
    private void advanceQueue() {
        for (Map.Entry<String, ToolTipQueue> element : new HashSet<>(main_queue.entrySet())) {
            ToolTipQueue q = element.getValue();
            
            if (q.advance())
                continue;
            
            main_queue.remove(element.getKey());
        }
    }

    /**
     * Get whether the queue is currently being processed.
     * 
     * @return <b>true</b> if the queue is being processed,
     *   <b>false</b> otherwise.
     */
    public boolean isQueueProcessing() {
        return task != null;
    }

    /**
     * Pauses the processing of the queue.
     * 
     * @throws IllegalStateException
     *     If processing of the queue is already stopped.
     */
    public void pauseQueue() {
        if (!isQueueProcessing())
            throw new IllegalStateException("Queue is already stopped!");
        
        task.cancel();
        task = null;
    }

    /**
     * Starts the processing of the queue.
     * 
     * @throws IllegalStateException
     *     If processing of the queue is already started.
     */
    public void startQueue() {
        if (isQueueProcessing())
            throw new IllegalStateException("Queue is already running!");
        
        task = new QueueHandler();
        task.runTaskTimer(plugin, 20L, 20L);
    }

    /**
     * Get whether or not the given player
     * currently has any messages in the queue.
     * 
     * @param player    The Player to test.
     * @return <b>true</b> if the player has messages
     *   in the queue, <b>false</b> otherwise.
     */
    public boolean hasQueue(Player player) {
        return main_queue.containsKey(player.getName());
    }
    
    /**
     * Add a message to the given player's queue,
     * with a default display length of 1 second.
     * 
     * @param player    The Player to act on.
     * @param message   The message to display.
     */
    public void queueMessage(Player player, String message) {
        queueMessage(player, message, 1);
    }
    
    /**
     * Add a message to the given player's queue,
     * with a specified display length.
     * 
     * @param player    The Player to act on.
     * @param message   The message to display.
     * @param seconds   The number of seconds to display for.
     */
    public void queueMessage(Player player, String message, int seconds) {
        if (player == null)
            throw new IllegalArgumentException("Player cannot be null!");
        
        if (message == null)
            throw new IllegalArgumentException("Message cannot be null!");
        
        ToolTipQueue q = main_queue.get(player.getName());
        
        if (q == null) {
            main_queue.put(player.getName(), new ToolTipQueue(player));
            q = main_queue.get(player.getName());
        }
        
        q.push(message, seconds);
        q.update();
    }
    
    /**
     * Force the given player's current message to advance;
     * subsequently, there may be no messages in the queue.
     * 
     * @param player    The Player whose queue should be advanced.
     */
    public void advanceQueue(Player player) {
        if (hasQueue(player)) {
            ToolTipQueue q = main_queue.get(player.getName());
            
            if (q != null) {
                q.drop();
            }
        }
    }

    /**
     * Clear the given player's message queue.
     * 
     * @param player    The Player whose queue should be cleared.
     */
    public void clearQueue(Player player) {
        if (hasQueue(player)) {
            main_queue.remove(player.getName());
        }
    }
    
    protected void finalize() throws Throwable {
        HandlerList.unregisterAll(this);
        super.finalize();
    }
    
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            
            if (hasQueue(player)) {
                plugin.getLogger().info("InvOpen w/ queue");
                ToolTipQueue q = main_queue.get(player.getName());
                
                for (int i = 0; i < 9; i++) {
                    sendReset(player, i);
                }
                
                if (q != null) {
                    q.operate = false;
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            
            if (hasQueue(player)) {
                plugin.getLogger().info("InvClose w/ queue");
                ToolTipQueue q = main_queue.get(player.getName());
                
                if (q != null) {
                    q.operate = true;
                    q.update();
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        
        if (hasQueue(player)) {
            ToolTipQueue q = main_queue.get(player.getName());
            
            if (q != null) {
                final ToolTipMessage message = q.peek();
                final int slot = player.getInventory().getHeldItemSlot();
                new BukkitRunnable() {
                    public void run() {
                        sendMessage(player, message.message, slot);
                    }
                }.runTask(plugin);
            }
        }
    }
    
    private class QueueHandler extends BukkitRunnable {
        public void run() {
            advanceQueue();
        }
    }
    
    /**
     * Send a player their actual item for the given slot (update their client).
     * 
     * @param player    The Player to send the packet to.
     * @param slot      The hotbar slot to update, [0, 8].
     */
    private static void sendReset(Player player, int slot) {
        if (slot > -1 && slot < 9) {
            ItemStack item = player.getInventory().getItem(slot);
            EntityHandler.sendPacket(player, getSlotChangePacket(HOTBAR_OFFSET + slot, item));
        } else throw new IllegalArgumentException("Slot must be in the range [0, 8]! Was " + slot);
    }
    
    /**
     * Send a player an item with a spoofed display name for the given slot.
     * 
     * @param player    The Player to send the packet to.
     * @param message   The name that the spoofed item should have.
     * @param slot      The slot to send this update for.
     */
    private static void sendMessage(Player player, String message, int slot) {
        if (slot > -1 && slot < 9) {
            ItemStack item = modifyMeta(player.getInventory().getItem(slot), message);
            EntityHandler.sendPacket(player, getSlotChangePacket(HOTBAR_OFFSET + slot, item));
        } else throw new IllegalArgumentException("Slot must be in the range [0, 8]! Was " + slot);
    }
    
    @SuppressWarnings("deprecation")
    private static ItemStack modifyMeta(ItemStack original, String display_name) {
        ItemStack display_item;
        
        if (original == null || original.getType().equals(Material.AIR)) {
            display_item = new ItemStack(EMPTY_MATERIAL.getItemType(), 1, EMPTY_MATERIAL.getData());
        } else {
            display_item = original.clone();
        }
        
        ItemMeta meta = display_item.getItemMeta();
        meta.setDisplayName(display_name);
        display_item.setItemMeta(meta);
        return display_item;
    }
    
    private static Object getSlotChangePacket(int slot, ItemStack item) {
        Class<?> classCraftItemStack = VersionHandler.getCraftBukkitClass("inventory.CraftItemStack");
        Method asNMSCopy = CommonReflection.getMethod(classCraftItemStack, "asNMSCopy", new Class<?>[] {ItemStack.class});
        Class<?> classNMSItemStack = VersionHandler.getCraftClass("ItemStack");
        
        Class<?> classPacketPlayOutSetSlot = VersionHandler.getCraftClass("PacketPlayOutSetSlot");
        Constructor<?> packetPlayOutSetSlot = CommonReflection.getConstructor(classPacketPlayOutSetSlot,
                new Class<?>[] {int.class, int.class, classNMSItemStack});
        
        Object packet = CommonReflection.constructNewInstance(packetPlayOutSetSlot,
                new Object[] {PLAYER_INVENTORY_ID, slot,
                CommonReflection.invokeMethodAndReturn(asNMSCopy, null, new Object[] {item})});
        return packet;
    }
    
    private static class ToolTipQueue implements Comparator<ToolTipMessage> {
        private final WeakReference<Player> player;
        private final PriorityQueue<ToolTipMessage> queue;
        private boolean tick;
        private boolean operate;
        
        private ToolTipQueue(Player player) {
            this.player = new WeakReference<>(player);
            this.queue = new PriorityQueue<>(11, this);
            this.tick = false;
            this.operate = true;
        }
        
        /**
         * Update this queue for the associated player.
         * This will not advance the queue.
         */
        private void update() {
            Player player = this.player.get();
            
            if (player != null) {
                ToolTipMessage message = queue.peek();
                
                if (message != null) {
                    message.display(player, tick);
                }
            }
        }
        
        /**
         * Advance this queue on the associated player.
         * <p/>
         * Returns <b>true</b> if this queue should be retained,
         * <b>false</b> if this queue should be removed.
         * 
         * @return Whether or not this queue should be retained.
         */
        private boolean advance() {
            Player player = this.player.get();
            
            if (player == null) {
                return false;
            }
            
            ToolTipMessage message = pop();
            
            if (message == null) {
                for (int i = 0; i < 9; i++) {
                    sendReset(player, i);
                }
                
                return false;
            }
            
            if (operate) {
                message.display(player, tick());
            }
            
            return true;
        }
        
        private boolean tick() {
            return tick = !tick;
        }
        
        private void push(String message, int seconds) {
            queue.add(new ToolTipMessage(message, seconds));
        }
        
        private ToolTipMessage peek() {
            if (queue.size() < 1) {
                return null;
            } else {
                return queue.peek();
            }
        }
        
        private ToolTipMessage pop() {
            if (queue.size() < 1) {
                return null;
            } else {
                if (queue.peek().keepAlive()) {
                    return queue.peek();
                } else {
                    queue.poll();
                    return pop();
                }
            }
        }
        
        private void drop() {
            queue.remove(0);
        }

        public int compare(ToolTipMessage one, ToolTipMessage two) {
            return one.priority - two.priority;
        }
    }
    
    private static class ToolTipMessage {
        private final String message;
        private final int priority;
        private int seconds;
        
        private ToolTipMessage(String message, int seconds) {
            this(message, DEFAULT_PRIORITY, seconds);
        }
        
        private ToolTipMessage(String message, int priority, int seconds) {
            this.message = message;
            this.priority = priority;
            this.seconds = seconds;
        }
        
        private boolean keepAlive() {
            return (seconds--) > 0;
        }
        
        private void display(Player player, boolean tick) {
            String send = message;
            
            if (tick)
                send = " " + message + " ";
            
            for (int i = 0; i < 9; i++) {
                sendMessage(player, send, i);
            }
        }
    }
}
