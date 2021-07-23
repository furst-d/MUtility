package com.mens.mutility.spigot.messages;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.inventory.InventoryManager;
import com.mens.mutility.spigot.inventory.TeleportData;
import com.mens.mutility.spigot.inventory.TeleportDataManager;
import com.mens.mutility.spigot.portal.PortalManager;
import com.mens.mutility.spigot.utils.Checker;
import com.mens.mutility.spigot.utils.ServerInfo;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;


/**
 * Trida zajistujici komunikaci mezi pluginy na serverech v ramci BungeeCord
 */
public class MessageChannelListener implements PluginMessageListener {

    private final MUtilitySpigot plugin;
    private final TeleportDataManager teleportDataManager;
    private final InventoryManager inventoryManager;

    /**
     * Konstruktor tridy
     * @param plugin Odkaz na main tridu
     */
    public MessageChannelListener(MUtilitySpigot plugin) {
        this.plugin = plugin;
        teleportDataManager = new TeleportDataManager(plugin);
        inventoryManager = new InventoryManager();
    }

    @Override
    public void onPluginMessageReceived(@Nonnull String channel, @Nonnull Player player, @Nonnull byte[] message) {
        try {
            DataInputStream stream = new DataInputStream(new ByteArrayInputStream(message));
            String subChannel = stream.readUTF();
            switch(subChannel) {
                case "mens:send-to-nether":
                    Player telPlayerNe = Bukkit.getPlayer(stream.readUTF());
                    assert telPlayerNe != null;
                    Location loc = player.getLocation();
                    boolean loadInventoryNe = stream.readBoolean();
                    double xNe = stream.readDouble();
                    double yNe = stream.readDouble();
                    double zNe = stream.readDouble();
                    String worldNe = "world_nether";
                    loc.setX(xNe);
                    loc.setY(yNe);
                    loc.setZ(zNe);
                    if(loadInventoryNe) {
                        TeleportData inventoryInfo = teleportDataManager.loadNewestPlayerInventory(telPlayerNe);
                        int inventoryId = inventoryInfo.getId();
                        JsonObject inventory = inventoryManager.toJsonObject(inventoryInfo.getInventory());
                        String server = plugin.getCurrentServer();
                        teleportDataManager.updateInventory(inventoryId, xNe, yNe, zNe, worldNe, server);
                        inventoryManager.loadInventory(telPlayerNe, inventory);
                        telPlayerNe.setGameMode(GameMode.valueOf(inventoryInfo.getGamemode()));
                    }
                    loc.setWorld(WorldCreator.name(worldNe).createWorld());
                    PortalManager pm = new PortalManager(player, loc);
                    pm.findPortal();
                    pm.createPortal();
                    if(pm.isPrepared()) {
                        telPlayerNe.teleport(pm.getPortalLocation());
                    }
                    break;
                case "mens:send-to-overworld":
                    Player telPlayerOw = Bukkit.getPlayer(stream.readUTF());
                    assert telPlayerOw != null;
                    loc = player.getLocation();
                    boolean loadInventoryOw = stream.readBoolean();
                    double xOw = stream.readDouble();
                    double yOw = stream.readDouble();
                    double zOw = stream.readDouble();
                    String worldOw = "world";
                    loc.setX(xOw);
                    loc.setY(yOw);
                    loc.setZ(zOw);
                    if(loadInventoryOw) {
                        TeleportData inventoryInfo = teleportDataManager.loadNewestPlayerInventory(telPlayerOw);
                        int inventoryId = inventoryInfo.getId();
                        JsonObject inventory = inventoryManager.toJsonObject(inventoryInfo.getInventory());
                        String server = plugin.getCurrentServer();
                        teleportDataManager.updateInventory(inventoryId, xOw, yOw, zOw, worldOw, server);
                        inventoryManager.loadInventory(telPlayerOw, inventory);
                        telPlayerOw.setGameMode(GameMode.valueOf(inventoryInfo.getGamemode()));
                    }
                    loc.setWorld(WorldCreator.name(worldOw).createWorld());
                    pm = new PortalManager(player, loc);
                    pm.findPortal();
                    pm.createPortal();
                    if(pm.isPrepared()) {
                        telPlayerOw.teleport(pm.getPortalLocation());
                    }
                    break;
                case "mens:send-to-end":
                    Player telPlayerEnd = Bukkit.getPlayer(stream.readUTF());
                    assert telPlayerEnd != null;
                    loc = player.getLocation();
                    boolean loadInventoryEnd = stream.readBoolean();
                    double xEnd = 98;
                    double yEnd = 48;
                    double zEnd = -2;
                    String worldEnd = "world_the_end";
                    loc.setX(xEnd);
                    loc.setY(yEnd);
                    loc.setZ(zEnd);
                    if(loadInventoryEnd) {
                        TeleportData inventoryInfo = teleportDataManager.loadNewestPlayerInventory(telPlayerEnd);
                        int inventoryId = inventoryInfo.getId();
                        JsonObject inventory = inventoryManager.toJsonObject(inventoryInfo.getInventory());
                        String server = plugin.getCurrentServer();
                        teleportDataManager.updateInventory(inventoryId, xEnd, yEnd, zEnd, worldEnd, server);
                        inventoryManager.loadInventory(telPlayerEnd, inventory);
                        telPlayerEnd.setGameMode(GameMode.valueOf(inventoryInfo.getGamemode()));
                    }
                    loc.setWorld(WorldCreator.name(worldEnd).createWorld());
                    pm = new PortalManager(player, loc);
                    if(pm.createEndPlatform()) {
                        telPlayerEnd.teleport(pm.getEndPlatformLocation());
                    }
                    break;
                case "mens:permissionRequest":
                    String permission = stream.readUTF();
                    String returnChannel = stream.readUTF();
                    Checker checker = new Checker(plugin);
                    StringBuilder permPlayers = new StringBuilder();
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if(checker.checkPermissions(onlinePlayer, permission)) {
                            permPlayers.append(onlinePlayer.getName()).append(";");
                        }
                    }
                    if(!permPlayers.toString().equalsIgnoreCase("")) {
                        permPlayers.substring(0, permPlayers.length() - 1);
                    }
                    MessageChannel messageChannel = new MessageChannel(plugin);
                    player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
                    messageChannel.sendToBungeeCord(player, returnChannel, permPlayers.toString());
                    break;
                case "mens:servers-info-response":
                    String[] servers = stream.readUTF().split(";");
                    String serverName = stream.readUTF();
                    plugin.getServers().clear();
                    for(String serverLoc : servers) {
                        if(serverLoc.equals(serverName)) {
                            plugin.getServers().add(new ServerInfo(serverLoc, true));
                        } else {
                            plugin.getServers().add(new ServerInfo(serverLoc, false));
                        }
                    }
                    break;
                case "mens:teleport-request":
                    Player telPlayer = Bukkit.getPlayer(stream.readUTF());
                    assert telPlayer != null;
                    double x = stream.readDouble();
                    double y = stream.readDouble();
                    double z = stream.readDouble();
                    World world = WorldCreator.name(stream.readUTF()).createWorld();
                    Location location = new Location(world, x, y, z);
                    telPlayer.teleport(location);
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
