package com.gmail.favorlock.commonutils.network.packets;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.gmail.favorlock.commonutils.CommonUtils;
import com.gmail.favorlock.commonutils.ui.MenuCommandBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import com.google.common.base.Charsets;

/**
 * A packet listener for use in cases where
 * packet listening is unavoidable. An
 * instance of this class will only be
 * active if ProtocolLib is present.
 * Functionality using this class
 * <b>may be unusable</b>
 * in the case that the
 * PacketListener is not active.
 * <p><br>
 * <code>CommonUtils.isPacketListenerActive();</code>
 * <p> can be used to determine if the
 * packet listener is active.
 */
public class PacketListener implements com.comphenix.protocol.events.PacketListener {

    private CommonUtils instance;

    public PacketListener(CommonUtils instance) {
        this.instance = instance;
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    @Override
    public Plugin getPlugin() {
        return instance;
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist
                .newBuilder()
                .normal()
                .gamePhase(GamePhase.PLAYING)
                .options(new ListenerOptions[]{})
                .types(
                        Client.CUSTOM_PAYLOAD,
                        Client.CHAT,
                        Client.ARM_ANIMATION,
                        Client.BLOCK_PLACE,
                        Client.WINDOW_CLICK,
                        Client.USE_ENTITY,
                        Client.BLOCK_DIG,
                        Client.CLOSE_WINDOW,
                        Client.SET_CREATIVE_SLOT)
                .build();
    }

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return ListeningWhitelist
                .newBuilder()
                .normal()
                .gamePhase(GamePhase.PLAYING)
                .options(new ListenerOptions[]{})
                .types(
                        Server.BLOCK_CHANGE,
                        Server.CLOSE_WINDOW,
                        Server.OPEN_WINDOW)
                .build();
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        PacketContainer packet = e.getPacket();
        Player effecting = e.getPlayer();
        PacketType type = e.getPacketType();

        if (MenuCommandBlock.hasMenuOpen(effecting)) {
            Location fake = MenuCommandBlock.getFakeBlockLocationFor(effecting);

            if (type.equals(PacketType.Play.Client.CUSTOM_PAYLOAD)) {
                String tag = packet.getStrings().read(0);
                byte[] data = packet.getByteArrays().read(0);

                if (tag.equals("MC|AdvCdm")) {
                    ByteArrayInputStream b_in = new ByteArrayInputStream(data);
                    DataInputStream in = new DataInputStream(b_in);

                    try {
                        if (in.readByte() == 0) {
                            int x = in.readInt(),
                                    y = in.readInt(),
                                    z = in.readInt();

                            in.readByte();

                            byte[] bytes = new byte[in.available()];
                            in.read(bytes);
                            String input = new String(bytes, Charsets.UTF_8).trim();

                            if ((fake.getBlockX() == x) &&
                                    (fake.getBlockY() == y) &&
                                    (fake.getBlockZ() == z)) {
                                MenuCommandBlock.processInput(effecting, input);
                                e.setCancelled(true);
                            } else {
                                MenuCommandBlock.cancelFor(effecting);
                            }
                        }
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            } else {
                MenuCommandBlock.cancelFor(effecting);
            }
        }
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        PacketContainer packet = e.getPacket();
        Player affected = e.getPlayer();
        PacketType type = e.getPacketType();

        if (type.equals(PacketType.Play.Server.BLOCK_CHANGE)) {
            if (MenuCommandBlock.hasMenuOpen(affected)) {
                Material material = packet.getBlocks().read(0);
                int affectedX = packet.getIntegers().read(0);
                int affectedY = packet.getIntegers().read(1);
                int affectedZ = packet.getIntegers().read(2);

                if (!material.equals(Material.COMMAND)) {
                    Location fake = MenuCommandBlock.getFakeBlockLocationFor(affected);

                    if ((fake.getBlockX() == affectedX) &&
                            (fake.getBlockY() == affectedY) &&
                            (fake.getBlockZ() == affectedZ)) {
                        e.setCancelled(true);
                    }
                }
            }
        } else if (type.equals(PacketType.Play.Server.CLOSE_WINDOW) || type.equals(PacketType.Play.Server.OPEN_WINDOW)) {
            MenuCommandBlock.cancelFor(affected);
        }
    }
}
