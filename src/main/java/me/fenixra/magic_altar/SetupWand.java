package me.fenixra.magic_altar;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetupWand {
    private final String id;
    private final Player owner;
    private Altar altar;
    private Location location;
    private int radius;
    private int frequency;

    private ItemStack wandItem;
    public SetupWand(Player player, String id, int radius, int frequency) {
        this.owner=player;
        this.id=id;
        this.radius=radius;
        this.frequency=frequency;

        wandItem=new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wandItem.getItemMeta();
        meta.setDisplayName("§cClick to make an altar");
        wandItem.setItemMeta(meta);
    }
    public SetupWand(Player player, Altar altar) {
        this.id=altar.getId();
        this.owner=player;
        this.altar=altar;


        wandItem=new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wandItem.getItemMeta();
        meta.setDisplayName("§cClick to make an altar");
        wandItem.setItemMeta(meta);
    }
    public void giveWand() {
        owner.getInventory().setItem(4,wandItem);

    }

    public void setLocation(Location value){
        location=value;
    }
    public String getId(){
        return id;
    }
    public Altar getAltar(){
        return altar;
    }
    public int getRadius(){
        return radius;
    }
    public int getFrequency(){
        return frequency;
    }
    public Location getLocation(){
        return location;
    }
    public Player getOwner(){
        return owner;
    }
}
