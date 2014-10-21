package com.archeinteractive.dev.commonutils.ui;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;

public class MenuFurnace {

    private final Block furnace;
    private final boolean force;
    
    public MenuFurnace(Block furnace) {
        this(furnace, false);
    }
    
    public MenuFurnace(Block furnace, boolean force_block) {
        if (!(furnace.getState() instanceof Furnace)) {
            if (force_block) {
                furnace.setType(Material.FURNACE);
            } else {
                throw new IllegalArgumentException("The given block was not a furnace, and was not forced!");
            }
        }
        
        this.furnace = furnace;
        this.force = force_block;
    }
    
    public void openMenu(Player player) {
        MenuFurnace.openMenu(player, furnace, force);
    }
    
    public static void openMenu(Player player, Block furnace) {
        MenuFurnace.openMenu(player, furnace, false);
    }
    
    public static void openMenu(Player player, Block furnace, boolean force_block) {
        if (!(furnace.getState() instanceof Furnace)) {
            if (force_block) {
                furnace.setType(Material.FURNACE);
            } else {
                throw new IllegalArgumentException("The given block was not a furnace, and was not forced!");
            }
        }
        
        Furnace f = (Furnace) furnace.getState();
        player.openInventory(f.getInventory());
    }
}
