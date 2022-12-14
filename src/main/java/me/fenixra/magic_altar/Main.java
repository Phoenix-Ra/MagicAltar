package me.fenixra.magic_altar;

import me.fenixra.magic_altar.files.AltarsPackage;
import me.fenixra.magic_altar.files.ConfigFile;
import me.fenixra.magic_altar.utils.FenixFileManager;
import me.fenixra.magic_altar.utils.effects.FenixEffectManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Main extends JavaPlugin {
    private static Main instance;
    private AltarManager altarM;
    private FenixFileManager fileManager;
    private AltarsPackage altarsPackage;
    private FenixEffectManager effectManager;

    @Override
    public void onEnable() {
        instance=this;

        Main.getInstance().getCommand("altar").setExecutor(new AltarCommand(Main.getInstance()));
        altarM=new AltarManager();
        fileManager=new FenixFileManager(Main.getInstance());
        fileManager.addFile(new ConfigFile(fileManager));
        fileManager.loadfiles();
        altarsPackage=new AltarsPackage();
        altarsPackage.reloadAction();
        effectManager=new FenixEffectManager(this);
        try {
            if ((new Metrics(this, 16158)).isEnabled()) {
                Bukkit.getConsoleSender().sendMessage("§7Metrics loaded successfully");
            }
            doAsync(this::checkVersion);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        altarM.clearAll(true);

    }
    public void reload(){
        fileManager.reloadFiles();
        altarsPackage.reloadAction();
    }

    private void checkVersion() {
        String currentVersion = Main.getInstance().getDescription().getVersion();
        Bukkit.getConsoleSender().sendMessage("§6Checking for updates... Your current version is v"+currentVersion);
        URL url;
        try {
            url = new URL("https://api.spigotmc.org/legacy/update.php?resource=104593");
        }
        catch (MalformedURLException e) {
            return;
        }
        URLConnection conn;
        try {
            conn = url.openConnection();
        }
        catch (IOException e) {
            return;
        }
        try {
            assert (conn != null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String newVersion=reader.readLine();
            if (!newVersion.equals(currentVersion)) {

                Bukkit.getConsoleSender().sendMessage("§6A new version available! Download §6MagicAltar v"
                        +newVersion+" §6at https://www.spigotmc.org/resources/104593/");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int getServerVersion(){
        return Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("_")[1]);
    }
    public static void doSync(Runnable runnable) {
        instance.getServer().getScheduler().runTask(instance, runnable);
    }
    public static void doAsync(Runnable runnable) {instance.getServer().getScheduler().runTaskAsynchronously(instance, runnable); }
    public static Main getInstance() {
        return instance;
    }
    public AltarManager getAltarM() {
        return altarM;
    }
    public AltarsPackage getAltarsPackage() {
        return altarsPackage;
    }
    public FenixEffectManager getEffectManager(){
        return effectManager;
    }
}
