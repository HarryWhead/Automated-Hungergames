package gamemanager.gamemanager.Holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import gamemanager.gamemanager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static gamemanager.gamemanager.GameManager.gameManager;

public class HologramManager
{
    Plugin plugin = GameManager.getPlugin(GameManager.class);
    Hologram LeaderBoard;
    Hologram SinceGameStart;
    public HologramManager() {
        LeaderBoard = createHologram("LeaderboardHologram");
        SinceGameStart = createHologram("LastGameHologram");
        updateHolograms();
    }

    public Hologram createHologram(String name) {

        if (!plugin.getConfig().contains(name)) {
            return null;
        }

        //creates new hologram
        World world = Bukkit.getWorld(plugin.getConfig().getString(name + ".world"));
        Location hologramLoc = new Location(world, plugin.getConfig().getInt(name + ".x"), plugin.getConfig().getInt(name + ".y"),plugin.getConfig().getInt(name + ".z"));

        return DHAPI.createHologram(name, hologramLoc);
    }

    public void updateHolograms() {
        //updates the hologram every minute
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () ->
        {
            plugin.reloadConfig();

            List<String> linesLeaderboard = gameManager.getTopKillers();
            linesLeaderboard.add(0,ChatColor.translateAlternateColorCodes('&',"&7-=&c&l&nKill Leaderboard&r&7=-"));
            DHAPI.setHologramLines(LeaderBoard, linesLeaderboard);

            List<String> linesTimer = gameManager.timeSinceGameStart();
            linesTimer.add(0,ChatColor.translateAlternateColorCodes('&',"&7-=&c&l&nGame Timer&r&7=-"));
            DHAPI.setHologramLines(SinceGameStart, linesTimer);
        }, 0L, 20L * 60);
    }
}
