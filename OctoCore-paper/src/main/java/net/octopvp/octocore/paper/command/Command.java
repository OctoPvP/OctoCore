package net.octopvp.octocore.paper.command;

import net.octopvp.octocore.paper.utils.permission.Permission;
import net.octopvp.octocore.paper.utils.permission.PermissionAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command Framework - Command <br>
 * The command annotation used to designate methods as commands. All methods
 * should have a single CommandArgs argument
 * 
 * @author minnymin3
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PermissionAnnotation
public @interface Command {
	/**
	 * The name of the command. If it is a sub command then its values would be
	 * separated by periods. ie. a command that would be a subcommand of test
	 * would be 'test.subcommandname'
	 * 
	 * @return
	 */
	String name();
	/**
	 * Gets the required permission of the command
	 *
	 * @return
	 */
	Permission permission() default Permission.NOTHING;
	/**
	 * Permissions string
	 * @return
	 */
	String strPermission() default "";
	/**
	 * A list of alternate names that the command is executed under. See
	 * name() for details on how names work
	 * 
	 * @return
	 */
	String[] aliases() default {};
	/**
	 * The description that will appear in /help of the command
	 * 
	 * @return
	 */
	String description() default "";
	/**
	 * The usage that will appear in /help (commandname)
	 * 
	 * @return
	 */
	String usage() default "";
	/**
	 * Whether or not the command is available to players only
	 * 
	 * @return
	 */
	boolean playerOnly() default false;
	/**
	 * The cooldown in seconds
	 * @return
	 */
	int cooldown() default 0;
	boolean disable() default false;
}
