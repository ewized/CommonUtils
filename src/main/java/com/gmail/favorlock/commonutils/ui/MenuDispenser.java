package com.gmail.favorlock.commonutils.ui;

import java.lang.reflect.Field;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;

public class MenuDispenser extends MenuHolder {

	private static InventoryType TYPE = InventoryType.DISPENSER;

	private Inventory inventory;
	private String title;

	public MenuDispenser(String title) {
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
