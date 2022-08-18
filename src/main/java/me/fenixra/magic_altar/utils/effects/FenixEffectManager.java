package me.fenixra.magic_altar.utils.effects;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FenixEffectManager {
    private final Plugin plugin;
    private final HashMap<FenixEffect, BukkitTask> runningEffects=new HashMap<>();;

    public FenixEffectManager(Plugin plugin){
        this.plugin=plugin;
    }

    public void startEffect(FenixEffect effect) {
        if (!plugin.isEnabled()) return;
        if (runningEffects.containsKey(effect)) return;
        BukkitScheduler s = Bukkit.getScheduler();
        BukkitTask task;
        if(effect.getIterations()==1) {
             task = effect.isAsync() ? s.runTaskAsynchronously(plugin, effect::run) : s.runTask(plugin, effect::run);
        }else{
            task = s.runTaskTimer(plugin, effect::run, 0, 1);
        }
        runningEffects.put(effect, task);
    }
    public void completeEffect(FenixEffect effect) {
        BukkitTask task = runningEffects.get(effect);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        runningEffects.remove(effect);
    }
    public List<FenixEffect> getRunningEffectsByID(String id){
        return runningEffects.keySet().stream().filter(effect -> effect.getId().equals(id)).collect(Collectors.toList());
    }
    public void cancelEffectsByID(String id){
        getRunningEffectsByID(id).forEach(effect -> effect.cancel(false));
    }
    public void cancelAllEffects(){
        List<FenixEffect> list = new ArrayList<>(runningEffects.keySet());

        list.forEach(effect -> {
            effect.cancel(false);
            if(runningEffects.containsKey(effect)) {
                runningEffects.get(effect).cancel();
                runningEffects.remove(effect);
            }
        });
    }
}
