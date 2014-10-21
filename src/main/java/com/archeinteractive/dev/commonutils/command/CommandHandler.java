package com.archeinteractive.dev.commonutils.command;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation interface that may be attached to
 * a method to designate it as a command handler.
 * When registering a handler with this class, only
 * methods marked with this annotation will be
 * considered for command registration.
 * 
 * @originalauthor AmoebaMan
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandHandler {
    String name();

    String[] aliases() default {""};

    String description() default "";

    String usage() default "";

    String permission() default "";

    String permissionMessage() default "You do not have permission to use that command";
}
