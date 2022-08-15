package me.fenixra.magic_altar;

import me.fenixra.magic_altar.files.ConfigFile;
import me.fenixra.magic_altar.files.DataFile;
import me.fenixra.magic_altar.utils.FenixFileManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin {
    private static Main instance;
    private AltarManager altarM;
    private FenixFileManager fileManager;

    @Override
    public void onEnable() {
        instance=this;

        //I've added it to fix an issue on plugin load. Idk why, but it doesn't care about
        // HolographicDisplay dependency and enables before that plugin.
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getInstance().getCommand("altar").setExecutor(new AltarCommand(Main.getInstance()));
                altarM=new AltarManager();
                fileManager=new FenixFileManager(Main.getInstance());
                fileManager.addFile(new ConfigFile(fileManager)).addFile(new DataFile(fileManager));
                fileManager.loadfiles();
            }
        }.runTaskTimer(this,20,0);
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
