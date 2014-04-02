package com.gmail.favorlock.commonutils.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

public class ExecutorMethod extends CommandMethod implements CommandExecutor {

    private static final Map<Command, ExecutorMethod> commands = new HashMap<>();
    
    /* Inherited from superclass:
     * 
     * Method main_method;
     * Object main_instance;
     * Map<String, SubCommand> subcommands;
     */

    private ExecutorMethod(Command command) {
        super();
        commands.put(command, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Sub Commands
        if (hasSubCommands()) {
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
                        Class<?> returns = sub_method.getReturnType();
                        
                        if (Boolean.class.isAssignableFrom(returns)) {
                            try {
                                boolean cmdsuccess = (Boolean) sub_method.invoke(sub_instance, sender, sub_args);
                                
                                if (!cmdsuccess) {
                                    if (!subcommand.usage.equals("")) {
                                        sender.sendMessage(subcommand.usage);
                                    }
                                }
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
                        } else if (boolean.class.isAssignableFrom(returns)) {
                            try {
                                boolean cmdsuccess = (boolean) sub_method.invoke(sub_instance, sender, sub_args);
                                
                                if (!cmdsuccess) {
                                    if (!subcommand.usage.equals("")) {
                                        sender.sendMessage(subcommand.usage);
                                    }
                                }
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
                        } else {
                            try {
                                sub_method.invoke(sub_instance, sender, sub_args);
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
                        }

                        return true;
                    } else {
                        if (!subcommand.permissionMessage.equals(""))
                            sender.sendMessage(ChatColor.RED + subcommand.permissionMessage);
                        return true;
                    }
                } else {
                    sender.sendMessage(String.format("%sThis command must be run by a a %s.",
                            ChatColor.RED, sub_method.getParameterTypes()[0].getSimpleName()));
                    return true;
                }
            }
        }

        // Main Command
        if (hasMainCommand()) {
            if (compatibleParameterTypes(main_method, sender)) {
                if ((command.getPermission() == null) || command.getPermission().equals("") || sender.hasPermission(command.getPermission())) {
                    Class<?> returns = main_method.getReturnType();
                    
                    if (Boolean.class.isAssignableFrom(returns)) {
                        try {
                            return (Boolean) main_method.invoke(main_instance, sender, args);
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
                    } else if (boolean.class.isAssignableFrom(returns)) {
                        try {
                            return (boolean) main_method.invoke(main_instance, sender, args);
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
                    } else {
                        try {
                            main_method.invoke(main_instance, sender, args);
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
                    }

                    return true;
                } else {
                    if ((command.getPermissionMessage() != null) && !command.getPermissionMessage().equals(""))
                        sender.sendMessage(ChatColor.RED + command.getPermissionMessage());
                    return true;
                }
            } else {
                sender.sendMessage(String.format("%sThis command must be run by a a %s.",
                        ChatColor.RED, main_method.getParameterTypes()[0].getSimpleName()));
                return true;
            }
        } else {
            return true;
        }
    }


    public static ExecutorMethod forCommand(PluginCommand plugin_command) {
        Command command = (Command) plugin_command;

        if (commands.containsKey(command)) {
            return commands.get(command);
        } else {
            return new ExecutorMethod(command);
        }
    }
}
