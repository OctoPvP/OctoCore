package net.octopvp.octocore.common.object;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//@Target(ElementType.)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Disables stuff
 */
public @interface Disable {
    String reason() default "";
}
