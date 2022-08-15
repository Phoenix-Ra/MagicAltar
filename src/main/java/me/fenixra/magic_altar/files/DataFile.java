package me.fenixra.magic_altar.files;

import me.fenixra.magic_altar.Altar;
import me.fenixra.magic_altar.Main;
import me.fenixra.magic_altar.utils.PhoenixFile;
import me.fenixra.magic_altar.utils.PhoenixFileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DataFile extends PhoenixFile {
    public DataFile(PhoenixFileManager fileM) {
        super(fileM, "data", null);
    }

    @Override
    public boolean handleLoad() {
        return true;
    }

    @Override
    public boolean reloadAction() {
        Main.getInstance().getAltarM().clearAll();
        SetupAltars();
        return true;
    }
    private void SetupAltars() {
        for(String path: this.getFileC().getConfigurationSection("altars").getKeys(false)) {
            if(this.getFileC().contains("altars."+path+".location.world")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " +path+" ... WORLD DOESN'T SPECIFIED");
                continue;
            }else if(Bukkit.getWorld(this.getFileC().getString("altars."+path+".location.world"))==null) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " +path+" ... SPECIFIED WORLD DOESN'T EXIST");
                continue;
            }
            if(!this.getFileC().contains("altars."+path+".location.posX")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " +path+" ... POSITION_X DOESN'T SPECIFIED");
                continue;
            }
            if(!this.getFileC().contains("altars."+path+".location.posY")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " +path+" ... POSITION_Y DOESN'T SPECIFIED");
                continue;
            }
            if(!this.getFileC().contains("altars."+path+".location.posZ")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " +path+" ... POSITION_Z DOESN'T SPECIFIED");
                continue;
            }
            if(!this.getFileC().contains("altars."+path+".radius")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " +path+" ... RADIUS DOESN'T SPECIFIED");
                continue;
            }
            if(!this.getFileC().contains("altars."+path+".cmds")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " +path+" ... CMDS DON'T SPECIFIED");
                continue;
            }
            if(!this.getFileC().contains("altars."+path+".frequency")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " +path+" ... FREQUENCY DOESN'T SPECIFIED");
                continue;
            }
            if(!this.getFileC().contains("altars."+path+".title")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " +path+" ... TITLE DOESN'T SPECIFIED");
                continue;
            }
            if(!this.getFileC().contains("altars."+path+".subtitle")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " +path+" ... SUBTITLE DOESN'T SPECIFIED");
                continue;
            }
            Location loc=new Location(Bukkit.getWorld(this.getFileC().getString("altars."+path+".location.world")),this.getFileC().getDouble("altars."+path+".location.posX"),this.getFileC().getDouble("altars."+path+".location.posY"),this.getFileC().getDouble("altars."+path+".location.posZ"));
            int radius=this.getFileC().getInt("altars."+path+".radius");
            int frequency=this.getFileC().getInt("altars."+path+".frequency");
            List<String> list=this.getFileC().getStringList("altars."+path+".cmds");
            String title=this.getFileC().getString("altars."+path+".title");
            String subtitle=this.getFileC().getString("altars."+path+".subtitle");
            String message=this.getFileC().getString("altars."+path+".message");
            HashMap<String,List<String>> don_nums=new HashMap<String,List<String>>();
            if(this.getFileC().getConfigurationSection("altars."+path+".donator_nums")!=null) {
                for(String s:this.getFileC().getConfigurationSection("altars."+path+".donator_nums").getKeys(false)) {
                    don_nums.put(s, this.getFileC().getStringList("altars."+path+".donator_nums."+s));
                }

            }
            Main.getInstance().getAltarM().addAltar(new Altar(path,loc,radius,list,don_nums,frequency,  title,subtitle, message));
        }
    }
    public void addAltar(Altar altar) {
        String path=altar.getId();
        this.getFileC().set("altars."+path+".location.world", altar.getBlockLocation().getWorld().getName());
        this.getFileC().set("altars."+path+".location.posX", altar.getBlockLocation().getBlockX());
        this.getFileC().set("altars."+path+".location.posY", altar.getBlockLocation().getBlockY());
        this.getFileC().set("altars."+path+".location.posZ", altar.getBlockLocation().getBlockZ());
        this.getFileC().set("altars."+path+".radius", altar.getRadius());
        this.getFileC().set("altars."+path+".cmds", Arrays.asList("eco give {player} 10"));
        this.getFileC().set("altars."+path+".frequency", altar.getFrequency());
        this.getFileC().set("altars."+path+".title", altar.title);
        this.getFileC().set("altars."+path+".subtitle", altar.subtitle);
        this.getFileC().set("altars."+path+".message", altar.message);
        this.save();
    }

}
