package gamemanager.gamemanager.QueueSystem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static gamemanager.gamemanager.QueueSystem.QueueCommandMethods.*;

public class QueueCommands implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(Objects.equals(args[0], "add")) {
            queueAddPlayer(sender, args[1]);
            return true;
        }

        if(sender instanceof Player) {
            if (!sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "You do not have permission for this command!");
                return true;
            }
        }

        switch (args[0]) {
            case "remove":
                queueRemovePlayer(sender, Bukkit.getOfflinePlayer(args[1]));
                break;
            case "clear":
                queueClear(sender);
                break;
            case "priority":
                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "incorrect usage, please use /queue priority (add/remove) player");
                    break;
                }

                if (args[1].equalsIgnoreCase("add")) {
                    addPriority(args[2], sender);
                } else if (args[1].equalsIgnoreCase("remove")) {
                    removePriority(args[2], sender);
                } else {
                    sender.sendMessage(ChatColor.RED + "incorrect usage, please use /queue priority (add/remove) player");
                }
                break;
        }

        return true;
    }
}
