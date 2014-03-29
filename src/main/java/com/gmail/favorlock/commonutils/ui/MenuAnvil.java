package com.gmail.favorlock.commonutils.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;

public class MenuAnvil extends MenuBase {

    private static HashMap<String, MenuAnvil> playersAnvils = new HashMap<>();

    private HashMap<String, Inventory> anvils = new HashMap<>();
    private HashMap<String, Object> containerAnvils = new HashMap<>();
    private MenuClickBehavior resultClickBehavior;

    public MenuAnvil() {
    }

    public static boolean hasOpenAnvil(Player player) {
        if (playersAnvils.containsKey(player.getName())) {
            return true;
        }
        return false;
    }

    public static MenuAnvil getOpenAnvil(Player player) {
        if (!hasOpenAnvil(player)) {
            return null;
        }
        return playersAnvils.get(player.getName());
    }

    public static MenuAnvil removeOpenAnvil(Player player) {
        if (!hasOpenAnvil(player)) {
            return null;
        }
        return playersAnvils.remove(player.getName());
    }

    public void onClose(Player player) {
        Inventory closed = anvils.remove(player.getName());
        if (closed == null) {
            Bukkit.getConsoleSender().sendMessage("Player " + player.getName() +
                    " closed an anvil inventory that was not registered!");
        } else {
            closed.clear(0);
            closed.clear(1);
        }
        containerAnvils.remove(player.getName());
    }

    public void setResultClickBehavior(MenuClickBehavior onClickAnvilResult) {
        resultClickBehavior = onClickAnvilResult;
    }

    public MenuClickBehavior getResultClickBehavior() {
        return resultClickBehavior;
    }

    @SuppressWarnings("deprecation")
    protected void selectMenuItem(Player player, int index) {
        if (index == 2) {
            Class<?> containerAnvilClass = VersionHandler.getCraftClass("ContainerAnvil");
            Field anvilTextField = CommonReflection.getField(containerAnvilClass, "n");
            anvilTextField.setAccessible(true);
            try {
                String anvilText = (String) anvilTextField.get(containerAnvils.get(player.getName()));
                if (resultClickBehavior != null) {
                    resultClickBehavior.onClick(player, anvilText);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage("Well, oops..");
            }
        } else if (items.containsKey(index)) {
            MenuItem item = items.get(index);
            item.onClick(player);
        }
        player.updateInventory();
    }

    @SuppressWarnings("deprecation")
    public void openMenu(Player player) {
        Class<?> containerAnvilClass = VersionHandler.getCraftClass("ContainerAnvil");
        Class<?> craftPlayerClass = VersionHandler.getCraftBukkitClass("entity.CraftPlayer");
        Class<?> entityPlayerClass = VersionHandler.getCraftClass("EntityPlayer");
        Class<?> entityHumanClass = VersionHandler.getCraftClass("EntityHuman");
        Class<?> entityLivingClass = VersionHandler.getCraftClass("EntityLiving");
        Class<?> entityClass = VersionHandler.getCraftClass("Entity");
        Class<?> craftInventoryViewClass = VersionHandler.getCraftBukkitClass("inventory.CraftInventoryView");
        Class<?> containerClass = VersionHandler.getCraftClass("Container");
        Class<?> iCraftingClass = VersionHandler.getCraftClass("ICrafting");
        Class<?> packetPlayOutOpenWindowClass = VersionHandler.getCraftClass("PacketPlayOutOpenWindow");

        try {
            Object craftPlayer = craftPlayerClass.cast(player);
            Method getHandle = CommonReflection.getMethod(craftPlayerClass, "getHandle", 0);
            Object entityPlayer = getHandle.invoke(craftPlayer);
            Object entityHuman = entityHumanClass.cast(entityPlayer);
            Object entityLiving = entityLivingClass.cast(entityHuman);
            Object entity = entityClass.cast(entityLiving);

            Field inventoryEntityPlayer = CommonReflection.getField(entityHumanClass, "inventory");
            Field worldEntityPlayer = CommonReflection.getField(entityClass, "world");

            Object containerAnvil = containerAnvilClass.getConstructor(inventoryEntityPlayer.getType(),
                    worldEntityPlayer.getType(), int.class, int.class, int.class, entityHumanClass).newInstance(
                    inventoryEntityPlayer.get(entityHuman), worldEntityPlayer.get(entity), 0, 0, 0, entityHuman);
            Field checkReachable = CommonReflection.getField(containerClass, "checkReachable");
            checkReachable.set(containerClass.cast(containerAnvil), false);
            Method getBukkitView = CommonReflection.getMethod(containerAnvilClass, "getBukkitView", 0);
            Method getTopInventory = CommonReflection.getMethod(craftInventoryViewClass, "getTopInventory", 0);

            containerAnvils.put(player.getName(), containerAnvil);
            Inventory inventory = (Inventory) getTopInventory.invoke(getBukkitView.invoke(containerAnvil));
            for (Entry<Integer, MenuItem> inMenu : items.entrySet()) {
                inventory.setItem(inMenu.getKey(), inMenu.getValue().getItemStack());
            }
            playersAnvils.put(player.getName(), this);
            anvils.put(player.getName(), inventory);

            Method nextContainerCounter = CommonReflection.getMethod(entityPlayerClass, "nextContainerCounter", 0);
            Field playerConnectionField = CommonReflection.getField(entityPlayerClass, "playerConnection");
            Method sendPacket = CommonReflection.getMethod(playerConnectionField.getType(), "sendPacket", 1);

            Field activeContainerField = CommonReflection.getField(entityHumanClass, "activeContainer");
            Method addSlotListener = CommonReflection.getMethod(containerClass, "addSlotListener", 1);
            Field windowIdField = CommonReflection.getField(containerClass, "windowId");

            int c = (Integer) nextContainerCounter.invoke(entityPlayer);

            Object packet = packetPlayOutOpenWindowClass.getConstructor(int.class, int.class, String.class, int.class,
                    boolean.class).newInstance(c, 8, "Anvil titles don't work", 9, true);

            Object playerConnection = playerConnectionField.get(entityPlayer);
            sendPacket.invoke(playerConnection, packet);
            activeContainerField.set(entityHuman, containerAnvil);
            addSlotListener.invoke(containerAnvil, iCraftingClass.cast(entityPlayer));
            windowIdField.set(containerAnvil, c);
            player.updateInventory();
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("I really tried.. :(");
        }
    }

    @SuppressWarnings("deprecation")
    public void closeMenu(Player player) {
        if (playersAnvils.containsKey(player.getName())) {
            player.closeInventory();
            player.updateInventory();
        }
    }

    public boolean addMenuItem(MenuItem item, int index) {
        if ((index > 1) || (index < 0)) {
            throw new IllegalArgumentException("Given index " + index + " is not in the range [0, 1]!");
        }
        items.put(index, item);
        item.addToMenu(this);

        return true;
    }

    public boolean removeMenuItem(int index) {
        items.remove(index).removeFromMenu(this);

        return true;
    }
}