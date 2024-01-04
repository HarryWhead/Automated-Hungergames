package gamemanager.gamemanager.MapLoader;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.*;

import static gamemanager.gamemanager.GameManager.currentWorld;

public class GameMap {
    private File sourceWorldFolder;
    private File activeWorldFolder;
    private World bukkitWorld;

    public GameMap(File worldFolder, String worldName, boolean loadOnInit) {
        this.sourceWorldFolder = new File(
                worldFolder,
                worldName
        );

        if (loadOnInit) load();
    }

    public boolean load() {
        if (sourceWorldFolder.getName().startsWith("Nether")) {
            this.activeWorldFolder = new File(
                    Bukkit.getWorldContainer().getParentFile(),
                    "Game_Nether"
            );
        } else {
            this.activeWorldFolder = new File(
                    Bukkit.getWorldContainer().getParentFile(),
                    sourceWorldFolder.getName()
            );
        }

        try {
            FileUtil.copy(sourceWorldFolder, activeWorldFolder);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to load GameMap from source folder ");
            e.printStackTrace();
            return false;
        }

        if (activeWorldFolder.getName().equals("Game_Nether")) {
            this.bukkitWorld = Bukkit.createWorld(
                    new WorldCreator(activeWorldFolder.getName()).environment(World.Environment.NETHER)
            );
        } else {
            this.bukkitWorld = Bukkit.createWorld(
                    new WorldCreator(activeWorldFolder.getName())
            );
        }

        if (bukkitWorld != null) this.bukkitWorld.setAutoSave(false);

        return isLoaded();
    }

    public void unload() {
        if (bukkitWorld != null) Bukkit.unloadWorld(bukkitWorld, false);
        if (activeWorldFolder != null) FileUtil.delete((activeWorldFolder));

        bukkitWorld = null;
        activeWorldFolder = null;
    }

    public boolean restoreFromSource() {
        unload();
        return load();
    }

    public boolean isLoaded() { return getWorld() != null; }
    public World getWorld() { return bukkitWorld;}
    public File getSourceWorld() { return sourceWorldFolder; }
}

