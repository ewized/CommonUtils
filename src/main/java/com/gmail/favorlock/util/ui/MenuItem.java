package com.gmail.favorlock.util.ui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuItem {

    private Menu menu;
    private int number;
    private MaterialData icon;
    private String text;
    private List<String> descriptions = new ArrayList<>();

    public MenuItem(String text) {
        this(text, new MaterialData(Material.PAPER));
    }

    public MenuItem(String text, MaterialData icon) {
        this(text, icon, 1);
    }

    public MenuItem(String text, MaterialData icon, int number) {
        this.text = text;
        this.icon = icon;
        this.number = number;
    }

    protected void addToMenu(Menu menu) {
        this.menu = menu;
    }

    protected void removeFromMenu(Menu menu) {
        if (this.menu == null) {
            this.menu = null;
        }
    }

    public Menu getMenu() {
        return menu;
    }

    public int getNumber() {
        return number;
    }

    public MaterialData getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }

    public void setDescriptions(List<String> lines) {
        descriptions = lines;
    }

    public ItemStack getSingleItemStack() {
        ItemStack slot = new ItemStack(getIcon().getItemType());
        ItemMeta meta = slot.getItemMeta();
        meta.setDisplayName(getText());
        meta.setLore(descriptions);
        slot.setItemMeta(meta);

        return slot;
    }

    public ItemStack getItemStack() {
        ItemStack slot = new ItemStack(getIcon().getItemType(), getNumber());
        ItemMeta meta = slot.getItemMeta();
        meta.setDisplayName(getText());
        meta.setLore(descriptions);
        slot.setItemMeta(meta);

        return slot;
    }

    public ItemStack getItemStack(short durability) {
        ItemStack slot = new ItemStack(getIcon().getItemType(), getNumber(), durability);
        ItemMeta meta = slot.getItemMeta();
        meta.setDisplayName(getText());
        meta.setLore(descriptions);
        slot.setItemMeta(meta);

        return slot;
    }

    public abstract void onClick(Player player);
}
