package com.archeinteractive.dev.commonutils.command;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandRegistry {

    private static final Map<String, VirtualCommand> virtual = new HashMap<>();
    
    public static void registerVirtualUniversalCommand(JavaPlugin plugin, String label, CommandAction<? super CommandSender> action) {
        if (virtual.containsKey(label) && (virtual.get(label).hasConsoleExecution() || virtual.get(label).hasPlayerExecution())) {
            throw new IllegalArgumentException("A virtual command is already registered under the given label!");
        }
        
        registerVirtualPlayerCommand(plugin, label, action);
        registerVirtualConsoleCommand(plugin, label, action);
    }
    
    public static void registerVirtualPlayerCommand(JavaPlugin plugin, String label, CommandAction<? super Player> action) {
        VirtualCommand v = virtual.get(label);
        
        if (v == null) {
            v = new VirtualCommand(plugin.getClass());
            virtual.put(label, v);
        }
        
        if (v.hasPlayerExecution()) {
            throw new IllegalArgumentException("A virtual command is already registered under the given label!");
        }
        
        v.player = action;
    }
    
    public static void registerVirtualConsoleCommand(JavaPlugin plugin, String label, CommandAction<? super ConsoleCommandSender> action) {
        VirtualCommand v = virtual.get(label);
        
        if (v == null) {
            v = new VirtualCommand(plugin.getClass());
            virtual.put(label, v);
        }
        
        if (v.hasConsoleExecution()) {
            throw new IllegalArgumentException("A virtual command is already registered under the given label!");
        }
        
        v.console = action;
    }
    
    public static void unregisterVirtualCommand(String label) {
        virtual.remove(label);
    }
    
    static VirtualCommand getCommand(String label) {
        return virtual.get(label);
    }
}
