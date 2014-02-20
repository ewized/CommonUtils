package com.gmail.favorlock.util.ui;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.gmail.favorlock.util.ui.MenuItem;

public abstract class MenuHolder extends MenuBase implements InventoryHolder {

	public void openMenu(Player player) {
		if (getInventory().getViewers().contains(player)) {
			throw new IllegalArgumentException(player.getName() + " is already viewing " + getInventory().getTitle());
		}

		player.openInventory(getInventory());
	}

	public void closeMenu(Player player) {
		if (getInventory().getViewers().contains(player)) {
			getInventory().getViewers().remove(player);
			player.closeInventory();
		}
	}
	
	@SuppressWarnings("deprecation")
	protected void selectMenuItem(Player player, int index) {
		if (items.containsKey(index)) {
			MenuItem item = items.get(index);
			item.onClick(player);
		}
		player.updateInventory();
	}

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

	public boolean removeMenuItem(int index) {
		ItemStack slot = getInventory().getItem(index);

		if (slot == null || slot.getType() == Material.AIR) {
			return false;
		}

		getInventory().clear(index);
		items.remove(index).removeFromMenu(this);

		return true;
	}

	@SuppressWarnings("deprecation")
	public void updateMenu() {
		for (HumanEntity entity : getInventory().getViewers()) {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				player.updateInventory();
			}
		}
	}

	@Override
	public abstract Inventory getInventory();

	protected abstract MenuHolder clone();
}