package com.mens.mutility.spigot.commands.commands.event.programmed.graveyard.eventhandlers;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.utils.Checker;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Objects;

public class OnBlockBreakEvent implements Listener {
    private final MUtilitySpigot plugin;
    private final Checker checker;

    public OnBlockBreakEvent(MUtilitySpigot plugin) {
        this.plugin = plugin;
        checker = new Checker(plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(plugin.getEvents().getData().getBoolean("Hrbitov.Enable")) {
            Player player = event.getPlayer();
            Material actionBlock = Material.getMaterial(Objects.requireNonNull(plugin.getEvents().getData().getString("Hrbitov.Block")).toUpperCase());

            Block block = event.getBlock();
            Location location = block.getLocation();
            World world = block.getWorld();
            if(checker.chechEventLocation(player.getLocation(), "Hrbitov")) {
                if(block.getType().equals(actionBlock)) {
                    Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.SOUL, location, 10, 0.4D, 0.75D, 0.4D, 0.1);
                    world.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 5, 1);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
