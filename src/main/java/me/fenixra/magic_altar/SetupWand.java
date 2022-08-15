package me.fenixra.magic_altar;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetupWand {
    public String id;
    public int radius;
    public int frequency;
    public Location loc;
    public Player player;
    public Altar altar;

    private ItemStack wandItem;
    public SetupWand(Player player, String id, int radius, int frequency) {
        this.player=player;
        this.id=id;
        this.radius=radius;
        this.frequency=frequency;

        wandItem=new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wandItem.getItemMeta();
        meta.setDisplayName("§cClick to make an altar");
        wandItem.setItemMeta(meta);
    }
    public SetupWand(Player player, Altar altar) {
        this.player=player;
        this.altar=altar;
    }
    public void giveWand() {
        player.getInventory().setItem(4,wandItem);

    }
}
