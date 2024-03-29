package com.archeinteractive.dev.commonutils.effects.particle;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.archeinteractive.dev.commonutils.network.packets.WrapperPlayOutWorldParticles;
import com.archeinteractive.dev.commonutils.reflection.EntityHandler;

/**
 * A serializable class for representing a Particle Effect.
 */
public class Particle implements Serializable {

    private static final long serialVersionUID = -7451082459323142696L;

    private final String name;
    private final float x;
    private final float y;
    private final float z;
    private final float speed;
    private final int amount;

    protected Particle(String name, float x, float y, float z, float speed, int amount) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.speed = speed;
        this.amount = amount;
    }

    /**
     * Play this Particle Effect, displaying it at the
     * location specified, and to the players specified.
     *
     * @param location The location to display particles at.
     * @param players  The players to display particles to.
     */
    public void play(Location location, Player... players) {
        Object packet = new WrapperPlayOutWorldParticles(name)
            .setLocation(location)
            .setDeviations(x, y, z)
            .setSpeed(speed)
            .setAmount(amount).get();
        EntityHandler.sendPacket(players, packet);
    }

    /**
     * Play this Particle Effect, displaying it at the
     * location specified. It will display to all
     * players within range of the location.
     *
     * @param location The location to display particles at.
     */
    public void play(Location location) {
        play(location, location.getWorld().getPlayers().toArray(new Player[0]));
    }
}
