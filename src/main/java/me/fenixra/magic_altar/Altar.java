package me.fenixra.magic_altar;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.fenixra.magic_altar.files.ConfigFile;
import me.fenixra.magic_altar.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Altar {
    private final HashMap<Player, Integer> players = new HashMap<>();
    public int timer;
    public String title;
    public String subtitle;
    public String message = "null";
    protected HashMap<String, List<String>> donate_nums = new HashMap<>();
    String id;
    Location loc;
    int radius;
    List<String> cmds;
    int frequency;
    private PvpChanger pvp;

    public Altar(String id, Location loc, int area, List<String> cmds, HashMap<String, List<String>> don_nums, int frequency, String title, String subtitle, String message) {
        this.id = id;
        this.loc = loc;
        this.radius = area;
        this.cmds = cmds;
        this.frequency = frequency;
        this.title = title;
        this.subtitle = subtitle;
        this.message = message;
        donate_nums = don_nums;
        if (Main.getInstance().getDataFile().getFileC().contains("altars." + id + ".pvpChange.enabled")) {
            if (Main.getInstance().getDataFile().getFileC().getBoolean("altars." + id + ".pvpChange.enabled")) {
                pvp = new PvpChanger(this);
            }
        }
    }

    public Altar(SetupWand wand) {
        this.id = wand.id;
        this.loc = wand.loc;
        this.radius = wand.radius;
        this.frequency = wand.frequency;
    }

    public Location getBlockLocation() {
        return loc;
    }

    public String getId() {
        return id;
    }

    public int getRadius() {
        return radius;
    }

    public List<String> getCmds() {
        return cmds;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<Player> getNearbyPlayers() {
        ArrayList<Player> arrayList = new ArrayList<>();
        for (Entity entity : loc.getWorld().getEntities()) {
            if (!this.isInBorder(loc, entity.getLocation(), radius)) continue;
            if (!(entity instanceof Player)) continue;
            arrayList.add((Player) entity);
        }

        return arrayList;
    }

    private boolean isInBorder(Location location, Location location2, int n) {
        int x1 = location.getBlockX();
        int y1 = location.getBlockY();
        int z1 = location.getBlockZ();
        int x2 = location2.getBlockX();
        int y2 = location2.getBlockY();
        int z2 = location2.getBlockZ();

        return x2 < x1 + n && x2 > x1 - n && z2 < z1 + n && z2 > z1 - n && y2 < y1 + n && y2 > y1 - n;
    }

    public void UpdateTimer(List<Player> players1) {
        List<Player> players2 = new ArrayList<>();
        for (Player p : players.keySet()) {
            if (!players1.contains(p)) {
                players2.add(p);
                continue;
            }
            if (!p.isOnline()) {
                players2.add(p);
                continue;
            }
            if (p.isDead()) {
                players2.add(p);
            }
        }
        for (Player p : players2) {
            players.remove(p);
        }
        for (Player p : players1) {
            if (players.containsKey(p)) continue;
            players.put(p, 0);
        }
        for (Map.Entry<Player, Integer> entry : players.entrySet()) {
            players.put(entry.getKey(), entry.getValue() + 1);
            entry.getKey().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ConfigFile.ConfigClass.msg_reward_time_left.replace("{time}",""+(frequency - players.get(entry.getKey())) )));
        }

        if (pvp != null) {
            try {
                pvp.updateTimer();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    public List<Player> getRewardedPlayers() {
        List<Player> players2 = new ArrayList<>();
        for (Map.Entry<Player, Integer> entry : players.entrySet()) {
            if (entry.getValue() >= frequency) {
                players2.add(entry.getKey());
            }
        }
        return players2;

    }

    public void clearRewarded() {
        List<Player> players2 = new ArrayList<>();
        for (Map.Entry<Player, Integer> entry : players.entrySet()) {
            if (entry.getValue() >= frequency) {
                players2.add(entry.getKey());
            }
        }
        for (Player p : players2) {
            players.remove(p);
        }
    }

    public void setLocation(Location loc) {
        this.loc = loc;
    }

    public HashMap<String, String> executeCmds(Player player) {
        HashMap<String, String> hm = new HashMap<>();
        for (String s : cmds) {
            for (Map.Entry<String, List<String>> entry : donate_nums.entrySet()) {
                if (s.contains("{donator_num-" + entry.getKey() + "}")) {
                    for (String perm : entry.getValue()) {
                        if (player.hasPermission("group." + perm.split(":")[0])) {
                            s = s.replace("{donator_num-" + entry.getKey() + "}", perm.split(":")[1]);
                            if (!hm.containsKey("{donator_num-" + entry.getKey() + "}")) {
                                hm.put("{donator_num-" + entry.getKey() + "}", perm.split(":")[1]);
                            }
                        }
                    }
                }
            }
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), s.replace("{player}", player.getName()));

        }
        return hm;
    }

    public PvpChanger getPvpChanger() {
        return pvp;
    }


    public static class PvpChanger {
        public Hologram hologram;
        protected int frequency;
        protected int timer;
        protected boolean state = false;
        Altar altar;

        PvpChanger(Altar altar) {
            FileConfiguration data = Main.getInstance().getDataFile().getFileC();
            this.altar = altar;
            frequency = data.getInt("altars." + altar.id + ".pvpChange.frequency");
            timer = frequency;
            hologram = HologramsAPI.createHologram(Main.getInstance(), altar.loc);
            this.updateHolo();
            List<String> cmds = data.getStringList("altars." + altar.id + ".pvpChange.off.cmds");

            for (String s : cmds) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s);
            }
        }


        protected void updateTimer() {
            timer--;
            if (timer <= 0) {
                timer = frequency;
                this.changeState();

            }
            updateHolo();

        }

        public boolean getCurrentState() {
            return state;
        }

        public void changeState() {
            FileConfiguration data = Main.getInstance().getDataFile().getFileC();

            if (state) {
                String msg = data.getString("altars." + altar.id + ".pvpChange.off.msg");
                String title = data.getString("altars." + altar.id + ".pvpChange.off.title");
                Sound sound = null;
                float n = 1.0f;
                float n1 = 1.0f;
                if (data.contains("altars." + altar.id + ".pvpChange.off.sound")) {
                    sound = Sound.valueOf(data.getString("altars." + altar.id + ".pvpChange.off.sound").split(":")[0].toUpperCase());
                    n = Float.parseFloat(data.getString("altars." + altar.id + ".pvpChange.off.sound").split(":")[1]);
                    n1 = Float.parseFloat(data.getString("altars." + altar.id + ".pvpChange.off.sound").split(":")[2]);
                }
                List<String> cmds = data.getStringList("altars." + altar.id + ".pvpChange.off.cmds");


                for (Player p : altar.getNearbyPlayers()) {
                    if (msg != null) {
                        p.sendMessage(msg.replace("&", "§"));
                    }
                    if (title != null) {
                        p.sendTitle(title.replace("&", "§"), "");
                    }
                    if (sound != null) {
                        p.playSound(altar.getBlockLocation(), sound, n, n1);
                    }
                }
                for (String s : cmds) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s);
                }
            } else {
                String msg = data.getString("altars." + altar.id + ".pvpChange.on.msg");
                String title = data.getString("altars." + altar.id + ".pvpChange.on.title");
                Sound sound = null;
                float n = 1.0f;
                float n1 = 1.0f;
                if (data.getString("altars." + altar.id + ".pvpChange.on.sound") != null) {
                    sound = Sound.valueOf(data.getString("altars." + altar.id + ".pvpChange.on.sound").split(":")[0].toUpperCase());
                    n = Float.parseFloat(data.getString("altars." + altar.id + ".pvpChange.on.sound").split(":")[1]);
                    n1 = Float.parseFloat(data.getString("altars." + altar.id + ".pvpChange.on.sound").split(":")[2]);
                }
                List<String> cmds = data.getStringList("altars." + altar.id + ".pvpChange.on.cmds");


                for (Player p : altar.getNearbyPlayers()) {
                    if (msg != null) {
                        p.sendMessage(msg.replace("&", "§"));
                    }
                    if (title != null) {
                        p.sendTitle(title.replace("&", "§"), "");
                    }
                    if (sound != null) {
                        p.playSound(altar.getBlockLocation(), sound, n, n1);
                    }
                }
                for (String s : cmds) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s);
                }
            }
            state = !state;
            timer = frequency;
        }

        protected void updateHolo() {
            FileConfiguration data = Main.getInstance().getDataFile().getFileC();
            List<String> holo;
            if (state) {
                holo = data.getStringList("altars." + altar.id + ".pvpChange.on.holo");
            } else {
                holo = data.getStringList("altars." + altar.id + ".pvpChange.off.holo");
            }
            hologram.clearLines();
            for (String s : holo) {
                if (s.equalsIgnoreCase("%timer%")) {
                    hologram.appendTextLine(Utils.getProgressBar(timer, frequency, 10, "\u2B1B", "§a", "§7"));
                } else {
                    hologram.appendTextLine(s.replace("&", "§"));
                }
            }

        }


        public void rewardForKill(Player p) {

            if (state) {
                FileConfiguration data = Main.getInstance().getDataFile().getFileC();
                for (String s : data.getStringList("altars." + altar.id + ".pvpChange.on.reward-on-kill")) {
                    for (Map.Entry<String, List<String>> entry : altar.donate_nums.entrySet()) {
                        if (s.contains("{donator_num-" + entry.getKey() + "}")) {
                            for (String perm : entry.getValue()) {
                                if (p.hasPermission("group." + perm.split(":")[0])) {
                                    s = s.replace("{donator_num-" + entry.getKey() + "}", perm.split(":")[1]);
                                }
                            }
                        }
                    }
                    if (s.contains("msg:")) {
                        p.sendMessage(s.replace("msg:", "").replace("&", "§"));
                    } else if (s.contains("title:")) {
                        p.sendTitle(s.replace("title:", "").replace("&", "§"), "");
                    } else {

                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replace("{player}", p.getName()));
                    }
                }
            }
        }
    }

}


