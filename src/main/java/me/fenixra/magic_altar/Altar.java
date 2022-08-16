package me.fenixra.magic_altar;

import me.fenixra.magic_altar.files.ConfigFile;
import me.fenixra.magic_altar.utils.Hologram;
import me.fenixra.magic_altar.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Altar {
    private final String id;
    private final HashMap<Player, Integer> players = new HashMap<>();
    private PvpChanger pvpChanger;
    private HashMap<String, List<String>> donate_nums = new HashMap<>();
    private int rewardFrequency;
    private int radius;
    private Location loc;
    private String rewardTitle;
    private String rewardSubtitle;
    private String rewardMessage;
    private List<String> rewardCommands;

    public Altar(String id, Location loc, int area, List<String> cmds, HashMap<String, List<String>> don_nums, int frequency, String title, String subtitle, String message) {
        this.id = id;
        this.loc = loc;
        this.radius = area;
        this.rewardCommands = cmds;
        this.rewardFrequency = frequency;
        this.rewardTitle = Utils.colorFormat(title);
        this.rewardSubtitle = Utils.colorFormat(subtitle);
        this.rewardMessage = Utils.colorFormat(message);
        donate_nums = don_nums;
        if (Main.getInstance().getDataFile().getFileC().contains("altars." + id + ".pvpChange.enabled")) {
            if (Main.getInstance().getDataFile().getFileC().getBoolean("altars." + id + ".pvpChange.enabled")) {
                pvpChanger = new PvpChanger(this);
            }
        }
    }

    public Altar(SetupWand wand) {
        this.id = wand.getId();
        this.loc = wand.getLocation();
        this.radius = wand.getRadius();
        this.rewardFrequency = wand.getFrequency();
    }

    public void UpdateTimer(List<Player> playersInBorder) {
        List<Player> playersPendingRemove = new ArrayList<>();
        for (Player p : players.keySet()) {
            if (!playersInBorder.contains(p)) {
                playersPendingRemove.add(p);
                continue;
            }
            if (!p.isOnline()) {
                playersPendingRemove.add(p);
                continue;
            }
            if (p.isDead()) {
                playersPendingRemove.add(p);
            }
        }
        for (Player p : playersPendingRemove) {
            players.remove(p);
        }
        for (Player p : playersInBorder) {
            if (players.containsKey(p)) continue;
            players.put(p, 0);
        }
        for (Map.Entry<Player, Integer> entry : players.entrySet()) {
            players.put(entry.getKey(), entry.getValue() + 1);
            entry.getKey().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ConfigFile.ConfigClass.msg_reward_time_left.replace("{time}", "" + (rewardFrequency - players.get(entry.getKey())))));
        }

        if (pvpChanger != null) {
            try {
                pvpChanger.updateTimer();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    public void rewardPlayer(Player player) {
        executeRewardCommands(player);

        if (this.rewardMessage != null) {
            player.sendMessage(replaceDonatorNums(player, this.rewardMessage));
        }
        String title = replaceDonatorNums(player, this.rewardTitle);
        String subTitle = replaceDonatorNums(player, this.rewardSubtitle);
        player.sendTitle(title, subTitle);

        player.playSound(player.getLocation(), Sound.valueOf(ConfigFile.ConfigClass.reward_sound), (float) ConfigFile.ConfigClass.sound_param1, (float) ConfigFile.ConfigClass.sound_param2);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ConfigFile.ConfigClass.msg_rewarded));

    }

    public List<Player> getRewardedPlayers() {
        List<Player> players2 = new ArrayList<>();
        for (Map.Entry<Player, Integer> entry : players.entrySet()) {
            if (entry.getValue() >= rewardFrequency) {
                players2.add(entry.getKey());
            }
        }
        return players2;

    }

    public void clearRewarded() {
        List<Player> players2 = new ArrayList<>();
        for (Map.Entry<Player, Integer> entry : players.entrySet()) {
            if (entry.getValue() >= rewardFrequency) {
                players2.add(entry.getKey());
            }
        }
        for (Player p : players2) {
            players.remove(p);
        }
    }

    public void executeRewardCommands(Player player) {
        for (String s : rewardCommands) {
            String cmd = replaceDonatorNums(player, s).replace("{player}", player.getName());
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);

        }
    }

    public String replaceDonatorNums(Player p, String s) {
        String newS = s;
        for (Map.Entry<String, List<String>> entry : donate_nums.entrySet()) {
            if (s.contains("{donator_num-" + entry.getKey() + "}")) {
                boolean replaced = false;
                String defaultVal = "";
                for (String perm : entry.getValue()) {
                    if (perm.contains("default")) {
                        defaultVal = perm.split(":")[1];
                    }
                    if (p.hasPermission(perm.split(":")[0])) {
                        newS = s.replace("{donator_num-" + entry.getKey() + "}", perm.split(":")[1]);
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) {
                    newS = s.replace("{donator_num-" + entry.getKey() + "}", defaultVal);
                }
            }
        }
        return newS;
    }

    public List<Player> getNearbyPlayers() {
        ArrayList<Player> arrayList = new ArrayList<>();
        for (Player player : loc.getWorld().getPlayers()) {
            if (!this.isInBorder(loc, player.getLocation(), radius)) continue;
            arrayList.add(player);
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

    public void setLocation(Location loc) {
        this.loc = loc;
    }

    public String getId() {
        return id;
    }
    public PvpChanger getPvpChanger() {
        return pvpChanger;
    }
    public String getRewardMessage() {
        return this.rewardMessage;
    }
    public String getRewardTitle() {
        return this.rewardTitle;
    }
    public String getRewardSubtitle() {
        return this.rewardSubtitle;
    }
    public Location getBlockLocation() {
        return loc;
    }
    public int getRadius() {
        return radius;
    }
    public int getFrequency() {
        return rewardFrequency;
    }


    public static class PvpChanger {
        public Hologram hologram;
        protected int frequency;
        protected int timer;
        protected boolean state = false;
        Altar altar;

        PvpChanger(Altar altar) {
            this.altar = altar;

            FileConfiguration data = Main.getInstance().getDataFile().getFileC();
            frequency = data.getInt("altars." + altar.getId() + ".pvpChange.frequency");
            timer = frequency;

            hologram = new Hologram(altar.loc, ConfigFile.ConfigClass.heightBetweenHoloLines * -1);
            this.updateHolo();
            List<String> cmds = data.getStringList("altars." + altar.getId() + ".pvpChange.off.cmds");

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

        protected void updateHolo() {
            FileConfiguration data = Main.getInstance().getDataFile().getFileC();
            List<String> holo;
            holo = state? data.getStringList("altars." + altar.getId() + ".pvpChange.on.holo") :
                          data.getStringList("altars." + altar.getId() + ".pvpChange.off.holo");

            List<String> lines = new ArrayList<>();
            for (String s : holo) {
                if (s.equalsIgnoreCase("%timer%")) {
                    lines.add(Utils.getProgressBar(timer, frequency, 10, "\u2B1B", "§a", "§7"));
                } else {
                    lines.add(Utils.colorFormat(s));
                }
            }
            hologram.setLines(lines);

        }

        public void changeState() {
            FileConfiguration data = Main.getInstance().getDataFile().getFileC();

            String stateString = state ? "off" : "on";
            Sound sound = null;
            float n = 1.0f;
            float n1 = 1.0f;
            if (data.contains("altars." + altar.getId() + ".pvpChange." + stateString + ".sound")) {
                sound = Sound.valueOf(data.getString("altars." + altar.getId() + ".pvpChange." + stateString + ".sound").split(":")[0].toUpperCase());
                n = Float.parseFloat(data.getString("altars." + altar.getId() + ".pvpChange." + stateString + ".sound").split(":")[1]);
                n1 = Float.parseFloat(data.getString("altars." + altar.getId() + ".pvpChange." + stateString + ".sound").split(":")[2]);
            }

            //Inform players
            String msg = data.getString("altars." + altar.getId() + ".pvpChange." + stateString + ".msg");
            if (msg != null) msg = Utils.colorFormat(msg);

            String title = data.getString("altars." + altar.getId() + ".pvpChange." + stateString + ".title");
            if (title != null) title = Utils.colorFormat(title);

            String subTitle = data.getString("altars." + altar.getId() + ".pvpChange." + stateString + ".subtitle");
            if (subTitle != null) subTitle = Utils.colorFormat(subTitle);

            for (Player p : altar.getNearbyPlayers()) {
                if (msg != null) {
                    p.sendMessage(msg);
                }
                if (title != null) {
                    p.sendTitle(title, subTitle);
                }
                if (sound != null) {
                    p.playSound(altar.getBlockLocation(), sound, n, n1);
                }
            }

            //ChangeState commands
            List<String> cmds = data.getStringList("altars." + altar.getId() + ".pvpChange." + stateString + ".cmds");
            for (String s : cmds) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s);
            }

            state = !state;
            timer = frequency;
        }


        public void rewardForKill(Player p) {

            if (state) {
                FileConfiguration data = Main.getInstance().getDataFile().getFileC();
                for (String s : data.getStringList("altars." + altar.getId() + ".pvpChange.on.reward-on-kill")) {
                    s = altar.replaceDonatorNums(p, s);

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

        public boolean getCurrentState() {
            return state;
        }
    }

}


