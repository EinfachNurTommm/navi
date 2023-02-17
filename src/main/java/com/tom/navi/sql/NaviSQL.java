package com.tom.navi.sql;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import com.tom.navi.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NaviSQL {

    private static Main plugin;

    public NaviSQL(Main plugin) {
        NaviSQL.plugin = plugin;
    }


    public static int setPunktKoordinate(Location loc) {
        int id = getNextPunktID();
        Main.sql.update("INSERT INTO `PunktKoordinaten`(`Punkt`, `x`, `y`, `z`) VALUES ('" + id + "','" + loc.getBlockX() + "','" + loc.getBlockY() + "','" + loc.getBlockZ() + "');");
        return id;
    }

    public static void setStrecke(Player p, int p1, int p2) {
        Location loc1 = getPunktKoordinate(p.getLocation(), p1);
        Location loc2 = getPunktKoordinate(p.getLocation(), p2);
        int distance = (int) loc1.distance(loc2);

        Main.sql.update("INSERT INTO `Strecken`(`P1`, `P2`, `Distanz`) VALUES ('" + p1 + "','" + p2 + "','" + distance + "');");
    }

    public static boolean streckeExists(int p1, int p2) {
        int id = -1;

        try {
            ResultSet rs = Main.sql.query("SELECT * FROM `Strecken` WHERE `P1` = \"" + p1 + "\" AND `P2` = \"" + p2 + "\" OR `P2` = \"" + p1 + "\" AND `P1` = \"" + p2 + "\";");

            while (rs.next()) {
                id = rs.getInt("P1");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id > 0;
    }

    public static Location getPunktKoordinate(Location loc1, int p1) {
        Location loc = null;
        try {
            ResultSet rs = Main.sql.query("SELECT `x`, `y`, `z` FROM `PunktKoordinaten` WHERE `Punkt` = \"" + p1 + "\";");

            while (rs.next()) {
                String world = loc1.getWorld().getName();
                double x = rs.getInt("x");
                double y = rs.getInt("y");
                double z = rs.getInt("z");

                loc = new Location(Bukkit.getWorld(world), x, y, z);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loc;
    }

    public static int getNextPunktID() {
        int id = 0;

        try {
            ResultSet rs = Main.sql.query("SELECT `Punkt` FROM `PunktKoordinaten` ORDER BY `Punkt` ASC;");

            while (rs.next()) {
                id = rs.getInt("Punkt");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ++id;
    }



    public static ValueGraph<String, Integer> getGraph() {
        MutableValueGraph<String, Integer> graph = ValueGraphBuilder.undirected().build();

        try {
            ResultSet rs = Main.sql.query("SELECT `P1`, `P2`, `Distanz` FROM `Strecken`;");

            while (rs.next()) {
                graph.putEdgeValue(String.valueOf(rs.getInt("P1")), String.valueOf(rs.getInt("P2")), rs.getInt("Distanz"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return graph;
    }


    public static List<Location> getStrecken(Player p, String listName) {
        List<Integer> list = new ArrayList<>();
        List<Location> locationList = new ArrayList<>();

        try {
            ResultSet rs = Main.sql.query("SELECT `P1`, `P2` FROM `Strecken`;");

            while (rs.next()) {
                list.add(rs.getInt(listName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < list.size(); i++) {
            locationList.add(getPunktKoordinate(p.getLocation(), list.get(i)));
        }

        return locationList;
    }

    public static void removeStrecke(int p1, int p2) {
        Main.sql.update("DELETE FROM `Strecken` WHERE `P1` = \"" + p1 + "\" AND `P2` = \"" + p2 + "\" OR `P2` = \"" + p1 + "\" AND `P1` = \"" + p2 + "\";");
    }


    public static void setOrt(String kategorie, String name, Location loc) {
        Main.sql.update("INSERT INTO `gespeicherteOrte`(`Kategorie`, `Name`, `world`, `x`, `y`, `z`) VALUES (\"" + kategorie + "\",\"" + name + "\",\"" + loc.getWorld().getName() + "\",\"" + loc.getBlockX() + "\",\"" + loc.getBlockY() + "\",\"" + loc.getBlockZ() + "\");");
    }


    public static List<String> getOrteNamen(String kategorie) {
        HashMap<String, String> myMap = new HashMap<>();
        List<String> myList = new ArrayList<>();
        try {
            ResultSet rs = Main.sql.query("SELECT `Name` FROM `gespeicherteOrte` WHERE `Kategorie` = \"" + kategorie + "\";");

            while (rs.next()) {
            String myName = rs.getString("Name");
                if(!myMap.containsKey(myName)) {
                    myMap.put(myName, kategorie);
                    myList.add(myName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return myList;
    }

    public static List<Location> getOrteLoc(String kategorie, Player p) {
        HashMap<String, String> myMap = new HashMap<>();
        List<Location> myLocList = new ArrayList<>();
        try {
            ResultSet rs = Main.sql.query("SELECT `Name`, `world`, `x`, `y`, `z` FROM `gespeicherteOrte` WHERE `Kategorie` = \"" + kategorie + "\";");

            while (rs.next()) {
                Location loc = new Location(p.getWorld(), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                if(!myMap.containsKey(rs.getString("Name"))) {
                    myMap.put(rs.getString("Name"), kategorie);
                    myLocList.add(loc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return myLocList;
    }


    public static Location getNearestObjekt(String kategorie, String ort, Player p) {
        Location loc = null;
        int distance = -1;
        try {
            ResultSet rs = Main.sql.query("SELECT `world`, `x`, `y`, `z` FROM `gespeicherteOrte` WHERE `Kategorie` = \"" + kategorie + "\" AND `Name` = \"" + ort + "\";");

            while (rs.next()) {
                Location newLoc = new Location(p.getWorld(), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                if(distance == -1) {
                    distance = (int) p.getLocation().distance(newLoc);

                    loc = newLoc;
                } else {
                    if(p.getLocation().distance(newLoc) < distance) {
                        distance = (int) p.getLocation().distance(newLoc);

                        loc = newLoc;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loc;
    }

    public static boolean ortExists(String kategorie, String ort) {
        List<String> myNamen = getOrteNamen(kategorie);

        for(int i = 0; i < myNamen.size(); i++) {
            if(myNamen.get(i).equalsIgnoreCase(ort)) {
                return true;
            }
        }
        return false;
    }

}
