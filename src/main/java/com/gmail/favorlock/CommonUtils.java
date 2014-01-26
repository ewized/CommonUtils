package com.gmail.favorlock;

import com.gmail.favorlock.util.ui.MenuAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class CommonUtils extends JavaPlugin {

    private static CommonUtils plugin;
    private static MenuAPI menuAPI;

    public void onEnable() {
        plugin = this;
        menuAPI = new MenuAPI(this);
    }

    public static CommonUtils getPlugin() {
        return plugin;
    }

    public static MenuAPI getMenuAPI() {
        return menuAPI;
    }

}
