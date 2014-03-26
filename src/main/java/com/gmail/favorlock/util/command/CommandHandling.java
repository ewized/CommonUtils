package com.gmail.favorlock.util.command;

/**
 * Utility for CommandController commands
 */
public enum CommandHandling {

    COMMAND_EXECUTION,
    TAB_COMPLETION,
    ;
    
    public boolean shouldCommandExecute() {
        switch (this) {
        case COMMAND_EXECUTION:
            return true;
        case TAB_COMPLETION:
        default:
            return false;
        }
    }
    
    public boolean shouldTabComplete() {
        switch (this) {
        case TAB_COMPLETION:
            return true;
        case COMMAND_EXECUTION:
        default:
            return false;
        }
    }
}
