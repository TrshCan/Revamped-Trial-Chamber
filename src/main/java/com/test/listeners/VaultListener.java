package com.test.listeners;

import com.test.MyPlugin;
import com.test.managers.CooldownManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;

public class VaultListener implements Listener {
    private final CooldownManager cooldownManager;

    public VaultListener(MyPlugin plugin, CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    @EventHandler
    public void onVaultInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        Block block = event.getClickedBlock();
        if (block == null) return;

        if (block.getType() == Material.VAULT) {
            Material itemType = event.getItem() != null ? event.getItem().getType() : Material.AIR;
            if (itemType == Material.TRIAL_KEY || itemType == Material.OMINOUS_TRIAL_KEY) {
                if (cooldownManager.isOnCooldown(event.getPlayer().getUniqueId())) {
                    long remainingMillis = cooldownManager.getRemainingCooldownMillis(event.getPlayer().getUniqueId());
                    long remainingMinutes = (remainingMillis / 1000) / 60;
                    long remainingSeconds = (remainingMillis / 1000) % 60;
                    
                    event.getPlayer().sendMessage("§cYou cannot open this Vault yet! Please wait " + remainingMinutes + "m " + remainingSeconds + "s.");
                    event.setCancelled(true);
                } else {
                    cooldownManager.setCooldown(event.getPlayer().getUniqueId());
                }
            }
        }
    }
}
