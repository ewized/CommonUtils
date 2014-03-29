package com.gmail.favorlock.commonutils.effects.sound;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundEffect implements Serializable {

    private static final long serialVersionUID = -1813042688282789503L;

    private Sound sound;
    private float volume;
    private float pitch;

    public SoundEffect(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @SuppressWarnings("deprecation")
    public SoundEffect(Sound sound, float volume, Note pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = SoundUtil.getPitchFor(pitch.getId());
    }

    /**
     * Play this Sound Effect at the location specified,
     * and to the players specified.
     *
     * @param location The location to play sound at.
     * @param players  The players to play sound to.
     */
    public void play(Location location, Player... players) {
        for (Player player : players)
            player.playSound(location, sound, volume, pitch);
    }

    /**
     * Play this Sound Effect at the location specified.
     * It will play to all players within range.
     *
     * @param location The location to play sound at.
     */
    public void play(Location location) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    /**
     * Play this Sound Effect for the players specified.
     * The sound will play at their location.
     *
     * @param players The players to play sound to.
     */
    public void play(Player... players) {
        for (Player player : players)
            player.playSound(player.getEyeLocation(), sound, volume, pitch);
    }
}
