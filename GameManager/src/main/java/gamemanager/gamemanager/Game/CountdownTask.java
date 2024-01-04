package gamemanager.gamemanager.Game;
import org.bukkit.scheduler.BukkitRunnable;

import static gamemanager.gamemanager.GameManager.gameManager;

public class CountdownTask extends BukkitRunnable
{
    private int count = 10;

    @Override
    public void run() {
        count--;

        if (count <= 0) {
            gameManager.setGameState(GameState.INGAME, null);
            this.cancel();
        }
    }
}
