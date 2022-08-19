package me.fenixra.magic_altar;

import me.fenixra.magic_altar.utils.effects.CustomLocation;
import me.fenixra.magic_altar.utils.effects.FenixEffect;
import me.fenixra.magic_altar.utils.effects.FenixEffectManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class CircleEffect implements FenixEffect {
    private final FenixEffectManager effectManager;
    private boolean completed;
    private CustomLocation origin;
    private Particle particle = Particle.REDSTONE;
    private Color particleColor;

    private int iterations;
    private double thickness = 0.5;
    private double radius = 0.5;

    private double radiusIncrementer = 0;
    private Vector rotation = null;

    protected CircleEffect(FenixEffectManager effectManager) {
        this.effectManager = effectManager;
    }

    @Override
    public void run() {
        try {
            if (completed) return;
            if (getLocation() == null) {
                cancel(false);
                return;
            }
            Location location = getLocation().getLocation();
            radius = radius + radiusIncrementer;
            int particles = (int) (Math.PI * radius * 2 / thickness);
            double step = Math.PI * 2 / particles;
            Vector vector = new Vector();
            for (int i = 0; i < particles; i++) {
                vector.setX(radius * Math.sin(step * i));
                vector.setZ(radius * Math.cos(step * i));
                vector.setY(1);
                if (rotation != null) {
                    vector.rotateAroundX(rotation.getX() *  Math.PI / 180);
                    vector.rotateAroundY(rotation.getY() *  Math.PI / 180);
                    vector.rotateAroundZ(rotation.getZ() *  Math.PI / 180);
                }
                displayParticle(particle, location.add(vector),particleColor,1);
                location.subtract(vector);
            }
        } catch (Exception e) {
            e.printStackTrace();
            cancel(true);
        }
        iterations--;
        if (iterations < 1) {
            complete();
        }
    }

    @Override
    public FenixEffectManager getEffectsManager() {
        return effectManager;
    }

    @Override
    public CustomLocation getLocation() {
        return origin;
    }

    @Override
    public void setLocation(CustomLocation location) {
        this.origin = location;
    }

    @Override
    public int getIterations() {
        return 0;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
    protected void setParticleColor(Color value) {
        this.particleColor = value;
    }
    public void setIterations(int value) {
        this.iterations = value;
    }
    protected void setThickness(double value) {
        this.thickness = value;
    }
    protected void setRadius(double value) {
        this.radius = value;
    }
    protected void setRadiusIncrementer(double value) {
        this.radiusIncrementer = value;
    }
    protected void setRotation(Vector value) {
        this.rotation = value;
    }
}
