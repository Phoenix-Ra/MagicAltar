package me.fenixra.magic_altar;

import me.fenixra.magic_altar.files.ConfigFile;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class AltarManager extends BukkitRunnable implements Listener {
    private final HashMap<String, Altar> altars = new HashMap<>();
    private final HashMap<Player, SetupWand> setups = new HashMap<>();
    public AltarManager() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        runTaskTimer(Main.getInstance(), 0, 20);
    }

    public void addAltar(Altar altar) {
        altars.put(altar.getId(), altar);
        Main.getInstance().getLogger().info("ALTAR WITH ID " + altar.getId() + " ... SUCCESSFULLY ADDED");
    }

    public void clearAll() {
        cancel();
        for (Altar altar : altars.values()) {
            if(altar.getPvpChanger()==null) continue;
            if (altar.getPvpChanger().hologram != null) {
                altar.getPvpChanger().hologram.remove();
            }
        }
        altars.clear();
        setups.clear();
    }

    public Altar getAltar(String s) {
        return altars.get(s);
    }

    public Altar removeAltar(String s) {
        return altars.remove(s);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        for (Altar altar : altars.values()) {
            try {
                altar.UpdateTimer(altar.getNearbyPlayers());
                //reward players
                for (Player player : altar.getRewardedPlayers()) {
                    HashMap<String, String> hm = altar.executeCmds(player);
                    String message = altar.message;
                    String title = altar.title;
                    String subtitle = altar.subtitle;
                    for (Map.Entry<String, String> entry : hm.entrySet()) {
                        message = message.replace(entry.getKey(), entry.getValue());
                        title = title.replace(entry.getKey(), entry.getValue());
                        subtitle = subtitle.replace(entry.getKey(), entry.getValue());
                    }
                    if (!message.equals("null")) {
                        player.sendMessage(message);
                    }
                    player.sendTitle(title, subtitle);
                    player.playSound(player.getLocation(), Sound.valueOf(ConfigFile.ConfigClass.reward_sound), ConfigFile.ConfigClass.sound_param1, ConfigFile.ConfigClass.sound_param2);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ConfigFile.ConfigClass.msg_rewarded));
                }
                altar.clearRewarded();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getItem() != null) {
                if (event.getItem().hasItemMeta()) {
                    if (event.getItem().getItemMeta().getDisplayName().equals("§cClick to make an altar") && setups.containsKey(player)) {
                        SetupWand wand = setups.get(player);
                        if (wand.altar != null) {
                            wand.altar.setLocation(event.getClickedBlock().getLocation());
                            this.addAltar(wand.altar);
                            Main.getInstance().getDataFile().addAltar(wand.altar);
                            player.getInventory().remove(event.getItem());
                            player.sendMessage("§aAltar has been successfully added");
                            event.setCancelled(true);
                        }
                        wand.loc = event.getClickedBlock().getLocation();
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

    public Altar getPlayerAltarIn(Player p) {
        for (Altar altar : altars.values()) {
            if (altar.getNearbyPlayers().contains(p)) return altar;
        }
        return null;
    }

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
}
