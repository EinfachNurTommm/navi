package com.tom.navi.commands;

import com.tom.navi.Main;
import com.tom.navi.NavigationAPI;
import com.tom.navi.gui.NavigationGUI;
import com.tom.navi.sql.NaviSQL;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NaviMapCommand implements CommandExecutor {

    private Main plugin;

    public NaviMapCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("setpunktkoordinate")) {
            Location loc;
            int id;

            double y = p.getLocation().getY() % 1;
            if (y == 0.5) {
                loc = new Location(p.getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY()+1, p.getLocation().getBlockZ());
                id = NaviSQL.setPunktKoordinate(loc);
            } else {
                loc = p.getLocation();
                id = NaviSQL.setPunktKoordinate(p.getLocation());
            }
            p.sendMessage("Du hast die PunktKoordinate Nr. " + id + " erstellt.");

            loc.getBlock().setType(Material.OAK_SIGN);
            BlockState state = loc.getBlock().getState();
            Sign sign = (Sign) state;
            sign.setLine(0, id + "");
            sign.update();


        } else if(cmd.getName().equalsIgnoreCase("setStrecke")) {
            if(args.length == 2) {
                int p1 = Integer.parseInt(args[0]);
                int p2 = Integer.parseInt(args[1]);
                if(!NaviSQL.streckeExists(p1, p2)) {
                    NaviSQL.setStrecke(p, p1, p2);
                    p.sendMessage("Du hast eine neue Strecke von " + p1 + " nach " + p2 + " erstellt!");
                } else {
                    p.sendMessage("Diese Strecke existiert bereits!");
                }
            } else {
                p.sendMessage("Nutze /setStrecke [Punkt1] [Punkt2]");
            }
        } else if(cmd.getName().equalsIgnoreCase("removeStrecke")) {
            if(args.length == 2) {
                int p1 = Integer.parseInt(args[0]);
                int p2 = Integer.parseInt(args[1]);
                NaviSQL.removeStrecke(p1, p2);
                p.sendMessage("Du hast die Strecke von " + p1 + " nach " + p2 + " entfernt!");
            } else {
                p.sendMessage("Nutze /setStrecke [Punkt1] [Punkt2]");
            }
        } else if(cmd.getName().equalsIgnoreCase("navi") || cmd.getName().equalsIgnoreCase("navigation")) {
            NavigationAPI api = new NavigationAPI();

            if(args.length == 3) {
                Location loc = new Location(p.getWorld(), Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));

                api.setNavigationLocation(p, loc, Color.AQUA);

                p.sendMessage("Du hast eine Navigation zu den Koordinaten x: " + loc.getBlockX() + ", y: " + loc.getBlockY() + ", z: " + loc.getBlockZ() + " gesetzt!");
            } else if(args.length == 1) {
                if(args[0].equalsIgnoreCase("cancel")) {
                    api.removeNavigation(p);
                } else if(args[0].equalsIgnoreCase("change")) {
                    if(Main.playerModus.containsKey(p)) {
                        if(Main.playerModus.get(p).equalsIgnoreCase("street")) {
                            Main.playerModus.put(p, "direct");
                        } else {
                            Main.playerModus.put(p, "street");
                        }
                    }
                }
            } else {
                p.openInventory(NavigationGUI.getGui("standard", p));
            }

        } else if(cmd.getName().equalsIgnoreCase("showStrecken")) {
            if(Main.playerMapViewer.containsKey(p)) {
                Main.playerMapViewer.remove(p);
            } else {
                Main.playerMapViewer.put(p, null);
            }
        } else if(cmd.getName().equalsIgnoreCase("setOrt")) {
            if(args.length >= 2) {
                String ortName = "";
                for (int i = 1; i < args.length; i++) {
                    ortName += args[i] + " ";
                }

                if(args[0].equalsIgnoreCase("SonstigeOrte")) {
                    if(!NaviSQL.ortExists("Sonstige Orte", ortName)) {
                        NaviSQL.setOrt("Sonstige Orte", ortName, p.getLocation());
                        p.sendMessage("Du hast einen Ort in der Kategorie \"Sonstige Orte\" erstellt namens \"" + ortName + "\"");
                    } else {
                        p.sendMessage("Dieser Ort existiert bereits!");
                    }
                } else if(args[0].equalsIgnoreCase("Fraktionen")) {
                    if(!NaviSQL.ortExists("Fraktionen", ortName)) {
                        NaviSQL.setOrt("Fraktionen", ortName, p.getLocation());
                        p.sendMessage("Du hast einen Ort in der Kategorie \"Fraktionen\" erstellt namens \"" + ortName + "\"");
                    } else {
                        p.sendMessage("Dieser Ort existiert bereits!");
                    }
                } else if(args[0].equalsIgnoreCase("InDerNähe") || args[0].equalsIgnoreCase("InDerNahe")) {
                    NaviSQL.setOrt("In der Nähe", ortName, p.getLocation());
                    p.sendMessage("Du hast einen Ort in der Kategorie \"In der Nähe\" erstellt namens \"" + ortName + "\"");
                }
            }
        }
        return false;
    }
}
