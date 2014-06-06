package com.gmail.favorlock.commonutils.network.packets;

import org.bukkit.entity.Player;

import com.gmail.favorlock.commonutils.reflection.EntityHandler;

public abstract class PacketWrapper {

    private final Class<?> packet_class;
    
    protected PacketWrapper(Class<?> packet_class) {
        this.packet_class = packet_class;
    }
    
    /**
     * Get the Class of the Packet represented by this class.
     * 
     * @return The Class of the Packet.
     */
    public final Class<?> getPacketClass() {
        return packet_class;
    }
    
    /**
     * Send an instance of the Packet represented by this class (as it would be
     * returned by {@link PacketWrapper#get()}) to the given player(s).
     * 
     * @param players The Player(s) to send this Packet to.
     */
    public void send(Player... players) {
        EntityHandler.sendPacket(players, get());
    }
    
    /**
     * Get an instance of the Packet represented by this class, with the values
     * known to the implementation of this PacketWrapper.
     * 
     * @return The Packet that was created.
     */
    public abstract Object get();
}
