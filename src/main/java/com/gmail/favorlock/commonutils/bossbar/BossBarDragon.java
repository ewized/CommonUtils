package com.gmail.favorlock.commonutils.bossbar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.MethodBuilder;
import org.bukkit.Location;

import com.gmail.favorlock.commonutils.reflection.VersionHandler;

public class BossBarDragon {
	public static final float FULL_HEALTH = 200;
	
	private Object dragon;
	private boolean isVisible = false;
	private float health = 0;
	private String displayedName;
	private Object world;
	private int id;
	
	private int xPos;
	private int yPos;
	private int zPos;
	private int yaw = 0;
	private int pitch = 0;
	private byte xVelocity = 0;
	private byte yVelocity = 0;
	private byte zVelocity = 0;
	
	
	public BossBarDragon(String name, Location loc, int percent){
		this.displayedName = name;
		this.xPos = loc.getBlockX();
		this.yPos = loc.getBlockY();
		this.zPos = loc.getBlockZ();
		this.health = percent / 100F * FULL_HEALTH;
		this.world = CommonReflection.getHandle(loc.getWorld());
	}
	public BossBarDragon(String name, Location loc){
		this.displayedName = name;
		this.xPos = loc.getBlockX();
		this.yPos = loc.getBlockY();
		this.zPos = loc.getBlockZ();
		this.world = CommonReflection.getHandle(loc.getWorld());
	}
	
	public Object getDragonSpawnPacket(){
		Class<?> Entity = VersionHandler.getCraftClass("Entity");
		Class<?> EntityLiving = VersionHandler.getCraftClass("EntityLiving");
		Class<?> EntityEnderDragon = VersionHandler.getCraftClass("EntityEnderDragon");
		Object packet = null;
		try{
			dragon = EntityEnderDragon.getConstructor(VersionHandler.getCraftClass("World")).newInstance(getWorld());
			Method setLoc = CommonReflection.getMethod(EntityEnderDragon, "setLocation", new Class<?>[]{double.class, double.class, double.class, float.class, float.class});
			setLoc.invoke(dragon, getX(), getY(), getZ(), getPitch(), getYaw());
			Method setInvis = CommonReflection.getMethod(EntityEnderDragon, "setInvisible", new Class<?>[]{boolean.class});
			setInvis.invoke(dragon, !isVisible());
			Method setName = CommonReflection.getMethod(EntityEnderDragon, "setCustomName", new Class<?>[]{String.class});
			setName.invoke(dragon, displayedName);
			Method setHealth = CommonReflection.getMethod(EntityEnderDragon, "setHealth", new Class<?>[]{float.class});
			setHealth.invoke(dragon, health);
			Field xVel = CommonReflection.getField(Entity, "motX");
			xVel.set(dragon, getXvelocity());
			Field yVel = CommonReflection.getField(Entity, "motX");
			yVel.set(dragon, getYvelocity());
			Field zVel = CommonReflection.getField(Entity, "motX");
			zVel.set(dragon, getZvelocity());
			Method getId = CommonReflection.getMethod(EntityEnderDragon, "getId", new Class<?>[]{});
			this.id = (Integer) getId.invoke(dragon);
			Class<?> PacketPlayOutSpawnEntityLiving = VersionHandler.getCraftClass("PacketPlayOutSpawnEntityLiving");
			packet = PacketPlayOutSpawnEntityLiving.getConstructor(new Class<?>[]{EntityLiving}).newInstance(dragon);
		}catch(Exception e){
			e.printStackTrace();
		}
		return packet;
	}
	
	public Object getDragonDestroyPacket(){
		Class<?> PacketPlayOutEntityDestroy = VersionHandler.getCraftClass("PacketPlayOutEntityDestroy");
		Object packet = null;
		try{
			packet = PacketPlayOutEntityDestroy.newInstance();
			Field a = PacketPlayOutEntityDestroy.getDeclaredField("a");
			a.setAccessible(true);
			a.set(packet, new int[]{id});
		}catch(Exception e){
			e.printStackTrace();
		}
		return packet;
	}
	
	public Object getMetaPacket(Object watcher){
		Class<?> DataWatcher = VersionHandler.getCraftClass("DataWatcher");
		Class<?> PacketPlayOutEntityMetadata = VersionHandler.getCraftClass("PacketPlayOutEntityMetadata");
		Object packet = null;
		try{
			packet = PacketPlayOutEntityMetadata.getConstructor(new Class<?>[]{int.class, DataWatcher, boolean.class})
					.newInstance(id, watcher, true);
		}catch(Exception e){
			e.printStackTrace();
		}
		return packet;
	}
	
	public Object getTeleportPacket(Location loc){
		Class<?> PacketPlayOutEntityTeleport = VersionHandler.getCraftClass("PacketPlayOutEntityTeleport");
		Object packet = null;
		try{
			packet = PacketPlayOutEntityTeleport.getConstructor(new Class<?>[]{int.class, int.class, int.class, int.class, byte.class, byte.class})
					.newInstance(this.id, loc.getBlockX() * 32, loc.getBlockY() * 32, loc.getBlockZ() * 32, (byte) ((int) loc.getYaw() * 256 / 360), (byte) ((int) loc.getPitch() * 256 / 360));
		}catch(Exception e){
			e.printStackTrace();
		}
		return packet;
	}
	
	public Object getWatcher(){
		Class<?> Entity = VersionHandler.getCraftClass("Entity");
		Class<?> DataWatcherClass = VersionHandler.getCraftClass("DataWatcher");
		Object watcher = null;
		try{
			watcher = DataWatcherClass.getConstructor(new Class<?>[]{Entity}).newInstance(dragon);
			
			new MethodBuilder(DataWatcherClass, "a", watcher, new Class<?>[]{int.class, Object.class})
			.invoke(0, isVisible() ? (byte) 0 : (byte) 0x20)
			.invoke(6, (Float) health)
			.invoke(7, (Integer) 0)
			.invoke(8, (Byte) (byte) 0)
			.invoke(10, (String) displayedName)
			.invoke(11, (Byte) (byte) 1);
		}catch(Exception e){
			e.printStackTrace();
		}
		return watcher;
	}
	
	public float getMaxHealth(){
		return FULL_HEALTH;
	}
	public float getHealth(){
		return health;
	}
	public void setHealthDirectly(float health){
		this.health = health;
	}
	public void setHealthPercent(int percent){
		health = (percent / 100f) * FULL_HEALTH;
	}
	
	public String getDisplayedName(){
		return displayedName;
	}
	public void setDisplayedName(String name){
		displayedName = name;
	}
	
	public int getX(){
		return xPos;
	}
	public void setX(int x){
		this.xPos = x;
	}
	
	public int getY(){
		return yPos;
	}
	public void setY(int y){
		yPos = y;
	}
	
	public int getZ(){
		return zPos;
	}
	public void setZ(int z){
		zPos = z;
	}
	
	public int getYaw(){
		return yaw;
	}
	public void setYaw(int yaw){
		this.yaw = yaw;
	}
	
	public int getPitch(){
		return pitch;
	}
	public void setPitch(int pitch){
		this.pitch = pitch;
	}
	
	public byte getXvelocity(){
		return xVelocity;
	}
	public void setXvelocity(byte xvel){
		xVelocity = xvel;
	}
	
	public byte getYvelocity(){
		return yVelocity;
	}
	public void setYvelocity(byte yvel){
		yVelocity = yvel;
	}
	
	public byte getZvelocity(){
		return zVelocity;
	}
	public void setZvelocity(byte zvel){
		zVelocity = zvel;
	}
	
	public boolean isVisible(){
		return isVisible;
	}
	public void setVisible(boolean visible){
		isVisible = visible;
	}
	
	public Object getWorld(){
		return world;
	}
	public void setWorld(Object cworld){
		world = cworld;
	}
}