package com.gmail.favorlock.commonutils.effects.sound;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {

    /**
     * Play a sound for a player.
     */
    public static void playSound(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
    
    /**
     * Play a sound for a player.
     */
    public static void playSound(Player player, Sound sound, float volume, Note pitch) {
        playSound(player, sound, volume, getPitchFor(pitch));
    }
    
    /**
     * Play a sound at a location.
     */
    public static void playSound(Location location, Sound sound, float volume, float pitch) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }
    
    /**
     * Play a sound at a location.
     */
    public static void playSound(Location location, Sound sound, float volume, Note pitch) {
        playSound(location, sound, volume, getPitchFor(pitch));
    }
    
    /**
     * Play a sound for all players.
     */
    public static void playAll(Sound sound, float volume, float pitch) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            playSound(player, sound, volume, pitch);
        }
    }
    
    /**
     * Play a sound for all players.
     */
    public static void playAll(Sound sound, float volume, Note pitch) {
        playAll(sound, volume, getPitchFor(pitch));
    }
    
    /**
     * Get the float pitch value for a given note.
     * 
     * @param note_id The note.
     * 
     * @return <b>float</b> representing the pitch of this note.
     */
    @SuppressWarnings("deprecation")
    public static float getPitchFor(Note note) {
        return getPitchFor(note.getId());
    }
    
    /**
     * Get the float pitch value for a given note id.
     * 
     * @param note_id The numerical id of the note (0-24).
     * 
     * @return <b>float</b> representing the pitch of this note.
     */
    public static float getPitchFor(byte note_id) {
        switch (note_id) {
        case 0:// F#
            return 0.5f;
        case 1:// G
            return 0.53f;
        case 2:// G#
            return 0.56f;
        case 3:// A
            return 0.6f;
        case 4:// A#
            return 0.63f;
        case 5:// B
            return 0.67f;
        case 6:// C
            return 0.7f;
        case 7:// C#
            return 0.76f;
        case 8:// D
            return 0.8f;
        case 9:// D#
            return 0.84f;
        case 10:// E
            return 0.9f;
        case 11:// F
            return 0.94f;
        case 12:// F#
            return 1.0f;
        case 13:// G
            return 1.06f;
        case 14:// G#
            return 1.12f;
        case 15:// A
            return 1.18f;
        case 16:// A#
            return 1.26f;
        case 17:// B
            return 1.34f;
        case 18:// C
            return 1.42f;
        case 19:// C#
            return 1.5f;
        case 20:// D
            return 1.6f;
        case 21:// D#
            return 1.68f;
        case 22:// E
            return 1.78f;
        case 23:// F
            return 1.88f;
        case 24:// F#
            return 2.0f;
        default:
            return 0f;
        }
    }
}
