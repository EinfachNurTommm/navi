package com.tom.navi.gui;

import com.tom.navi.Main;
import com.tom.navi.NavigationAPI;
import com.tom.navi.sql.NaviSQL;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class NavigationGUI {

    static String lightBlueF = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzAxYzFlMjliYWIyNzI0NzljNTI2ZjIxOGYwNTE1ODZjMjJmMjlkYzA5MjhiNmNhOThhZTE0YTNlMDdhZDkifX19";
    static String limeS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQzNTlhZWI3NjdlOTE0NTIxZmY1YjlkMmM2ODVkYjU0ODcxZGMzZjdlOGY3MmY2ZWI4YzFmMmNhOGMyZDIifX19";
    static String cyanI = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjFmMDdkMWI5Mjk3MTQxYjMwODNmYzhkMTU2OTgxYzc4ZGQ2ZGFlY2VjNTI3NGViMGQ4N2UzYThiZGU5YmY4In19fQ==";
    static String orangeG = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFkMTQzN2QxNzEzYzVmMmU4ODBkYzk1YzM5NDU5NzEwZGJjYjRmNzNiNTEwNGQ2ODhkZmRhNzdiYzhiYjkifX19";
    static String redX = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==";
    static String blackQuestionMark = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZiYTYzMzQ0ZjQ5ZGQxYzRmNTQ4OGU5MjZiZjNkOWUyYjI5OTE2YTZjNTBkNjEwYmI0MGE1MjczZGM4YzgyIn19fQ==";

    public static Inventory getGui(String invName, Player p) {
        Inventory inv = null;

        if(invName.equalsIgnoreCase("standard")) {
            inv = Bukkit.getServer().createInventory(null, 27, "§8» §3Navigation");
        } else if(invName.equalsIgnoreCase("Fraktionen")) {
            inv = Bukkit.getServer().createInventory(null, 27, "§8» §3Navigation §8- §3Fraktionen");
        } else if(invName.equalsIgnoreCase("Sonstige Orte")) {
            inv = Bukkit.getServer().createInventory(null, 27, "§8» §3Navigation §8- §aSonstige Orte");
        } else if(invName.equalsIgnoreCase("In der Nähe")) {
            inv = Bukkit.getServer().createInventory(null, 27, "§8» §3Navigation §8- §bIn der Nähe");
        } else if(invName.equalsIgnoreCase("Gespeichert")) {
            inv = Bukkit.getServer().createInventory(null, 27, "§8» §3Navigation §8- §6Gespeichert");
        } else if(invName.equalsIgnoreCase("Suche")) {
            inv = Bukkit.getServer().createInventory(null, 27, "§8» §3Navigation §8- §7Suche");
        }

        for(int i = 0; i < 27; i++) {
            inv.setItem(i, Main.buildIS(Material.BLACK_STAINED_GLASS_PANE, "§a", 1, 0));
        }

        if(invName.equalsIgnoreCase("standard")) {
            inv.setItem(11, SkullCreator.buildHeadBase(lightBlueF, "§3Fraktionen"));
            inv.setItem(4, SkullCreator.buildHeadBase(limeS, "§aSonstige Orte"));
            inv.setItem(9, SkullCreator.buildHeadBase(cyanI, "§bIn der Nähe"));
            inv.setItem(15, SkullCreator.buildHeadBase(orangeG, "§6Gespeichert"));
            inv.setItem(17, SkullCreator.buildHeadBase(blackQuestionMark, "§7Suche"));

            inv.setItem(22, SkullCreator.buildHeadBase(redX, "§cNavigation beenden"));
        } else if(invName.equalsIgnoreCase("Fraktionen")) {

            List<String> myList = NaviSQL.getOrteNamen("Fraktionen");
            for(int i = 0; i < myList.size(); i++) {
                inv.setItem(i, Main.buildIS(Material.PAPER, "§3" + myList.get(i), 1, 0));
            }

        } else if(invName.equalsIgnoreCase("Sonstige Orte")) {

            List<String> myList = NaviSQL.getOrteNamen("Sonstige Orte");
            for(int i = 0; i < myList.size(); i++) {
                inv.setItem(i, Main.buildIS(Material.PAPER, "§3" + myList.get(i), 1, 0));
            }

        } else if(invName.equalsIgnoreCase("In der Nähe")) {

            List<String> myList = NaviSQL.getOrteNamen("In der Nähe");
            for(int i = 0; i < myList.size(); i++) {
                inv.setItem(i, Main.buildIS(Material.PAPER, "§3" + myList.get(i), 1, 0));
            }

        } else if(invName.equalsIgnoreCase("Gespeichert")) {

        } else if(invName.equalsIgnoreCase("Suche")) {

        } else if(invName.equalsIgnoreCase("Test")) {
            List<String> myList = NaviSQL.getOrteNamen("In der Nähe");
            for(int i = 0; i < myList.size(); i++) {
                inv.setItem(i, Main.buildIS(Material.PAPER, "§3" + myList.get(i), 1, 0));
            }
            NavigationAPI api = new NavigationAPI();
            Location loc = NaviSQL.getNearestObjekt("In der Nähe", "Bankautomat", p);
            p.sendMessage("x: " + loc.getX() + "  y: " + loc.getY() + "  z: " + loc.getZ());
            api.setNavigationLocation(p, loc, Color.GREEN);

        }

        return inv;
    }



}
