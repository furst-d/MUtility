package com.mens.mutility.spigot.eventhandlers;

import com.mens.mutility.spigot.commands.commands.mparticle.ParticlePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuitEvent implements Listener {

    @EventHandler
    public void OnPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ParticlePlayer.unregisterPlayer(player);
    }
}
