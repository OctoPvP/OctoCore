package net.octopvp.octocore.paper.command;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.utils.permission.PermissionAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PermissionAnnotation
public @interface SubCommand {
    String name();
    Permission permission() default Permission.NOTHING;
    String description() default "Default Description";
    String strPermission() default "";
    String[] aliases() default {};
    boolean playerOnly() default false;
    int cooldown() default 0;
}
