package com.gmail.favorlock.commonutils.network.packets;

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
    public Class<?> getPacketClass() {
        return packet_class;
    }
    
    /**
     * Get an instance of the Packet represented by this class, with the values
     * known to the implementation of this PacketWrapper.
     * 
     * @return The Packet that was created.
     */
    public abstract Object get();
}
