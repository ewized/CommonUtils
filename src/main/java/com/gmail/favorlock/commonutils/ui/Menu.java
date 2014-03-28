package com.gmail.favorlock.commonutils.ui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class Menu extends MenuHolder {

	private Inventory inventory;
	private String title;
	private int rows;

	public Menu(String title, int rows) {
		this.title = title;
		this.rows = rows;
	}

	public boolean addMenuItem(MenuItem item, int x, int y) {
		return addMenuItem(item, y * 9 + x);
	}

	public boolean addMenuItem(MenuItem item, int x, int y, short durability) {
		return addMenuItem(item, y * 9 + x, durability);
	}

	@Override
	public Inventory getInventory() {
		if (inventory == null) {
			inventory = Bukkit.createInventory(this, rows * 9, title);
		}

		return inventory;
	}

	@Override
	protected MenuHolder clone() {
		MenuHolder clone = new Menu(title, rows);
		clone.setExitOnClickOutside(exitOnClickOutside);
		clone.setMenuCloseBehavior(menuCloseBehavior);

		for (int index : items.keySet()) {
			addMenuItem(items.get(index), index);
		}

		return clone;
	}
}
