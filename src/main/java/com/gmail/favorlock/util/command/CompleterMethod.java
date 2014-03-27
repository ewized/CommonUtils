package com.gmail.favorlock.util.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public class CompleterMethod extends CommandMethod implements TabCompleter {

    private static final Map<Command, CompleterMethod> commands = new HashMap<>();
    
    /* Inherited from superclass:
     * 
     * Method main_method;
     * Object main_instance;
     * Map<String, SubCommand> subcommands;
     */
    
    private CompleterMethod(Command command) {
        super();
        commands.put(command, this);
    }
    
    @Override @SuppressWarnings("unchecked")
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Sub Commands
        if ((args.length > 0) && hasSubCommands()) {
            String subcommand_string = CommandController.SubCommand.toStringFor(command, args[0]);
            CommandController.SubCommand subcommand = subcommands.get(subcommand_string);
            
            if (subcommand != null) {
                Method sub_method = subcommand.method;
                Object sub_instance = subcommand.instance;
                
                if (compatibleParameterTypes(sub_method, sender)) {
                    String[] sub_args = new String[args.length - 1];
                    
                    for (int i = 1; i < args.length; i++) {
                        sub_args[i - 1] = args[i];
                    }
                    
                    if (subcommand.permission.equals("") || sender.hasPermission(subcommand.permission)) {
                        try {
                            return (List<String>) sub_method.invoke(sub_instance, sender, sub_args);
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            return new ArrayList<>();
                        }
                    } else {
                        return new ArrayList<>();
                    }
                } else {
                    return new ArrayList<>();
                }
            }
        }
        
        // Main Command
        if (hasMainCommand()) {
            if (compatibleParameterTypes(main_method, sender)) {
                if ((command.getPermission() == null) || command.getPermission().equals("") || sender.hasPermission(command.getPermission())) {
                    try {
                        return (List<String>) main_method.invoke(main_instance, sender, args);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        return new ArrayList<>();
                    }
                } else {
                    return new ArrayList<>();
                }
            } else {
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }
    
    
    public static CompleterMethod forCommand(PluginCommand plugin_command) {
        Command command = (Command) plugin_command;
        
        if (commands.containsKey(command)) {
            return commands.get(command);
        } else {
            return new CompleterMethod(command);
        }
    }
}
