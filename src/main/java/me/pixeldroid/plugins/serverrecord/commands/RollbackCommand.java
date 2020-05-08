package me.pixeldroid.plugins.serverrecord.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class RollbackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("record"))
            if(sender instanceof ConsoleCommandSender) {
                rollbackCommand(sender, args);
            } else if(sender instanceof Player) {
                Player player = (Player)sender;

                if(player.hasPermission("serverrecord.*")) {
                    rollbackCommand(sender, args);
                }
            }
        return false;
    }

    public void rollbackCommand(CommandSender sender, String[] args) {
        int l = args.length;

        String cause = "";
        String time = "";
        String radius = "";

        boolean ignoreNext = false;
        for(int i = 0; i < l; i++) {
            if(ignoreNext || (i + 1)==l) {
                ignoreNext = false;
                continue;
            }

            String arg = args[i];

            if(arg.startsWith("-")) {
                if(arg.equalsIgnoreCase("-c")) {
                    cause = args[i + 1];
                    ignoreNext = true;
                } else if(arg.equalsIgnoreCase("-t")) {
                    time = args[i + 1];
                    ignoreNext = true;
                } else if(arg.equalsIgnoreCase("-r")) {
                    radius = args[i + 1];
                    ignoreNext = true;
                }
            } else {
                sender.sendMessage("Invalid syntax.");
            }
        }


    }
}
