package com.mens.mutility.spigot.messages;

import com.google.common.collect.Iterables;
import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.chat.json.JsonBuilder;
import com.mens.mutility.spigot.commands.commands.tpdata.Tpdata;
import com.mens.mutility.spigot.inventory.TeleportData;
import com.mens.mutility.spigot.portal.PortalManager;
import com.mens.mutility.spigot.utils.AreaInfo;
import com.mens.mutility.spigot.utils.BorderInfo;
import com.mens.mutility.spigot.utils.Checker;
import com.mens.mutility.spigot.utils.ServerInfo;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;


/**
 * Trida zajistujici komunikaci mezi pluginy na serverech v ramci BungeeCord
 */
public class MessageChannelListener implements PluginMessageListener {

    private final MUtilitySpigot plugin;
    private final Tpdata teleportDataManager;
    private final PluginColors colors;
    private final Prefix prefix;

    /**
     * Konstruktor tridy
     * @param plugin Odkaz na main tridu
     */
    public MessageChannelListener(MUtilitySpigot plugin) {
        this.plugin = plugin;
        teleportDataManager = new Tpdata(plugin);
        colors = new PluginColors();
        prefix = new Prefix();
    }

    @Override
    public void onPluginMessageReceived(@Nonnull String channel, @Nonnull Player player, @Nonnull byte[] message) {
        try {
            DataInputStream stream = new DataInputStream(new ByteArrayInputStream(message));
            String subChannel = stream.readUTF();
            switch(subChannel) {
                case "mens:send-to-nether":
                    Location loc = player.getLocation();
                    boolean loadDataNe = stream.readBoolean();
                    double xNe = stream.readDouble();
                    double yNe = stream.readDouble();
                    double zNe = stream.readDouble();
                    String worldNe = "world_nether";
                    loc.setX(xNe);
                    loc.setY(yNe);
                    loc.setZ(zNe);
                    loc.setWorld(WorldCreator.name(worldNe).createWorld());
                    if(loadDataNe) {
                        teleportDataManager.applyData(player, teleportDataManager.loadNewestPlayerData(player), xNe, yNe, zNe, worldNe);
                    }
                    PortalManager pm = new PortalManager(player, loc);
                    pm.findPortal();
                    pm.createPortal();
                    if(pm.isPrepared()) {
                        player.teleport(pm.getPortalLocation());
                    }
                    break;

                case "mens:send-to-overworld":
                    loc = player.getLocation();
                    boolean loadDataOw = stream.readBoolean();
                    double xOw = stream.readDouble();
                    double yOw = stream.readDouble();
                    double zOw = stream.readDouble();
                    String worldOw = "world";
                    loc.setX(xOw);
                    loc.setY(yOw);
                    loc.setZ(zOw);
                    loc.setWorld(WorldCreator.name(worldOw).createWorld());
                    if(loadDataOw) {
                        teleportDataManager.applyData(player, teleportDataManager.loadNewestPlayerData(player), xOw, yOw, zOw, worldOw);
                    }
                    pm = new PortalManager(player, loc);
                    pm.findPortal();
                    pm.createPortal();
                    if(pm.isPrepared()) {
                        player.teleport(pm.getPortalLocation());
                    }
                    break;

                case "mens:send-to-end":
                    loc = player.getLocation();
                    boolean loadDataEnd = stream.readBoolean();
                    double xEnd = 98;
                    double yEnd = 48;
                    double zEnd = -2;
                    String worldEnd = "world_the_end";
                    loc.setX(xEnd);
                    loc.setY(yEnd);
                    loc.setZ(zEnd);
                    loc.setWorld(WorldCreator.name(worldEnd).createWorld());
                    if(loadDataEnd) {
                        teleportDataManager.applyData(player, teleportDataManager.loadNewestPlayerData(player), xEnd, yEnd, zEnd, worldEnd);
                    }
                    pm = new PortalManager(player, loc);
                    if(pm.createEndPlatform()) {
                        player.teleport(pm.getEndPlatformLocation());
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
                    MessageChannel messageChannel = new MessageChannel();
                    player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
                    messageChannel.sendToBungeeCord(player, returnChannel, permPlayers.toString());
                    break;

                case "mens:servers-info-response":
                    if(plugin.getServers().isEmpty()) {
                        String[] servers = stream.readUTF().split(";");
                        String[] borders = stream.readUTF().split(";");
                        String[] rtLoc = stream.readUTF().split(";");
                        String serverName = stream.readUTF();
                        plugin.getServers().clear();
                        for(String serverLoc : servers) {
                            if(serverLoc.equals(serverName)) {
                                ServerInfo info = new ServerInfo(serverLoc, true);
                                if(borders.length > 1) {
                                    info.setBorder1(new BorderInfo(Integer.parseInt(borders[0]), Integer.parseInt(borders[1]), -64, 320, Integer.parseInt(borders[2]), Integer.parseInt(borders[3]), borders[4]));
                                    info.setBorder2(new BorderInfo(Integer.parseInt(borders[5]), Integer.parseInt(borders[6]), -64, 320, Integer.parseInt(borders[7]), Integer.parseInt(borders[8]), borders[9]));
                                }
                                if(rtLoc.length > 1) {
                                    info.setRandomTeleport(new AreaInfo(Integer.parseInt(rtLoc[0]), Integer.parseInt(rtLoc[1]), Integer.parseInt(rtLoc[2]), Integer.parseInt(rtLoc[3]), Integer.parseInt(rtLoc[4]), Integer.parseInt(rtLoc[5])));
                                }
                                plugin.getServers().add(info);
                            } else {
                                plugin.getServers().add(new ServerInfo(serverLoc, false));
                            }
                        }
                    }
                    break;

                case "mens:teleport-request":
                    double x = stream.readDouble();
                    double y = stream.readDouble();
                    double z = stream.readDouble();
                    boolean loadDataTel = stream.readBoolean();
                    World world = WorldCreator.name(stream.readUTF()).createWorld();
                    Location location = new Location(world, x, y, z);
                    if(loadDataTel) {
                        assert world != null;
                        TeleportData data = teleportDataManager.loadNewestPlayerData(player);
                        teleportDataManager.applyData(player, data, x, y, z, world.getName());
                    }
                    player.teleport(location);
                    break;

                case "mens:teleport-data-request":
                    teleportDataManager.applyData(player, teleportDataManager.loadNewestPlayerData(player), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), Objects.requireNonNull(player.getLocation().getWorld()).getName());
                    break;

                case "mens:random-teleport-request":
                    double centerX = stream.readDouble();
                    double centerZ = stream.readDouble();
                    int radius = stream.readInt();
                    boolean loadDataRt = stream.readBoolean();
                    if(loadDataRt) {
                        teleportDataManager.applyData(player, teleportDataManager.loadNewestPlayerData(player), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), Objects.requireNonNull(player.getLocation().getWorld()).getName());
                    }
                    JsonBuilder jb = new JsonBuilder()
                            .addJsonSegment(prefix.getRandomTeleportPrefix(true, true))
                            .text(": Pokud jsi byl portnut do země nebo se ti lokace nelíbí, klikni ")
                            .color(colors.getSecondaryColorHEX())
                            .text("➥Zde")
                            .color(colors.getPrimaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, new JsonBuilder()
                                    .text(">> Klikni pro ")
                                    .color(colors.getSecondaryColorHEX())
                                    .text("Teleportaci ")
                                    .color(colors.getPrimaryColorHEX())
                                    .text("na spawn <<")
                                    .color(colors.getSecondaryColorHEX())
                                    .toString() ,true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/spawn");
                    jb.toPlayer(player);
                    /*loc = player.getLocation();
                    loc.setX((int)(Math.random() * ((centerX + radius) - (centerX - radius)) + (centerX - radius)));*/
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers " + centerX + " " + centerZ + " 500 " + radius + " false " + player.getName());
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
