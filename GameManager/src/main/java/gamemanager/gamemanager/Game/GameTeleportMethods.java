package gamemanager.gamemanager.Game;

import gamemanager.gamemanager.GameManager;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static gamemanager.gamemanager.GameManager.currentWorld;
import static gamemanager.gamemanager.GameManager.gameManager;

public class GameTeleportMethods
{
    static Plugin plugin = GameManager.getPlugin(GameManager.class);
    public static boolean teleportPlayersToCornucopia(CommandSender p)
    {
        plugin.reloadConfig();

        Location center = new Location(currentWorld.getWorld(), plugin.getConfig().getInt("Worlds." + currentWorld.getSourceWorld().getName() + ".Mid.x"),plugin.getConfig().getInt("Worlds." + currentWorld.getSourceWorld().getName() + ".Mid.y"),plugin.getConfig().getInt("Worlds." + currentWorld.getSourceWorld().getName() + ".Mid.z")) ;
        int radius = plugin.getConfig().getInt("Worlds." + currentWorld.getSourceWorld().getName() + ".Radius");
        Material block = Material.valueOf(plugin.getConfig().getString("Worlds." + currentWorld.getSourceWorld().getName() + ".Block"));

        if(center == null) {
            p.sendMessage(ChatColor.RED + "Center of cornucopia is not currently set.");
            return false;
        } else if (radius == 0) {
            p.sendMessage(ChatColor.RED + "Radius of cornucopia is not currently set.");
            return false;
        } else if (block == null) {
            p.sendMessage(ChatColor.RED + "Cornucopia player block is not currently set.");
            return false;
        }

        List<Location> BlockLocations = new ArrayList<>();

        // Find  block locations around the cornucopia
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location location = center.clone().add(x, y, z);
                    if (location.getBlock().getType() == block) {
                        BlockLocations.add(location);
                    }
                }
            }
        }

        List<Player> onlinePlayers = new ArrayList<>();

        for (UUID tempP : gameManager.getPlayers()) {
            onlinePlayers.add(Bukkit.getPlayer(tempP));
        }

        int BlocksCount = BlockLocations.size();
        int playersCount = onlinePlayers.size();

        if (BlocksCount == 0) {
            p.sendMessage(ChatColor.RED + "There are no " + block + " blocks found around cornucopia.");
            return false;
        }

        int playersRemaining = playersCount - BlockLocations.size();
        int playersTeleported = 0;

        for (Location blockLocation : BlockLocations) {

            Location teleportLocation = blockLocation.clone().add(0.5, 0, 0.5);

            // Calculate the direction vector towards the cornucopia center
            Vector direction = center.toVector().subtract(teleportLocation.toVector());

            // Normalize the direction vector
            direction.normalize();

            int playersToTeleport = 1 + (playersRemaining > 0 ? 1 : 0);

            if(playersRemaining > 0)
                playersRemaining--;

            for (int i = 0; i < playersToTeleport && playersTeleported < playersCount; i++) {
                Player player = onlinePlayers.get(playersTeleported);
                teleportLocation.setDirection(direction);

                player.teleport(teleportLocation);

                playersTeleported++;
            }
        }
        return true;
    }
}
