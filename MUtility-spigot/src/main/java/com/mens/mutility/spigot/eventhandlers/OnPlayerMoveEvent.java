package com.mens.mutility.spigot.eventhandlers;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.messages.MessageChannel;
import com.mens.mutility.spigot.utils.ServerInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

public class OnPlayerMoveEvent implements Listener {
    private final MUtilitySpigot plugin;
    MessageChannel messageChannel;

    public OnPlayerMoveEvent(MUtilitySpigot plugin) {
        this.plugin = plugin;
        messageChannel = new MessageChannel();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(event.getFrom().getBlockX() != Objects.requireNonNull(event.getTo()).getBlockX()
                || event.getFrom().getBlockY() != Objects.requireNonNull(event.getTo()).getBlockY()
                || event.getFrom().getBlockZ() != Objects.requireNonNull(event.getTo()).getBlockZ()) {
            ServerInfo server = plugin.getCurrentServer();
            if(server != null) {
                if(server.getBorder1() != null) {
                    if(server.getBorder1().getFromX() <= event.getTo().getBlockX()
                            && event.getTo().getBlockX() <= server.getBorder1().getToX()
                            && server.getBorder1().getFromZ() <= event.getTo().getBlockZ()
                            && event.getTo().getBlockZ() <= server.getBorder1().getToZ()) {
                        System.out.println("Border 1");
                    } else if(server.getBorder2().getFromX() <= event.getTo().getBlockX()
                            && event.getTo().getBlockX() <= server.getBorder2().getToX()
                            && server.getBorder2().getFromZ() <= event.getTo().getBlockZ()
                            && event.getTo().getBlockZ() <= server.getBorder2().getToZ()) {
                        System.out.println("Border 2");
                    }
                }
                if(server.getRandomTeleport() != null) {
                    if(server.getRandomTeleport().getFromX() <= event.getTo().getBlockX()
                            && event.getTo().getBlockX() <= server.getRandomTeleport().getToX()
                            && server.getRandomTeleport().getFromY() <= event.getTo().getBlockY()
                            && event.getTo().getBlockY() <= server.getRandomTeleport().getToY()
                            && server.getRandomTeleport().getFromZ() <= event.getTo().getBlockZ()
                            && event.getTo().getBlockZ() <= server.getRandomTeleport().getToZ()) {
                        System.out.println("RT");
                    }
                }
            }
        }
    }
}
