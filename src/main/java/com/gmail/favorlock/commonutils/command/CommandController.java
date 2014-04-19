package com.gmail.favorlock.commonutils.command;

import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;

public class CommandController {

    protected static class SubCommand {
        public final Command parent;
        public final String child;
        public final String usage;
        public final String permission;
        public final String permissionMessage;

        public final Method method;
        public final Object instance;

        public SubCommand(Command parent, String child, String usage, String permission, String permissionMessage, Method method, Object instance) {
            this.parent = parent;
            this.child = child.toLowerCase();
            this.usage = usage;
            this.permission = permission;
            this.permissionMessage = permissionMessage;

            this.method = method;
            this.instance = instance;
        }

        public boolean equals(Object x) {
            return toString().equals(x.toString());
        }

        public String toString() {
            return (parent.getName() + " " + child).trim();
        }

        public static String toStringFor(Command parent, String child) {
            return (parent.getName() + " " + child).trim();
        }
    }
    
    /**
     * Registers all command handlers and subcommand handlers
     * in the given classes, matching them with their corresponding
     * commands and subcommands registered to the specified plugin.
     * Output will only be sent to the console in cases of error.
     *
     * @param plugin    The plugin whose commands should be considered for registration
     * @param instances Instances of the classes whose methods should be considered for registration
     */
    public static void registerCommands(JavaPlugin plugin, Object... instances) {
        for (Object instance : instances) {
            registerCommands(plugin, instance);
        }
    }
    
    /**
     * Registers all command handlers and subcommand handlers
     * in the given classes, matching them with their corresponding
     * commands and subcommands registered to the specified plugin.
     *
     * @param plugin    The plugin whose commands should be considered for registration
     * @param verbose  If true, very verbose output will be sent to the console even for success
     * @param instances Instances of the classes whose methods should be considered for registration
     */
    public static void registerCommands(JavaPlugin plugin, boolean verbose, Object... instances) {
        for (Object instance : instances) {
            registerCommands(plugin, verbose, instance);
        }
    }
    
    /**
     * Registers all command handlers and subcommand handlers
     * in the given class, matching them with their corresponding
     * commands and subcommands registered to the specified plugin.
     * Output will only be sent to the console in cases of error.
     *
     * @param plugin   The plugin whose commands should be considered for registration
     * @param instance An instance of the class whose methods should be considered for registration
     */
    public static void registerCommands(JavaPlugin plugin, Object instance) {
        registerCommands(plugin, false, instance);
    }
    
    /**
     * Registers all command handlers and subcommand handlers
     * in the given class, matching them with their corresponding
     * commands and subcommands registered to the specified plugin.
     *
     * @param plugin   The plugin whose commands should be considered for registration
     * @param verbose  If true, very verbose output will be sent to the console even for success
     * @param instance An instance of the class whose methods should be considered for registration
     */
    public static void registerCommands(JavaPlugin plugin, boolean verbose, Object instance) {
        if (verbose) {
            plugin.getLogger().info(String.format("[CommandController]\nReceived registration from plugin (main %s)"
                    + " to register an instance of %s", plugin.getDescription().getMain(), instance.getClass().getCanonicalName()));
        }
        
        for (Method method : instance.getClass().getMethods()) {
            Class<?>[] params = method.getParameterTypes();

            if (params.length == 2 && CommandSender.class.isAssignableFrom(params[0]) && String[].class.equals(params[1])) {
                if (method.isAnnotationPresent(CommandHandler.class)) {
                    CommandHandler annotation = method.getAnnotation(CommandHandler.class);

                    if (plugin.getCommand(annotation.name()) != null) {
                        CommandHandling handling = CommandHandling.COMMAND_EXECUTION;
                        PluginCommand command = plugin.getCommand(annotation.name());
                        handling.handleCommand(command, method, instance);

                        if (!(annotation.aliases().equals(new String[]{""})))
                            command.setAliases(Lists.newArrayList(annotation.aliases()));

                        if (!annotation.description().equals(""))
                            command.setDescription(annotation.description());

                        if (!annotation.usage().equals(""))
                            command.setUsage(annotation.usage());

                        if (!annotation.permission().equals(""))
                            command.setPermission(annotation.permission());

                        if (!annotation.permissionMessage().equals(""))
                            command.setPermissionMessage(ChatColor.RED + annotation.permissionMessage());
                        
                        if (verbose) {
                            plugin.getLogger().info(String.format("[CommandController]\nRegistered command execution of command" +
                                    " name %s to an instance of %s.",
                                    annotation.name(), instance.getClass().getCanonicalName()));
                        }
                    } else {
                        plugin.getLogger().warning(String.format("[CommandController]\nCould not register command of" +
                                " name %s from an instance of %s; this plugin does not define command in its plugin.yml.",
                                annotation.name(), instance.getClass().getCanonicalName()));
                    }
                }
                
                if (method.isAnnotationPresent(CommandCompleter.class)) {
                    CommandCompleter annotation = method.getAnnotation(CommandCompleter.class);
                    
                    if (plugin.getCommand(annotation.name()) != null) {
                        CommandHandling handling = CommandHandling.TAB_COMPLETION;
                        PluginCommand command = plugin.getCommand(annotation.name());
                        handling.handleCommand(command, method, instance);
                        
                        if (verbose) {
                            plugin.getLogger().info(String.format("[CommandController]\nRegistered tab completion of command" +
                                    " name %s to an instance of %s.",
                                    annotation.name(), instance.getClass().getCanonicalName()));
                        }
                    } else {
                        plugin.getLogger().warning(String.format("[CommandController]\nCould not register command of" +
                                " name %s from an instance of %s; this plugin does not define command in its plugin.yml.",
                                annotation.name(), instance.getClass().getCanonicalName()));
                    }
                }

                if (method.isAnnotationPresent(SubCommandHandler.class)) {
                    SubCommandHandler annotation = method.getAnnotation(SubCommandHandler.class);

                    if (plugin.getCommand(annotation.parent()) != null) {
                        PluginCommand command = plugin.getCommand(annotation.parent());
                        SubCommand subcommand = new SubCommand(
                                command,
                                annotation.name(),
                                annotation.usage(),
                                annotation.permission(),
                                annotation.permissionMessage(),
                                method,
                                instance);
                        CommandHandling.COMMAND_EXECUTION.handleSubCommand(command, subcommand);
                        
                        if (verbose) {
                            plugin.getLogger().info(String.format("[CommandController]\nRegistered command execution of subcommand" +
                                    " name %s to an instance of %s.",
                                    annotation.parent() + ":" + annotation.name(), instance.getClass().getCanonicalName()));
                        }
                    } else {
                        plugin.getLogger().warning(String.format("[CommandController]\nCould not register subcommand of" +
                                " name %s (super %s) from an instance of %s; this plugin does not define command in its" +
                                " plugin.yml file.",
                                annotation.name(), annotation.parent(), instance.getClass().getCanonicalName()));
                    }
                }
                
                if (method.isAnnotationPresent(SubCommandCompleter.class)) {
                    SubCommandCompleter annotation = method.getAnnotation(SubCommandCompleter.class);
                    
                    if (plugin.getCommand(annotation.parent()) != null) {
                        PluginCommand command = plugin.getCommand(annotation.parent());
                        SubCommand subcommand = new SubCommand(
                                command,
                                annotation.name(),
                                "",
                                "",
                                "",
                                method,
                                instance);
                        CommandHandling.TAB_COMPLETION.handleSubCommand(command, subcommand);
                        
                        if (verbose) {
                            plugin.getLogger().info(String.format("[CommandController]\nRegistered tab completion of subcommand" +
                                    " name %s to an instance of %s.",
                                    annotation.parent() + ":" + annotation.name(), instance.getClass().getCanonicalName()));
                        }
                    } else {
                        plugin.getLogger().warning(String.format("[CommandController]\nCould not register subcommand of" +
                                " name %s (super %s) from an instance of %s; this plugin does not define command in its" +
                                " plugin.yml file.",
                                annotation.name(), annotation.parent(), instance.getClass().getCanonicalName()));
                    }
                }
            }
        }
    }
}