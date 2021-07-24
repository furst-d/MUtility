package com.mens.mutility.spigot.eventhandlers;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.inventory.TeleportDataManager;
import com.mens.mutility.spigot.messages.MessageChannel;
import com.mens.mutility.spigot.utils.Timer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OnPlayerPortalEvent implements Listener {
    private final MUtilitySpigot plugin;
    private final MessageChannel messageChannel;
    private final TeleportDataManager teleportDataManager;
    private final List<Player> conectingPlayers;

    public OnPlayerPortalEvent(MUtilitySpigot plugin) {
        this.plugin = plugin;
        messageChannel = new MessageChannel(plugin);
        teleportDataManager = new TeleportDataManager(plugin);
        conectingPlayers = new ArrayList<>();
    }

    @EventHandler
    public void onPlayerPortalEvent(PlayerPortalEvent event) {
        event.setCancelled(true);
        if(!plugin.getServers().isEmpty()) {
            Player player = event.getPlayer();
            double x = event.getFrom().getX();
            double y = event.getFrom().getY();
            double z = event.getFrom().getZ();
            String world = Objects.requireNonNull(event.getFrom().getWorld()).getName();
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
                teleportDataManager.saveData(player, x, y, z, world);
                if(world.equalsIgnoreCase("world")) {
                    messageChannel.sendPortalInfoToBungeeCord(player, "mens:portalRequest", "nether", x / 8, y, z / 8);
                } else if(world.equalsIgnoreCase("world_nether")) {
                    messageChannel.sendPortalInfoToBungeeCord(player, "mens:portalRequest", "overworld", x * 8, y, z * 8);
                }
            } else if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
                if(conectingPlayers.stream().noneMatch(playerLoc -> playerLoc.getName().equals(player.getName()))) {
                    conectingPlayers.add(player);
                    Timer timer = new Timer();
                    timer.setOnFinish((sec, tt) -> conectingPlayers.removeIf(playerLoc -> playerLoc.getName().equals(player.getName())));
                    teleportDataManager.saveData(player, x, y, z, world);
                    teleportDataManager.deleteOldPlayerData(player, 30);
                    messageChannel.sendPortalInfoToBungeeCord(player, "mens:portalRequest", "end", 0, 0, 0);
                    timer.startTimer(5);
                }
            }
        }
    }
}
