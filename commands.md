# OctoCore Commands

This document lists the commands available across the OctoCore network, including Essentials, Staff, Punishments, and RPG-specific commands.

## Essentials
*   **/ping** - Pong! Check your latency. Special message if ping is exactly 56ms.
*   **/pog** - Poggers! Just a fun command.
*   **/seen <player>** - Check when a player was last seen online or if they are currently on the network.
*   **/trash** - Open a disposal menu to discard unwanted items.
*   **/list [--text]** - List all online players. Use `--text` for a chat-based list, otherwise opens a GUI.
*   **/give <player> <item> [amount]** - Give a specific item and amount to a player.
*   **/enchant <enchant> [level]** - Apply a vanilla enchantment to the item in your hand. Supports unsafe levels.
*   **/gamemode <mode> [player]** - Change a player's gamemode. Aliases: `/gms`, `/gmc`, `/gma`, `/gmsp`.
*   **/console <command>** - Execute a command as the console.
*   **/executeonall <command>** - Execute a command across every connected server in the network.
*   **/message <player> <message>** - Send a private message to another player across the network. Aliases: `/msg`, `/w`, `/tell`.
*   **/reply <message>** - Quickly reply to the last person who messaged you. Alias: `/r`.
*   **/ignore <add|remove|list> <player>** - Manage your ignore list to block messages from specific players.
*   **/settings** - Open your personal user settings menu (Messages, sounds, etc.).
*   **/getuuid** - Retrieve your own UUID and copy it to your clipboard.

## Staff & Administration
*   **/vanish [player] [--priority <#>] [--silent]** - Hide from other players. Higher priority vanish hides you from staff with lower priority.
*   **/clearchat** - Clear the public chat for all non-staff players. Alias: `/cc`.
*   **/staffchat [message]** - Send a message to staff chat or toggle staff chat mode. Alias: `/sc`.
*   **/adminchat [message]** - Send a message to admin chat or toggle admin chat mode. Alias: `/ac`.
*   **/ipaddress <player> [limit]** - View the recent IP addresses of a player.
*   **/alts <player>** - View potential alternative accounts associated with a player's IP.
*   **/gstop** - Trigger a network-wide shutdown of all connected Minecraft servers.
*   **/callgc** - Manually trigger the Java Garbage Collector (requires confirmation as it may lag).
*   **/master** - Display the URL for the Master server management panel.
*   **/sysinfo** - View detailed system information, including memory, CPU usage, and thread counts.
*   **/queuerestart [--now] [--cancel] [--time <seconds>] [reason]** - Schedule a graceful server restart with countdown and BungeeCord redirection.
*   **/dumpplayerdata <player>** - Export a player's full data profile to a Hastebin link for debugging.
*   **/loop <times> <delay> <command>** - Execute a command multiple times with a specified tick delay. Use `%i` for the current iteration.
*   **/dupethis** - Duplicate the item currently held in your hand.

## Punishments
*   **/ban <player> [duration] <reason> [-s]** - Ban a player from the network. Use `-s` for a silent ban.
*   **/unban <player> [reason] [-s]** - Remove an active ban from a player.
*   **/mute <player> [duration] <reason> [-s]** - Mute a player in public chat.
*   **/unmute <player> [reason] [-s]** - Unmute a player.
*   **/blacklist <player> <reason> [-s]** - Permanently blacklist a player (IP-based).
*   **/unblacklist <player> [reason] [-s]** - Remove a player from the blacklist.
*   **/kick <player> <reason> [-s]** - Kick a player from the current server.
*   **/history <player>** - View the complete punishment history for a player via GUI.
*   **/staffhistory <staff>** - View the history of punishments issued by a specific staff member.
*   **/staffrollback <staff> <duration> <type>** - Roll back punishments (bans, mutes, etc.) issued by a staff member within a timeframe.

## Rank & Permission Management
*   **/grant <player>** - Open the GUI to grant or manage ranks for a player.
*   **/createrank <name>** - Create a new network-wide rank and open the editor.
*   **/deleterank <rank> [--confirm]** - Permanently delete an existing rank.
*   **/editrank <rank>** - Open the editor GUI to modify rank settings (prefix, suffix, weight, permissions).
*   **/reloadranks** - Force a reload of all ranks from the database across the network.
*   **/haspermission <player> <permission> [-p]** - Check if a player has a permission. Use `-p` to print the full node tree to console. Alias: `/perminfo`.
*   **/proxyhasperm <permission>** - Check your permission value specifically on the Velocity proxy.

## Tags & Cosmetics
*   **/tag** - Open the menu to select and change your active player tag.
*   **/tagadmin** - Open the administrative menu to create and manage player tags.
*   **/createtag** - Shortcut to open the tag creation menu.
*   **/removetag <player> <tag>** - Manually remove a specific tag from a player's owned tags.

## OctoRPG
*   **/rpgitem [id]** - Open the RPG items menu or give yourself a specific custom RPG item by ID.
*   **/rpgenchant <enchant> [level]** - Apply RPG-specific enchantments to the item in your hand.
*   **/rpgdebug** - Toggle RPG debug mode to see real-time attribute and damage calculations.
*   **/rpgstats <player> <stat|clear> [value]** - View/modify RPG stats (Vitality, Strength, etc.) or clear all stats.
*   **/rpgclear [player]** - Remove all RPG-related items from a player's inventory.
*   **/rpgquest <player> <reset|complete> [questId]** - Manage, reset, or force-complete player quest progress.
*   **/rpgeffect <player> <effect> [duration]** - Apply custom RPG status effects (Blight, Stun, Bad Luck).
*   **/rpghelp** - Display a help menu for RPG-specific commands.
