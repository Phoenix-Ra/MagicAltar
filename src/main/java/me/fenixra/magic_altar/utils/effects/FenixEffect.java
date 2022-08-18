package me.fenixra.magic_altar.utils.effects;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.UUID;

public interface FenixEffect {
    String id = UUID.randomUUID().toString();
    void run();
    default void complete(){
        getEffectsManager().completeEffect(this);
    }
    default void cancel(boolean callCompleted){
        if(callCompleted) complete();
    }
    default String getId(){ return id; }
    FenixEffectManager getEffectsManager();
    CustomLocation getLocation();
    int getIterations();
    void setLocation(CustomLocation location);
    boolean isCompleted();
    boolean isAsync();
    default void displayParticle(Particle effect, Location location) {
        displayParticle(effect, location, null, 1);
    }
    default void displayParticle(Particle particle, Location location, Color color, int amount) {
        ParticleUtils.display(particle, location, 0, 0, 0, 0, amount,
                color, null, (byte) 0, 100);
    }
    default void displayParticle(Particle particle, Location location, Color color, float particleOffsetX,
                                 float particleOffsetY,
                                 float particleOffsetZ, int amount) {
        ParticleUtils.display(particle, location, particleOffsetX, particleOffsetY, particleOffsetZ, 0, amount,
                color, null, (byte) 0, 100);
    }

    default void displayParticle(Particle particle, Location location, Color color, float particleOffsetX,
                                 float particleOffsetY,
                                 float particleOffsetZ, int amount, float speed,
                                 Material material, byte materialData,
                                 double range) {
        ParticleUtils.display(particle, location, particleOffsetX, particleOffsetY, particleOffsetZ, speed, amount,
                color, material, materialData, range);
    }

}
