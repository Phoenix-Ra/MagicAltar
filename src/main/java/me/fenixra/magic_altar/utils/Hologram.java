package me.fenixra.magic_altar.utils;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.*;

public class Hologram {

    private static final Set<ArmorStand> holograms = new HashSet<>();

    private final List<ArmorStand> lines = new ArrayList<>();

    private Location location;
    private final double height;

    public Hologram(Location loc, double heightBetweenLines) {
        Preconditions.checkNotNull(loc, "Location cannot be null");

        this.location = loc;
        this.height = heightBetweenLines;
    }

    private void createLine(Location loc, String text) {
        ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        as.setCanPickupItems(false);
        as.setCustomNameVisible(true);
        as.setCustomName(Utils.colorFormat(text));
        as.setVisible(false);
        as.setGravity(false);
        as.setSmall(true);

        this.lines.add(as);
        Hologram.holograms.add(as);
    }

    public void setLines(List<String> lines) {
        if (lines == null)
            return;

        Location teleportTo = this.location.clone();
        for (int i = 0; i < lines.size(); i++) {
            if (isValidIndex(i)) {
                if (!this.lines.get(i).getCustomName().equals(lines.get(i)))
                    this.lines.get(i).setCustomName(lines.get(i));
            } else {
                createLine(teleportTo.add(0, this.height, 0), lines.get(i));

            }
        }
        setVisible(true);
    }
    public void teleport(Location loc) {
        if (loc == null)
            return;

        Location teleportTo = loc.clone();
        for (ArmorStand as : this.lines) {
            as.teleport(teleportTo.add(0, this.height, 0));
        }
        location=teleportTo;

    }
    public void remove() {
        for (ArmorStand line : this.lines) {
            Hologram.holograms.remove(line);
            line.remove();
        }

        this.lines.clear();
    }

    public void setVisibleLine(int line, boolean visible) {
        if (isValidIndex(line))
            this.lines.get(line).setCustomNameVisible(visible);
    }
    public void setVisible( boolean visible) {
        for(ArmorStand line: this.lines){
            line.setCustomNameVisible(visible);
        }
    }
    public boolean isVisible(int line) {
        return isValidIndex(line) && this.lines.get(line).isCustomNameVisible();
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < this.lines.size();
    }

    public Location getLocation(){
        return location;
    }
}
