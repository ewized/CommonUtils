package com.gmail.favorlock.commonutils.ui;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class MenuHolder extends MenuBase implements InventoryHolder {

    protected MenuHolder(int max_items) {
        super(max_items);
    }
    
    public void openMenu(Player player) {
        if (getInventory().getViewers().contains(player)) {
            throw new IllegalStateException(player.getName() + " is already viewing " + getInventory().getTitle());
        }
        
        player.openInventory(getInventory());
    }
    
    public void closeMenu(Player player) {
        if (getInventory().getViewers().contains(player)) {
            getInventory().getViewers().remove(player);
            player.closeInventory();
        }
    }
    
    @SuppressWarnings("deprecation")
    protected boolean selectMenuItem(Inventory inventory, Player player, int index, InventoryClickEvent e) {
        boolean allow = false;
        
        if (index > -1 && index < getMaxItems()) {
            MenuItem item = items[index];
            
            if (item != null) {
                allow = item.onClick(player, e);
            }
        }
        
        player.updateInventory();
        return allow;
    }
    
    public boolean addMenuItem(MenuItem item, int index) {
        ItemStack slot = getInventory().getItem(index);
        
        if (slot != null && slot.getType() != Material.AIR) {
            return false;
        } else if (index < 0 || index >= getMaxItems()) {
            return false;
        }
        
        getInventory().setItem(index, item.getItemStack());
        items[index] = item;
        item.addToMenu(this);
        
        return true;
    }
    
    public boolean removeMenuItem(int index) {
        ItemStack slot = getInventory().getItem(index);
        
        if (slot == null || slot.getType() == Material.AIR) {
            return false;
        } else if (index < 0 || index >= getMaxItems()) {
            return false;
        }
        
        getInventory().clear(index);
        MenuItem remove = items[index];
        items[index] = null;
        remove.removeFromMenu(this);
        
        return true;
    }
    
    @SuppressWarnings("deprecation")
    public void updateMenu() {
        for (HumanEntity entity : getInventory().getViewers()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.updateInventory();
            }
        }
    }
    
    public void updateInventory() {
        getInventory().clear();
        
        for (int i = 0; i < getMaxItems(); i++) {
            MenuItem item = super.items[i];
            
            if (item != null) {
                getInventory().setItem(i, item.getItemStack());
            }
        }
    }
    
    public abstract Inventory getInventory();
    
    protected abstract MenuHolder clone();
}