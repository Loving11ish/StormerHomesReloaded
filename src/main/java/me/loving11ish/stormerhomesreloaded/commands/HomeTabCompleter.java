package me.loving11ish.stormerhomesreloaded.commands;

import me.loving11ish.stormerhomesreloaded.models.Home;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> list = new ArrayList<>();

        String s = "";
        int depth = 0;

        if(sender instanceof Player) {

            Player p = (Player) sender;

            if(cmd.getName().equalsIgnoreCase("home") || cmd.getName().equalsIgnoreCase("delhome")) {
                for(Home home : Home.getPlayerHomes(p)) {
                    s = home.getName(); if(args.length == depth || (args.length == depth + 1 && s.toLowerCase().startsWith(args[depth].toLowerCase()))) list.add(s);
                }
            }
        }
        return list;
    }
}
