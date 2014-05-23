package com.gmail.favorlock.commonutils.ui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class Menu extends MenuHolder {

    private final String title;
    private final int rows;
    private Inventory inventory;
    
    public Menu(String title, int rows) {
        super(9 * rows);
        
        this.title = title;
        this.rows = rows;
    }
    
    public boolean addMenuItem(MenuItem item, int x, int y) {
        return addMenuItem(item, y * 9 + x);
    }
    
    public boolean addMenuItem(MenuItem item, int x, int y, short durability) {
        return addMenuItem(item, y * 9 + x, durability);
    }
    
    public int getRows() {
        return rows;
    }
    
    public Inventory getInventory() {
        if (inventory == null) {
            inventory = Bukkit.createInventory(this, rows * 9, title);
        }

        return inventory;
    }
    
    protected MenuHolder clone() {
        MenuHolder clone = new Menu(title, rows);
        clone.setExitOnClickOutside(exitOnClickOutside);
        clone.setMenuCloseBehavior(menuCloseBehavior);
        clone.items = items.clone();
        
        return clone;
    }
}
