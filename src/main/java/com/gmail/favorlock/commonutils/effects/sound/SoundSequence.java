package com.gmail.favorlock.commonutils.effects.sound;

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

import com.gmail.favorlock.commonutils.CommonUtils;

public class SoundSequence implements Serializable {

    private static final long serialVersionUID = -5803173434004574724L;

    private final Map<Integer, List<SoundDisplay>> sequence = new HashMap<>();
    private transient List<TaskPlaySound> tasks = new ArrayList<>();

    /**
     * Play this SoundSequence at the specified location.
     * Any players within range will hear the sounds.
     *
     * @param location The location to play sounds at.
     */
    public void play(final Location location) {
        initTransient();
        tasks.add(new TaskPlaySound(sequence, location, null, SoundPlayType.LOCATION, tasks).start());
    }

    /**
     * Play this SoundSequence at the specified location.
     * Only players specified and within range will hear the sounds.
     *
     * @param location The location to play sounds at.
     * @param players  The players to play sounds to.
     */
    public void play(final Location location, final Player... players) {
        initTransient();
        tasks.add(new TaskPlaySound(sequence, location, players, SoundPlayType.LOCATION_PLAYERS, tasks).start());
    }

    /**
     * Play this SoundSequence to the specified players.
     * Each player will hear the sounds at their location.
     *
     * @param players The players to play sounds to.
     */
    public void play(final Player... players) {
        initTransient();
        tasks.add(new TaskPlaySound(sequence, null, players, SoundPlayType.PLAYERS, tasks).start());
    }

    /**
     * Stops all currently running tasks for this sequence.
     */
    public void stopAll() {
        initTransient();

        for (TaskPlaySound task : tasks) {
            try {
                task.cancel();
            } catch (Exception e) {
            }
        }

        tasks.clear();
    }

    /**
     * Stops all currently running tasks for this sequence,
     * for the specified player only. This will have no effect
     * on a sequence that was played at a specific location.
     *
     * @param player The player for whom this sequence
     *               should stop.
     */
    public void stopFor(Player player) {
        initTransient();

        for (TaskPlaySound task : tasks) {
            task.stopFor(player);
        }
    }

    /**
     * Add a SoundEffect to this sequence, with no offset and no delay.
     *
     * @param sound The SoundEffect to add to this sequence.
     */
    public void add(SoundEffect sound) {
        add(sound, new Vector(), 0);
    }

    /**
     * Add a SoundEffect to this sequence, with a specified offset and no delay.
     *
     * @param sound  The SoundEffect to add to this sequence.
     * @param offset The offset relative to the central location that
     *               this sound should be played at.
     */
    public void add(SoundEffect sound, Vector offset) {
        add(sound, offset, 0);
    }

    /**
     * Add a SoundEffect to this sequence, with no offset and a specified delay.
     *
     * @param sound       The SoundEffect to add to this sequence.
     * @param ticks_delay The time relative to the start of this sequence
     *                    that this sound should be played at.
     */
    public void add(SoundEffect sound, int ticks_delay) {
        add(sound, new Vector(), ticks_delay);
    }

    /**
     * Add a SoundEffect to this sequence, with a specified offset and delay.
     *
     * @param sound       The SoundEffect to add to this sequence.
     * @param offset      The offset relative to the central location that
     *                    this sound should be played at.
     * @param ticks_delay The time relative to the start of this sequence
     *                    that this sound should be played at.
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
     * <p/>
     * If this sequence is currently being played,
     * it may cause that sequence to terminate early.
     */
    public void clear() {
        sequence.clear();
    }

    /**
     * Save this SoundSequence to a file.
     *
     * @param file The file to save to.
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

    private void initTransient() {
        if (tasks == null)
            tasks = new ArrayList<>();
    }

    private static class SoundDisplay implements Serializable {

        private static final long serialVersionUID = -5992270977058731326L;

        private final SoundEffect sound;
        private final Map<String, Object> vector;

        private SoundDisplay(SoundEffect sound, Vector offset) {
            this.sound = sound;
            this.vector = offset.serialize();
        }
    }

    private static enum SoundPlayType {LOCATION, LOCATION_PLAYERS, PLAYERS}

    private static class TaskPlaySound extends BukkitRunnable {
        private final Map<Integer, List<SoundDisplay>> seq;
        private final Location location;
        private Player[] players;
        private final SoundPlayType type;
        private final List<TaskPlaySound> running_tasks;
        private int tick = 0;
        private int found = 0;

        private TaskPlaySound(Map<Integer, List<SoundDisplay>> sequence, Location initial, Player[] players, SoundPlayType type, List<TaskPlaySound> tasks) {
            this.seq = new HashMap<>(sequence);
            this.location = initial;
            this.players = players;
            this.type = type;
            this.running_tasks = tasks;
        }

        public TaskPlaySound start() {
            this.runTaskTimer(CommonUtils.getPlugin(), 0L, 1L);
            return this;
        }

        public void stopFor(Player cancel) {
            if (type.equals(SoundPlayType.LOCATION) || (players == null))
                return;

            Player[] replacement = new Player[players.length - 1];
            int newindex = 0;

            for (Player player : players) {
                if (player.equals(cancel))
                    continue;

                replacement[newindex++] = player;
            }

            players = replacement;
        }

        public void run() {
            List<SoundDisplay> sounds = seq.get(new Integer(tick++));

            if (sounds != null) {
                if (type.equals(SoundPlayType.LOCATION))
                    for (SoundDisplay display : sounds) {
                        Location loc = location.clone().add(Vector.deserialize(display.vector));
                        display.sound.play(loc);
                    }

                if (type.equals(SoundPlayType.LOCATION_PLAYERS))
                    for (SoundDisplay display : sounds) {
                        Location loc = location.clone().add(Vector.deserialize(display.vector));
                        display.sound.play(loc, players);
                    }

                if (type.equals(SoundPlayType.PLAYERS))
                    for (SoundDisplay display : sounds) {
                        for (Player player : players) {
                            Location loc = player.getEyeLocation().clone().add(Vector.deserialize(display.vector));
                            display.sound.play(loc, player);
                        }
                    }

                found++;
            }

            if (found > seq.size()) {
                try {
                    this.cancel();
                } catch (IllegalStateException e) {
                }

                running_tasks.remove(this);
            }
        }
    }

    /**
     * Load a SoundSequence from a file.
     *
     * @param file The file to load from.
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
