package com.gmail.favorlock.commonutils.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.favorlock.commonutils.CommonUtils;

public class EntityHandler {

    public static boolean injectCustomEntity(
            JavaPlugin plugin, Class<?> entity_class, EntityType emulate) {
        return injectCustomEntity(plugin, entity_class, emulate, false);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
    public static boolean injectCustomEntity(
            JavaPlugin plugin, Class<?> entity_class, EntityType emulate, boolean overwrite_id) {
        String entity_name = String.format("%s:%s", plugin.getName(), entity_class.getSimpleName());
        Class<?> classEntityTypes = VersionHandler.getNMSClass("EntityTypes");
        Field fieldStringToClass, fieldClassToString, fieldIdToClass, fieldClassToId, fieldStringToId;
        Map string_class, class_string, id_class, class_id, string_id;
        
        try {
            (fieldStringToClass = classEntityTypes.getDeclaredField("c")).setAccessible(true);
            (fieldClassToString = classEntityTypes.getDeclaredField("d")).setAccessible(true);
            (fieldClassToId = classEntityTypes.getDeclaredField("f")).setAccessible(true);
            (fieldStringToId = classEntityTypes.getDeclaredField("g")).setAccessible(true);
            (string_class = (Map) fieldStringToClass.get(null)).put(entity_name, entity_class);
            (class_string = (Map) fieldClassToString.get(null)).put(entity_class, entity_name);
            (class_id = (Map) fieldClassToId.get(null)).put(entity_class, new Integer(emulate.getTypeId()));
            (string_id = (Map) fieldStringToId.get(null)).put(entity_name, new Integer(emulate.getTypeId()));
            fieldStringToClass.set(null, string_class);
            fieldClassToString.set(null, class_string);
            fieldClassToId.set(null, class_id);
            fieldStringToId.set(null, string_id);
            
            if (overwrite_id) {
                (fieldIdToClass = classEntityTypes.getDeclaredField("e")).setAccessible(true);
                (id_class = (Map) fieldIdToClass.get(null)).put(new Integer(emulate.getTypeId()), entity_class);
                fieldIdToClass.set(null, id_class);
            }
            
            CommonUtils.getPlugin().getLogger().info(String.format(
                    "Injected a custom entity %s -> %s", entity_name, emulate.name()));
            return true;
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            CommonUtils.getPlugin().getLogger().info(String.format(
                    "Failed to inject a custom entity %s -> %s", entity_name, emulate.name()));
            e.printStackTrace();
            return false;
        }
    }
    
    public static <T extends Entity> T spawnEntity(Location spawn, Class<T> entity_class) {
        return spawn.getWorld().spawn(spawn, entity_class);
    }
    
    public static void sendPacketToAll(Object packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(player, packet);
        }
    }

    public static void sendPacket(Player[] players, Object packet) {
        for (Player player : players)
            sendPacket(player, packet);
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object nmsPlayer = CommonReflection.getHandle(player);
            Field playerConnection = nmsPlayer.getClass().getField("playerConnection");
            Object connection = playerConnection.get(nmsPlayer);
            Method sendPacket = CommonReflection.getMethod(connection.getClass(), "sendPacket");
            sendPacket.invoke(connection, packet);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static Object getWatcher(Entity entity, boolean visible, float health, String name) {
        Class<?> clazz = VersionHandler.getNMSClass("DataWatcher");
        Object watcher = null;

        try {
            Object nmsEntity = CommonReflection.getHandle(entity);
            watcher = clazz.getConstructors()[0].newInstance(nmsEntity);

            new MethodBuilder(clazz, "a", watcher, new Class<?>[]{int.class, Object.class})
                    .invoke(0, visible ? (byte) 0 : (byte) 0x20)
                    .invoke(6, (Float) health)
                    .invoke(7, (Integer) 0)
                    .invoke(8, (Byte) (byte) 0)
                    .invoke(10, name)
                    .invoke(11, (Byte) (byte) 1);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return watcher;
    }

    public static Object getWatcher(Object entity, boolean visible, float health, String name) {
        Class<?> clazz = VersionHandler.getNMSClass("DataWatcher");
        Object watcher = null;

        try {
            watcher = clazz.getConstructors()[0].newInstance(entity);

            new MethodBuilder(clazz, "a", watcher, new Class<?>[]{int.class, Object.class})
                    .invoke(0, visible ? (byte) 0 : (byte) 0x20)
                    .invoke(6, (Float) health)
                    .invoke(7, (Integer) 0)
                    .invoke(8, (Byte) (byte) 0)
                    .invoke(10, name)
                    .invoke(11, (Byte) (byte) 1);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return watcher;
    }

}
