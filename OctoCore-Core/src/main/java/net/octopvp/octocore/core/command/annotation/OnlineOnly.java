package net.octopvp.octocore.core.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlineOnly {
    boolean network() default false; // if true, the player must be on the network, else they must be on the server
}
