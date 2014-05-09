package com.gmail.favorlock.commonutils.timekeeping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerSettings {

    private final long starting_delay;
    private final long interval_delay;
    private final Repitition repitition;
    private final int initial_count;
    private int repeat_count;
    
    private Set<Method> methods;
    private Object instance;
    private String label;
    private boolean reflection_initialized;
    
    private BukkitRunnable task;
    private Map<TimerSettings, JavaPlugin> sync_with;

    private TimerSettings(Builder builder) {
        this.starting_delay = builder.delay;
        this.initial_count = builder.repeat;
        this.repeat_count = builder.repeat;
        this.repitition = builder.repitition;
        this.interval_delay = repitition.equals(Repitition.NEVER) ? 1 : builder.interval;
        
        this.methods = new HashSet<>();
        this.instance = null;
        this.label = null;
        this.reflection_initialized = false;
        
        this.task = null;
        this.sync_with = new HashMap<>();
        
        builder = null;
    }

    private TimerSettings(TimerSettings copy) {
        this.starting_delay = copy.starting_delay;
        this.initial_count = copy.initial_count;
        this.repeat_count = copy.repeat_count;
        this.repitition = copy.repitition;
        this.interval_delay = repitition.equals(Repitition.NEVER) ? 1 : copy.interval_delay;
        
        this.methods = new HashSet<>();
        this.instance = null;
        this.label = null;
        this.reflection_initialized = false;
        
        this.task = null;
        this.sync_with = new HashMap<>();
    }
    
    public static final class Builder {
        
        private long delay;
        private long interval;
        private int repeat;
        private Repitition repitition;
        
        private Builder() {
            delay = 0;
            interval = 1;
            repeat = 0;
            repitition = Repitition.NEVER;
        }
        
        public Builder withStartingDelayTicks(long delay) {
            if (delay < 0)
                throw new IllegalArgumentException("Delay cannot be negative!");
            
            this.delay = delay;
            return this;
        }
        
        public Builder withStartingDelay(long delay, TimeUnit ofUnit) {
            if (delay < 0)
                throw new IllegalArgumentException("Delay cannot be negative!");
            
            this.delay = 20 * ofUnit.toSeconds(delay);
            return this;
        }
        
        public Builder withLoopingIntervalTicks(long interval) {
            if (interval < 1)
                throw new IllegalArgumentException("Interval cannot be nonpositive!");
            
            this.interval = interval;
            return this;
        }
        
        public Builder withLoopingInterval(long interval, TimeUnit ofUnit) {
            if (interval < 1)
                throw new IllegalArgumentException("Interval cannot be nonpositive!");
            
            this.interval = 20 * ofUnit.toSeconds(interval);
            return this;
        }
        
        public Builder withExactRepeat(int times) {
            repeat = times;
            repitition = Repitition.EXACT;
            return this;
        }
        
        public Builder withNoRepeat() {
            repeat = 0;
            repitition = Repitition.NEVER;
            return this;
        }
        
        public Builder withInfiniteRepeat() {
            repeat = Integer.MAX_VALUE;
            repitition = Repitition.INFINITE;
            return this;
        }
        
        public TimerSettings build() {
            return new TimerSettings(this);
        }
    }

    private static enum Repitition { INFINITE, EXACT, NEVER };

    public static TimerSettings.Builder builder() {
        return new Builder();
    }

    public long getStartingDelay() {
        return starting_delay;
    }

    public long getIntervalDelay() {
        return interval_delay;
    }

    public boolean shouldRepeat() {
        switch (repitition) {
            case EXACT:
            case NEVER:
                return (repeat_count--) >= 0;
            case INFINITE:
                return true;
            default:
                return false;
        }
    }

    public TimerSettings clone() {
        return new TimerSettings(this);
    }

    protected void initializeFor(String label, Object instance) {
        if (label == null)
            throw new IllegalArgumentException("Label cannot be null!");
        
        this.instance = instance;
        this.label = label;
        Set<Method> found = new HashSet<>();
        
        for (Method m : instance.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(TimerHandler.class)) {
                TimerHandler annotation = m.getAnnotation(TimerHandler.class);
                
                if (annotation.label().equals(label)) {
                    if (m.getParameterTypes().length == 0) {
                        found.add(m);
                    } else {
                        continue;
                    }
                }
            }
        }
        
        if (found.isEmpty()) {
            throw new IllegalStateException(String.format(
                    "No valid methods were found for TimerHandler label %s in an instance of %s!",
                    label, instance.getClass().getCanonicalName()));
        }
        
        methods = found;
        reflection_initialized = true;
    }

    public boolean isReflectionInitialized() {
        return reflection_initialized;
    }

    private void invoke() {
        if (!reflection_initialized)
            throw new IllegalStateException("This TimerSettings hasn't had its methods and instance initialized yet!");
        
        for (Method m : methods) {
            try {
                m.setAccessible(true);
                m.invoke(instance);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(String.format("Failed to handle TimerHandler method %s for an instance of %s.",
                        m.getName(), instance.getClass().getCanonicalName()));
                e.printStackTrace();
            }
        }
    }
    
    protected void addSync(TimerSettings sync_settings, JavaPlugin plugin) {
        if (isRunning() && (!sync_settings.isRunning())) {
            sync_with.put(sync_settings, plugin);
        }
    }

    protected void start(JavaPlugin plugin) {
        if (!reflection_initialized)
            throw new IllegalStateException("This TimerSettings hasn't had its methods and instance initialized yet!");

        task = new BukkitRunnable() {
            public void run() {
                for (Map.Entry<TimerSettings, JavaPlugin> sync : sync_with.entrySet()) {
                    sync.getKey().start(sync.getValue());
                }
                
                sync_with = new HashMap<>();
                
                if (shouldRepeat()) {
                    invoke();
                } else {
                    unregister();
                }
            }
        };
        
        this.repeat_count = this.initial_count;
        task.runTaskTimer(plugin, starting_delay, interval_delay);
    }

    public boolean isRunning() {
        return task != null;
    }

    protected void stop() {
        if (reflection_initialized) {
            if (task != null) {
                try {
                    task.cancel();
                } catch (Exception e) {
                    // task wasn't running
                }
                
                this.task = null;
                this.sync_with = new HashMap<>();
                return;
            } else {
                throw new IllegalStateException("This TimerSettings hasn't had its task started yet!");
            }
        } else {
            throw new IllegalStateException("This TimerSettings hasn't had its methods and instance initialized yet!");
        }
    }
    
    public void unregister() {
        TimeKeeper.unregisterTimer(label);
    }
    
    public void synchronize(JavaPlugin plugin, String sync_with) {
        TimeKeeper.startSynchronized(this.label, plugin, sync_with);
    }
    
    protected Object getInvokedInstance() {
        return instance;
    }
}
