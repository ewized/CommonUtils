package com.gmail.favorlock.util.command;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

import com.gmail.favorlock.util.command.CommandController.SubCommand;

public abstract class CommandMethod {

    Method main_method;
    Object main_instance;
    Map<String, SubCommand> subcommands;
    
    CommandMethod() {
        this.main_method = null;
        this.main_instance = null;
        this.subcommands = new HashMap<>();
    }
    
    boolean compatibleParameterTypes(Method method, CommandSender sender) {
        Class<?> type = method.getParameterTypes()[0];
        
        if (type.isAssignableFrom(sender.getClass()))
            return true;
        
        return false;
    }
    
    public void addMainCommand(Method method, Object instance) {
        this.main_method = method;
        this.main_instance = instance;
    }
    
    public void addSubCommand(CommandController.SubCommand subcommand) {
        this.subcommands.put(subcommand.toString(), subcommand);
    }
    
    public boolean hasMainCommand() {
        return (main_method != null) && (main_instance != null);
    }
    
    public boolean hasSubCommands() {
        return subcommands.size() > 0;
    }
}
