package com.gmail.favorlock.commonutils.bossbar;

import org.bukkit.Location;
import org.bukkit.World;

public abstract class FakeDragon {

    public static final float MAX_HEALTH = 200;
    private int x;
    private int y;
    private int z;

    private int pitch = 0;
    private int yaw = 0;
    private byte xvel = 0;
    private byte yvel = 0;
    private byte zvel = 0;
    public float health = 0;
    private boolean visible = false;
    public String name;

    public FakeDragon(String name, Location location, int percent) {
        this.name = name;
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.yaw = (int) location.getYaw();
        this.pitch = (int) location.getPitch();
        this.health = percent / 100F * MAX_HEALTH;
    }

    public FakeDragon(String name, Location location) {
        this.name = name;
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }


    public float getMaxHealth() {
        return MAX_HEALTH;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float percent) {
        this.health = percent / 100F * MAX_HEALTH;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.pitch = (int) location.getPitch();
        this.yaw = (int) location.getY();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public int getYaw() {
        return yaw;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public byte getXvel() {
        return xvel;
    }

    public void setXvel(byte xvel) {
        this.xvel = xvel;
    }

    public byte getYvel() {
        return yvel;
    }

    public void setYvel(byte yvel) {
        this.yvel = yvel;
    }

    public byte getZvel() {
        return zvel;
    }

    public void setZvel(byte zvel) {
        this.zvel = zvel;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public abstract Object getSpawnPacket(World world);

    public abstract Object getDestroyPacket();

    public abstract Object getMetaPacket(Object watcher);

    public abstract Object getTeleportPacket(Location loc);

    public abstract Object getWatcher();
}
