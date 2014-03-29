package com.gmail.favorlock.commonutils.ui;

import java.util.HashMap;

import org.bukkit.entity.Player;

public abstract class MenuBase {

    HashMap<Integer, MenuItem> items = new HashMap<>();
    boolean exitOnClickOutside = true;
    MenuCloseBehavior menuCloseBehavior;

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
