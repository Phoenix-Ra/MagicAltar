package me.fenixra.magic_altar.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class FenixFileManager {
    JavaPlugin plugin;
    private final HashMap<String, FenixFile> files= new HashMap<>();
    public FenixFileManager addFile(FenixFile file) {
        files.put(file.getFileName(),file);
        return this;
    }
    public void loadfiles() {
        for(Map.Entry<String, FenixFile> f : files.entrySet()) {
            if(!f.getValue().load()) {
                plugin.getLogger().severe("Error occurred while loading configuration file "+f.getKey()+".yml  Probably, file is corrupted or has a syntax mistake");
                return;
            }
        }
    }
    public void loadFile(String name){
        if(!files.containsKey(name)){
            plugin.getLogger().severe("Tried to load file "+name+ " that is not on the list of the FileManager");
            return;
        }
        if(!files.get(name).load()) {
            plugin.getLogger().severe("Error occurred while loading configuration file "+name+".yml  Probably, file is corrupted or has a syntax mistake");
        }
    }
    public void removeFile(String name){
        if(!files.containsKey(name)){
            plugin.getLogger().severe("Tried to remove file "+name+ " that is not on the list of the FileManager");
            return;
        }
        files.remove(name);
    }
    public void reloadFiles() {
        for(Map.Entry<String, FenixFile> f : files.entrySet()) {
            if(!f.getValue().reload()) {
                plugin.getLogger().severe("Error occurred while loading configuration file "+f.getKey()+".yml  Probably, file is corrupted or has a syntax mistake");

                return;
            }
        }
    }
    public FileConfiguration loadFromResource(String name, File out, boolean copy) {
        try {
            InputStream is =  plugin.getResource(name);
            FileConfiguration f = YamlConfiguration.loadConfiguration(out);
            if(is!=null) {
                InputStreamReader isReader = new InputStreamReader(is);
                f.setDefaults(YamlConfiguration.loadConfiguration(isReader));
                f.options().copyDefaults(copy);
                f.save(out);
            }
            return f;
        }catch(IOException e) {
            return null;
        }
    }
    public FenixFile getFile(String name) {
        return files.get(name);
    }
    public JavaPlugin getPlugin() {
        return plugin;
    }

}
