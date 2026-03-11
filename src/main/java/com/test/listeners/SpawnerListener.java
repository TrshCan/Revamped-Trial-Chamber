package com.test.listeners;

import com.test.MyPlugin;
import org.bukkit.Difficulty;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class SpawnerListener implements Listener {
    private final MyPlugin plugin;

    public SpawnerListener(MyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTrialSpawnerSpawn(SpawnerSpawnEvent event) {
        if (event.getSpawner().getBlock().getType() != org.bukkit.Material.TRIAL_SPAWNER) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();

        long playerCount = entity.getWorld().getNearbyEntities(entity.getLocation(), 14, 14, 14, 
            e -> e instanceof Player && !((Player)e).getGameMode().name().equals("SPECTATOR")).size();

        if (playerCount <= 1) return;

        int extraPlayers = (int) (playerCount - 1);
        double healthMultiplier = plugin.getConfig().getDouble("health-multiplier-per-player", 0.2);
        double damageMultiplier = plugin.getConfig().getDouble("damage-multiplier-per-player", 0.2);
        boolean scaleWaves = plugin.getConfig().getBoolean("spawner-waves-increase", true);
        double hardModeMulti = plugin.getConfig().getDouble("hard-mode-multiplier", 1.5);

        double totalHealthMult = 1.0 + (extraPlayers * healthMultiplier);
        double totalDamageMult = 1.0 + (extraPlayers * damageMultiplier);

        if (scaleWaves && entity.getWorld().getDifficulty() == Difficulty.HARD) {
            totalHealthMult *= hardModeMulti;
            totalDamageMult *= hardModeMulti;
        }

        AttributeInstance healthAttr = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttr != null) {
            double baseHealth = healthAttr.getBaseValue();
            healthAttr.setBaseValue(baseHealth * totalHealthMult);
            entity.setHealth(healthAttr.getBaseValue());
        }

        AttributeInstance damageAttr = entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (damageAttr != null) {
            double baseDamage = damageAttr.getBaseValue();
            damageAttr.setBaseValue(baseDamage * totalDamageMult);
        }
    }
}
