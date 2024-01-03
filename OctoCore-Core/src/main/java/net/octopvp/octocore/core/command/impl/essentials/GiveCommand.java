package net.octopvp.octocore.core.command.impl.essentials;

public class GiveCommand {
    /*
    @Command(name = "give", aliases = {"i", "g", "item"})
    @Permission(Permissions.GIVE)
    @PlayerOnly
    public CommandResult execute(@Sender Player sender, String[] args) {
        String argsJoined = String.join(" ", args);
        if (argsJoined.contains("{")) { // contains nbt, use vanilla mc command
            sender.performCommand("minecraft:give " + argsJoined);
            return CommandResult.SUCCESS;
        }
        if (args.length == 1) {
            Material material = ItemUtils.getItemMap().get(args[0].toUpperCase());
            if (material == null) {
                sender.sendMessage(CC.RED + "Usage: /i <item> [amount]");
                return CommandResult.SUCCESS;
            }
            sender.getPlayer().getInventory().addItem(ItemBuilder.from(material).amount(1).build());
            return CommandResult.SUCCESS;
        } else if (args.length == 2) {
            Material material = ItemUtils.getItemMap().get(args[0].toUpperCase());
            if (material == null) {
                sender.sendMessage(CC.RED + "Usage: /i <item> [amount]");
                return CommandResult.SUCCESS;
            }
            int i = 1;
            try {
                i = Integer.getInteger(args[1]);
            } catch (Exception e) {
                sender.sendMessage(CC.RED + "Invalid Amount! Usage: /i <item> [amount]\nDefaulting to 1.");
            }
            sender.getPlayer().getInventory().addItem(ItemBuilder.from(material).amount(i).build());
            return CommandResult.SUCCESS;
        } else sender.sendMessage(CC.RED + "Usage: /i <item> [amount]");
        return CommandResult.SUCCESS;
    }
     */
}
