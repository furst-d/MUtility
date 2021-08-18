package com.mens.mutility.spigot.eventhandlers;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.commands.tpdata.Tpdata;
import com.mens.mutility.spigot.messages.MessageChannel;
import com.mens.mutility.spigot.utils.ServerInfo;
import com.mens.mutility.spigot.utils.Timer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OnPlayerMoveEvent implements Listener {
    private final MUtilitySpigot plugin;
    private final MessageChannel messageChannel;
    private final Prefix prefix;
    private final List<Player> pendingRequests;
    private final String borderMessage;
    private final Tpdata teleportDataManager;

    public OnPlayerMoveEvent(MUtilitySpigot plugin) {
        this.plugin = plugin;
        messageChannel = new MessageChannel();
        prefix = new Prefix();
        pendingRequests = new ArrayList<>();
        borderMessage = "Nacházíš se v blízkosti hranice serveru! Za okamžik budeš teleportován na druhou stranu hranice";
        teleportDataManager = new Tpdata(plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(event.getFrom().getBlockX() != Objects.requireNonNull(event.getTo()).getBlockX()
                || event.getFrom().getBlockY() != Objects.requireNonNull(event.getTo()).getBlockY()
                || event.getFrom().getBlockZ() != Objects.requireNonNull(event.getTo()).getBlockZ()) {
            ServerInfo server = plugin.getCurrentServer();
            if(server != null) {
                if(!pendingRequests.contains(event.getPlayer())) {
                    if(server.getBorder1() != null) {
                        if(server.getBorder1().getFromX() <= event.getTo().getBlockX()
                                && event.getTo().getBlockX() <= server.getBorder1().getToX()
                                && server.getBorder1().getFromZ() <= event.getTo().getBlockZ()
                                && event.getTo().getBlockZ() <= server.getBorder1().getToZ()) {
                            player.sendMessage(prefix.getKostkujPrefix(true, false) + borderMessage);
                            pendingRequests.add(player);
                            //TODO
                            setTimer(player);
                        } else if(server.getBorder2().getFromX() <= event.getTo().getBlockX()
                                && event.getTo().getBlockX() <= server.getBorder2().getToX()
                                && server.getBorder2().getFromZ() <= event.getTo().getBlockZ()
                                && event.getTo().getBlockZ() <= server.getBorder2().getToZ()) {
                            player.sendMessage(prefix.getKostkujPrefix(true, false) + borderMessage);
                            pendingRequests.add(player);
                            //TODO
                            setTimer(player);
                        }
                    }
                    if(server.getRandomTeleport() != null) {
                        if(server.getRandomTeleport().getFromX() <= event.getTo().getBlockX()
                                && event.getTo().getBlockX() <= server.getRandomTeleport().getToX()
                                && server.getRandomTeleport().getFromY() <= event.getTo().getBlockY()
                                && event.getTo().getBlockY() <= server.getRandomTeleport().getToY()
                                && server.getRandomTeleport().getFromZ() <= event.getTo().getBlockZ()
                                && event.getTo().getBlockZ() <= server.getRandomTeleport().getToZ()) {
                            pendingRequests.add(player);
                            teleportDataManager.saveData(player, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), Objects.requireNonNull(player.getLocation().getWorld()).getName());
                            teleportDataManager.deleteOldPlayerData(player, 30);
                            messageChannel.sendToBungeeCord(player, "mens:random-teleport", player.getName());
                            setTimer(player);
                        }
                    }
                }
            }
        }
    }

    private void setTimer(Player player) {
        Timer timer = new Timer();
        timer.setOnFinish((sec, tt) -> pendingRequests.removeIf(playerLoc -> playerLoc.getName().equals(player.getName())));
        timer.startTimer(5);
    }
}
