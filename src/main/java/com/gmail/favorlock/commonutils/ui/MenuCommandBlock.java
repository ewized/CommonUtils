package com.gmail.favorlock.commonutils.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.gmail.favorlock.commonutils.CommonUtils;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;
public class MenuCommandBlock {

	private static double FAKE_BLOCK_OFFSET_FACTOR = -4.0;
	private static Map<String, FakeCommandBlock> fakeBlocks = new HashMap<>();
	
	private MenuReadBehavior menuReadBehavior = null;
	private String defaultCommandLine;
	
	public MenuCommandBlock() {
		this("");
	}
	
	public MenuCommandBlock(String defaultCommandLine) {
		if (!CommonUtils.isPacketListenerActive())
			throw new IllegalStateException("Packet Listener is not active, MenuCommandBlock not available.");
		
		if (defaultCommandLine == null)
			this.defaultCommandLine = "";
		else
			this.defaultCommandLine = defaultCommandLine;
	}
	
	private static class FakeCommandBlock {
		private Location location;
		private MenuCommandBlock menu;
		
		private FakeCommandBlock(Location location, MenuCommandBlock menu) {
			this.location = location;
			this.menu = menu;
		}
	}
	
	public static boolean hasMenuOpen(Player player) {
		if (fakeBlocks.containsKey(player.getName()))
			return true;
		return false;
	}
	
	public static MenuCommandBlock getMenuFor(Player player) {
		if (hasMenuOpen(player)) {
			return fakeBlocks.get(player.getName()).menu;
		} else {
			return null;
		}
	}
	
	public static Location getFakeBlockLocationFor(Player player) {
		if (hasMenuOpen(player)) {
			return fakeBlocks.get(player.getName()).location;
		} else {
			return null;
		}
	}
	
	public static void processInput(Player player, String input) {
		cancelFor(player, input);
	}
	
	public static void cancelFor(Player player) {
		cancelFor(player, null);
	}
	
	private static void cancelFor(Player player, String text) {
		if (hasMenuOpen(player)) {
			MenuCommandBlock existing = fakeBlocks.get(player.getName()).menu;
			existing.onClose(player, text);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void onClose(Player player, String text) {
		Location fix = fakeBlocks.remove(player.getName()).location;
		
		if (fix == null) {
			Bukkit.getConsoleSender().sendMessage("Player " + player.getName() +
					" closed a command block menu that was not registered!");
		} else {
			player.sendBlockChange(fix, fix.getBlock().getType(), fix.getBlock().getData());
		}
		
		if (menuReadBehavior != null) {
			if ((text != null) || menuReadBehavior.allowNullInput()) {
				menuReadBehavior.onInputRead(player, text);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void openMenu(Player player) {
		Vector offset = player.getLocation().getDirection().clone().normalize().multiply(FAKE_BLOCK_OFFSET_FACTOR);
		Location fakeBlock = player.getLocation().clone().add(offset);
		
		try {
			Class<?> craftPlayerClass = VersionHandler.getCraftBukkitClass("entity.CraftPlayer");
			Class<?> entityPlayerClass = VersionHandler.getCraftClass("EntityPlayer");
			
			Object craftPlayer = craftPlayerClass.cast(player);
			Method getHandle = CommonReflection.getMethod(craftPlayerClass, "getHandle", 0);
			Object entityPlayer = getHandle.invoke(craftPlayer);
			
			Field playerConnectionField = CommonReflection.getField(entityPlayerClass, "playerConnection");
			Method sendPacket = CommonReflection.getMethod(playerConnectionField.getType(), "sendPacket", 1);
			Object playerConnection = playerConnectionField.get(entityPlayer);
			
			Class<?> nbtTagCompoundClass = VersionHandler.getCraftClass("NBTTagCompound");
			Method setString = CommonReflection.getMethod(nbtTagCompoundClass, "setString", new Class<?>[]{
					String.class, String.class});
			Method setInt = CommonReflection.getMethod(nbtTagCompoundClass, "setInt", new Class<?>[]{
					String.class, int.class});
			
			Object nbtTagCompound = nbtTagCompoundClass.newInstance();
			setString.invoke(nbtTagCompound, "id", "Control");
			setString.invoke(nbtTagCompound, "Command", this.defaultCommandLine);
			setInt.invoke(nbtTagCompound, "x", fakeBlock.getBlockX());
			setInt.invoke(nbtTagCompound, "y", fakeBlock.getBlockY());
			setInt.invoke(nbtTagCompound, "z", fakeBlock.getBlockZ());
			
			Class<?> packetPlayOutTileEntityDataClass = VersionHandler.getCraftClass("PacketPlayOutTileEntityData");
			Constructor<?> packetPlayOutTileEntityDataConstructor = packetPlayOutTileEntityDataClass.getConstructor(
					int.class, int.class, int.class, int.class, nbtTagCompoundClass);
			Object packetPlayOutTileEntityData = packetPlayOutTileEntityDataConstructor.newInstance(
					fakeBlock.getBlockX(), fakeBlock.getBlockY(), fakeBlock.getBlockZ(), 2, nbtTagCompound);
			
			Class<?> packetPlayOutOpenSignEditorClass = VersionHandler.getCraftClass("PacketPlayOutOpenSignEditor");
			Constructor<?> packetPlayOutOpenSignEditorConstructor = packetPlayOutOpenSignEditorClass.getConstructor(
					int.class, int.class, int.class);
			Object packetPlayOutOpenSignEditor = packetPlayOutOpenSignEditorConstructor.newInstance(
					fakeBlock.getBlockX(), fakeBlock.getBlockY(), fakeBlock.getBlockZ());
			
			player.sendBlockChange(fakeBlock, Material.COMMAND, (byte) 0);
			sendPacket.invoke(playerConnection, packetPlayOutTileEntityData);
			sendPacket.invoke(playerConnection, packetPlayOutOpenSignEditor);
		} catch (Exception e) {
			// whoops
			e.printStackTrace();
		}
		
		cancelFor(player);
		fakeBlocks.put(player.getName(), new FakeCommandBlock(fakeBlock, this));
	}
	
	public MenuReadBehavior getMenuReadBehavior() {
		return menuReadBehavior;
	}
	
	public void setMenuReadBehavior(MenuReadBehavior menuReadBehavior) {
		this.menuReadBehavior = menuReadBehavior;
	}
	
	public String getDefaultCommandLine() {
		return defaultCommandLine;
	}
	
	public void setDefaultCommandLine(String defaultCommandLine) {
		this.defaultCommandLine = defaultCommandLine;
	}
}
