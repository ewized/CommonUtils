package com.gmail.favorlock.commonutils.ui;

import org.bukkit.entity.Player;

public abstract class MenuReadBehavior {
	private boolean allowNullImput;
	
	public MenuReadBehavior() {
		allowNullImput = false;
	}
	
	public MenuReadBehavior(boolean allowNullImput) {
		this.allowNullImput = allowNullImput;
	}
	
	public abstract void onInputRead(Player player, String input_given);
	
	public final boolean allowNullInput() {
		return allowNullImput;
	}
}
