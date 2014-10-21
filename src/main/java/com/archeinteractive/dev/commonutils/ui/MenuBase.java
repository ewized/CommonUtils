package com.archeinteractive.dev.commonutils.ui;

import org.bukkit.entity.Player;

public abstract class MenuBase {

//    HashMap<Integer, MenuItem> items = new HashMap<>();
    private final int max_items;
    protected MenuItem[] items;
    protected boolean exitOnClickOutside = true;
    protected MenuCloseBehavior menuCloseBehavior;
    
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
    
    public int getMaxItems() {
        return max_items;
    }

    public abstract void openMenu(Player player);

    public abstract void closeMenu(Player player);

    @Deprecated
    public void switchMenu(Player player, MenuBase menu) {
        throw new UnsupportedOperationException("Use #switchMenu(MenuAPI, Player, MenuBase)");
//        MenuAPI.switchMenu(player, this, menu);
    }
    
    public void switchMenu(MenuAPI api, Player player, MenuBase menu) {
        api.switchMenu(player, this, menu);
    }
}
