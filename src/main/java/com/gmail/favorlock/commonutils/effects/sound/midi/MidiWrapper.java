package com.gmail.favorlock.commonutils.effects.sound.midi;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import org.bukkit.entity.Player;

public class MidiWrapper {

    private final WeakReference<MidiTransceiver> midi;
    
    private MidiWrapper(MidiTransceiver midi) {
        this.midi = new WeakReference<>(midi);
    }
    
    /**
     * Get whether or not this MidiWrapper's MidiTransceiver is still valid.
     * 
     * @return <b>true</b> if the transceiver is still valid,
     *         <b>false</b> otherwise.
     */
    public boolean isValid() {
        return midi.get() != null;
    }
    
    /**
     * Add the given player to the transmitter's player list.
     * 
     * @throws IllegalStateException
     *     If this MidiWrapper's transceiver is no longer valid.
     * 
     * @param player    The Player to add.
     */
    public void addPlayer(Player player) {
        MidiTransceiver transceiver = midi.get();
        
        if (isValid()) {
            transceiver.addPlayer(player);
        } else {
            throw new IllegalStateException("This MidiWrapper is no longer valid; the sequence has likely ended.");
        }
    }
    
    /**
     * Remove the given player from the transmitter's player list, if present.
     * 
     * @throws IllegalStateException
     *     If this MidiWrapper's transceiver is no longer valid.
     * 
     * @param player    The Player to remove.
     */
    public void removePlayer(Player player) {
        MidiTransceiver transceiver = midi.get();
        
        if (isValid()) {
            transceiver.removePlayer(player);            
        } else {
            throw new IllegalStateException("This MidiWrapper is no longer valid; the sequence has likely ended.");
        }
    }
    
    /**
     * Clear the transceiver's player list.
     * 
     * @throws IllegalStateException
     *     If this MidiWrapper's transceiver is no longer valid.
     */
    public void clearPlayers() {
        MidiTransceiver transceiver = midi.get();
        
        if (isValid()) {
            transceiver.clearPlayers();
        } else {
            throw new IllegalStateException("This MidiWrapper is no longer valid; the sequence has likely ended.");
        }
    }
    
    /**
     * Set the transceiver's volume range.
     * 
     * @throws IllegalStateException
     *     If this MidiWrapper's transceiver is no longer valid.
     * 
     * @param volume    The new volume to use.
     */
    public void setVolume(float volume) {
        MidiTransceiver transceiver = midi.get();
        
        if (isValid()) {
            transceiver.setVolume(volume);
        } else {
            throw new IllegalStateException("This MidiWrapper is no longer valid; the sequence has likely ended.");
        }
    }
    
    
    /**
     * Attempt to play a MIDI file, with the given parameters, to the given
     * players.
     * <p/>
     * A MidiWrapper object will be returned, which can be used to affect the
     * sequence while its playing.
     * 
     * @throws IllegalArgumentException
     *     If the given File cannot be loaded into a MIDI sequence.
     * 
     * @param file      The File to read MIDI from.
     * @param tempo     The tempo to play at.
     * @param volume    The volume to play at.
     * @param players   The Players to play to.
     * @return  A MidiWrapper object.
     */
    public static MidiWrapper playMidi(File file, float tempo, float volume, Player... players) {
        try {
            return playMidi(MidiSystem.getSequence(file), tempo, volume, players);
        } catch (InvalidMidiDataException e) {
            throw new IllegalArgumentException("The given file was not valid MIDI!");
        } catch (IOException e) {
            throw new IllegalArgumentException("The given file could not be loaded to play!");
        }
    }
    
    /**
     * Attempt to play a MIDI file, with the given tempo, to the given players.
     * <p/>
     * A MidiWrapper object will be returned, which can be used to affect the
     * sequence while its playing.
     * 
     * @throws IllegalArgumentException
     *     If the given File cannot be loaded into a MIDI sequence.
     * 
     * @param file      The File to read MIDI from.
     * @param tempo     The tempo to play at.
     * @param players   The Players to play to.
     * @return  A MidiWrapper object.
     */
    public static MidiWrapper playMidi(File file, float tempo, Player... players) {
        return playMidi(file, tempo, MidiTransceiver.DEFAULT_VOLUME, players);
    }
    
    /**
     * Attempt to play a MIDI file to the given players.
     * <p/>
     * A MidiWrapper object will be returned, which can be used to affect the
     * sequence while its playing.
     * 
     * @throws IllegalArgumentException
     *     If the given File cannot be loaded into a MIDI sequence.
     * 
     * @param file      The File to read MIDI from.
     * @param players   The Players to play to.
     * @return  A MidiWrapper object.
     */
    public static MidiWrapper playMidi(File file, Player... players) {
        return playMidi(file, MidiTransceiver.DEFAULT_TEMPO, MidiTransceiver.DEFAULT_VOLUME, players);
    }
    
    private static MidiWrapper playMidi(Sequence sequence, float tempo, float volume, Player... players) {
        try {
            Sequencer sequencer = MidiSystem.getSequencer(false);
            sequencer.setSequence(sequence);
            sequencer.open();
            sequencer.setTempoFactor(tempo);
            
            MidiTransceiver receiver = new MidiTransceiver(volume, players);
            sequencer.getTransmitter().setReceiver(receiver);
            sequencer.start();
            return new MidiWrapper(receiver);
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            return null;
        }
    }
}
