package com.mens.mutility.spigot.messages;

import com.google.common.collect.Iterables;
import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.portal.PortalManager;
import com.mens.mutility.spigot.utils.Checker;
import com.mens.mutility.spigot.utils.ServerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
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

    /**
     * Konstruktor tridy
     * @param plugin Odkaz na main tridu
     */
    public MessageChannelListener(MUtilitySpigot plugin) {
        this.plugin = plugin;
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
                    loc.setX(stream.readDouble());
                    loc.setY(stream.readDouble());
                    loc.setZ(stream.readDouble());
                    loc.setWorld(WorldCreator.name("world_nether").createWorld());
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
                    loc.setX(stream.readDouble());
                    loc.setY(stream.readDouble());
                    loc.setZ(stream.readDouble());
                    loc.setWorld(WorldCreator.name("world").createWorld());
                    pm = new PortalManager(player, loc);
                    pm.findPortal();
                    pm.createPortal();
                    if(pm.isPrepared()) {
                        telPlayerOw.teleport(pm.getPortalLocation());
                    }
                    break;
                case "mens:permissionRequest":
                    String permission = stream.readUTF();
                    String returnChannel = stream.readUTF();
                    Checker checker = new Checker();
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
                    for(String server : servers) {
                        if(server.equals(serverName)) {
                            plugin.getServers().add(new ServerInfo(server, true));
                        } else {
                            plugin.getServers().add(new ServerInfo(server, false));
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
