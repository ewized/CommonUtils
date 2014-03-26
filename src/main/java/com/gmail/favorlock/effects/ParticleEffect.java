package com.gmail.favorlock.effects;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.favorlock.PacketFactory;
import com.gmail.favorlock.entity.EntityHandler;

public enum ParticleEffect {
    HUGE_EXPLOSION("hugeexplosion"),
    LARGE_EXPLODE("largeexplode"),
    FIREWORKS_SPARK("fireworksSpark"),
    BUBBLE("bubble"),
    SUSPEND("suspend"),
    DEPTH_SUSPEND("depthSuspend"),
    TOWN_AURA("townaura"),
    CRIT("crit"),
    MAGIC_CRIT("magicCrit"),
    MOB_SPELL("mobSpell"),
    MOB_SPELL_AMBIENT("mobSpellAmbient"),
    SPELL("spell"),
    INSTANT_SPELL("instantSpell"),
    WITCH_MAGIC("witchMagic"),
    NOTE("note"),
    PORTAL("portal"),
    ENCHANTMENT_TABLE("enchantmenttable"),
    EXPLODE("explode"),
    FLAME("flame"),
    LAVA("lava"),
    SPLASH("splash"),
    LARGE_SMOKE("largesmoke"),
    CLOUD("cloud"),
    RED_DUST("reddust"),
    SNOWBALL_POOF("snowballpoof"),
    DRIP_WATER("dripWater"),
    DRIP_LAVA("dripLava"),
    SNOW_SHOVEL("snowshovel"),
    SLIME("slime"),
    HEART("heart"),
    ANGRY_VILLAGER("angryVillager"),
    HAPPY_VILLAGER("happyVillager");

    private String particleName;

    ParticleEffect(String particleName) {
        this.particleName = particleName;
    }

    public String getName() {
        return particleName;
    }

    public static void sendPacket(Player player, Location location, ParticleEffect effect, float xDev, float yDev, float zDev, float speed, int amount) {
        Object packet = PacketFactory.getWorldParticlesPacket(effect.getName(), location, xDev, yDev, zDev, speed, amount);
        EntityHandler.sendPacket(player, packet);
    }
}
