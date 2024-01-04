package gamemanager.gamemanager.QueueSystem;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static gamemanager.gamemanager.GameManager.queue;

public class QueueListener implements Listener
{
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();

        if (queue.getQueue().contains(p.getUniqueId())) {
            //remove from queue if leave
            queue.getQueue().remove(p.getUniqueId());
        }
    }
}
