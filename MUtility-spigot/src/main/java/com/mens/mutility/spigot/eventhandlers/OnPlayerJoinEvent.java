package com.mens.mutility.spigot.eventhandlers;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.messages.MessageChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.TimerTask;

public class OnPlayerJoinEvent implements Listener {
    private final MUtilitySpigot plugin;
    MessageChannel messageChannel;

    public OnPlayerJoinEvent(MUtilitySpigot plugin) {
        this.plugin = plugin;
        messageChannel = new MessageChannel();
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

        boolean isVanished = false;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.canSee(player)) {
                isVanished = true;
                break;
            }
        }
        if(!isVanished) {
            if(plugin.getJoinEffects().getData().contains("JoinEffects." + player.getName())) {
                if(plugin.getJoinEffects().getData().getBoolean("JoinEffects." + player.getName() + ".Enable")) {
                    List<String> list = plugin.getJoinEffects().getData().getStringList("JoinEffects." + player.getName() + ".Effects");
                    if(!list.isEmpty()) {
                        for (String s : list) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
                        }
                        list.clear();
                    }
                }
            }
        }
    }
}
