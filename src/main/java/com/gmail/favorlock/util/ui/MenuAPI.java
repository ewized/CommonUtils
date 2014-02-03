package com.gmail.favorlock.util.ui;

import com.gmail.favorlock.CommonUtils;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class MenuAPI implements Listener {

    private static CommonUtils instance;

    public MenuAPI(CommonUtils plugin) {
        this.instance = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static Menu createMenu(String title, int rows) {
        return new Menu(title, rows);
    }

    public static Menu createMenu(String title, boolean center, int rows) {
        int spaces = (32 - title.length()) / 2;
        String name = "";
        for (int i = 0; i < spaces; i++) {
            name += " ";
        }
        name += title;
        if (center) {
            return new Menu(name, rows);
        } else {
            return new Menu(title, rows);
        }
    }

    public static Menu cloneMenu(Menu menu) {
        return menu.clone();
    }

    public static void removeMenu(Menu menu) {
        for (HumanEntity viewer : menu.getInventory().getViewers()) {
            if (viewer instanceof Player) {
                menu.closeMenu((Player) viewer);
            } else {
                viewer.closeInventory();
            }
        }
    }

    public static void switchMenu(final Player player, Menu fromMenu, final Menu toMenu) {
        fromMenu.closeMenu(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                toMenu.openMenu(player);
            }
        }.runTask(instance);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMenuItemClicked(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof Menu) {
            Menu menu = (Menu) inventory.getHolder();
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
                    if (menu.exitOnClickOutside()) {
                        menu.closeMenu(player);
                    }
                } else {
                    int index = event.getRawSlot();
                    if (index < inventory.getSize()) {
                        menu.selectMenuItem(player, index);
                    } else {
                        if (menu.exitOnClickOutside()) {
                            menu.closeMenu(player);
                        }
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMenuClosed(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Inventory inventory = event.getInventory();
            if (inventory.getHolder() instanceof Menu) {
                Menu menu = (Menu) inventory.getHolder();
                MenuCloseBehavior menuCloseBehavior = menu.getMenuCloseBehavior();

                if (menuCloseBehavior != null) {
                    menuCloseBehavior.onClose((Player) event.getPlayer());
                }
            }
        }
    }
}
