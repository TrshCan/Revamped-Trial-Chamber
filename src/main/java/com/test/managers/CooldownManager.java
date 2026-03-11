package com.test.managers;

import com.test.MyPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final MyPlugin plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public CooldownManager(MyPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "cooldowns.yml");
        loadCooldowns();
    }

    public void loadCooldowns() {
        if (!dataFile.exists()) {
            return;
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        if (dataConfig.contains("cooldowns")) {
            for (String uuidStr : dataConfig.getConfigurationSection("cooldowns").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    long expireTime = dataConfig.getLong("cooldowns." + uuidStr);
                    cooldowns.put(uuid, expireTime);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void saveCooldowns() {
        if (dataConfig == null) {
            dataConfig = new YamlConfiguration();
        }
        dataConfig.set("cooldowns", null);
        for (Map.Entry<UUID, Long> entry : cooldowns.entrySet()) {
            dataConfig.set("cooldowns." + entry.getKey().toString(), entry.getValue());
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save cooldowns.yml");
        }
    }

    public boolean isOnCooldown(UUID uuid) {
        if (!cooldowns.containsKey(uuid)) return false;
        long expireTime = cooldowns.get(uuid);
        return System.currentTimeMillis() < expireTime;
    }

    public long getRemainingCooldownMillis(UUID uuid) {
        if (!cooldowns.containsKey(uuid)) return 0;
        long remaining = cooldowns.get(uuid) - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    public void setCooldown(UUID uuid) {
        long cooldownMinutes = plugin.getConfig().getLong("vault-cooldown-minutes", 30);
        long expireTime = System.currentTimeMillis() + (cooldownMinutes * 60 * 1000);
        cooldowns.put(uuid, expireTime);
        saveCooldowns();
    }
}
