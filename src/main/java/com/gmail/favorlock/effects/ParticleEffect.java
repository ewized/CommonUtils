package com.gmail.favorlock.effects;

import com.gmail.favorlock.PacketFactory;
import com.gmail.favorlock.entity.EntityHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

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
    private static Random random = new Random();

    ParticleEffect(String particleName) {
        this.particleName = particleName;
    }

    public String getName() {
        return particleName;
    }

    public static void sendPacket(Player player, Location location, ParticleEffect effect) {
        Object packet = PacketFactory.getWorldParticlesPacket(effect.getName(), location);
        EntityHandler.sendPacket(player, packet);
    }
}
