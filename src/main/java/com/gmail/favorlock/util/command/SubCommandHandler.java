package com.gmail.favorlock.util.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author AmoebaMan
 * An annotation interface that may be attached to a method to designate it as a subcommand handler.
 * When registering a handler with this class, only methods marked with this annotation will be considered for subcommand registration.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommandHandler {
    String parent();
    String name();
    String permission() default "";
    String permissionMessage() default "You do not have permission to use that command";
}