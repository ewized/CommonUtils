package com.gmail.favorlock.commonutils.command;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation interface that may be attached to
 * a method to designate it as a subcommand handler.
 * When registering a handler with this class, only
 * methods marked with this annotation will be
 * considered for subcommand registration.
 * @originalauthor AmoebaMan
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubCommandHandler {
    String parent();
    String name();
    String permission() default "";
    String permissionMessage() default "You do not have permission to use that command";
    
    CommandHandling handling() default CommandHandling.COMMAND_EXECUTION;
}
