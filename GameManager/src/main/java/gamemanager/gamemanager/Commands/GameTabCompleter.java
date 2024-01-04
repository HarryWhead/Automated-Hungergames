package gamemanager.gamemanager.Commands;

import gamemanager.gamemanager.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameTabCompleter implements TabCompleter
{
    List<String> arguments1 = new ArrayList<>();
    List<String> worlds = new ArrayList<>();
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(arguments1.isEmpty())
        {
            arguments1.add("start");
            arguments1.add("end");
            arguments1.add("freeze");
            arguments1.add("worldBorder");
            arguments1.add("teleportWorld");
            arguments1.add("add");
            arguments1.add("remove");
            arguments1.add("players");
        }

        if (GameManager.getPlugin(GameManager.class).getConfig().getConfigurationSection("Worlds") != null) {
            worlds.addAll(GameManager.getPlugin(GameManager.class).getConfig().getConfigurationSection("Worlds").getKeys(false));
        }

        List<String> result = new ArrayList<>();

        if (args.length == 1)
        {

            for (String s : arguments1) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(s);
                }
            }
            return result;
        }

        if (args.length == 2) {
            if (Objects.equals(args[0], "start")) {
                for (String s : worlds) {
                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                        result.add(s);
                    }
                }

                worlds.clear();
                return result;
            }
        }

        return null;
    }
}
