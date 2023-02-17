package com.tom.navi;

import com.tom.navi.commands.NaviMapCommand;
import com.tom.navi.listeners.InventoryListener;
import com.tom.navi.sql.MySQL;
import com.tom.navi.sql.NaviSQL;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.nio.charset.StandardCharsets;
import java.util.*;

public final class Main extends JavaPlugin {

    public static HashMap<Player, Location> naviLocation = new HashMap<>();
    public static HashMap<Player, Color> naviColor = new HashMap<>();
    public static HashMap<Player, Location> firstLoc = new HashMap<>();

    public static HashMap<Player, List<Location>> playerWay = new HashMap<>();
    public static HashMap<Player, Boolean> canStartWay = new HashMap<>();

    public static HashMap<Player, List<Location>> playerMapViewer = new HashMap<>();

    public static HashMap<Player, String> playerModus = new HashMap<>();

    public static MySQL sql;

    @Override
    public void onEnable() {
        sql = new MySQL("univastro.net", "starvalcity_dev_navigation", "starvalcity_dev", "KDLJ5WBNRO5GsjWD");
        registerMyCommands();
        registerMyEvents();
        startNaviScheduler();
        startMapViewScheduler();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerMyEvents(){
        new InventoryListener(this);
    }

    public void registerMyCommands(){
        getCommand("navi").setExecutor(new NaviMapCommand(this));
        getCommand("navigation").setExecutor(new NaviMapCommand(this));

        getCommand("setpunktkoordinate").setExecutor(new NaviMapCommand(this));
        getCommand("setStrecke").setExecutor(new NaviMapCommand(this));
        getCommand("removeStrecke").setExecutor(new NaviMapCommand(this));
        getCommand("showStrecken").setExecutor(new NaviMapCommand(this));
        getCommand("setOrt").setExecutor(new NaviMapCommand(this));
    }


    private void startMapViewScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(playerMapViewer.size() > 0) {
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        if(playerMapViewer.containsKey(all)) {
                            List<Location> list1 = NaviSQL.getStrecken(all, "P1");
                            List<Location> list2 = NaviSQL.getStrecken(all, "P2");
                            ParticleManager manager = new ParticleManager();
                            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.AQUA, 0.6F);
                            for(int i = 0; i < list1.size(); i++) {
                                manager.particleLine(all, dustOptions, list1.get(i), list2.get(i), 0.5);
                                manager.particleCube(all, list1.get(i), dustOptions);
                                manager.particleCube(all, list2.get(i), dustOptions);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 5, 5);
    }

    private void startNaviScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(naviLocation.size() > 0 || playerWay.size() > 0 || firstLoc.size() > 0) {
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        if(!playerModus.containsKey(all)) {
                            playerModus.put(all, "direct");
                        }

                        if(playerModus.get(all).equalsIgnoreCase("street")) {

                            if(playerWay.containsKey(all) && canStartWay.containsKey(all)) {
                                if(canStartWay.get(all)) {
                                    List<Location> locList = playerWay.get(all);

                                    if(all.getLocation().distance(locList.get(locList.size()-1)) <= 2) {
                                        playerWay.remove(all);
                                        if(firstLoc.containsKey(all)) {
                                            naviLocation.put(all, firstLoc.get(all));
                                            firstLoc.remove(all);
                                        }
                                    }
                                    ParticleManager manager = new ParticleManager();
                                    Particle.DustOptions dustOptions = new Particle.DustOptions(naviColor.get(all), 0.6F);

                                    for(int i = 0; i < locList.size(); i++) {
                                        if(all.getLocation().distance(locList.get(1)) <= 5) {
                                            List<Location> newLocList = new ArrayList<>();
                                            for(int u = 1; u < locList.size(); u++) {
                                                newLocList.add(locList.get(u));
                                            }
                                            playerWay.put(all, newLocList);
                                            locList = newLocList;
                                        }
                                    }

                                    if(locList.size() >= 3) {
                                        manager.particleLine(all, dustOptions, locList.get(0), locList.get(1), 0.5);
                                        manager.particleLine(all, dustOptions, locList.get(1), locList.get(2), 0.5);
                                        manager.particleCube(all, locList.get(0), dustOptions);
                                        manager.particleCube(all, locList.get(1), dustOptions);
                                        manager.particleCube(all, locList.get(2), dustOptions);
                                    } else {
                                        manager.particleLine(all, dustOptions, locList.get(0), locList.get(1), 0.5);
                                        manager.particleCube(all, locList.get(0), dustOptions);
                                        manager.particleCube(all, locList.get(1), dustOptions);
                                    }
                                }
                            }

                            if(naviLocation.containsKey(all)) {
                                ParticleManager pm = new ParticleManager();
                                pm.sendParticleLine(all, naviLocation.get(all), naviColor.get(all));

                                if(all.getLocation().distance(naviLocation.get(all)) <= 2) {
                                    naviLocation.remove(all);
                                    canStartWay.put(all, true);
                                }
                            }

                            if(firstLoc.containsKey(all)) {
                                if(all.getLocation().distance(firstLoc.get(all)) <= 5) {
                                    NavigationAPI api = new NavigationAPI();
                                    api.removeNavigation(all);
                                }
                            }
                        } else {
                            ParticleManager pm = new ParticleManager();
                            if(firstLoc.containsKey(all)) {
                                Particle.DustOptions dustOptions = new Particle.DustOptions(naviColor.get(all), 0.6F);
                                pm.sendParticleLine(all, firstLoc.get(all), naviColor.get(all));
                                pm.particleCube(all, firstLoc.get(all), dustOptions);

                                if(all.getLocation().distance(firstLoc.get(all)) <= 2) {
                                    NavigationAPI api = new NavigationAPI();
                                    api.removeNavigation(all);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 5, 5);
    }

    public static ItemStack buildIS(Material mat, String name, int amount, int s){
        ItemStack is = new ItemStack(mat, amount, (byte)s);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        is.setItemMeta(im);
        return is;
    }

}
