package com.gmail.favorlock.commonutils;

import com.gmail.favorlock.commonutils.network.packets.PacketListener;

import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.favorlock.commonutils.ui.MenuAPI;

public class CommonUtils extends JavaPlugin {

    private static CommonUtils plugin;
    private static MenuAPI menuAPI;
    private static PacketListener packetListener;

    public void onEnable() {
        plugin = this;
        menuAPI = new MenuAPI(this);

        if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            packetListener = new PacketListener(this);
            getLogger().info("ProtocolLib found; PacketListener active.");
        } else {
            packetListener = null;
            getLogger().warning("ProtocolLib not found; PacketListener inactive.");
        }
    }

    public static CommonUtils getPlugin() {
        return plugin;
    }

    public static MenuAPI getMenuAPI() {
        return menuAPI;
    }

    public static PacketListener getPacketListener() {
        return packetListener;
    }

    public static boolean isPacketListenerActive() {
        return (packetListener != null);
    }
}
