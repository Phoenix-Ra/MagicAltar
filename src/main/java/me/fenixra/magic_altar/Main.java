package me.fenixra.magic_altar;

import me.fenixra.magic_altar.files.ConfigFile;
import me.fenixra.magic_altar.files.DataFile;
import me.fenixra.magic_altar.utils.FenixFileManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;
    private AltarManager altarM;
    private FenixFileManager fileManager;

    @Override
    public void onEnable() {
        instance=this;
        fileManager=new FenixFileManager();
        fileManager.addFile(new ConfigFile(fileManager)).addFile(new DataFile(fileManager));
        fileManager.loadfiles();
        this.getCommand("altar").setExecutor(new AltarCommand(this));
        altarM=new AltarManager();

    }

    public void reload(){
        fileManager.reloadFiles();
    }


    @Override
    public void onDisable() {
        altarM.clearAll();

    }

    public static Main getInstance() {
        return instance;
    }
    public AltarManager getAltarM() {
        return altarM;
    }
    public DataFile getDataFile() {
        return (DataFile) fileManager.getFile("data");
    }
}
