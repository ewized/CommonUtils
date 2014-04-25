package com.gmail.favorlock.commonutils.ui;

import org.bukkit.entity.Player;

public abstract class MenuBase {

//    HashMap<Integer, MenuItem> items = new HashMap<>();
    final int max_items;
    MenuItem[] items;
    boolean exitOnClickOutside = true;
    MenuCloseBehavior menuCloseBehavior;
    
    protected MenuBase(int max_items) {
        this.max_items = max_items;
        this.items = new MenuItem[max_items];
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

    public boolean exitOnClickOutside() {
        return exitOnClickOutside;
    }

    public abstract void openMenu(Player player);

    public abstract void closeMenu(Player player);

    public void switchMenu(Player player, MenuBase menu) {
        MenuAPI.switchMenu(player, this, menu);
    }
}
