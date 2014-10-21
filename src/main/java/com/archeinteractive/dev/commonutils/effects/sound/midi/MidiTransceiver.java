package com.archeinteractive.dev.commonutils.effects.sound.midi;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.archeinteractive.dev.commonutils.effects.sound.SoundUtil;

/**
 * Acts as a conduit between a MIDI sequencer and the Bukkit API's Sound enum.
 * Will play the sequencer's sound as accurately as is possible with the note
 * block instruments.
 * <p/>
 * The MidiTransceiver will monitor the state of the transmitting Sequencer,
 * and close the Sequencer when transmitting has stopped. At that time, the
 * MidiTransceiver will close itself, and should be finalizable.
 */
public class MidiTransceiver implements Receiver {

    protected static final float DEFAULT_VOLUME = 10.0f;
    protected static final float DEFAULT_TEMPO = 1.0f;
    
    private final Map<Integer, Integer> channel_patches;
    private final Set<WeakReference<Player>> players;
    private final WeakReference<Sequencer> seq;
    private float volume;
    private boolean playing;
    
    protected MidiTransceiver(Sequencer seq, float volume, Player... players) {
        this.channel_patches = new HashMap<>();
        this.players = new HashSet<>();
        this.seq = new WeakReference<>(seq);
        this.volume = volume;
        this.playing = true;
        
        for (Player player : players)
            this.players.add(new WeakReference<>(player));
    }
    
    protected void addPlayer(Player player) {
        this.players.add(new WeakReference<>(player));
    }
    
    protected void removePlayer(Player player) {
        for (WeakReference<Player> wplayer : new HashSet<>(players)) {
            if (wplayer.get() != null) {
                Player p = wplayer.get();
                
                if (player.equals(p)) {
                    players.remove(wplayer);
                    return;
                }
            }
        }
    }
    
    protected void clearPlayers() {
        this.players.clear();
    }
    
    protected void setVolume(float volume) {
        this.volume = volume;
    }
    
    public boolean isPlaying() {
        return playing;
    }
    
    public void send(MidiMessage mm, long time) {
        if (mm instanceof ShortMessage) {
            ShortMessage sm = (ShortMessage) mm;
            
            switch (sm.getCommand()) {
            case ShortMessage.NOTE_ON:
                play(sm);
                return;
            case ShortMessage.NOTE_OFF:
                return;
            case ShortMessage.PROGRAM_CHANGE:
                int channel = sm.getChannel();
                int patch = sm.getData1();
                channel_patches.put(channel, patch);
                break;
            default:
                break;
            }
            
            if (playing) {
                if (seq.get() == null || !seq.get().isRunning()) {
                    close();
                }
            }
        }
    }
    
    public void play(ShortMessage sm) {
        if (sm.getCommand() == ShortMessage.NOTE_ON) {
            float pitch = SoundUtil.getPitchFor(sm);
            float volume = this.volume * (sm.getData2() / 127f);
            
            Integer patch = channel_patches.get(sm.getChannel());
            
            Sound play = Sound.NOTE_PIANO;
            
            if (patch != null)
                play = SoundUtil.getSoundFor(patch.intValue());
            
            for (WeakReference<Player> wplayer : new HashSet<>(players)) {
                if (wplayer.get() != null) {
                    Player player = wplayer.get();
                    player.playSound(player.getLocation(), play, volume, pitch);
                } else {
                    players.remove(wplayer);
                }
            }
        }
    }
    
    public void close() {
        this.playing = false;
        channel_patches.clear();
        players.clear();
        seq.clear();
    }
    
    
    public static float getDefaultTempo() {
        return DEFAULT_TEMPO;
    }
    
    public static float getDefaultVolume() {
        return DEFAULT_VOLUME;
    }
}
