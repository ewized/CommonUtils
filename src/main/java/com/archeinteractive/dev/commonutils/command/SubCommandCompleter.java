package com.archeinteractive.dev.commonutils.command;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation interface that may be attached to
 * a method to designate it as a subcommand tab completer.
 * When registering a handler with this class, only
 * methods marked with this annotation will be
 * considered for subcommand completion registration.
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubCommandCompleter {

    String parent();
    
    String name();
}
