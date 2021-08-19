package com.mens.mutility.bungeecord.utils;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import com.mens.mutility.bungeecord.messages.MessageChannel;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Response {
    private final MUtilityBungeeCord plugin;
    private final MessageChannel messageChannel;

    public Response() {
        plugin = MUtilityBungeeCord.getInstance();
        messageChannel = new MessageChannel();
    }

    public void sendServerInfoResponse(ProxiedPlayer player) {
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

    public void broadcastPlayersInfo(ProxiedPlayer player, boolean onDisconnect) {
        StringBuilder players = new StringBuilder();
        plugin.getProxy().getServers().values().forEach(server -> {
            for(ProxiedPlayer playerLoc : server.getPlayers()) {
                if(playerLoc.getName().equals(player.getName())) {
                    if(!onDisconnect) {
                        players.append(playerLoc.getName()).append(";");
                    }
                } else {
                    players.append(playerLoc.getName()).append(";");
                }
            }
        });
        if(!players.toString().isEmpty()) {
            players.substring(0, players.length() - 1);
        }
        messageChannel.broadcastMessage("mens:players-info", players.toString());
    }
}
