package com.test.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class AntiGriefListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        if (type == Material.VAULT || type == Material.TRIAL_SPAWNER) {
            if (!event.getPlayer().hasPermission("revampedtrialchamber.admin")) {
                event.getPlayer().sendMessage("§cYou cannot break Trial Chamber blocks!");
                event.setCancelled(true);
            }
        }
    }
}
