package com.tom.navi.listeners;

import com.tom.navi.Main;
import com.tom.navi.NavigationAPI;
import com.tom.navi.gui.NavigationGUI;
import com.tom.navi.sql.NaviSQL;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class InventoryListener implements Listener {

    private Main plugin;

    public InventoryListener(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        // Prüfen ob der Klick im Inventar war
        if(e.getSlot() !=-999) {
            if(e.getView().getTitle().contains("§8» §3Navigation")){

                if(e.getCurrentItem().hasItemMeta() || e.getCurrentItem().equals(Material.AIR)) {

                    Material cI = e.getCurrentItem().getType();
                    if(e.getView().getTitle().equalsIgnoreCase("§8» §3Navigation")) {
                        if(cI.equals(Material.PLAYER_HEAD)) {
                            if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§3Fraktionen")) {
                                p.openInventory(NavigationGUI.getGui("Fraktionen", p));
                            } else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aSonstige Orte")) {
                                p.openInventory(NavigationGUI.getGui("Sonstige Orte", p));
                            } else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§bIn der Nähe")) {
                                p.openInventory(NavigationGUI.getGui("In der Nähe", p));
                            } else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6Gespeichert")) {
                                p.openInventory(NavigationGUI.getGui("Gespeichert", p));
                            } else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7Suche")) {
                                p.sendMessage("§cNoch in arbeit!");
                                p.closeInventory();
                            } else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§cNavigation beenden")) {
                                NavigationAPI api = new NavigationAPI();
                                api.removeNavigation(p);
                                p.closeInventory();
                            }
                        }
                    } else if(e.getView().getTitle().startsWith("§8» §3Navigation §8- ")) {
                        String[] myInventoryNameList = e.getView().getTitle().split("§8- ");
                        String myInventoryName = myInventoryNameList[1];
                        myInventoryName = myInventoryName.replace("§3", "").replace("§a", "").replace("§b", "").replace("§6", "").replace("§7", "");

                        if(cI.equals(Material.PAPER)) {
                            if(e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§3")) {
                                String[] myItemNameList = e.getCurrentItem().getItemMeta().getDisplayName().split("§3");
                                String myItemName = myItemNameList[1];
                                p.closeInventory();
                                Location loc = NaviSQL.getNearestObjekt(myInventoryName, myItemName, p);
                                NavigationAPI api = new NavigationAPI();
                                api.setNavigationLocation(p, loc, Color.AQUA);
                            }
                        }
                    }
                }
                e.setCancelled(true);
            }
        }
    }
}
