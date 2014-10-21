package com.archeinteractive.dev.commonutils.timekeeping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define methods that should be called when a compatible
 * timer decides to tick.
 * <p/>
 * A method is considered compatible with a timer by defining the same label in
 * its annotation.
 * <p/>
 * A method is considered self-sufficient if it defines a nonnegative delay
 * length and positive interval length. A self-sufficient method allows the
 * implicit creation of it's TimerSettings upon reading the annotation.
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TimerHandler {

    /**
     * The label of this TimerHandler method; this must always be defined, and
     * should be unique to avoid conflicts.
     */
    public String label();
    
    /**
     * The delay of this TimerHandler method; if unused, this defaults to -1. If
     * both the delay and interval are set, the annotated method can be
     * registered as a self-sufficient timer method.
     * <p>
     * The valid range for a TimerHandler delay is [0, {@link Long#MAX_VALUE}]
     * <p>
     * If either {@link TimerHandler#delay()} or {@link TimerHandler#interval()}
     * are their default of -1, the annotated method will not be considered
     * self-sufficient.
     */
    public long delay() default -1;
    
    /**
     * The interval of this TimerHandler method; if unused, this defaults to -1. If
     * both the delay and interval are set, the annotated method can be
     * registered as a self-sufficient timer method.
     * <p>
     * The valid range for a TimerHandler interval is [1, {@link Long#MAX_VALUE}]
     * <p>
     * If either {@link TimerHandler#delay()} or {@link TimerHandler#interval()}
     * are their default of -1, the annotated method will not be considered
     * self-sufficient.
     */
    public long interval() default -1;
    
    /**
     * The number of times this TimerHandler method should repeat; a negative
     * value indicates that this TimerHandler should repeat indefinitely.
     * <p>
     * The repitition value has no bearing on whether or not a timer method can be
     * considered self-sufficient, but will not be used unless the timer method is
     * self-sufficient.
     */
    public int repitition() default -1;
}
