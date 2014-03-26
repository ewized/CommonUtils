package com.gmail.favorlock.util.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;

public class CommandController implements TabExecutor {

    private static final HashMap<Command, Object> executionHandlers = new HashMap<>();
    private static final HashMap<Command, Method> executionMethods = new HashMap<>();
    
    private static final HashMap<Command, Object> completionHandlers = new HashMap<>();
    private static final HashMap<Command, Method> completionMethods = new HashMap<>();
    
    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();
    
    private static final HashMap<String, Object> subExecutionHandlers = new HashMap<>();
    private static final HashMap<String, Method> subExecutionMethods = new HashMap<>();
    
    private static final HashMap<String, Object> subCompletionHandlers = new HashMap<>();
    private static final HashMap<String, Method> subCompletionMethods = new HashMap<>();
    
    /**
     * Registers all command handlers and subcommand handlers
     * in the given classes, matching them with their corresponding
     * commands and subcommands registered to the specified plugin.
     * 
     * @param plugin
     *   The plugin whose commands should be considered for registration
     * @param handlers
     *   Instances of the classes whose methods should be considered for registration
     */
    public static void registerCommands(JavaPlugin plugin, Object... handlers) {
        for (Object handler : handlers) {
            registerCommands(plugin, handler);
        }
    }
    
    /**
     * Registers all command handlers and subcommand handlers
     * in the given class, matching them with their corresponding
     * commands and subcommands registered to the specified plugin.
     * 
     * @param plugin
     *   The plugin whose commands should be considered for registration
     * @param handler
     *   An instance of the class whose methods should be considered for registration
     */
    public static void registerCommands(JavaPlugin plugin, Object handler) {

        for (Method method : handler.getClass().getMethods()) {
            Class<?>[] params = method.getParameterTypes();
            
            if (params.length == 2 &&
                    CommandSender.class.isAssignableFrom(params[0]) &&
                    String[].class.equals(params[1])) {

                if (isCommandHandler(method)) {
                    CommandHandler annotation = method.getAnnotation(CommandHandler.class);
                    
                    if (plugin.getCommand(annotation.name()) != null) {
                        boolean apply_settings = false;
                        
                        switch (annotation.handling()) {
                        case COMMAND_EXECUTION:
                        default:
                            plugin.getCommand(annotation.name()).setExecutor(new CommandController());
                            executionHandlers.put(plugin.getCommand(annotation.name()), handler);
                            executionMethods.put(plugin.getCommand(annotation.name()), method);
                            apply_settings = true;
                            break;
                        case TAB_COMPLETION:
                            Class<?> returns = method.getReturnType();
                            
                            if (!List.class.isAssignableFrom(returns)) {
                                plugin.getLogger().warning(String.format("[CommandController]\nCould not register" +
                                        " command of name %s from an instance of %s; the method of name %s specifies" +
                                        " that it should be used for tab completion, however its return type is not" +
                                        " compatible with List<String>.",
                                        annotation.name(), handler.getClass().getCanonicalName(), method.getName()));
                                continue;
                            }
                            
                            plugin.getCommand(annotation.name()).setTabCompleter(new CommandController());
                            completionHandlers.put(plugin.getCommand(annotation.name()), handler);
                            completionMethods.put(plugin.getCommand(annotation.name()), method);
                            break;
                        }
                        
                        if (apply_settings && !(annotation.aliases().equals(new String[] { "" })))
                            plugin.getCommand(annotation.name()).setAliases(Lists.newArrayList(annotation.aliases()));
                        
                        if (apply_settings && !annotation.description().equals(""))
                            plugin.getCommand(annotation.name()).setDescription(annotation.description());
                        
                        if (apply_settings && !annotation.usage().equals(""))
                            plugin.getCommand(annotation.name()).setUsage(annotation.usage());
                        
                        if (apply_settings && !annotation.permission().equals(""))
                            plugin.getCommand(annotation.name()).setPermission(annotation.permission());
                        
                        if (apply_settings && !annotation.permissionMessage().equals(""))
                            plugin.getCommand(annotation.name()).setPermissionMessage(ChatColor.RED + annotation.permissionMessage());
                    } else {
                        plugin.getLogger().warning(String.format("[CommandController]\nCould not register command of" +
                                " name %s from an instance of %s; this plugin does not define command in its plugin.yml.",
                                annotation.name(), handler.getClass().getCanonicalName()));
                    }
                }

                if (isSubCommandHandler(method)) {
                    SubCommandHandler annotation = method.getAnnotation(SubCommandHandler.class);
                    
                    if (plugin.getCommand(annotation.parent()) != null) {
                        SubCommand subCommand = new SubCommand(plugin.getCommand(annotation.parent()), annotation.name());
                        subCommand.permission = annotation.permission();
                        subCommand.permissionMessage = annotation.permissionMessage();
                        subCommands.put(subCommand.toString(), subCommand);
                        
                        switch (annotation.handling()) {
                        case COMMAND_EXECUTION:
                            plugin.getCommand(annotation.parent()).setExecutor(new CommandController());
                            subExecutionHandlers.put(subCommand.toString(), handler);
                            subExecutionMethods.put(subCommand.toString(), method);
                            break;
                        case TAB_COMPLETION:
                            Class<?> returns = method.getReturnType();
                            
                            if (!List.class.isAssignableFrom(returns)) {
                                plugin.getLogger().warning(String.format("[CommandController]\nCould not register" +
                                        " subcommand of name %s (super %s) from an instance of %s; the method of name" +
                                        " %s specifies that it should be used for tab completion, however its return" +
                                        " type is not compatible with List<String>.",
                                        annotation.name(), annotation.parent(),
                                        handler.getClass().getCanonicalName(), method.getName()));
                                continue;
                            }
                            
                            plugin.getCommand(annotation.parent()).setTabCompleter(new CommandController());
                            subCompletionHandlers.put(subCommand.toString(), handler);
                            subCompletionMethods.put(subCommand.toString(), method);
                            break;
                        }
                    } else {
                        plugin.getLogger().warning(String.format("[CommandController]\nCould not register subcommand of" +
                                " name %s (super %s) from an instance of %s;" +
                                " this plugin does not define command in its plugin.yml.",
                                annotation.name(), annotation.parent(), handler.getClass().getCanonicalName()));
                    }
                }
            }
        }
    }

    /**
     * Tests if a method is a command handler
     */
    private static boolean isCommandHandler(Method method) {
        return method.getAnnotation(CommandHandler.class) != null;
    }

    /**
     * Tests if a method is a subcommand handler
     */
    private static boolean isSubCommandHandler(Method method) {
        return method.getAnnotation(SubCommandHandler.class) != null;
    }

    /**
     * A class for representing subcommands
     */
    private static class SubCommand {
        public final Command superCommand;
        public final String subCommand;
        public String permission;
        public String permissionMessage;

        public SubCommand(Command superCommand, String subCommand) {
            this.superCommand = superCommand;
            this.subCommand = subCommand.toLowerCase();
        }

        public boolean equals(Object x) {
            return toString().equals(x.toString());
        }

        public String toString() {
            return (superCommand.getName() + " " + subCommand).trim();
        }
    }

    /**
     * This is the method that "officially" processes commands,
     * but in reality it will always delegate responsibility to
     * the handlers and methods assigned to the command or subcommand.
     * <p>
     * Beyond checking permissions, checking player/console sending,
     * and invoking handlers and methods, this method does not
     * actually act on the commands.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // If a subcommand may be present...
        if (args.length > 0) {
            // Get the subcommand given and the handler and method attached to it
            SubCommand subCommand = new SubCommand(command, args[0]);
            subCommand = subCommands.get(subCommand.toString());
            
            // If and only if the subcommand actually exists...
            if (subCommand != null) {
                Object subHandler = subExecutionHandlers.get(subCommand.toString());
                Method subMethod = subExecutionMethods.get(subCommand.toString());
                
                // If and only if both handler and method exist...
                if (subHandler != null && subMethod != null) {
                    // Reorder the arguments so we don't resend the subcommand
                    String[] subArgs = new String[args.length - 1];
                    
                    for (int i = 1; i < args.length; i++) {
                        subArgs[i - 1] = args[i];
                    }
                    
                    // If the method requires a player and the subcommand wasn't sent by one, don't continue
                    if (subMethod.getParameterTypes()[0].equals(Player.class) && !(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "This command requires a player sender");
                        return true;
                    }
                    
                    // If the method requires a console and the subcommand wasn't sent by one, don't continue
                    if (subMethod.getParameterTypes()[0].equals(ConsoleCommandSender.class)
                            && !(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(ChatColor.RED + "This command requires a console sender");
                        return true;
                    }
                    
                    // If a permission is attached to this subcommand and the sender doens't have it, don't continue
                    if (!subCommand.permission.isEmpty() && !sender.hasPermission(subCommand.permission)) {
                        sender.sendMessage(ChatColor.RED + subCommand.permissionMessage);
                        return true;
                    }
                    
                    // Try to process the command
                    try {
                        subMethod.invoke(subHandler, sender, subArgs);
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + "An error occurred while trying to process the command");
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        }
        
        // If a subcommand was successfully executed, the command will not reach this point Get the handler and method attached to this command
        Object handler = executionHandlers.get(command);
        Method method = executionMethods.get(command);
        
        // If and only if both handler and method exist...
        if (handler != null && method != null) {
            
            // If the method requires a player and the command wasn't sent by one, don't continue
            if (method.getParameterTypes()[0].equals(Player.class) && !(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command requires a player sender");
                return true;
            }
            
            // If the method requires a console and the command wasn't sent by one, don't continue
            if (method.getParameterTypes()[0].equals(ConsoleCommandSender.class)
                    && !(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(ChatColor.RED + "This command requires a console sender");
                return true;
            }
            
            // Try to process the command
            try {
                method.invoke(handler, sender, args);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "An error occurred while trying to process the command");
                e.printStackTrace();
            }
        } else {
            // Otherwise we have to fake not recognising the command
            sender.sendMessage("Unknown command. Type \"help\" for help.");
        }

        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
     // If a subcommand may be present...
        if (args.length > 0) {
            // Get the subcommand given and the handler and method attached to it
            SubCommand subCommand = new SubCommand(command, args[0]);
            subCommand = subCommands.get(subCommand.toString());
            
            // If and only if the subcommand actually exists...
            if (subCommand != null) {
                Object subHandler = subCompletionHandlers.get(subCommand.toString());
                Method subMethod = subCompletionMethods.get(subCommand.toString());
                
                // If and only if both handler and method exist...
                if (subHandler != null && subMethod != null) {
                    // Reorder the arguments so we don't resend the subcommand
                    String[] subArgs = new String[args.length - 1];
                    
                    for (int i = 1; i < args.length; i++) {
                        subArgs[i - 1] = args[i];
                    }
                    
                    // If the method requires a player and the subcommand wasn't sent by one, don't continue
                    if (subMethod.getParameterTypes()[0].equals(Player.class) && !(sender instanceof Player)) {
                        return new ArrayList<>();
                    }
                    
                    // If the method requires a console and the subcommand wasn't sent by one, don't continue
                    if (subMethod.getParameterTypes()[0].equals(ConsoleCommandSender.class)
                            && !(sender instanceof ConsoleCommandSender)) {
                        return new ArrayList<>();
                    }
                    
                    // If a permission is attached to this subcommand and the sender doens't have it, don't continue
                    if (!subCommand.permission.isEmpty() && !sender.hasPermission(subCommand.permission)) {
                        return new ArrayList<>();
                    }
                    
                    // Try to process the command
                    try {
                        // Cast should be safe due to checking assignability before registering command
                        return (List<String>) subMethod.invoke(subHandler, sender, subArgs);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ArrayList<>();
                    }
                }
            }
        }
        
        // If a subcommand was successfully executed, the command will not reach this point Get the handler and method attached to this command
        Object handler = completionHandlers.get(command);
        Method method = completionMethods.get(command);
        
        // If and only if both handler and method exist...
        if (handler != null && method != null) {
            
            // If the method requires a player and the command wasn't sent by one, don't continue
            if (method.getParameterTypes()[0].equals(Player.class) && !(sender instanceof Player)) {
                return new ArrayList<>();
            }
            
            // If the method requires a console and the command wasn't sent by one, don't continue
            if (method.getParameterTypes()[0].equals(ConsoleCommandSender.class)
                    && !(sender instanceof ConsoleCommandSender)) {
                return new ArrayList<>();
            }
            
            // Try to process the command
            try {
                // Cast should be safe due to checking assignability before registering command
                return (List<String>) method.invoke(handler, sender, args);
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        } else {
            // Otherwise we have to fake not recognising the command
            return new ArrayList<>();
        }

    }
}