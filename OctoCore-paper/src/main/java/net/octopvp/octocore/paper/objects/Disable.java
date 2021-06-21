package net.octopvp.octocore.paper.objects;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//@Target(ElementType.)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Disabls a command/manager if used
 */
public @interface Disable {
    String reason() default "";
}
