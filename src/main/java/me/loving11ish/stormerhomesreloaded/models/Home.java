package me.loving11ish.stormerhomesreloaded.models;

import io.papermc.lib.PaperLib;
import me.loving11ish.stormerhomesreloaded.StormerHomesReloaded;
import me.loving11ish.stormerhomesreloaded.common.Lang;
import me.loving11ish.stormerhomesreloaded.common.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Home {

    public static Integer taskID1;
    private Location location;
    private final UUID ownerUUID;
    private final String ownerName;
    private final String name;

    public static List<Home> all = new ArrayList<>();

    public static Home createHome(Location loc, UUID uuid, String homeName, String ownerName) {
        for(Home home : all) {
            if(home.ownerUUID.equals(uuid) && home.name.equalsIgnoreCase(homeName)) {
                home.setLocation(loc);
                saveToConfig(home);
                return home;
            }
        }
        return new Home(loc, uuid, homeName, ownerName);
    }

    private Home(Location loc, UUID uuid, String homeName, String ownerName) {
        this.ownerName = ownerName;
        this.location = loc;
        this.ownerUUID = uuid;
        this.name = homeName;
        saveToConfig(this);
        all.add(this);
    }

    private static void saveToConfig(Home home) {
        String path = "homes2." + home.ownerUUID + "." + home.name + ".";
        StormerHomesReloaded.i.getConfig().set(path + "x", home.location.getX());
        StormerHomesReloaded.i.getConfig().set(path + "y", home.location.getY());
        StormerHomesReloaded.i.getConfig().set(path + "z", home.location.getZ());
        StormerHomesReloaded.i.getConfig().set(path + "yaw", home.location.getYaw());
        StormerHomesReloaded.i.getConfig().set(path + "pitch", home.location.getPitch());
        StormerHomesReloaded.i.getConfig().set(path + "world", home.location.getWorld().getName());
        StormerHomesReloaded.i.getConfig().set(path + "playername", home.ownerName);
        StormerHomesReloaded.i.loadConfig();
    }

    public static void deleteHome(Home home) {
        String path = "homes2." + home.ownerUUID + "." + home.name;
        StormerHomesReloaded.i.getConfig().set(path, null);
        StormerHomesReloaded.i.cleanupPlayerWithNoHomes();
        StormerHomesReloaded.i.loadConfig();
        all.remove(home);
    }

    public void delete() {
        deleteHome(this);
    }

    public static Set<Home> getPlayerHomes(UUID uuid){
        Set<Home> homes = new HashSet<>();
        for(Home home : Home.all) {
            if(home.getOwner().equals(uuid)) {
                homes.add(home);
            }
        }
        return homes;
    }

    public static Set<Home> getPlayerHomes(String ownerName){
        Set<Home> homes = new HashSet<>();
        for(Home home : Home.all) if(home.getOwnerName().equals(ownerName)) homes.add(home);
        return homes;
    }

    public static Set<Home> getPlayerHomes(Player p){
        return getPlayerHomes(p.getUniqueId());
    }

    public static Home findHome(Player owner, String name) {
        return findHome(owner.getUniqueId(), name);
    }

    public static Home findHome(UUID uuid, String name) {
        for(Home home : getPlayerHomes(uuid)) if(home.getName().equalsIgnoreCase(name)) return home;
        return null;
    }

    public static Home findHome(String ownerName, String name) {
        for(Home home : all) if(home.getName().equalsIgnoreCase(name) && home.ownerName.equals(ownerName)) return home;
        return null;
    }

    public void home(Player p) {
        Message.normal(p, Lang.COMMAND_SUCCESS_HOME.toString().replace("{HOME}", getName()));
        Location originalLocation = p.getLocation();
        boolean cancelOnMove = StormerHomesReloaded.i.getConfig().getBoolean("cancelonmove");
        taskID1 = Bukkit.getScheduler().scheduleSyncRepeatingTask(StormerHomesReloaded.i, new Runnable() {
            int timer = StormerHomesReloaded.i.getConfig().getInt("teleportationDelay");
            @Override
            public void run() {
                if (timer == 0){
                    PaperLib.teleportAsync(p, getLocation());
                    Bukkit.getScheduler().cancelTask(taskID1);
                    return;
                }
                if(cancelOnMove && (originalLocation.getWorld() != p.getWorld() || originalLocation.distanceSquared(p.getLocation()) > 1)) {
                    Message.error(p, Lang.ERROR_MOVED.toString());
                    Bukkit.getScheduler().cancelTask(taskID1);
                    return;
                }
                timer --;
            }
        }, 0, 1);
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public UUID getOwner() {
        return this.ownerUUID;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "[Home {"+this.location.toString()+","+this.ownerUUID+","+this.name+"}]";
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Integer getTaskID1() {
        return taskID1;
    }
}
