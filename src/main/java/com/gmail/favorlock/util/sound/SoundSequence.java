package com.gmail.favorlock.util.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.gmail.favorlock.CommonUtils;
import com.gmail.favorlock.util.math.SerializableVector;

public class SoundSequence implements Serializable {

    private static final long serialVersionUID = -5803173434004574724L;
    
    private final Map<Integer, List<SoundDisplay>> sequence;
    
    public SoundSequence() {
        sequence = new HashMap<>();
    }
    
    /**
     * Play this SoundSequence at the specified location.
     * Any players within range will hear the sounds.
     * 
     * @param location  The location to play sounds at.
     */
    public void play(final Location location) {
        new BukkitRunnable() {
            private int tick = 0;
            private int found = 0;
            private final Map<Integer, List<SoundDisplay>> seq = sequence;
            
            public void run() {
                List<SoundDisplay> sounds = seq.get(new Integer(tick++));
                
                if (sounds != null) {
                    for (SoundDisplay display : sounds) {
                        Location loc = location.clone().add(display.vector.toVector());
                        display.sound.play(loc);
                        found++;
                    }
                }
                
                if (found > seq.size()) {
                    try {
                        this.cancel();
                    } catch (IllegalStateException e) {}
                }
            }
        }.runTaskTimer(CommonUtils.getPlugin(), 0L, 1L);
    }
    
    /**
     * Play this SoundSequence at the specified location.
     * Only players specified and within range will hear the sounds.
     * 
     * @param location  The location to play sounds at.
     * @param players   The players to play sounds to.
     */
    public void play(final Location location, final Player... players) {
        new BukkitRunnable() {
            private int tick = 0;
            private int found = 0;
            private final Map<Integer, List<SoundDisplay>> seq = sequence;
            
            public void run() {
                List<SoundDisplay> sounds = seq.get(new Integer(tick++));
                
                if (sounds != null) {
                    for (SoundDisplay display : sounds) {
                        Location loc = location.clone().add(display.vector.toVector());
                        display.sound.play(loc, players);
                        found++;
                    }
                }
                
                if (found > seq.size()) {
                    try {
                        this.cancel();
                    } catch (IllegalStateException e) {}
                }
            }
        }.runTaskTimer(CommonUtils.getPlugin(), 0L, 1L);
    }
    
    /**
     * Play this SoundSequence to the specified players.
     * Each player will hear the sounds at their location.
     * 
     * @param players   The players to play sounds to.
     */
    public void play(final Player... players) {
        new BukkitRunnable () {
            private int tick = 0;
            private int found = 0;
            private final Map<Integer, List<SoundDisplay>> seq = sequence;
            
            public void run() {
                List<SoundDisplay> sounds = seq.get(new Integer(tick++));
                
                if (sounds != null) {
                    for (SoundDisplay display : sounds) {
                        for (Player player : players) {
                            Location loc = player.getEyeLocation().clone().add(display.vector.toVector());
                            display.sound.play(loc, player);
                            found++;
                        }
                    }
                }
                
                if (found > seq.size()) {
                    try {
                        this.cancel();
                    } catch (IllegalStateException e) {}
                }
            }
        }.runTaskTimer(CommonUtils.getPlugin(), 0L, 1L);
    }
    
    /**
     * Add a SoundEffect to this sequence, with no offset and no delay.
     * 
     * @param sound         The SoundEffect to add to this sequence.
     */
    public void add(SoundEffect sound) {
        add(sound, new Vector(), 0);
    }
    
    /**
     * Add a SoundEffect to this sequence, with a specified offset and no delay.
     * 
     * @param sound         The SoundEffect to add to this sequence.
     * @param offset        The offset relative to the central location that
     *                      this sound should be played at.
     */
    public void add(SoundEffect sound, Vector offset) {
        add(sound, offset, 0);
    }
    
    /**
     * Add a SoundEffect to this sequence, with no offset and a specified delay.
     * 
     * @param sound         The SoundEffect to add to this sequence.
     * @param ticks_delay   The time relative to the start of this sequence
     *                      that this sound should be played at.
     */
    public void add(SoundEffect sound, int ticks_delay) {
        add(sound, new Vector(), ticks_delay);
    }
    
    /**
     * Add a SoundEffect to this sequence, with a specified offset and delay.
     * 
     * @param sound         The SoundEffect to add to this sequence.
     * @param offset        The offset relative to the central location that
     *                      this sound should be played at.
     * @param ticks_delay   The time relative to the start of this sequence
     *                      that this sound should be played at.
     */
    public void add(SoundEffect sound, Vector offset, int ticks_delay) {
        if (ticks_delay < 0)
            throw new IllegalArgumentException("Time cannot be negative!");
        
        Integer delay = ticks_delay;
        List<SoundDisplay> sounds = null;
        
        if (sequence.get(delay) != null) {
            sounds = sequence.get(delay);
        } else {
            sounds = new ArrayList<>();
            sequence.put(delay, sounds);
        }
        
        sounds.add(new SoundDisplay(sound, offset));
    }
    
    /**
     * Clear this SoundSequence.
     * <p>
     * If this sequence is currently being played,
     * it may cause that sequence to terminate early.
     */
    public void clear() {
        sequence.clear();
    }
    
    /**
     * Save this SoundSequence to a file.
     * 
     * @param file  The file to save to.
     * 
     * @return <b>true</b> if the operation completed
     * successfully, <b>false</b> otherwise.
     */
    public boolean save(File file) {
        try {
            if (!file.exists()) {
                File dir = file.getParentFile();
                
                if (dir != null)
                    dir.mkdirs();
                
                file.createNewFile();
            }
            
            FileOutputStream file_out = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(file_out);
            
            out.writeObject(this);
            out.flush();
            out.close();
            
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    private static class SoundDisplay implements Serializable {
        
        private static final long serialVersionUID = -5992270977058731326L;
        
        private final SoundEffect sound;
        private final SerializableVector vector;
        
        private SoundDisplay(SoundEffect sound, Vector offset) {
            this.sound = sound;
            this.vector = new SerializableVector(offset);
        }
    }
    
    /**
     * Load a SoundSequence from a file.
     * 
     * @param file  The file to load from.
     * 
     * @return The loaded SoundSequence,
     * or <b>null</b> if an error occurred.
     */
    public static SoundSequence load(File file) {
        try {
            FileInputStream file_in = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(file_in);
            SoundSequence loaded = (SoundSequence) in.readObject();
            in.close();
            
            return loaded;
        } catch (Exception e) {
            return null;
        }
    }
}
