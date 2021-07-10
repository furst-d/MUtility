package com.mens.mutility.spigot.commands.commands.mstavba.eventhandlers;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.commands.commands.mstavba.MStavbaVoteManager;
import com.mens.mutility.spigot.utils.Timer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoinEvent implements Listener {
    private final MUtilitySpigot plugin;

    public OnPlayerJoinEvent(MUtilitySpigot plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        MStavbaVoteManager manager = new MStavbaVoteManager(plugin);
        if(manager.isActive()) {
            Player player = event.getPlayer();
            Timer timer = new Timer();
            timer.setOnFinish((sec, tt) -> {
                manager.createVoteLink(player);
            });
            timer.startTimer(20);
        }
    }
}
