package gamemanager.gamemanager.Game;

import gamemanager.gamemanager.CustomConfig.CustomConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import static gamemanager.gamemanager.GameManager.*;


public class GameListeners implements Listener
{

    @EventHandler
    public void onRespawnEvent(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        if (gameManager.getPlayers().contains(p.getUniqueId())) {
            //teleport back to hub
            gameManager.getPlayers().remove(p.getUniqueId());
        }

        e.setRespawnLocation(gameManager.getHub().getSpawnLocation());
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();

        if (!gameManager.getPlayers().contains(p.getUniqueId())) {

            p.teleport(gameManager.getHub().getSpawnLocation());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (gameManager.getPlayers().contains(player.getUniqueId()) && gameManager.isFrozen()) {
            if(!player.isOp())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (gameManager.getPlayers().contains(player.getUniqueId()) && gameManager.isFrozen()) {
                  event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player killer = event.getEntity().getKiller();

        //check if killed by player
        if (killer != null) {
            CustomConfig.reload();

            //if player already has kills, add onto that
            if (CustomConfig.get().contains("killTracker." + killer.getName())) {
                int currentKills = CustomConfig.get().getInt("killTracker." + killer.getName());

                currentKills = currentKills + 1;
                CustomConfig.get().set("killTracker." + killer.getName(), currentKills);
                CustomConfig.save();
                return;
            }

            //if new killer just assign 1 kill
            CustomConfig.get().set("killTracker." + killer.getName(), 1);
            CustomConfig.save();
        }
    }
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e) {
        World fromWorld = e.getFrom().getWorld();
        switch (e.getCause()) {
            case NETHER_PORTAL:
                switch (fromWorld.getEnvironment()) {
                    case NORMAL:
                        e.getTo().setWorld(currentNetherWorld.getWorld());
                        break;
                    case NETHER:
                        Location newTo = e.getFrom().multiply(8.0D);
                        newTo.setWorld(currentWorld.getWorld());
                        e.setTo(newTo);
                        break;
                    default:
                }
        }
    }
}
