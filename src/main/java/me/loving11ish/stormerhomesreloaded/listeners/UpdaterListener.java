package me.loving11ish.stormerhomesreloaded.listeners;

import me.loving11ish.stormerhomesreloaded.StormerHomesReloaded;
import me.loving11ish.stormerhomesreloaded.common.Message;
import me.loving11ish.stormerhomesreloaded.models.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;

public class UpdaterListener implements Listener {

    public UpdaterListener() {
        reduceOldData();
    }

    private static void reduceOldData() {
        ConfigurationSection homesSection = StormerHomesReloaded.i.getConfig().getConfigurationSection("homes");
        for(String playerName : homesSection.getKeys(false)) {
            if(homesSection.getConfigurationSection(playerName).getKeys(false).size() == 0) homesSection.set(playerName, null);
        }
        if(homesSection.getKeys(false).size() == 0) StormerHomesReloaded.i.getConfig().set("homes", null);
        StormerHomesReloaded.i.loadConfig();
    }


    @EventHandler
    public void onjoin(PlayerJoinEvent e) {
        boolean debug = StormerHomesReloaded.i.getConfig().getBoolean("logHomesRegistering");
        reduceOldData();
        if(debug) Message.systemNormal("Checking if player " + e.getPlayer().getName() + " has any homes stored in the old format...");

        String formattedPlayerName = e.getPlayer().getName().replace(".", "");
        ConfigurationSection homesSection = StormerHomesReloaded.i.getConfig().getConfigurationSection("homes");

        Set<String> playerNames = homesSection.getKeys(false);
        if(!playerNames.contains(formattedPlayerName)) {
            if(debug) Message.systemNormal("Everything seems up to date");
            return;
        }

        ConfigurationSection playerhomesSection = homesSection.getConfigurationSection(formattedPlayerName);

        Set<String> playerHomes = playerhomesSection.getKeys(false);
        Message.systemNormal("Found data, trying to port");
        for(String playerHome : playerHomes) {
            Message.systemNormal("Trying to port " + playerHome);

            ConfigurationSection playerHomeSection = playerhomesSection.getConfigurationSection(playerHome);
            if(playerHomeSection == null) {
                playerhomesSection.set(playerHome, null);
                Message.systemNormal(playerHome + " was only a remaining artifact of <0.0.8, removing...");
                continue;
            }

            double x = playerHomeSection.getDouble("x");
            double y = playerHomeSection.getDouble("y");
            double z = playerHomeSection.getDouble("z");
            double yaw = playerHomeSection.getDouble("yaw");
            double pitch = playerHomeSection.getDouble("pitch");
            String worldname = playerHomeSection.getString("world");

            World world = Bukkit.getWorld(worldname);
            if(world == null) {
                Message.systemError("Home " + playerHome + " refers to a non existant world " + worldname);
                continue;
            }

            Home.createHome(new Location(world, x, y, z, (float) yaw, (float) pitch), e.getPlayer().getUniqueId(), playerHome, e.getPlayer().getName());
            playerhomesSection.set(playerHome, null);
            Message.systemNormal(playerHome + " successfully ported!");
        }

        reduceOldData();
    }
}
