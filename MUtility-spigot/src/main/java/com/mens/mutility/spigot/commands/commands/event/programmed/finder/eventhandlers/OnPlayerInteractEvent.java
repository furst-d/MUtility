package com.mens.mutility.spigot.commands.commands.event.programmed.finder.eventhandlers;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.utils.Checker;
import com.mens.mutility.spigot.utils.CraftCoinManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;
import java.util.Random;

public class OnPlayerInteractEvent implements Listener {
    private final MUtilitySpigot plugin;
    private final Checker checker;
    private final Prefix prefix;
    private final PluginColors colors;
    private final CraftCoinManager ccManager;

    public OnPlayerInteractEvent(MUtilitySpigot plugin) {
        this.plugin = plugin;
        checker = new Checker(plugin);
        prefix = new Prefix();
        colors = new PluginColors();
        ccManager = new CraftCoinManager(plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(plugin.getEvents().getData().getBoolean("Hledacka.Enable")) {
            Player player = event.getPlayer();
            Action action = event.getAction();
            Material actionBlock = Material.getMaterial(Objects.requireNonNull(plugin.getEvents().getData().getString("Hledacka.Block")).toUpperCase());

            if(action.equals(Action.LEFT_CLICK_BLOCK)) {
                Block block = event.getClickedBlock();
                assert block != null;
                Location location = block.getLocation();
                World world = block.getWorld();
                if(block.getType().equals(actionBlock)) {
                    if(checker.chechEventLocation(player.getLocation(), "Hledacka")) {
                        Random rd = new Random();
                        int isCC = rd.nextInt(2);
                        if(isCC == 1) {
                            int finalCC = rd.nextInt(30) + 1;
                            player.sendMessage(prefix.getCustomPrefix("Hledačka", true, false) +
                                    "Gratuluji, našel jsi " + colors.getPrimaryColor() + finalCC + colors.getSecondaryColor() + " CC!");
                            ccManager.addCC(finalCC, player.getName(), 9);
                            location.getBlock().setType(Material.AIR);
                            Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.VILLAGER_HAPPY, location, 5, 0.4D, 0.75D, 0.4D, 0.05);
                            world.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 1);
                        } else {
                            player.sendMessage(prefix.getCustomPrefix("Hledačka", true, false) +
                                    "Bohužel, zde nic není!");
                            location.getBlock().setType(Material.AIR);
                            Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.FLAME, location, 5, 0.4D, 0.75D, 0.4D, 0.05);
                            world.playSound(player.getLocation(), Sound.ENTITY_BAT_DEATH, 5, 1);
                        }
                    }
                }
            }
        }
    }
}
