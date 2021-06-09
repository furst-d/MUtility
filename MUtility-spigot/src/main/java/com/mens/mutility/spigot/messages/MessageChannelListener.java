package com.mens.mutility.spigot.messages;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.portal.PortalManager;
import com.mens.mutility.spigot.portal.PortalRequestChecker;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;


/**
 * Trida zajistujici komunikaci mezi pluginy na serverech v ramci BungeeCord
 */
public class MessageChannelListener implements PluginMessageListener {

    private final MUtilitySpigot plugin;
    private PortalRequestChecker checker;

    /**
     * Konstruktor tridy
     * @param plugin Odkaz na main tridu
     */
    public MessageChannelListener(MUtilitySpigot plugin) {
        this.plugin = plugin;
        checker = new PortalRequestChecker();
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        try {
            DataInputStream stream = new DataInputStream(new ByteArrayInputStream(message));
            String subChannel = stream.readUTF();
            if(subChannel.equals("mens:send-to-nether")) {
                Location loc = player.getLocation();
                loc.setX(stream.readDouble());
                loc.setY(stream.readDouble());
                loc.setZ(stream.readDouble());
                loc.setWorld(WorldCreator.name("world_nether").createWorld());
                PortalManager pm = new PortalManager(player, loc);
                pm.findPortal();
                pm.createPortal();
                if(pm.isPrepared()) {
                    MUtilitySpigot.portalQueue.add(pm);
                    checker = new PortalRequestChecker();
                }
            }
            if(subChannel.equals("mens:send-to-overworld")) {
                Location loc = player.getLocation();
                loc.setX(stream.readDouble());
                loc.setY(stream.readDouble());
                loc.setZ(stream.readDouble());
                loc.setWorld(WorldCreator.name("world").createWorld());
                PortalManager pm = new PortalManager(player, loc);
                pm.findPortal();
                pm.createPortal();
                if(pm.isPrepared()) {
                    MUtilitySpigot.portalQueue.add(pm);
                    checker.checkRequests();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
