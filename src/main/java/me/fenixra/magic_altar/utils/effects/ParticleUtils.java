package me.fenixra.magic_altar.utils.effects;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ParticleUtils {
    public static void display(Particle particle, Location center, float offsetX, float offsetY, float offsetZ, float speed, int amount, Color color, Material material, byte materialData, double range) {

        if (particle == Particle.ITEM_CRACK) {
            displayItemCrack(particle, center, offsetX, offsetY, offsetZ, speed, amount, material, materialData,range);
            return;
        }

        if (color != null && (particle == Particle.SPELL_MOB_AMBIENT || particle == Particle.SPELL_MOB)) {
            displayLegacyColoredParticle(particle, center,speed, color,range);
            return;
        }

        Object data = null;
        if (particle == Particle.BLOCK_DUST || particle == Particle.BLOCK_CRACK || particle == Particle.FALLING_DUST) {
            data = material.createBlockData();
        }

        if (particle == Particle.REDSTONE) {
            if (color == null) {
                color = Color.RED;
            }
            data = new Particle.DustOptions(color, 1);
        }

        display(particle, center, offsetX, offsetY, offsetZ, speed, amount, data, range);
    }


    private static void display(Particle particle, Location center, float offsetX, float offsetY, float offsetZ, float speed, int amount, Object data, double range) {
        try {
            double squaredRange = range * range;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld() != center.getWorld() || player.getLocation().distanceSquared(center) > squaredRange) {
                    continue;
                }
                player.spawnParticle(particle, center, amount, offsetX, offsetY, offsetZ, speed, data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void displayItemCrack(Particle particle, Location center, float offsetX, float offsetY, float offsetZ, float speed, int amount, Material material, byte materialData, double range) {
        if (material == null || material == Material.AIR) {
            return;
        }
        ItemStack item = new ItemStack(material);
        item.setDurability(materialData);
        display(particle, center, offsetX, offsetY, offsetZ, speed, amount, item, range);
    }

    private static void displayLegacyColoredParticle(Particle particle, Location center, float speed,  Color color, double range) {
        int amount = 0;
        if (speed == 0) {
            speed = 1;
        }
        float offsetX = (float) color.getRed() / 255;
        float offsetY = (float) color.getGreen() / 255;
        float offsetZ = (float) color.getBlue() / 255;
        if (offsetX < Float.MIN_NORMAL) {
            offsetX = Float.MIN_NORMAL;
        }
        display(particle, center, offsetX, offsetY, offsetZ, speed, amount, null, range);
    }
}
