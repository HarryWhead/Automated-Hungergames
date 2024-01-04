package gamemanager.gamemanager.QueueSystem;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static gamemanager.gamemanager.GameManager.gameManager;
import static gamemanager.gamemanager.GameManager.queue;

public class QueueTask extends BukkitRunnable
{
    @Override
    public void run() {
        //checks queue position every second
        for (Player p : gameManager.getHub().getPlayers()) {
            checkPlayerQueue(p);
        }
    }

    public static void checkPlayerQueue(Player p) {
            TextComponent inQueue;

            //checks if player is in queue
            if (queue.getQueue().contains(p.getUniqueId())) {
                inQueue = new TextComponent(ChatColor.translateAlternateColorCodes('&',  "&aQueue position: " + (queue.getQueue().indexOf(p.getUniqueId()) + 1)));
            } else {
                inQueue = new TextComponent(ChatColor.translateAlternateColorCodes('&',  "&cNot in queue."));
            }

            //sends the result just above the players hot bar
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new BaseComponent[]{inQueue});
    }
}
