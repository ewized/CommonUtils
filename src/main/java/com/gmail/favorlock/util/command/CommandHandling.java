package com.gmail.favorlock.util.command;

import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.command.PluginCommand;

/**
 * Utility for CommandController commands
 */
public enum CommandHandling {

    COMMAND_EXECUTION {
        public void handleCommand(PluginCommand command, Method method, Object instance) {
            ExecutorMethod executor = ExecutorMethod.forCommand(command);
            executor.addMainCommand(method, instance);
            command.setExecutor(executor);
        }
        
        public void handleSubCommand(PluginCommand command, CommandController.SubCommand subcommand) {
            ExecutorMethod executor = ExecutorMethod.forCommand(command);
            executor.addSubCommand(subcommand);
            command.setExecutor(executor);
        }
    },
    TAB_COMPLETION {
        public void handleCommand(PluginCommand command, Method method, Object instance) {
            Class<?> returns = method.getReturnType();
            
            if (!List.class.isAssignableFrom(returns)) {
                command.getPlugin().getLogger().warning(String.format("[CommandController]\nCould not register" +
                        " command of name %s from an instance of %s; the method of name %s specifies" +
                        " that it should be used for tab completion, however its return type is not" +
                        " compatible with List<String>.",
                        command.getName(), instance.getClass().getCanonicalName(), method.getName()));
                return;
            }
            
            CompleterMethod completer = CompleterMethod.forCommand(command);
            completer.addMainCommand(method, instance);
            command.setTabCompleter(completer);
        }
        
        public void handleSubCommand(PluginCommand command, CommandController.SubCommand subcommand) {
            Method method = subcommand.method;
            Object instance = subcommand.instance;
            Class<?> returns = method.getReturnType();
            
            if (!List.class.isAssignableFrom(returns)) {
                command.getPlugin().getLogger().warning(String.format("[CommandController]\nCould not register" +
                        " command of name %s from an instance of %s; the method of name %s specifies" +
                        " that it should be used for tab completion, however its return type is not" +
                        " compatible with List<String>.",
                        command.getName(), instance.getClass().getCanonicalName(), method.getName()));
                return;
            }
            
            CompleterMethod completer = CompleterMethod.forCommand(command);
            completer.addSubCommand(subcommand);
            command.setTabCompleter(completer);
        }
    };
    
    public abstract void handleCommand(PluginCommand command, Method method, Object instance);
    
    public abstract void handleSubCommand(PluginCommand command, CommandController.SubCommand subcommand);
}
