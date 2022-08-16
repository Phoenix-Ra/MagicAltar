package me.fenixra.magic_altar.files;

import me.fenixra.magic_altar.Altar;
import me.fenixra.magic_altar.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AltarsPackage {

    private HashMap<String, File> files=new HashMap<>();
    private HashMap<String, FileConfiguration> configs=new HashMap<>();

    public AltarsPackage(){
        loadPackage();
    }

    private void loadPackage(){
        configs.clear();
        files.clear();
        File folder = new File(Main.getInstance().getDataFolder().getPath()+"/altars");
        if(!folder.exists()){
            folder.mkdir();

            File sample = new File(Main.getInstance().getDataFolder().getPath()+"/altars", "example.yml");
            InputStream is =  Main.getInstance().getResource("sampleAltar.yml");
            FileConfiguration f = YamlConfiguration.loadConfiguration(sample);
            if(is!=null) {
                InputStreamReader isReader = new InputStreamReader(is);
                f.setDefaults(YamlConfiguration.loadConfiguration(isReader));
                f.options().copyDefaults(true);
                try {
                    f.save(sample);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            files.put(sample.getName().split("\\.")[0],sample);
            configs.put(sample.getName().split("\\.")[0],f);
        }else {
            loadFilesFromFolder(folder);
        }
    }

    private void loadFilesFromFolder(final File folder){

        files.clear();
        configs.clear();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                loadFilesFromFolder(fileEntry);
            } else {
                files.put(fileEntry.getName().split("\\.")[0],fileEntry);
                configs.put(fileEntry.getName().split("\\.")[0],YamlConfiguration.loadConfiguration(fileEntry));

            }
        }
    }


    public boolean reloadAction() {
        Main.getInstance().getAltarM().clearAll(false);
        loadPackage();
        setupAltars();
        return true;
    }
    private void setupAltars() {
        for(Map.Entry<String,FileConfiguration> entry: configs.entrySet()) {
            if (!entry.getValue().contains("location.world")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " + entry.getKey() + " ... WORLD DOESN'T SPECIFIED");
                continue;
            } else if (Bukkit.getWorld(entry.getValue().getString( "location.world")) == null) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " + entry.getKey() + " ... SPECIFIED WORLD DOESN'T EXIST");
                continue;
            }
            if (!entry.getValue().contains("location.posX")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " + entry.getKey()  + " ... POSITION_X DOESN'T SPECIFIED");
                continue;
            }
            if (!entry.getValue().contains("location.posY")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " + entry.getKey()  + " ... POSITION_Y DOESN'T SPECIFIED");
                continue;
            }
            if (!entry.getValue().contains("location.posZ")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " + entry.getKey()  + " ... POSITION_Z DOESN'T SPECIFIED");
                continue;
            }
            if (!entry.getValue().contains("radius")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " + entry.getKey()  + " ... RADIUS DOESN'T SPECIFIED");
                continue;
            }
            if (!entry.getValue().contains("commands")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " + entry.getKey()  + " ... COMMANDS DON'T SPECIFIED");
                continue;
            }
            if (!entry.getValue().contains("frequency")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " + entry.getKey()  + " ... FREQUENCY DOESN'T SPECIFIED");
                continue;
            }
            if (!entry.getValue().contains("title")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " + entry.getKey()  + " ... TITLE DOESN'T SPECIFIED");
                continue;
            }
            if (!entry.getValue().contains("subtitle")) {
                Main.getInstance().getLogger().severe("CANNOT ADD ALTAR WITH ID " + entry.getKey()  + " ... SUBTITLE DOESN'T SPECIFIED");
                continue;
            }
            Location loc = new Location(Bukkit.getWorld(entry.getValue().getString("location.world")), entry.getValue().getDouble("location.posX"), entry.getValue().getDouble("location.posY"), entry.getValue().getDouble( "location.posZ"));
            int radius = entry.getValue().getInt("radius");
            int frequency = entry.getValue().getInt("frequency");
            List<String> list = entry.getValue().getStringList("commands");
            String title = entry.getValue().getString("title");
            String subtitle = entry.getValue().getString("subtitle");
            String message = entry.getValue().getString("message");
            HashMap<String, List<String>> don_nums = new HashMap<>();
            if (entry.getValue().getConfigurationSection("donator_nums") != null) {
                for (String s : entry.getValue().getConfigurationSection("donator_nums").getKeys(false)) {
                    don_nums.put(s, entry.getValue().getStringList( "donator_nums." + s));
                }

            }
            Main.getInstance().getAltarM().addAltar(new Altar(entry.getKey(), loc, radius, list, don_nums, frequency, title, subtitle, message));




        }
    }
    public void saveAltar(Altar altar) {
        File file = new File(Main.getInstance().getDataFolder().getPath()+"/altars", altar.getId()+".yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("Failed to save new altar, unexpected error occurred");
                return;
            }
        }
        FileConfiguration config=YamlConfiguration.loadConfiguration(file);
        config.set("location.world", altar.getBlockLocation().getWorld().getName());
        config.set("location.posX", altar.getBlockLocation().getBlockX());
        config.set("location.posY", altar.getBlockLocation().getBlockY());
        config.set("location.posZ", altar.getBlockLocation().getBlockZ());
        config.set("radius", altar.getRadius());
        config.set("commands", Arrays.asList("eco give {player} 10"));
        config.set("frequency", altar.getFrequency());
        config.set("title", altar.getRewardTitle());
        config.set("subtitle", altar.getRewardSubtitle());
        config.set("message", altar.getRewardMessage());

        files.put(altar.getId(),file);
        configs.put(altar.getId(),config);
        saveConfig(altar.getId());
    }
    public FileConfiguration getAltarConfig(String name){
        return configs.get(name);
    }
    public void saveConfig(String name){
        try {
            if (configs.containsKey(name) && files.containsKey(name)) {
                configs.get(name).save(files.get(name));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("Failed to save altar configuration file with name "+name+".yml");
        }
    }

}
