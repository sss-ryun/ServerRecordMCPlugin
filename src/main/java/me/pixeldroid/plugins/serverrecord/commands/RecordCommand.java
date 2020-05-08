package me.pixeldroid.plugins.serverrecord.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class RecordCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(command.getName().equalsIgnoreCase("record"))
            if(commandSender instanceof ConsoleCommandSender) {
                recordCommand(commandSender, args);
            } else if(commandSender instanceof Player) {
                Player player = (Player)commandSender;

                if(player.hasPermission("serverrecord.*")) {
                    recordCommand(commandSender, args);
                }
            }

        return false;
    }

    public void recordCommand(CommandSender commandSender, String[] args) {
        if(commandSender instanceof ConsoleCommandSender) {

        } else if(commandSender instanceof Player) {
            Player player = (Player)commandSender;


        }
    }
}
