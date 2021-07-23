package com.mens.mutility.spigot.eventhandlers;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.inventory.InventoryManager;
import com.mens.mutility.spigot.inventory.TeleportDataManager;
import com.mens.mutility.spigot.messages.MessageChannel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Objects;

public class OnPlayerPortalEvent implements Listener {
    private final MUtilitySpigot plugin;
    private final MessageChannel messageChannel;
    private final TeleportDataManager teleportDataManager;
    private final InventoryManager inventoryManager;

    public OnPlayerPortalEvent(MUtilitySpigot plugin) {
        this.plugin = plugin;
        messageChannel = new MessageChannel(plugin);
        teleportDataManager = new TeleportDataManager(plugin);
        inventoryManager = new InventoryManager();
    }

    @EventHandler
    public void onPlayerPortalEvent(PlayerPortalEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        double x = event.getFrom().getX();
        double y = event.getFrom().getY();
        double z = event.getFrom().getZ();
        String world = Objects.requireNonNull(event.getFrom().getWorld()).getName();
        String server = plugin.getCurrentServer();
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if(world.equalsIgnoreCase("world")) {
                messageChannel.sendPortalInfoToBungeeCord(player, "mens:portalRequest", "nether", x / 8, y, z / 8);
            } else if(world.equalsIgnoreCase("world_nether")) {
                messageChannel.sendPortalInfoToBungeeCord(player, "mens:portalRequest", "overworld", x * 8, y, z * 8);
            }
            teleportDataManager.saveInventory(player, inventoryManager.getInventory(player), x, y, z, world, server);
        } else if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if(world.equalsIgnoreCase("world")) {
                messageChannel.sendPortalInfoToBungeeCord(player, "mens:portalRequest", "end", 0, 0, 0);
            } else if(world.equalsIgnoreCase("world_the_end")) {
                messageChannel.sendPortalInfoToBungeeCord(player, "mens:portalRequest", "lobby", 0, 0, 0);
            }
            teleportDataManager.saveInventory(player, inventoryManager.getInventory(player), x, y, z, world, server);
        }
    }
}
