package com.mens.mutility.bungeecord.events;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import com.mens.mutility.bungeecord.messages.MessageChannel;
import com.mens.mutility.bungeecord.messages.MessageChannelListener;
import com.mens.mutility.bungeecord.requests.PortalRequest;
import com.mens.mutility.bungeecord.requests.RandomTeleportRequest;
import com.mens.mutility.bungeecord.requests.TeleportDataRequest;
import com.mens.mutility.bungeecord.requests.TeleportRequest;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class OnServerSwitchEvent implements Listener {
    private final MUtilityBungeeCord plugin;
    private final MessageChannel messageChannel;
    private ScheduledTask st;

    public OnServerSwitchEvent() {
        plugin = MUtilityBungeeCord.getInstance();
        messageChannel = new MessageChannel();
    }

    @EventHandler
    public void OnServerSwitch(ServerSwitchEvent event) {
        sendServerInfoResponse(event.getPlayer());
        if(event.getFrom() != null) {
            Optional<TeleportRequest> optTeleportRequests = MessageChannelListener.teleportRequests.stream().filter(request -> request.getPlayer().getName().equals(event.getPlayer().getName())).findFirst();
            if(optTeleportRequests.isPresent()) {
                TeleportRequest request = optTeleportRequests.get();
                messageChannel.sendTeleportRequest(request.getPlayer(), request.getX(), request.getY(), request.getZ(), request.isLoadTeleportData(), request.getWorld());
                MessageChannelListener.teleportRequests.remove(request);
            }

            Optional<RandomTeleportRequest> optRTTeleportRequests = MessageChannelListener.rtRequests.stream().filter(request -> request.getPlayer().getName().equals(event.getPlayer().getName())).findFirst();
            if(optRTTeleportRequests.isPresent()) {
                RandomTeleportRequest request = optRTTeleportRequests.get();
                st = plugin.getProxy().getScheduler().schedule(plugin, () -> {
                    messageChannel.sendRandomTeleportRequest(request.getPlayer(), request.getCenterX(), request.getCenterZ(), request.getRadius(), request.isLoadTeleportData());
                    MessageChannelListener.rtRequests.remove(request);
                    st.cancel();
                }, 1000, 1, TimeUnit.MILLISECONDS);
            }

            Optional<TeleportDataRequest> optTeleportDataRequests = MessageChannelListener.teleportDataRequests.stream().filter(request -> request.getPlayer().getName().equals(event.getPlayer().getName())).findFirst();
            if(optTeleportDataRequests.isPresent()) {
                TeleportDataRequest request = optTeleportDataRequests.get();
                messageChannel.sendToServer(request.getPlayer(), "mens:teleport-data-request");
                MessageChannelListener.teleportDataRequests.remove(request);
            }
        }

        Optional<PortalRequest> optPortalRequests = MessageChannelListener.portalRequests.stream().filter(request -> request.getPlayer().getName().equals(event.getPlayer().getName())).findFirst();
        if(optPortalRequests.isPresent()) {
            PortalRequest request = optPortalRequests.get();
            String subChannel = "mens:send-to-";
            switch (request.getWorld()) {
                case "world":
                    subChannel += "overworld";
                    break;
                case "world_nether":
                    subChannel += "nether";
                    break;
                case "world_the_end":
                    subChannel += "end";
                    break;
            }
            messageChannel.sendPortalInfoToServer(request.getPlayer(), subChannel, request.getX(), request.getY(), request.getZ(), request.isLoadTeleportData());
            MessageChannelListener.portalRequests.remove(request);
        }
    }

    private void sendServerInfoResponse(ProxiedPlayer player) {
        StringBuilder servers = new StringBuilder();
        StringBuilder borders = new StringBuilder();
        StringBuilder rtLoc = new StringBuilder();
        for(ServerInfo server : plugin.getProxy().getServers().values()) {
            servers.append(server.getName()).append(";");
            boolean isOw = false;
            String configServerName = "";
            if(player.getServer().getInfo().getName().equals(server.getName())) {
                if(server.getName().equals(plugin.getConfiguration().getString("RandomTeleport.Server"))) {
                    rtLoc.append(plugin.getConfiguration().getInt("RandomTeleport.From.X")).append(";");
                    rtLoc.append(plugin.getConfiguration().getInt("RandomTeleport.To.X")).append(";");
                    rtLoc.append(plugin.getConfiguration().getInt("RandomTeleport.From.Y")).append(";");
                    rtLoc.append(plugin.getConfiguration().getInt("RandomTeleport.To.Y")).append(";");
                    rtLoc.append(plugin.getConfiguration().getInt("RandomTeleport.From.Z")).append(";");
                    rtLoc.append(plugin.getConfiguration().getInt("RandomTeleport.To.Z"));
                }
                if(server.getName().equals(plugin.getConfiguration().getString("Servers.OverWorld 1.Name"))) {
                    isOw = true;
                    configServerName = "OverWorld 1";
                } else if(server.getName().equals(plugin.getConfiguration().getString("Servers.OverWorld 2.Name"))) {
                    isOw = true;
                    configServerName = "OverWorld 2";
                } else if(server.getName().equals(plugin.getConfiguration().getString("Servers.OverWorld 3.Name"))) {
                    isOw = true;
                    configServerName = "OverWorld 3";
                } else if(server.getName().equals(plugin.getConfiguration().getString("Servers.OverWorld 4.Name"))) {
                    isOw = true;
                    configServerName = "OverWorld 4";
                }
                if(isOw) {
                    borders.append(plugin.getConfiguration().getInt("Servers." + configServerName + ".TP border 1.From.X")).append(";");
                    borders.append(plugin.getConfiguration().getInt("Servers." + configServerName + ".TP border 1.To.X")).append(";");
                    borders.append(plugin.getConfiguration().getInt("Servers." + configServerName + ".TP border 1.From.Z")).append(";");
                    borders.append(plugin.getConfiguration().getInt("Servers." + configServerName + ".TP border 1.To.Z")).append(";");
                    borders.append(plugin.getConfiguration().getString("Servers." + configServerName + ".TP border 1.Direction")).append(";");
                    borders.append(plugin.getConfiguration().getInt("Servers." + configServerName + ".TP border 2.From.X")).append(";");
                    borders.append(plugin.getConfiguration().getInt("Servers." + configServerName + ".TP border 2.To.X")).append(";");
                    borders.append(plugin.getConfiguration().getInt("Servers." + configServerName + ".TP border 2.From.Z")).append(";");
                    borders.append(plugin.getConfiguration().getInt("Servers." + configServerName + ".TP border 2.To.Z")).append(";");
                    borders.append(plugin.getConfiguration().getString("Servers." + configServerName + ".TP border 2.Direction"));
                }
                break;
            }
        }
        servers.substring(0, servers.length() - 1);
        messageChannel.sendToServer(player, "mens:servers-info-response", servers.toString(), borders.toString(), rtLoc.toString(), player.getServer().getInfo().getName());
    }
}
