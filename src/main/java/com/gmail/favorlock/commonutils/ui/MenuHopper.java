package com.gmail.favorlock.commonutils.ui;

import java.lang.reflect.Field;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.favorlock.commonutils.reflection.VersionHandler;

public class MenuHopper extends MenuHolder {

	private static InventoryType TYPE = InventoryType.HOPPER;

	private Inventory inventory;
	private String title;

	public MenuHopper(String title) {
		inventory = Bukkit.createInventory(this, TYPE);
		this.title = title;
		Class<?> craftInventoryClass = VersionHandler.getCraftBukkitClass("inventory.CraftInventory");
		Class<?> craftInventoryCustomClass = VersionHandler.getCraftBukkitClass("inventory.CraftInventoryCustom");
		Class<?> minecraftInventoryClass = null;

		Class<?>[] craftInventoryCustomInnerClasses = craftInventoryCustomClass.getDeclaredClasses();
		for(Class<?> cls : craftInventoryCustomInnerClasses){
			String canonicalName = cls.getCanonicalName();
			String className = canonicalName.substring(canonicalName.lastIndexOf('.') + 1);
			if(className.equals("MinecraftInventory"))
				minecraftInventoryClass = cls;
		}
		if(minecraftInventoryClass == null){
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
		}

		getInventory().setItem(index, item.getItemStack());
		items.put(index, item);
		item.addToMenu(this);

		return true;
	}

	@Override
	public boolean removeMenuItem(int index) {
		ItemStack slot = getInventory().getItem(index);

		if (slot == null || slot.getType() == Material.AIR) {
			return false;
		}

		getInventory().clear(index);
		items.remove(index).removeFromMenu(this);

		return true;
	}

	@Override
	protected MenuHolder clone() {
		MenuHolder clone = new MenuDispenser(title);
		clone.setExitOnClickOutside(exitOnClickOutside);
		clone.setMenuCloseBehavior(menuCloseBehavior);

		for (int index : items.keySet()) {
			addMenuItem(items.get(index), index);
		}

		return clone;
	}
}
