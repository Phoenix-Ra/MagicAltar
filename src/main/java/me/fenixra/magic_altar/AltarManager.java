package me.fenixra.magic_altar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class AltarManager extends BukkitRunnable implements Listener {
    private final HashMap<String, Altar> altars = new HashMap<>();
    private final HashMap<Player, SetupWand> setups = new HashMap<>();
    public AltarManager() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        runTaskTimer(Main.getInstance(), 0, 20);
    }
    @Override
    public void run() {
        for (Altar altar : altars.values()) {
            try {
                altar.UpdateTimer(altar.getNearbyPlayers());
                //reward players
                for (Player player : altar.getRewardedPlayers()) {
                    altar.rewardPlayer(player);
                }
                altar.clearRewarded();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void clearAll(boolean cancelTask) {
        if(cancelTask) cancel();
        for (Altar altar : altars.values()) {
            if(altar.getPvpChanger()==null) continue;
            if (altar.getPvpChanger().hologram != null) {
                altar.getPvpChanger().hologram.remove();
            }
        }
        altars.clear();
        setups.clear();
    }

    public void addAltar(Altar altar) {
        altars.put(altar.getId(), altar);
        Main.getInstance().getLogger().info("ALTAR WITH ID " + altar.getId() + " SUCCESSFULLY ADDED");
    }

    public void removeAltar(String id) {
        altars.remove(id);
    }

    public Altar getPlayerAltarIn(Player p) {
        for (Altar altar : altars.values()) {
            if (altar.getNearbyPlayers().contains(p)) return altar;
        }
        return null;
    }

    public void giveSetupWand(Player player, Altar altar) {
        SetupWand wand = new SetupWand(player, altar);
        setups.put(player, wand);
        wand.giveWand();
    }

    public void giveSetupWand(Player player, String id, int radius, int frequency) {
        SetupWand wand = new SetupWand(player, id, radius, frequency);
        setups.put(player, wand);
        wand.giveWand();
    }


    //Special stick interaction
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getItem() != null) {
                if (event.getItem().hasItemMeta()) {
                    if (event.getItem().getItemMeta().getDisplayName().equals("§cClick to make an altar") && setups.containsKey(player)) {
                        SetupWand wand = setups.get(player);
                        if (wand.getAltar() != null) {
                            wand.getAltar().setLocation(event.getClickedBlock().getLocation());
                            this.addAltar(wand.getAltar());
                            Main.getInstance().getDataFile().addAltar(wand.getAltar());
                            player.getInventory().remove(event.getItem());
                            player.sendMessage("§aAltar has been successfully added");
                            event.setCancelled(true);
                            return;
                        }
                        wand.setLocation(event.getClickedBlock().getLocation());

                        Altar altar = new Altar(wand);
                        this.addAltar(altar);
                        Main.getInstance().getDataFile().addAltar(altar);
                        player.getInventory().remove(event.getItem());
                        player.sendMessage("§aAltar has been successfully added");
                        event.setCancelled(true);
                    }
                }
            }
        }

    }

    //reward on kill
    @EventHandler
    public void DeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            if (!killer.getName().equals(player.getName())) {
                Altar altar = getPlayerAltarIn(killer);
                if (altar != null) {
                    if (altar.getPvpChanger().getCurrentState()) {
                        altar.getPvpChanger().rewardForKill(killer);
                    }
                }
            }
        }
    }

    public Altar getAltar(String id) {
        return altars.get(id);
    }
}
