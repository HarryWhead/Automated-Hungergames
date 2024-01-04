package gamemanager.gamemanager.Game;

import gamemanager.gamemanager.CustomConfig.CustomConfig;
import gamemanager.gamemanager.GameManager;
import gamemanager.gamemanager.MapLoader.GameMap;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sun.management.Sensor;

import java.util.*;

import static gamemanager.gamemanager.Game.GameTeleportMethods.plugin;
import static gamemanager.gamemanager.Game.GameTeleportMethods.teleportPlayersToCornucopia;
import static gamemanager.gamemanager.GameManager.*;

public class Manager
{
    private final List<UUID> players;
    public List<UUID> getPlayers() { return players; };
    private final World hub;
    private boolean frozen;
    public void setFrozen(boolean frozen) {this.frozen = frozen; }
    public boolean isFrozen() { return frozen; }
    public World getHub() { return hub; }
    private GameState gameState = GameState.LOBBY;
    private long sinceLastGame;

    public Manager() {
        this.players = new ArrayList<>();
        this.hub = Bukkit.getWorld("world");
        frozen = false;
        sinceLastGame = 0;
    }

    public void deleteWorld()
    {
        if (currentWorld.getWorld() != null) {
            for (Player p : currentWorld.getWorld().getPlayers()) {
                p.teleport(hub.getSpawnLocation());
            }
            currentWorld.restoreFromSource();
        }

        if (currentNetherWorld.getWorld() != null) {
            for (Player p : currentNetherWorld.getWorld().getPlayers()) {
                p.teleport(hub.getSpawnLocation());
            }
            currentNetherWorld.unload();
        }
    }

    public void createNewWorld(CommandSender sender, String worldName) {
        plugin.reloadConfig();

        if (maps.size() == 0) {
            sender.sendMessage(ChatColor.RED + "There are no maps currently loaded");
        }

        for (GameMap map : maps) {
            if (map.getWorld().getName().equals(worldName)) {
                currentWorld = map;
            }
        }

        if (!netherMaps.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(netherMaps.size());
            currentNetherWorld = netherMaps.get(randomIndex);
            currentNetherWorld.load();
            currentNetherWorld.getWorld().getWorldBorder().setCenter(currentWorld.getWorld().getWorldBorder().getCenter());
            currentNetherWorld.getWorld().getWorldBorder().setSize(1000);
            currentNetherWorld.getWorld().setGameRule(GameRule.NATURAL_REGENERATION, true);
            return;
        }

        sender.sendMessage(ChatColor.RED + "This is not a valid world or there are no netherMaps loaded");
    }

    public void teleportWorld(Player p, String world) {
        if (Bukkit.getWorld(world) == null) {
            p.sendMessage(ChatColor.RED + "This world doesn't exist.");
            return;
        }

        //teleport player to specified world spawn
        p.teleport(Bukkit.getWorld(world).getSpawnLocation());

    }
    public void worldBorder(int radius, int time) {
        if(currentWorld == null) {
            return;
        }

        //set the world border to the specified size
        currentWorld.getWorld().getWorldBorder().setSize(radius, time);
    }

    public void sendPlayersLeft(CommandSender player) {
        player.sendMessage("There are currently: " + ChatColor.BOLD + gameManager.players.size() + " players left\n");
        player.sendMessage(ChatColor.GREEN + "online " + ChatColor.WHITE + "|" + ChatColor.RED + " offline\n");
        player.sendMessage("\n");

            StringBuilder message = new StringBuilder("players: ");
            for (UUID playerUUID : gameManager.players) {

                OfflinePlayer players = Bukkit.getOfflinePlayer(playerUUID);

                if (!players.isOnline()) {
                    message.append(ChatColor.RED).append(players.getName()).append(ChatColor.WHITE).append(", ");
                } else {
                    message.append(ChatColor.GREEN).append(players.getName()).append(ChatColor.WHITE).append(", ");
                }
            }

            player.sendMessage(String.valueOf(message));
    }

    public void screenMessage(String[] args) {
        if(currentWorld == null) {
            return;
        }

        StringBuilder title = new StringBuilder();
        StringBuilder subtitle = new StringBuilder();
        boolean nextLine = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-s")) {
                nextLine = true;
            } else {
                if (nextLine) {
                    subtitle.append(arg).append(' ');
                } else {
                    title.append(arg).append(' ');
                }
            }
        }

        //send screen message to each player in the world
        for (Player p : currentWorld.getWorld().getPlayers()) {
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', String.valueOf(title)), ChatColor.translateAlternateColorCodes('&', String.valueOf(subtitle)), 10,30, 10);
        }

        if (currentNetherWorld == null) {
            return;
        }

        for (Player p : currentNetherWorld.getWorld().getPlayers()) {
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', String.valueOf(title)), ChatColor.translateAlternateColorCodes('&', String.valueOf(subtitle)), 10,30, 10);
        }
    }

    public void broadcastMessage(String message) {
        if(currentWorld == null) {
            return;
        }

        //send broadcast message to each player in the world
        for (Player p : currentWorld.getWorld().getPlayers()) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        if (currentNetherWorld == null) {
            return;
        }

        for (Player p : currentNetherWorld.getWorld().getPlayers()) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public List<String> getTopKillers() {
        CustomConfig.reload();
        List<String> topKillers = new ArrayList<>();

        // Check if the killTracker section exists
        if (!CustomConfig.get().contains("killTracker")) {
            return topKillers;
        }

        // Get the killTracker section
        ConfigurationSection killers = CustomConfig.get().getConfigurationSection("killTracker");

        // Create a list to sort players by kills
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>();

        // Loop through each player's kills and add them to the list
        for (String player : killers.getKeys(false)) {
            int pKills = CustomConfig.get().getInt("killTracker." + player);
            sortedList.add(new AbstractMap.SimpleEntry<>(player, pKills));
        }

        // Sort the list based on the number of kills (descending order)
        sortedList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // Get the top 10 players
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedList) {
            topKillers.add(ChatColor.translateAlternateColorCodes('&', "&c#" + (count + 1) + " " + "&7- &r" + entry.getKey() + ": &c" + entry.getValue()));
            count++;

            if (count >= 10) {
                break;  // Stop when you have the top 10 players
            }
        }

        return topKillers;
    }

    public void kickPlayer(String player) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kick " + player + " You have died!");
    }

    public List<String> timeSinceGameStart() {
        List<String> lines = new ArrayList<>();

        long elapsedTime;

        if (sinceLastGame == 0) {
            elapsedTime = 0;
        } else {

            long currentTime = System.currentTimeMillis();
            long elapsedTimeMillis = currentTime - sinceLastGame;

            // Convert milliseconds to minutes
            elapsedTime = elapsedTimeMillis / (60 * 1000);
        }

        // Format the result as a string
        lines.add("");
        lines.add(ChatColor.translateAlternateColorCodes('&', "&cCurrent game timer: ") + String.format("%d mins", elapsedTime));
        lines.add("");
        lines.add(ChatColor.translateAlternateColorCodes('&', "&eEstimated time per game: ") + "90mins");

        return lines;
    }

    public void clearItems()
    {
        for (UUID tempP : gameManager.getPlayers()) {
            Player p = Bukkit.getPlayer(tempP);
            if (p != null) {
                p.setTotalExperience(0);
                p.getInventory().clear();
                p.setExp(0);
                p.setLevel(0);
                p.setHealth(20);
                p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                p.getInventory().setHelmet(new ItemStack(Material.AIR));
                p.getInventory().setChestplate(new ItemStack(Material.AIR));
                p.getInventory().setLeggings(new ItemStack(Material.AIR));
                p.getInventory().setBoots(new ItemStack(Material.AIR));
            }
        }
    }


    public void setGameState(GameState gamestate, CommandSender sender) {
         this.gameState = gamestate;

         switch (gamestate) {
             case STARTING:

                 //teleport players to world
                 teleportPlayersToCornucopia(sender);

                 //freeze players
                 gameManager.setFrozen(true);

                 //set to midday & clear
                 currentWorld.getWorld().setTime(6000);
                 currentWorld.getWorld().setClearWeatherDuration(1000);

                 //game rules
                 currentWorld.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
                 currentWorld.getWorld().setGameRule(GameRule.DO_MOB_SPAWNING, false);
                 currentWorld.getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                 currentWorld.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                 currentWorld.getWorld().setGameRule(GameRule.DO_INSOMNIA, false);
                 currentWorld.getWorld().setGameRule(GameRule.DO_FIRE_TICK, false);

                 //start countdown
                 new CountdownTask().runTaskTimer(GameManager.getPlugin(GameManager.class),20 * 10L,20L);

                 //announce game start
                 sender.sendMessage(ChatColor.GREEN + "Game Started...");

                 //clear player items
                 clearItems();
                 break;

             case INGAME:

                 //start game timer
                 sinceLastGame = System.currentTimeMillis();

                 //start the events
                 Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "events end");
                 Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "events start");
                 break;

             case LOBBY:

                 //clears player items
                 clearItems();

                 //delete & restore world
                 deleteWorld();

                 //gets rid of game players
                 players.clear();

                 //stop the events
                 Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "events end");

                 //clear teams
                 Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "adminteams clear");

                 //announce game end
                 sender.sendMessage(ChatColor.RED + "Game Stopped...");

                 //reset since last game
                 sinceLastGame = 0;
                 break;
         }
    }

    public GameState getGameState() { return gameState; }
}
