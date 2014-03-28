package com.gmail.favorlock.effects;

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

public class ParticleSequence implements Serializable {

    private static final long serialVersionUID = -7315058828374667542L;

    private final Map<Integer, List<ParticleDisplay>> sequence;
    
    public ParticleSequence() {
        sequence = new HashMap<>();
    }
    
    /**
     * Play this ParticleSequence at the specified location.
     * Any players within range will see the particles.
     * 
     * @param location  The location to display particles at.
     */
    public void play(Location location) {
        play(location, location.getWorld().getPlayers().toArray(new Player[0]));
    }
    
    /**
     * Play this ParticleSequence at the specified location.
     * Only players specified and within range will see the particles.
     * 
     * @param location  The location to display particles at.
     * @param players   The players to display particles to.
     */
    public void play(final Location location, final Player... players) {
        new BukkitRunnable() {
            private int tick = 0;
            private int found = 0;
            private final Map<Integer, List<ParticleDisplay>> seq = sequence;
            
            public void run() {
                List<ParticleDisplay> particles = seq.get(new Integer(tick++));
                
                if (particles != null) {
                    for (ParticleDisplay display : particles) {
                        Location loc = location.clone().add(display.vector.toVector());
                        display.particle.play(loc, players);
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
     * Add a Particle to this sequence, with no offset and no delay.
     * 
     * @param particle  The Particle to add to this sequence.
     */
    public void add(Particle particle) {
        add(particle, new Vector(), 0);
    }
    
    /**
     * Add a Particle to this sequence, with a specified offset and no delay.
     * 
     * @param particle      The Particle to add to this sequence.
     * @param offset        The offset relative to the central location that
     *                      this particle should be displayed at.
     */
    public void add(Particle particle, Vector offset) {
        add(particle, offset, 0);
    }
    
    /**
     * Add a Particle to this sequence, with no offset and a specified delay.
     * 
     * @param particle      The Particle to add to this sequence.
     * @param ticks_delay   The time relative to the start of this sequence
     *                      that this particle should be displayed.
     */
    public void add(Particle particle, int ticks_delay) {
        add(particle, new Vector(), ticks_delay);
    }
    
    /**
     * Add a Particle to this sequence, with a specified offset and delay.
     * 
     * @param particle      The Particle to add to this sequence.
     * @param offset        The offset relative to the central location that
     *                      this particle should be displayed at.
     * @param ticks_delay   The time relative to the start of this sequence
     *                      that this particle should be displayed at.
     */
    public void add(Particle particle, Vector offset, int ticks_delay) {
        if (ticks_delay < 0)
            throw new IllegalArgumentException("Time cannot be negative!");
        
        Integer delay = ticks_delay;
        List<ParticleDisplay> particles = null;
        
        if (sequence.get(delay) != null) {
            particles = sequence.get(delay);
        } else {
            particles = new ArrayList<>();
            sequence.put(delay, particles);
        }
        
        particles.add(new ParticleDisplay(particle, offset));
    }
    
    /**
     * Clear this ParticleSequence.
     * <p>
     * If this sequence is currently being displayed,
     * it may cause that sequence to terminate early.
     */
    public void clear() {
        sequence.clear();
    }
    
    /**
     * Save this ParticleSequence to a file.
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
    
    private static class ParticleDisplay implements Serializable {
        
        private static final long serialVersionUID = -923651335456182534L;
        
        private final Particle particle;
        private final SerializableVector vector;
        
        private ParticleDisplay(Particle particle, Vector offset) {
            this.particle = particle;
            this.vector = new SerializableVector(offset);
        }
    }
    
    /**
     * Load a ParticleSequence from a file.
     * 
     * @param file  The file to load from.
     * 
     * @return The loaded ParticleSequence,
     * or <b>null</b> if an error occurred.
     */
    public static ParticleSequence load(File file) {
        try {
            FileInputStream file_in = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(file_in);
            ParticleSequence loaded = (ParticleSequence) in.readObject();
            in.close();
            
            return loaded;
        } catch (Exception e) {
            return null;
        }
    }
}
