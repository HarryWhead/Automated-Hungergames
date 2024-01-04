package gamemanager.gamemanager.QueueSystem;

import gamemanager.gamemanager.CustomConfig.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static gamemanager.gamemanager.GameManager.gameManager;

public class QueueManager
{
    private final List<UUID> queue;
    public List<UUID> getQueue() {
        return queue;
    }


    public QueueManager(){
        queue = new ArrayList<>();
    }

    public void removeOfflinePlayers() {
        for (UUID player : queue) {
            OfflinePlayer p = Bukkit.getPlayer(player);

            if (p == null)
                queue.remove(player);

            if (!p.isOnline())
                queue.remove(player);
        }
    }

    public boolean isPriorityPlayer(String p) {
        CustomConfig.reload();

        //checks if player is in the priority config
        List<String> priorityPlayers = CustomConfig.get().getStringList("priority");
        return priorityPlayers.contains(p);
    }

    public void addPlayersToGame(int playerAmount) {
        //adds up to 100 players into the game list
        int playersToAdd = Math.min(queue.size(), playerAmount);

        gameManager.getPlayers().clear();

        for (int i = 0; i < playersToAdd; i++) {
            UUID player = queue.get(0);
            queue.remove(player);

            gameManager.getPlayers().add(player);
        }
    }
}
