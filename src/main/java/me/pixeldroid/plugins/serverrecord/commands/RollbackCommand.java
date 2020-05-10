package me.pixeldroid.plugins.serverrecord.commands;

import me.pixeldroid.plugins.serverrecord.core.ServerRecorder;
import me.pixeldroid.plugins.serverrecord.utils.LongTIme;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

public class RollbackCommand implements CommandExecutor {

    private final ServerRecorder serverRecorder;

    public RollbackCommand(ServerRecorder serverRecorder) {
        this.serverRecorder = serverRecorder;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("rollback"))
            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage("Only players can access this command at the moment.");
            } else if(sender instanceof Player) {
                Player player = (Player)sender;

                if(player.hasPermission("serverrecord.*")) {
                    rollbackCommand(sender, args);
                    return true;
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

        if(time.equals("")) {
            sender.sendMessage("The argument " + ChatColor.YELLOW + "time" + ChatColor.RESET + " is required.");
        }

        // Supplement the missing times, the code will break if not worked on.

        Date date = new Date();

        // Split the string into an array of arguments
        String[] timeArgs = time.split("_");

        Bukkit.getLogger().log(Level.SEVERE, time);

        int tLength = timeArgs.length;
        int max = 6;

        int toAdd = max - tLength;

        StringBuilder newTime = new StringBuilder();

        newTime.append(time);

        // Complete the time format
        for(int i = 0; i < toAdd; i++) {
            newTime.append("_0");
        }

        // Remove excess arguments that will break the time parser
        if(toAdd < 0) {

            // Select the first six arguments
            for(int i = 0; i < 6; i++) {
                if(i==0) {
                    newTime.append(timeArgs[i]);
                } else {
                    newTime.append("_").append(timeArgs[i]);
                }
            }

            // Tell the player his stupid mistake
            sender.sendMessage(
                    ChatColor.RED + "Your input is " +
                    ChatColor.YELLOW + time +
                    ChatColor.RED + " instead of " +
                    ChatColor.YELLOW + newTime.toString() +
                    ChatColor.RED + " in the time argument. " +
                    ChatColor.GREEN + "It was corrected."
            );
        }

        Bukkit.getLogger().log(Level.SEVERE, newTime.toString());
        // Parse the time

        Date rollbackDate = new Date();
        rollbackDate.setTime(rollbackDate.getTime() - LongTIme.getTime(newTime.toString()));

        boolean hasMoreArguments = false;

        // Parse the radius variable
        int r;

        // Check if the radius argument is present
        if(!radius.equals("")) {
            try {
                r = Integer.parseInt(radius);
                hasMoreArguments = true;
            } catch (NumberFormatException e) {
                // Stop the code because the sender made a mistake
                sender.sendMessage(
                        ChatColor.RED + radius +
                                " is an invalid number."
                );
                sender.sendMessage(
                        ChatColor.RED + "Failed to parse the radius argument. Please try again."
                );

                return;
            }
        } else {
            r = -1;
        }

        // Make sure that we have other arguments besides the time argument
        if(!cause.equals("")) {
            hasMoreArguments = true;
        } else {
            cause = "@NO_PLAYER";
        }

        // Call the rollback(Date, int, String) method if the arguments are sufficient
        // Otherwise, don't do anything
        if (hasMoreArguments) {
            serverRecorder.rollback(sender, rollbackDate, r, cause);
        } else {
            // We can't rollback anything with time argument only
            sender.sendMessage(
                    ChatColor.RED + "You need at least 2 arguments."
            );
        }

    }
}
