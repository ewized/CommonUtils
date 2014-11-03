package com.archeinteractive.dev.commonutils.command;

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
    volatile CommandAction<? super Player> player;
    volatile CommandAction<? super ConsoleCommandSender> console;

    VirtualCommand(Class<? extends JavaPlugin> plugincls) {
        this.plugincls = plugincls;
        this.player = null;
        this.console = null;
    }

    public String toString() {
        return String.format("VirtualCommand{plugin=%s,p exec=%s, c exec=%s}", plugincls.getSimpleName(),
                hasPlayerExecution(), hasConsoleExecution());
    }

    boolean hasPlayerExecution() {
        return player != null;
    }

    boolean hasConsoleExecution() {
        return console != null;
    }
}
