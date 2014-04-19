package com.gmail.favorlock.commonutils.effects.sound;

import java.io.File;

import javax.sound.midi.ShortMessage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.gmail.favorlock.commonutils.effects.sound.midi.MidiTransceiver;
import com.gmail.favorlock.commonutils.effects.sound.midi.MidiWrapper;

public class SoundUtil {

    private static final int[] midi_instruments = {
        0, 0, 0, 0, 0, 0, 0, 5, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5,
        6, 6, 6, 6, 6, 6, 6, 6, 5, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 3, 1, 1, 1, 5, 1, 1, 1, 1, 1, 2, 4, 3};
    
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
     * Attempt to play a MIDI file, with the given parameters, to the given
     * players.
     * <p/>
     * A MidiWrapper object will be returned, which can be used to affect the
     * sequence while its playing.
     * <p/>
     * If an exception is encountered during loading the file, it will be
     * caught, and null will be returned.
     * 
     * @param file      The File to read MIDI from.
     * @param tempo     The tempo to play at.
     * @param volume    The volume to play at.
     * @param players   The Players to play to.
     * @return  A MidiWrapper object.
     */
    public static MidiWrapper playMidi(File file, float tempo, float volume, Player... players) {
        try {
            return MidiWrapper.playMidi(file, tempo, volume, players);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Attempt to play a MIDI file, with the given tempo, to the given players.
     * <p/>
     * A MidiWrapper object will be returned, which can be used to affect the
     * sequence while its playing.
     * <p/>
     * If an exception is encountered during loading the file, it will be
     * caught, and null will be returned.
     * 
     * @param file      The File to read MIDI from.
     * @param tempo     The tempo to play at.
     * @param players   The Players to play to.
     * @return  A MidiWrapper object.
     */
    public static MidiWrapper playMidi(File file, float tempo, Player... players) {
        return playMidi(file, tempo, MidiTransceiver.getDefaultVolume(), players);
    }
    
    /**
     * Attempt to play a MIDI file to the given players.
     * <p/>
     * A MidiWrapper object will be returned, which can be used to affect the
     * sequence while its playing.
     * <p/>
     * If an exception is encountered during loading the file, it will be
     * caught, and null will be returned.
     * 
     * @param file      The File to read MIDI from.
     * @param players   The Players to play to.
     * @return  A MidiWrapper object.
     */
    public static MidiWrapper playMidi(File file, Player... players) {
        return playMidi(file, MidiTransceiver.getDefaultTempo(), MidiTransceiver.getDefaultVolume(), players);
    }
    
    /**
     * Get the float pitch value for a given note.
     *
     * @param note_id The note.
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
     * @return <b>float</b> representing the pitch of this note.
     */
    public static float getPitchFor(byte note_id) {
        if (note_id < 0)
            return .0f;
        
        if (note_id > 24)
            return 2.0f;
        
        final byte middle_note_id = 12;
        
        return (float)  Math.pow(2.0, (note_id - middle_note_id) / 12.0);
    }
    
    public static float getPitchFor(ShortMessage sm) {
        return getPitchFor(getNoteFor(sm));
    }
    
    public static Note getNoteFor(ShortMessage sm) {
        if (sm.getCommand() == ShortMessage.NOTE_ON) {
            final byte midi_middle_note_id = 54;
            
            return new Note((sm.getData1() - midi_middle_note_id % 12) % 24);
        } else {
            return null;
        }
    }
    
    public static Sound getSoundFor(int midi_patch) {
        switch (midi_instruments[midi_patch]) {
        case 1:
            return Sound.NOTE_BASS_GUITAR;
        case 2:
            return Sound.NOTE_SNARE_DRUM;
        case 3:
            return Sound.NOTE_STICKS;
        case 4:
            return Sound.NOTE_BASS_DRUM;
        case 5:
            return Sound.NOTE_PLING;
        case 6:
            return Sound.NOTE_BASS;
        default:
            return Sound.NOTE_PIANO;
        }
    }
}
