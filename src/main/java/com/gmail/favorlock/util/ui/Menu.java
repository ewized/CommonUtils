package com.gmail.favorlock.util.ui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Menu implements InventoryHolder {

    private HashMap<Integer, MenuItem> items = new HashMap<>();
    private Inventory inventory;
    private String title;
    private int rows;
    private boolean exitOnClickOutside = true;
    private MenuCloseBehavior menuCloseBehavior;

    public Menu(String title, int rows) {
        this.title = title;
        this.rows = rows;
    }

    public void setMenuCloseBehavior(MenuCloseBehavior menuCloseBehavior) {
        this.menuCloseBehavior = menuCloseBehavior;
    }

    public MenuCloseBehavior getMenuCloseBehavior() {
        return menuCloseBehavior;
    }

    public void setExitOnClickOutside(boolean exit) {
        this.exitOnClickOutside = exit;
    }

    public boolean addMenuItem(MenuItem item, int x, int y) {
        return addMenuItem(item, y * 9 + x);
    }

    public boolean addMenuItem(MenuItem item, int index) {
        ItemStack slot = getInventory().getItem(index);

        if (slot != null && slot.getType() != Material.AIR) {
            return false;
        }

        getInventory().setItem(index, item.getItemStack());
        items.put(index, item);
        item.addToMenu(this);

        return true;
    }

    public boolean removeMenuItem(int index) {
        ItemStack slot = getInventory().getItem(index);

        if (slot == null || slot.getType() == Material.AIR) {
            return false;
        }

        getInventory().clear(index);
        items.remove(index).removeFromMenu(this);

        return true;
    }

    protected void selectMenuItem(Player player, int index) {
        if (items.containsKey(index)) {
            MenuItem item = items.get(index);
            item.onClick(player);
        }
    }

    public void openMenu(Player player) {
        if (getInventory().getViewers().contains(player)) {
            throw new IllegalArgumentException(player.getName() + " is already viewing " + getInventory().getTitle());
        }

        player.openInventory(getInventory());
    }

    public void closeMenu(Player player) {
        if (getInventory().getViewers().contains(player)) {
            getInventory().getViewers().remove(player);
            player.closeInventory();
        }
    }

    public void switchMenu(Player player, Menu menu) {
        MenuAPI.switchMenu(player, this, menu);
    }

    @Override
    public Inventory getInventory() {
        if (inventory == null) {
            inventory = Bukkit.createInventory(this, rows * 9, title);
        }

        return inventory;
    }

    public boolean exitOnClickOutside() {
        return exitOnClickOutside;
    }

    @Override
    protected Menu clone() {
        Menu clone = new Menu(title, rows);
        clone.setExitOnClickOutside(exitOnClickOutside);
        clone.setMenuCloseBehavior(menuCloseBehavior);

        for (int index : items.keySet()) {
            addMenuItem(items.get(index), index);
        }

        return clone;
    }

    public void updateMenu() {
        for (HumanEntity entity : getInventory().getViewers()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.updateInventory();
            }
        }
    }
}
