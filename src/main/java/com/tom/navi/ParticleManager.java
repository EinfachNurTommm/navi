package com.tom.navi;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleManager {

    private Vector getDirectionBetweenLocations(Location Start, Location End) {
        Vector from = Start.toVector();
        Vector to = End.toVector();
        return to.subtract(from);
    }

    public void sendParticleLine(Player p, Location loc, Color c) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(c, 0.6F);

        Location Loc2 = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY()+1, p.getLocation().getZ());

        particleCube(p, loc, dustOptions);

        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        Location myLoc = new Location(loc.getWorld(), x+0.5, y, z+0.5);

        Vector vector = getDirectionBetweenLocations(myLoc, Loc2);

        if(myLoc.distance(Loc2) < 10) {
            particleLine(p, dustOptions, myLoc, Loc2, 0);
        } else {
            for (double i = myLoc.distance(Loc2)-8; i <= myLoc.distance(Loc2); i += 0.1) {
                vector.multiply(i);
                myLoc.add(vector);

                p.spawnParticle(Particle.REDSTONE, myLoc, 1, 0, 0, 0, 0, dustOptions);
                myLoc.subtract(vector);
                vector.normalize();
            }
        }
    }


    public void particleCube(Player p, Location loc, Particle.DustOptions dustOptions) {
        double x = loc.getX();
        double y = loc.getY()-1;
        double z = loc.getZ();
        Location myLoc1 = new Location(loc.getWorld(), x, y, z);
        Location myLoc2 = new Location(loc.getWorld(), x, y, z+1);
        Location myLoc3 = new Location(loc.getWorld(), x+1, y, z+1);
        Location myLoc4 = new Location(loc.getWorld(), x+1, y, z);
        Location myLoc5 = new Location(loc.getWorld(), x, y+1, z);
        Location myLoc6 = new Location(loc.getWorld(), x, y+1, z+1);
        Location myLoc7 = new Location(loc.getWorld(), x+1, y+1, z+1);
        Location myLoc8 = new Location(loc.getWorld(), x+1, y+1, z);


        particleLine(p, dustOptions, myLoc1, myLoc2, 0);
        particleLine(p, dustOptions, myLoc1, myLoc4, 0);
        particleLine(p, dustOptions, myLoc1, myLoc5, 0);

        particleLine(p, dustOptions, myLoc2, myLoc6, 0);
        particleLine(p, dustOptions, myLoc2, myLoc3, 0);

        particleLine(p, dustOptions, myLoc3, myLoc4, 0);
        particleLine(p, dustOptions, myLoc3, myLoc7, 0);

        particleLine(p, dustOptions, myLoc4, myLoc8, 0);

        particleLine(p, dustOptions, myLoc5, myLoc6, 0);
        particleLine(p, dustOptions, myLoc5, myLoc8, 0);

        particleLine(p, dustOptions, myLoc6, myLoc7, 0);

        particleLine(p, dustOptions, myLoc7, myLoc8, 0);
    }


    public void particleLine(Player p, Particle.DustOptions dustOptions, Location loc1, Location loc2, double offset) {
        double x1 = loc1.getX()+offset;
        double y1 = loc1.getY();
        double z1 = loc1.getZ()+offset;
        double x2 = loc2.getX()+offset;
        double y2 = loc2.getY();
        double z2 = loc2.getZ()+offset;
        Location myLoc1 = new Location(p.getWorld(), x1, y1, z1);
        Location myLoc2 = new Location(p.getWorld(), x2, y2, z2);

        Vector vector1 = getDirectionBetweenLocations(myLoc1, myLoc2).normalize();
        for(double i = 0; i <= myLoc1.distance(myLoc2); i += 0.1) {
            Vector addition = new Vector().copy(vector1).multiply(i);
            Location newLoc = myLoc1.clone().add(addition);
            p.spawnParticle(Particle.REDSTONE, newLoc, 1, 0, 0, 0, 0, dustOptions);
        }
    }
}
