package gamemanager.gamemanager;

import gamemanager.gamemanager.Commands.GameCommands;
import gamemanager.gamemanager.Commands.GameTabCompleter;
import gamemanager.gamemanager.CustomConfig.CustomConfig;
import gamemanager.gamemanager.CustomConfig.RestoreConfig;
import gamemanager.gamemanager.Game.GameListeners;
import gamemanager.gamemanager.Game.Manager;
import gamemanager.gamemanager.Holograms.HologramManager;
import gamemanager.gamemanager.MapLoader.GameMap;
import gamemanager.gamemanager.QueueSystem.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class GameManager extends JavaPlugin {

    public static QueueManager queue = null;
    public static Manager gameManager = null;
    public static List<GameMap> maps;
    public static List<GameMap> netherMaps;
    public static GameMap currentWorld;
    public static GameMap currentNetherWorld;
    @Override
    public void onEnable() {
        // Plugin startup logic
        queue = new QueueManager();
        gameManager = new Manager();
        maps = new ArrayList<>();
        netherMaps = new ArrayList<>();

        initConfig();
        initCommands();
        initListeners();
        initGameMaps();

        new HologramManager();

        new RestoreConfig().addConfig();
    }

    public void initConfig()
    {
        CustomConfig.setup();
        CustomConfig.get().options().copyDefaults(true);
        CustomConfig.save();

        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }
    public void initCommands()
    {
        getCommand("queue").setExecutor(new QueueCommands());
        getCommand("queue").setTabCompleter(new QueueTabCompleter());
        getCommand("game").setExecutor(new GameCommands());
        getCommand("game").setTabCompleter(new GameTabCompleter());
    }

    public void initListeners()
    {
        getServer().getPluginManager().registerEvents(new QueueListener(), this);
        getServer().getPluginManager().registerEvents(new GameListeners(), this);
        new QueueTask().runTaskTimer(this,0L,20L);
    }

    public void initGameMaps()
    {
        getDataFolder().mkdirs();

        File gameMapsFolder = new File(getDataFolder(), "gameMaps");
        if (!gameMapsFolder.exists()) {
            gameMapsFolder.mkdirs();
        }

        if (GameManager.getPlugin(GameManager.class).getConfig().getConfigurationSection("Worlds") == null)
            return;

        for (String world : GameManager.getPlugin(GameManager.class).getConfig().getConfigurationSection("Worlds").getKeys(false)) {
            GameMap map = new GameMap(gameMapsFolder, world, true);
            maps.add(map);
        }

        List<String> nMaps = GameManager.getPlugin(GameManager.class).getConfig().getStringList("NetherWorlds");

        for (String world : nMaps) {
            GameMap map = new GameMap(gameMapsFolder, world, false);
            netherMaps.add(map);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        new RestoreConfig().saveGameConfig();

        for (GameMap map : maps) {
            if (map != currentWorld)
               map.unload();
        }

        for (GameMap map : netherMaps) {
            if (map != currentNetherWorld)
                map.unload();
        }
    }
}
