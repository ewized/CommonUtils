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
        public final String permission;
        public final String permissionMessage;

        public final Method method;
        public final Object instance;

        public SubCommand(Command parent, String child, String permission, String permissionMessage, Method method, Object instance) {
            this.parent = parent;
            this.child = child.toLowerCase();
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
     * in the given class, matching them with their corresponding
     * commands and subcommands registered to the specified plugin.
     *
     * @param plugin   The plugin whose commands should be considered for registration
     * @param instance An instance of the class whose methods should be considered for registration
     */
    public static void registerCommands(JavaPlugin plugin, Object instance) {

        for (Method method : instance.getClass().getMethods()) {
            Class<?>[] params = method.getParameterTypes();

            if (params.length == 2 && CommandSender.class.isAssignableFrom(params[0]) && String[].class.equals(params[1])) {
                if (method.isAnnotationPresent(CommandHandler.class)) {
                    CommandHandler annotation = method.getAnnotation(CommandHandler.class);

                    if (plugin.getCommand(annotation.name()) != null) {
                        CommandHandling handling = annotation.handling();
                        PluginCommand command = plugin.getCommand(annotation.name());
                        handling.handleCommand(command, method, instance);

                        if (handling.equals(CommandHandling.COMMAND_EXECUTION)) {
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
                        }
                    } else {
                        plugin.getLogger().warning(String.format("[CommandController]\nCould not register command of" +
                                        " name %s from an instance of %s; this plugin does not define command in its plugin.yml.",
                                annotation.name(), instance.getClass().getCanonicalName()
                        ));
                    }
                }

                if (method.isAnnotationPresent(SubCommandHandler.class)) {
                    SubCommandHandler annotation = method.getAnnotation(SubCommandHandler.class);

                    if (plugin.getCommand(annotation.parent()) != null) {
                        PluginCommand command = plugin.getCommand(annotation.parent());
                        SubCommand subcommand = new SubCommand(
                                plugin.getCommand(annotation.parent()),
                                annotation.name(),
                                annotation.permission(),
                                annotation.permissionMessage(),
                                method,
                                instance);
                        annotation.handling().handleSubCommand(command, subcommand);
                    } else {
                        plugin.getLogger().warning(String.format("[CommandController]\nCould not register subcommand of" +
                                        " name %s (super %s) from an instance of %s; this plugin does not define command in its" +
                                        " plugin.yml file.",
                                annotation.name(), annotation.parent(), instance.getClass().getCanonicalName()
                        ));
                    }
                }
            }
        }
    }
}