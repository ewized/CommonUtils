package com.gmail.favorlock.commonutils.bossbar.version.v1_7;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Location;
import org.bukkit.World;

import com.gmail.favorlock.commonutils.bossbar.FakeDragon;
import com.gmail.favorlock.commonutils.entity.EntityHandler;
import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.MethodBuilder;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;

public class Dragon extends FakeDragon {

    private Object dragon;
    private int id;

    public Dragon(String name, Location location) {
        super(name, location);
    }

    @Override
    public Object getSpawnPacket(World world) {
        Class<?> Entity = VersionHandler.getCraftClass("Entity");
        Class<?> EntityLiving = VersionHandler.getCraftClass("EntityLiving");
        Class<?> EntityEnderDragon = VersionHandler.getCraftClass("EntityEnderDragon");
        Object packet = null;

        try {
            dragon = EntityEnderDragon.getConstructor(VersionHandler.getCraftClass("World"))
                    .newInstance(CommonReflection.getHandle(world));

            new MethodBuilder(EntityEnderDragon, "setLocation", dragon, new Class<?>[] { double.class, double.class, double.class, float.class, float.class })
                    .invoke(getX(), getY(), getZ(), getPitch(), getYaw());

            new MethodBuilder(EntityEnderDragon, "setInvisible", dragon, new Class<?>[] { boolean.class }).invoke(isVisible());

            new MethodBuilder(EntityEnderDragon, "setCustomName", dragon, new Class<?>[] { String.class }).invoke(name);

            new MethodBuilder(EntityEnderDragon, "setHealth", dragon, new Class<?>[] { float.class }).invoke(health);

            Field motX = CommonReflection.getField(Entity, "motX");
            motX.set(dragon, getXvel());

            Field motY = CommonReflection.getField(Entity, "motY");
            motY.set(dragon, getYvel());

            Field motZ = CommonReflection.getField(Entity, "motZ");
            motZ.set(dragon, getZvel());

            this.id = (Integer) new MethodBuilder(EntityEnderDragon, "getId", dragon, new Class<?>[] {}).invokeReturn();

            Class<?> PacketPlayOutSpawnEntityLiving = VersionHandler.getCraftClass("PacketPlayOutSpawnEntityLiving");

            packet = PacketPlayOutSpawnEntityLiving.getConstructor(new Class<?>[] { EntityLiving }).newInstance(dragon);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return packet;
    }

    @Override
    public Object getDestroyPacket() {
        Class<?> PacketPlayOutEntityDestroy = VersionHandler.getCraftClass("PacketPlayOutEntityDestroy");
        Object packet = null;

        try {
            packet = PacketPlayOutEntityDestroy.newInstance();
            Field a = PacketPlayOutEntityDestroy.getDeclaredField("a");
            a.setAccessible(true);
            a.set(packet, new int[] { id });
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return packet;
    }

    @Override
    public Object getMetaPacket(Object watcher) {
        Class<?> DataWatcher = VersionHandler.getCraftClass("DataWatcher");
        Class<?> PacketPlayOutEntityMetadata = VersionHandler.getCraftClass("PacketPlayOutEntityMetadata");
        Object packet = null;

        try {
            packet = PacketPlayOutEntityMetadata.getConstructor(new Class<?>[] { int.class, DataWatcher, boolean.class }).newInstance(id, watcher, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return packet;
    }

    @Override
    public Object getTeleportPacket(Location loc) {
        Class<?> PacketPlayOutEntityTeleport = VersionHandler.getCraftClass("PacketPlayOutEntityTeleport");
        Object packet = null;

        try {
            packet = PacketPlayOutEntityTeleport.getConstructor(new Class<?>[] { int.class, int.class, int.class, int.class, byte.class, byte.class })
                    .newInstance(this.id, loc.getBlockX() * 32,
                            loc.getBlockY() * 32, loc.getBlockZ() * 32,
                            (byte) ((int) loc.getYaw() * 256 / 360),
                            (byte) ((int) loc.getPitch() * 256 / 360));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return packet;
    }

    @Override
    public Object getWatcher() {
        return EntityHandler.getWatcher(dragon, false, health, name);
    }
}
