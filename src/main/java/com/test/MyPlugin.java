package com.test;

import com.test.listeners.AntiGriefListener;
import com.test.listeners.SpawnerListener;
import com.test.listeners.VaultListener;
import com.test.managers.CooldownManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {
    
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        cooldownManager = new CooldownManager(this);

        getServer().getPluginManager().registerEvents(new VaultListener(this, cooldownManager), this);
        getServer().getPluginManager().registerEvents(new SpawnerListener(this), this);
        getServer().getPluginManager().registerEvents(new AntiGriefListener(), this);

        getLogger().info("RevampedTrialChamber has been enabled!");
    }

    @Override
    public void onDisable() {
        if (cooldownManager != null) {
            cooldownManager.saveCooldowns();
        }
        getLogger().info("RevampedTrialChamber has been disabled!");
    }
}
