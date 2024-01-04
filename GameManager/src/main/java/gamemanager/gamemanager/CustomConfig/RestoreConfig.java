package gamemanager.gamemanager.CustomConfig;

import gamemanager.gamemanager.Game.GameState;
import gamemanager.gamemanager.MapLoader.GameMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static gamemanager.gamemanager.GameManager.*;

public class RestoreConfig
{
    public void saveGameConfig() {
        CustomConfig.reload();

        List<String> gamePlayers = new ArrayList<>();
        for (UUID p : gameManager.getPlayers()) {
            gamePlayers.add(p.toString());
        }

        //saves all game info on reload
        CustomConfig.get().set("gamePlayers", gamePlayers);
        CustomConfig.get().set("currentWorld", currentWorld.getWorld().getName());
        CustomConfig.get().set("currentNetherWorld", currentNetherWorld.getWorld().getName());
        CustomConfig.get().set("gameState", gameManager.getGameState());
        CustomConfig.save();
    }

    public void addConfig() {
        CustomConfig.reload();

        String state = CustomConfig.get().getString("gameState");

        if (state != null)
            switch (state) {
                case "LOBBY":
                    return;
                case "INGAME":
                case "STARTING":
                    gameManager.setGameState(GameState.INGAME, null);
                    break;
            }

        //gets stored players and adds them to the game
        List<String> gamePlayers =  CustomConfig.get().getStringList("gamePlayers");
        for (String p : gamePlayers) {
            gameManager.getPlayers().add(UUID.fromString(p));
        }

        //get world
        for (GameMap map : maps) {
            if (map.getWorld().getName().equals(CustomConfig.get().getString("currentWorld"))) {
                currentWorld = map;
                return;
            }
        }

        for (GameMap map : netherMaps) {
            if (map.getWorld().getName().equals(CustomConfig.get().getString("currentNetherWorld"))) {
                currentNetherWorld = map;
                return;
            }
        }
    }
}
