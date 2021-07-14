package com.mens.mutility.spigot.eventhandlers;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.messages.MessageChannel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.TimerTask;

public class OnPlayerJoinEvent implements Listener {
    private final MUtilitySpigot plugin;
    MessageChannel messageChannel;

    public OnPlayerJoinEvent(MUtilitySpigot plugin) {
        this.plugin = plugin;
        messageChannel = new MessageChannel(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(plugin.getServers().isEmpty()) {
            new java.util.Timer().schedule(new TimerTask(){
                int seconds = 0;
                @Override
                public void run() {
                        if (seconds == 5) {
                            messageChannel.sendToBungeeCord(player, "mens:servers-info-request", player.getName());
                            this.cancel();
                        }
                        seconds++;
                }
            },0,1000);
        }
    }
}
