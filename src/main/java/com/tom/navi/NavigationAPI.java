package com.tom.navi;

import com.google.common.graph.ValueGraph;
import com.tom.navi.dijkstra.DijkstraWithPriorityQueue;
import com.tom.navi.sql.NaviSQL;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NavigationAPI {

    /**
     *
     * @param name
     * @return Location
     */
    public Location getPlayerNavigation(String name) {
        Player p = Bukkit.getPlayer(name);
        if(p != null) {
            if(Main.naviLocation.containsKey(p)) {
                return Main.naviLocation.get(p);
            }
        }
        return null;
    }

    /**
     *
     * @param p
     * @return Location
     */
    public Location getPlayerNavigation(Player p) {
        if(p != null) {
            if(Main.naviLocation.containsKey(p)) {
                return Main.naviLocation.get(p);
            }
        }
        return null;
    }


    public boolean setNavigationLocation(Player p, Location loc, Color c) {
        if(p != null && loc != null && c != null) {
            String p1 = String.valueOf(getNearestPunkt(p.getLocation()));
            String p2 = String.valueOf(getNearestPunkt(loc));

            ValueGraph<String, Integer> graph = NaviSQL.getGraph();
            List<String> shortestPath = DijkstraWithPriorityQueue.findShortestPath(graph, p1, p2);
            List<Location> shortestPathLocation = new ArrayList<>();

            for(int i = 0; i<shortestPath.size(); i++) {
                shortestPathLocation.add(NaviSQL.getPunktKoordinate(p.getLocation(), Integer.parseInt(shortestPath.get(i))));
            }

            Main.naviLocation.put(p, shortestPathLocation.get(0));
            Main.naviColor.put(p, c);
            Main.canStartWay.put(p, false);
            Main.playerWay.put(p, shortestPathLocation);
            Main.firstLoc.put(p, loc);

            return true;
        }
        return false;
    }


    public int getNearestPunkt(Location loc) {
        int punkt = 0;
        int distance = -1;
        for(int i = 1; i < NaviSQL.getNextPunktID(); i++) {
            if(distance == -1) {
                distance = (int) loc.distance(NaviSQL.getPunktKoordinate(loc, i)); // Ist Gefährlich, wenn eine Kreuzung gelöscht wurde
                punkt = i;
            } else {
                if(loc.distance(NaviSQL.getPunktKoordinate(loc, i)) < distance) {
                    distance = (int) loc.distance(NaviSQL.getPunktKoordinate(loc, i));
                    punkt = i;
                }
            }
        }
        return punkt;
    }


    public boolean removeNavigation(Player p) {
        if(p != null) {
            if(Main.naviLocation.containsKey(p)) {
                Main.naviLocation.remove(p);
            }
            if(Main.naviColor.containsKey(p)) {
                Main.naviColor.remove(p);
            }
            if(Main.canStartWay.containsKey(p)) {
                Main.canStartWay.remove(p);
            }
            if(Main.playerWay.containsKey(p)) {
                Main.playerWay.remove(p);
            }
            if(Main.firstLoc.containsKey(p)) {
                Main.firstLoc.remove(p);
            }
            return true;
        }
        return false;
    }

}
