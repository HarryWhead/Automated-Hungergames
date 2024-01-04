package gamemanager.gamemanager.Commands;

import gamemanager.gamemanager.CustomConfig.CustomConfig;
import gamemanager.gamemanager.Game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static gamemanager.gamemanager.GameManager.gameManager;
import static gamemanager.gamemanager.GameManager.queue;

public class GameCommands implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player) {
            if (!sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "You do not have permission for this command!");
                return true;
            }
        }

        CustomConfig.reload();

        switch (args[0]) {
            case "start":
                if(args.length == 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect usage, please use /game start (world) (amount of players)");
                    return true;
                }
                if (gameManager.getGameState() != GameState.LOBBY) {
                    sender.sendMessage(ChatColor.RED + "There is already another game happening.");
                    return true;
                } else if (queue.getQueue().size() <= 0) {
                    sender.sendMessage(ChatColor.RED + "Not enough players to start a game.");
                    return true;
                }

                int playerAmount = 92;

                if (args.length == 3) {
                    try {
                        playerAmount = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis is not a valid number."));
                        return true;
                    }
                }

                gameManager.createNewWorld(sender, args[1]);
                queue.addPlayersToGame(playerAmount);
                gameManager.setGameState(GameState.STARTING, sender);
                break;
            case "end":
                if (gameManager.getGameState() != GameState.INGAME) {
                    sender.sendMessage(ChatColor.RED + "There is no game taking place.");
                    return true;
                }
                gameManager.setGameState(GameState.LOBBY, sender);
                break;
            case "freeze":
                gameManager.setFrozen(!gameManager.isFrozen());
                break;
            case "worldBorder":
                if (args.length == 2) {
                    gameManager.worldBorder(Integer.parseInt(args[1]), 0);
                } else if (args.length == 3) {
                    gameManager.worldBorder(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                }
                break;
            case "broadcast":
                if (args.length > 2) {
                    String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    gameManager.broadcastMessage(message);
                }
                break;
            case "screenmessage":
                if (args.length > 2) {
                    gameManager.screenMessage(Arrays.copyOfRange(args, 1, args.length));
                }
                break;
            case "teleportWorld":
                gameManager.teleportWorld((Player) sender, args[1]);
                break;
            case "players":
                gameManager.sendPlayersLeft(sender);
                break;
            case "add":
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect usage, please use /game add (player)");
                }

                if (Bukkit.getPlayer(args[1]) == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis is not a valid player."));
                    return true;
                }

                Player join = Bukkit.getPlayer(args[1]);

                if (gameManager.getPlayers().contains(join.getUniqueId())) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis player is already in the game."));
                    return true;
                }

                gameManager.getPlayers().add(join.getUniqueId());
                break;
            case "remove":
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect usage, please use /game remove (player)");
                }

                if (Bukkit.getPlayer(args[1]) == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis is not a valid player."));
                    return true;
                }

                Player leave = Bukkit.getPlayer(args[1]);

                if (!gameManager.getPlayers().contains(leave.getUniqueId())) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis player is not in the game."));
                    return true;
                }

                gameManager.getPlayers().remove(leave.getUniqueId());
                leave.teleport(gameManager.getHub().getSpawnLocation());
                break;
        }

        return true;
    }
}
