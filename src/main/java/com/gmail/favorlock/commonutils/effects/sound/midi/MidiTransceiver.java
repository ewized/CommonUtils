package com.gmail.favorlock.commonutils.effects.sound.midi;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.gmail.favorlock.commonutils.effects.sound.SoundUtil;

/**
 * Acts as a conduit between a MIDI sequencer and the Bukkit API's Sound enum.
 * Will play the sequencer's sound as accurately as is possible with the note
 * block instruments.
 */
public class MidiTransceiver implements Receiver {

    protected static final float DEFAULT_VOLUME = 10.0f;
    protected static final float DEFAULT_TEMPO = 1.0f;
    
    private final Map<Integer, Integer> channel_patches;
    private final Set<WeakReference<Player>> players;
    private float volume;
    
    protected MidiTransceiver(float volume, Player... players) {
        this.channel_patches = new HashMap<>();
        this.players = new HashSet<>();
        this.volume = volume;
        
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
    
    public void send(MidiMessage mm, long time) {
        if (mm instanceof ShortMessage) {
            ShortMessage sm = (ShortMessage) mm;
            
            switch (sm.getCommand()) {
            case ShortMessage.NOTE_ON:
                play(sm);
                break;
            case ShortMessage.NOTE_OFF:
                break;
            case ShortMessage.PROGRAM_CHANGE:
                int channel = sm.getChannel();
                int patch = sm.getData1();
                channel_patches.put(channel, patch);
                break;
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
        channel_patches.clear();
        players.clear();
    }
    
    
    public static float getDefaultTempo() {
        return DEFAULT_TEMPO;
    }
    
    public static float getDefaultVolume() {
        return DEFAULT_VOLUME;
    }
}
