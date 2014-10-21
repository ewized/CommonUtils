package com.archeinteractive.dev.commonutils.ui;

import java.lang.reflect.Field;

import com.archeinteractive.dev.commonutils.reflection.CommonReflection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.archeinteractive.dev.commonutils.reflection.VersionHandler;

public class MenuHopper extends MenuHolder {

    private static InventoryType TYPE = InventoryType.HOPPER;
    
    private Inventory inventory;
    private String title;
    
    public MenuHopper(String title) {
        super(5);
        inventory = Bukkit.createInventory(this, TYPE);
        this.title = title;
        Class<?> craftInventoryClass = VersionHandler.getOBCClass("inventory.CraftInventory");
        Class<?> craftInventoryCustomClass = VersionHandler.getOBCClass("inventory.CraftInventoryCustom");
        Class<?> minecraftInventoryClass = null;
        
        Class<?>[] craftInventoryCustomInnerClasses = craftInventoryCustomClass.getDeclaredClasses();
        
        for (Class<?> cls : craftInventoryCustomInnerClasses) {
            String canonicalName = cls.getCanonicalName();
            String className = canonicalName.substring(canonicalName.lastIndexOf('.') + 1);
            
            if (className.equals("MinecraftInventory"))
                minecraftInventoryClass = cls;
        }
        
        if (minecraftInventoryClass == null) {
            Bukkit.getConsoleSender().sendMessage("I tried to find it, I really did :(");
            return;
        }
        
        Object craftInventoryCustom = craftInventoryCustomClass.cast(inventory);
        Object craftInventory = craftInventoryClass.cast(craftInventoryCustom);
        
        Field iinventoryField = CommonReflection.getField(craftInventoryClass, "inventory");
        iinventoryField.setAccessible(true);
        
        try {
            Object iinventory = iinventoryField.get(craftInventory);
            Object minecraftInventory = minecraftInventoryClass.cast(iinventory);

            Field titleField = CommonReflection.getField(minecraftInventoryClass, "title");
            titleField.setAccessible(true);
            titleField.set(minecraftInventory, this.title);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("I really tried... :(");
        }
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    @Override
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
    
    @Override
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
    
    @Override
    protected MenuHolder clone() {
        MenuHolder clone = new MenuDispenser(title);
        clone.setExitOnClickOutside(exitOnClickOutside);
        clone.setMenuCloseBehavior(menuCloseBehavior);
        clone.items = items.clone();
        
        return clone;
    }
}
