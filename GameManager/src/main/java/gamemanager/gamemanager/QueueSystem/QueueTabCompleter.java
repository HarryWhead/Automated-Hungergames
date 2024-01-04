package gamemanager.gamemanager.QueueSystem;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QueueTabCompleter implements TabCompleter
{
    List<String> arguments1 = new ArrayList<>();
    List<String> arguments2 = new ArrayList<>();
    List<Player> players = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        if (arguments1.isEmpty()) {
            arguments1.add("add");
            arguments1.add("remove");
            arguments1.add("clear");
            arguments1.add("priority");
        }

        if (arguments2.isEmpty()) {
            arguments2.add("add");
            arguments2.add("remove");
        }

        players.addAll(Bukkit.getServer().getOnlinePlayers());

        List<String> result = new ArrayList<>();

        if (args.length == 1)
        {

            for (String s : arguments1) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(s);
                }
            }

            players.clear();
            return result;
        }

        if (args.length == 2) {
            if (Objects.equals(args[0], "priority")) {
                for (String s : arguments2) {
                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                        result.add(s);
                    }
                }

                players.clear();
                return result;
            }
        }

        if (args.length == 3) {
            if (Objects.equals(args[0], "priority")) {
                for (Player p : players) {
                    if (p.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                        result.add(p.getName());
                    }
                }

                players.clear();
                return result;
            }
        }
        return null;
    }
}
