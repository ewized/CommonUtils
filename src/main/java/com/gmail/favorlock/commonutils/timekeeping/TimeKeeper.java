package com.gmail.favorlock.commonutils.timekeeping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

public class TimeKeeper {

    private static Map<String, TimerSettings> timers = new HashMap<>();
    
    /**
     * Register any self-sufficient TimerHandler methods in the given instance
     * (see {@link TimerHandler}'s documentation for more information on
     * self-sufficient TimerHandlers).
     * 
     * @param instance  The instance whose self-sufficient TimerHandler methods
     *                  should be registered.
     * 
     * @throws IllegalArgumentException
     *             If the given instance is null.
     */
    public static void registerAll(Object instance) {
        if (instance != null) {
            for (Method m : instance.getClass().getDeclaredMethods()) {
                if (m.isAnnotationPresent(TimerHandler.class)) {
                    TimerHandler annotation = m.getAnnotation(TimerHandler.class);
                    String label = annotation.label();
                    
                    if (isTimerRegistered(label))
                        continue;
                    
                    if (isTimerHandlerSelfSufficient(annotation)) {
                        TimerSettings settings = getTimerSettingsForTimerHandler(annotation);
                        settings.initializeFor(label, instance);
                        timers.put(label, settings);
                    } else {
                        continue;
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Cannot register TimerHandler methods for a null instance!");
        }
    }
    
    /**
     * Register a new timer under the given label. The annotated method must
     * qualify as a self-sufficient TimerHandler (see {@link TimerHandler}'s
     * documentation for more information on self-sufficient TimerHandlers).
     * 
     * @param label    The label to search for and register timers under.
     * @param instance The instance whose methods should be searched for
     *                 TimerHandlers of the given label.
     * 
     * @throws IllegalStateException
     *             If a timer has already been registered under the given label.
     * @throws IllegalArgumentException
     *             If the given label is the empty string, or the instance is
     *             null.
     * 
     * @return <b>true</b> if a timer was registered as a result of this call,
     *         <b>false</b> otherwise.
     */
    public static boolean registerTimer(String label, Object instance) {
        if (label.equals(""))
            throw new IllegalArgumentException("Cannot register a timer under the label of an empty string!");
        if (isTimerRegistered(label))
            throw new IllegalStateException(String.format("A timer of label %s has already been registered!", label));
        if (instance == null)
            throw new IllegalArgumentException("Cannot register TimerHandler methods for a null instance!");
        
        for (Method m : instance.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(TimerHandler.class)) {
                TimerHandler annotation = m.getAnnotation(TimerHandler.class);
                
                if (annotation.label().equals(label) && isTimerHandlerSelfSufficient(annotation)) {
                    TimerSettings settings = getTimerSettingsForTimerHandler(annotation);
                    settings.initializeFor(label, instance);
                    timers.put(label, settings);
                    return true;
                } else {
                    continue;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Explicitly register a new timer under the given label and settings.
     *
     * @param settings The TimerSettings to use for this timer.
     * @param label    The TimerHandler annotation label to search methods for.
     * @param instance The instance whose compatible methods should be called
     *                 by this timer when appropriate.
     * 
     * @throws IllegalStateException
     *             If a timer has already been registered under the given label.
     * @throws IllegalArgumentException
     *             If the given label is the empty string, or the instance is null.
     */
    public static void registerTimer(TimerSettings settings, String label, Object instance) {
        if (label.equals(""))
            throw new IllegalArgumentException("Cannot register a timer under the label of an empty string!");
        if (isTimerRegistered(label))
            throw new IllegalStateException(String.format("A timer of label %s has already been registered!", label));
        if (instance == null)
            throw new IllegalArgumentException("Cannot register TimerHandler methods for a null instance!");
        
        TimerSettings cloned = settings.clone();
        cloned.initializeFor(label, instance);
        timers.put(label, cloned);
    }
    
    /**
     * Unregister a timer of the given label.
     *
     * @param label The label of the timer to unregister.
     * 
     * @throws IllegalArgumentException
     *             If there is no timer registered under the given label.
     */
    public static void unregisterTimer(String label) {
        if (isTimerRegistered(label)) {
            TimerSettings settings = timers.remove(label);
            boolean running = settings.isRunning();
            
            if (running)
                settings.stop();
        } else {
            throw new IllegalArgumentException(String.format("No timer of label %s has been registered," +
                    " so it cannot be unregistered!", label));
        }
    }
    
    /**
     * Unregisters all timers that are registered for any
     * instance of the given class.
     * 
     * @param cls The class to match.
     */
    public static void unregisterTimersForClass(Class<?> cls) {
        for (Map.Entry<String, TimerSettings> timer : timers.entrySet()) {
            if (timer.getValue().getInvokedInstance().getClass().equals(cls)) {
                unregisterTimer(timer.getKey());
            }
        }
    }
    
    /**
     * Unregisters all timers that are registered to the
     * specific instance given.
     * 
     * @param instance The instance to match.
     */
    public static void unregisterTimersForInstance(Object instance) {
        for (Map.Entry<String, TimerSettings> timer : timers.entrySet()) {
            if (timer.getValue().getInvokedInstance().equals(instance)) {
                unregisterTimer(timer.getKey());
            }
        }
    }
    
    /**
     * Get if a timer of the given label is registered.
     *
     * @param label The label to match.
     * 
     * @return <b>true</b> if a timer has been registered
     * under this label, <b>false</b> otherwise.
     */
    public static boolean isTimerRegistered(String label) {
        if (timers.containsKey(label))
            return true;
        return false;
    }
    
    /**
     * Start the timer of the given label on the next tick of the timer given by the sync_with label.
     * 
     * @param label     The label of the timer to start.
     * @param plugin    The plugin to schedule the task for.
     * @param sync_with The label of the timer to synchronize with.
     * 
     * @throws IllegalArgumentException If there is no timer registered under the given label,
     *                                  or if there is no timer registered under sync_label.
     * @throws IllegalStateException    If the timer registered under the given label is already running,
     *                                  or if the timer given by sync_with is not currently running.
     */
    public static void startSynchronized(String label, JavaPlugin plugin, String sync_with) {
        if (isTimerRegistered(label)) {
            if (isTimerRegistered(sync_with)) {
                TimerSettings settings = timers.get(label);
                TimerSettings sync = timers.get(sync_with);
                
                if (settings.isRunning()) {
                    throw new IllegalStateException(String.format(
                            "Registered timer with label %s is already running, so it can't be started!", label));
                }
                
                if (!sync.isRunning()) {
                    throw new IllegalStateException(String.format(
                            "Cannot synchronize with timer with label %s, as it is not running!", label));
                }
                
                sync.addSync(settings, plugin);
            } else {
                throw new IllegalArgumentException(String.format(
                        "Cannot synchronize with timer of label %s, as it has not been registered!", sync_with));
            }
        } else {
            throw new IllegalArgumentException(String.format("No timer of label %s has been registered!", label));
        }
    }
    
    /**
     * Start the timer of the given label.
     *
     * @param label  The label of the timer to start.
     * @param plugin The plugin to schedule the task for.
     * 
     * @throws IllegalArgumentException
     *             If there is no timer registered under the given label.
     * @throws IllegalStateException
     *             If the timer is registered but already running.
     */
    public static void start(String label, JavaPlugin plugin) {
        if (isTimerRegistered(label)) {
            TimerSettings settings = timers.get(label);
            
            if (settings.isRunning()) {
                throw new IllegalStateException(String.format(
                        "Registered timer with label %s is already running, so it can't be started!", label));
            }
            
            settings.start(plugin);
        } else {
            throw new IllegalArgumentException(String.format("No timer of label %s has been registered!", label));
        }
    }
    
    /**
     * Stop the timer of the given label.
     *
     * @param label The label of the timer to stop.
     * 
     * @throws IllegalArgumentException
     *             If there is no timer registered under the given label.
     * @throws IllegalStateException
     *             If the timer is registered but is not currently running.
     */
    public static void stop(String label) {
        if (isTimerRegistered(label)) {
            TimerSettings settings = timers.get(label);
            
            if (!settings.isRunning()) {
                throw new IllegalStateException(String.format(
                        "Registered timer with label %s is not running, so it can't be stopped!", label));
            }
            
            settings.stop();
        } else {
            throw new IllegalArgumentException(String.format("No timer of label %s has been registered!", label));
        }
    }
    
    /**
     * Get if a timer of the given label is running.
     *
     * @param label The label to match.
     * 
     * @return <b>true</b> iff a timer of the given label
     * has been registered and is running, <b>false</b>
     * otherwise, including if no timer has been registered
     * under the given label.
     */
    public static boolean isTimerRunning(String label) {
        if (isTimerRegistered(label)) {
            return timers.get(label).isRunning();
        } else {
            return false;
        }
    }
    
    private static boolean isTimerHandlerSelfSufficient(TimerHandler annotation) {
        return annotation.delay() >= 0 && annotation.interval() > 0;
    }
    
    private static TimerSettings getTimerSettingsForTimerHandler(TimerHandler annotation) {
        if (!isTimerHandlerSelfSufficient(annotation))
            return null;
        
        TimerSettings.Builder builder = TimerSettings.builder()
                .withStartingDelayTicks(annotation.delay())
                .withLoopingIntervalTicks(annotation.interval());
        
        if (annotation.repitition() > 0) {
            builder.withExactRepeat(annotation.repitition());
        } else if (annotation.repitition() == 0) {
            builder.withNoRepeat();
        } else {
            builder.withInfiniteRepeat();
        }
        
        return builder.build();
    }
}
