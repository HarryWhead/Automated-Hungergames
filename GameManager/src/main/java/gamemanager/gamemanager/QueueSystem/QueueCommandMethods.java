package gamemanager.gamemanager.QueueSystem;

import gamemanager.gamemanager.CustomConfig.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static gamemanager.gamemanager.GameManager.queue;
import static gamemanager.gamemanager.QueueSystem.QueueTask.checkPlayerQueue;

public class QueueCommandMethods
{
    public static void queueAddPlayer(CommandSender sender, String player)
    {
        Player onlinePlayer = Bukkit.getPlayer(player);

        //checks whether player is online or not
        if (onlinePlayer == null) {
            sender.sendMessage(ChatColor.RED + "not a valid player");
            return;
        }

        checkPlayerQueue(onlinePlayer);

        // If already in the queue, remove them
        if (queue.getQueue().contains(onlinePlayer.getUniqueId())) {
            queue.getQueue().remove(onlinePlayer.getUniqueId());
            return;
        }

        // Check if the player has priority or not
        if (queue.isPriorityPlayer(onlinePlayer.getName())) {
            // Find the position to insert behind the last priority player
            int index = 0;
            for (UUID uuid : queue.getQueue()) {
                if (!queue.isPriorityPlayer(Bukkit.getOfflinePlayer(uuid).getName())) {
                    break;
                }
                index++;
            }
            queue.getQueue().add(index, onlinePlayer.getUniqueId());
        } else {
            // Non-priority player, add them to the end of the queue
            queue.getQueue().add(onlinePlayer.getUniqueId());
        }
    }

    public static void queueRemovePlayer(CommandSender sender, OfflinePlayer offlinePlayer)
    {
        if (!queue.getQueue().contains(offlinePlayer.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "This player is not in the queue.");
            return;
        }

        //manually remove player from queue
        queue.getQueue().remove(offlinePlayer.getUniqueId());
        sender.sendMessage(ChatColor.GRAY + offlinePlayer.getName() + ChatColor.WHITE + " removed from the queue.");
    }

    public static void queueClear(CommandSender sender)
    {
       queue.getQueue().clear();
       sender.sendMessage("queue has been cleared.");
    }

    public static void addPriority(String player, CommandSender p) {
        List<String> priorityQueue = CustomConfig.get().getStringList("priority");

        //adds player to priority config
        if (!priorityQueue.contains(player)) {
            priorityQueue.add(player);
            CustomConfig.get().set("priority", priorityQueue);
            CustomConfig.save();
            p.sendMessage(player + " added to the priority list.");
        } else {
            p.sendMessage(ChatColor.RED + "This player is already in the priority queue");
        }
    }

    public static void removePriority(String player, CommandSender p) {
        List<String> priorityQueue = CustomConfig.get().getStringList("priority");

        //removes player from priority config
        if (priorityQueue.contains(player)) {
            priorityQueue.remove(player);
            CustomConfig.get().set("priority", priorityQueue);
            CustomConfig.save();
            p.sendMessage(player + " removed from the priority list.");
        } else {
            p.sendMessage(ChatColor.RED + "This player is not in the priority queue");
        }
    }
}
