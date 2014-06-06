package com.gmail.favorlock.commonutils.effects.particle;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import com.gmail.favorlock.commonutils.network.packets.WrapperPlayOutWorldParticles;
import com.gmail.favorlock.commonutils.reflection.EntityHandler;

public enum ParticleEffect {

    ANGRY_VILLAGER("angryVillager"),
    BUBBLE("bubble"),
    CLOUD("cloud"),
    CRIT("crit"),
    DEPTH_SUSPEND("depthSuspend"),
    DRIP_LAVA("dripLava"),
    DRIP_WATER("dripWater"),
    ENCHANTMENT_TABLE("enchantmenttable"),
    EXPLODE("explode"),
    FIREWORKS_SPARK("fireworksSpark"),
    FLAME("flame"),
    FOOTSTEP("footstep"),
    HAPPY_VILLAGER("happyVillager"),
    HEART("heart"),
    HUGE_EXPLOSION("hugeexplosion"),
    INSTANT_SPELL("instantSpell"),
    LARGE_EXPLODE("largeexplode"),
    LARGE_SMOKE("largesmoke"),
    LAVA("lava"),
    MAGIC_CRIT("magicCrit"),
    MOB_SPELL("mobSpell"),
    MOB_SPELL_AMBIENT("mobSpellAmbient"),
    NOTE("note"),
    PORTAL("portal"),
    RED_DUST("reddust"),
    SLIME("slime"),
    SMOKE("smoke"),
    SNOW_SHOVEL("snowshovel"),
    SNOWBALL_POOF("snowballpoof"),
    SPELL("spell"),
    SPLASH("splash"),
    SUSPEND("suspend"),
    TOWN_AURA("townaura"),
    WAKE("wake"),
    WITCH_MAGIC("witchMagic"),;

    private String particleName;

    private ParticleEffect(String particleName) {
        this.particleName = particleName;
    }

    /**
     * Get a Particle with the specified options.
     * This Particle can then be easily displayed at any location.
     *
     * @param xDev   A value representing how far the particle can vary
     *               on the x-axis.
     * @param yDev   A value representing how far the particle can vary
     *               on the y-axis.
     * @param zDev   A value representing how far the particle can vary
     *               on the z-axis.
     * @param speed  This value sometimes controls the speed, other times
     *               it may change the color or some other attribute.
     * @param amount The number of particles to display.
     * @return A Particle object representing the particle effect.
     */
    public Particle get(float xDev, float yDev, float zDev, float speed, int amount) {
        return new Particle(particleName, xDev, yDev, zDev, speed, amount);
    }

    /**
     * Send a Particle once, with the specified options. All players
     * specified and within range will be able to see the particles.
     *
     * @param location The location that these particles should display at.
     * @param xDev     A value representing how far the particle can vary
     *                 on the x-axis.
     * @param yDev     A value representing how far the particle can vary
     *                 on the y-axis.
     * @param zDev     A value representing how far the particle can vary
     *                 on the z-axis.
     * @param speed    This value sometimes controls the speed, other times
     *                 it may change the color or some other attribute.
     * @param amount   The number of particles to display.
     * @param players  The player(s) that should see these particles.
     */
    public void send(Location location, float xDev, float yDev, float zDev, float speed, int amount, Player... players) {
        Object packet = new WrapperPlayOutWorldParticles(particleName)
            .setLocation(location)
            .setDeviations(xDev, yDev, zDev)
            .setSpeed(speed)
            .setAmount(amount).get();
        EntityHandler.sendPacket(players, packet);
    }

    /**
     * Send a Particle once, with the specified options. Any players
     * within range of the particles will be able to see them.
     *
     * @param location The location that these particles should display at.
     * @param xDev     A value representing how far the particle can vary
     *                 on the x-axis.
     * @param yDev     A value representing how far the particle can vary
     *                 on the y-axis.
     * @param zDev     A value representing how far the particle can vary
     *                 on the z-axis.
     * @param speed    This value sometimes controls the speed, other times
     *                 it may change the color or some other attribute.
     * @param amount   The number of particles to display.
     */
    public void send(Location location, float xDev, float yDev, float zDev, float speed, int amount) {
        send(location, xDev, yDev, zDev, speed, amount, location.getWorld().getPlayers().toArray(new Player[0]));
    }

    // Particles that take on the appearance of blocks
    public static class BLOCK_PARTICLE {
        /**
         * Get a Particle with the specified options.
         * This Particle can then be easily displayed at any location.
         *
         * @param materialdata The material data of the block that should be used
         *                     to create the particle(s).
         * @param xDev         A value representing how far the particle can vary
         *                     on the x-axis.
         * @param yDev         A value representing how far the particle can vary
         *                     on the y-axis.
         * @param zDev         A value representing how far the particle can vary
         *                     on the z-axis.
         * @param speed        A value representing how fast the particle will move.
         * @param amount       The number of particles to display.
         * @return A Particle object representing the particle effect.
         */
        @SuppressWarnings("deprecation")
        public static Particle get(MaterialData materialdata, float xDev, float yDev, float zDev, float speed, int amount) {
            int id = materialdata.getItemType().getId();
            byte data = materialdata.getData() > 0 ? materialdata.getData() : 0;
            String particle_name = "blockdust_" + id + "_" + data;
            return new Particle(particle_name, xDev, yDev, zDev, speed, amount);
        }

        /**
         * Send a Particle once, with the specified options. All players
         * specified and within range will be able to see the particles.
         *
         * @param materialdata The material data of the block that should be used
         *                     to create the particle(s).
         * @param location     The location that these particles should display at.
         * @param xDev         A value representing how far the particle can vary
         *                     on the x-axis.
         * @param yDev         A value representing how far the particle can vary
         *                     on the y-axis.
         * @param zDev         A value representing how far the particle can vary
         *                     on the z-axis.
         * @param speed        A value representing how fast the particle will move.
         * @param amount       The number of particles to display.
         * @param players      The player(s) that should see these particles.
         */
        @SuppressWarnings("deprecation")
        public static void send(MaterialData materialdata, Location location, float xDev, float yDev, float zDev, float speed, int amount, Player... players) {
            int id = materialdata.getItemType().getId();
            byte data = materialdata.getData() > 0 ? materialdata.getData() : 0;
            Object packet = new WrapperPlayOutWorldParticles("blockdust_" + id + "_" + data)
                .setLocation(location)
                .setDeviations(xDev, yDev, zDev)
                .setSpeed(speed)
                .setAmount(amount).get();
            EntityHandler.sendPacket(players, packet);
        }

        /**
         * Send a Particle once, with the specified options. Any players
         * within range of the particles will be able to see them.
         *
         * @param materialdata The material data of the block that should be used
         *                     to create the particle(s).
         * @param location     The location that these particles should display at.
         * @param xDev         A value representing how far the particle can vary
         *                     on the x-axis.
         * @param yDev         A value representing how far the particle can vary
         *                     on the y-axis.
         * @param zDev         A value representing how far the particle can vary
         *                     on the z-axis.
         * @param speed        A value representing how fast the particle will move.
         * @param amount       The number of particles to display.
         */
        public static void send(MaterialData materialdata, Location location, float xDev, float yDev, float zDev, float speed, int amount) {
            send(materialdata, location, xDev, yDev, zDev, speed, amount, location.getWorld().getPlayers().toArray(new Player[0]));
        }
    }

    // Particles that take on the appearance of items
    public static class ITEM_PARTICLE {
        /**
         * Get a Particle with the specified options.
         * This Particle can then be easily displayed at any location.
         *
         * @param material The material that should be used for the particle.
         *                 Will cause unrecognizable particles if not an item.
         * @param xDev     A value representing how far the particle can vary
         *                 on the x-axis.
         * @param yDev     A value representing how far the particle can vary
         *                 on the y-axis.
         * @param zDev     A value representing how far the particle can vary
         *                 on the z-axis.
         * @param speed    A value representing how fast the particle will move.
         * @param amount   The number of particles to display.
         * @return A Particle object representing the particle effect.
         */
        @SuppressWarnings("deprecation")
        public static Particle get(Material material, float xDev, float yDev, float zDev, float speed, int amount) {
            int id = material.getId();
            String particle_name = "iconcrack_" + id;
            return new Particle(particle_name, xDev, yDev, zDev, speed, amount);
        }

        /**
         * Send a Particle once, with the specified options. All players
         * specified and within range will be able to see the particles.
         *
         * @param material The material that should be used for the particle.
         *                 Will cause unrecognizable particles if not an item.
         * @param location The location that these particles should display at.
         * @param xDev     A value representing how far the particle can vary
         *                 on the x-axis.
         * @param yDev     A value representing how far the particle can vary
         *                 on the y-axis.
         * @param zDev     A value representing how far the particle can vary
         *                 on the z-axis.
         * @param speed    A value representing how fast the particle will move.
         * @param amount   The number of particles to display.
         * @param players  The player(s) that should see these particles.
         */
        @SuppressWarnings("deprecation")
        public static void send(Material material, Location location, float xDev, float yDev, float zDev, float speed, int amount, Player... players) {
            int id = material.getId();
            Object packet = new WrapperPlayOutWorldParticles("iconcrack_" + id)
                .setLocation(location)
                .setDeviations(xDev, yDev, zDev)
                .setSpeed(speed)
                .setAmount(amount).get();
            EntityHandler.sendPacket(players, packet);
        }

        /**
         * Send a Particle once, with the specified options. Any players
         * within range of the particles will be able to see them.
         *
         * @param material The material that should be used for the particle.
         *                 Will cause unrecognizable particles if not an item.
         * @param location The location that these particles should display at.
         * @param xDev     A value representing how far the particle can vary
         *                 on the x-axis.
         * @param yDev     A value representing how far the particle can vary
         *                 on the y-axis.
         * @param zDev     A value representing how far the particle can vary
         *                 on the z-axis.
         * @param speed    A value representing how fast the particle will move.
         * @param amount   The number of particles to display.
         */
        public static void send(Material material, Location location, float xDev, float yDev, float zDev, float speed, int amount) {
            send(material, location, xDev, yDev, zDev, speed, amount, location.getWorld().getPlayers().toArray(new Player[0]));
        }
    }

    /**
     * Vestigial method of an older system;
     * provided for backwards compatibility.
     *
     * @deprecated This may be removed
     * in a later version.
     */
    public static void sendPacket(Player player, Location location, ParticleEffect effect, float xDev, float yDev, float zDev, float speed, int amount) {
        Object packet = new WrapperPlayOutWorldParticles(effect.particleName)
            .setLocation(location)
            .setDeviations(xDev, yDev, zDev)
            .setSpeed(speed)
            .setAmount(amount).get();
        EntityHandler.sendPacket(player, packet);
    }
}
