package com.archeinteractive.dev.commonutils.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A Class for representing a VirtualCommand, which is registered from an
 * implementation of the functional interfaces
 * {@link io.xime.core.command.CommandAction} and
 * {@link io.xime.core.command.CommandCompleter}
 */
class VirtualCommand {

    final Class<? extends JavaPlugin> plugincls;
    final Map<String, VirtualSubCommand> subcommands;
    volatile CommandAction<? super Player> player;
    volatile CommandAction<? super ConsoleCommandSender> console;

    VirtualCommand(Class<? extends JavaPlugin> plugincls) {
        this.plugincls = plugincls;
        this.subcommands = new HashMap<>();
        this.player = null;
        this.console = null;
    }
    
    boolean invokeConsole(ConsoleCommandSender sender, String[] args) {
        VirtualSubCommand s;
        
        if (args.length < 1 || (s = getSubCommand(args[0])) == null) {
            if (hasConsoleExecution()) {
                console.invoke(sender, args);
                return true;
            }
            
            return false;
        }
        
        if (s.hasConsoleExecution()) {
            s.console.invoke(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }
        
        return false;
    }
    
    boolean invokePlayer(Player sender, String[] args) {
        VirtualSubCommand s;
        
        if (args.length < 1 || (s = getSubCommand(args[0])) == null) {
            if (hasPlayerExecution()) {
                player.invoke(sender, args);
                return true;
            }
            
            return false;
        }
        
        if (s.hasPlayerExecution()) {
            s.player.invoke(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }
        
        return false;
    }

    public String toString() {
        return String.format("VirtualCommand{plugin=%s,p exec=%s, c exec=%s}",
                plugincls.getSimpleName(), hasPlayerExecution(), hasConsoleExecution());
    }

    boolean hasPlayerExecution() {
        return player != null;
    }

    boolean hasConsoleExecution() {
        return console != null;
    }
    
    VirtualSubCommand getSubCommand(String sub_label) {
        for (Map.Entry<String, VirtualSubCommand> entry : subcommands.entrySet()) {
            if (sub_label.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return null;
    }

    class VirtualSubCommand {
    
        volatile CommandAction<? super Player> player;
        volatile CommandAction<? super ConsoleCommandSender> console;
        
        boolean hasPlayerExecution() {
            return player != null;
        }
        
        boolean hasConsoleExecution() {
            return console != null;
        }
    }
}
