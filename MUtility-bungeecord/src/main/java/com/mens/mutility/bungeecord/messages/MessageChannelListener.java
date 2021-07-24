package com.mens.mutility.bungeecord.messages;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import com.mens.mutility.bungeecord.chat.PluginColors;
import com.mens.mutility.bungeecord.chat.Prefix;
import com.mens.mutility.bungeecord.chat.json.JsonBuilder;
import com.mens.mutility.bungeecord.commands.anketa.Anketa;
import com.mens.mutility.bungeecord.requests.PortalRequest;
import com.mens.mutility.bungeecord.requests.TeleportDataRequest;
import com.mens.mutility.bungeecord.requests.TeleportRequest;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MessageChannelListener implements Listener {
    private final MUtilityBungeeCord plugin;
    private final MessageChannel messageChannel;
    private Anketa survey;
    JsonBuilder surveyNotCreated;
    StringBuilder servers;
    ServerInfo target;
    private ScheduledTask st;

    public MessageChannelListener(MUtilityBungeeCord plugin) {
        this.plugin = plugin;
        messageChannel = new MessageChannel();
        Prefix prefix = new Prefix();
        PluginColors colors = new PluginColors();
        surveyNotCreated = new JsonBuilder()
                .addJsonSegment(prefix.getAnketaPrefix(true, true))
                .text(" Nejprve je nutné vytvořit anketu, použijte ")
                .color(colors.getSecondaryColorHEX())
                .text("/anketa vytvor [<Název ankety>]")
                .color(colors.getPrimaryColorHEX());
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if(!event.getTag().equalsIgnoreCase("BungeeCord")) return;
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(event.getData()));
        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
        try {
            String channel = stream.readUTF();
            switch (channel) {
                case "mens:portalRequest":
                    String world = stream.readUTF();
                    double x = stream.readDouble();
                    double y = stream.readDouble();
                    double z = stream.readDouble();
                    switch (world) {
                        case "overworld": {
                            String targetStr = "";
                            boolean loadPlayerData = false;
                            if ((plugin.getConfiguration().getInt("Servers.OverWorld 1.From.X") < x) && (x < plugin.getConfiguration().getInt("Servers.OverWorld 1.To.X"))) {
                                if ((plugin.getConfiguration().getInt("Servers.OverWorld 1.From.Z") < z) && (z < plugin.getConfiguration().getInt("Servers.OverWorld 1.To.Z"))) {
                                    targetStr = plugin.getConfiguration().getString("Servers.OverWorld 1.Name");
                                    loadPlayerData = plugin.getConfiguration().getBoolean("Servers.OverWorld 1.LoadPlayerData");
                                }
                            } else if ((plugin.getConfiguration().getInt("Servers.OverWorld 2.From.X") < x) && (x < plugin.getConfiguration().getInt("Servers.OverWorld 2.To.X"))) {
                                if ((plugin.getConfiguration().getInt("Servers.OverWorld 2.From.Z") < z) && (z < plugin.getConfiguration().getInt("Servers.OverWorld 2.To.Z"))) {
                                    targetStr = plugin.getConfiguration().getString("Servers.OverWorld 2.Name");
                                    loadPlayerData = plugin.getConfiguration().getBoolean("Servers.OverWorld 2.LoadPlayerData");
                                }
                            } else if ((plugin.getConfiguration().getInt("Servers.OverWorld 3.From.X") < x) && (x < plugin.getConfiguration().getInt("Servers.OverWorld 3.To.X"))) {
                                if ((plugin.getConfiguration().getInt("Servers.OverWorld 3.From.Z") < z) && (z < plugin.getConfiguration().getInt("Servers.OverWorld 3.To.Z"))) {
                                    targetStr = plugin.getConfiguration().getString("Servers.OverWorld 3.Name");
                                    loadPlayerData = plugin.getConfiguration().getBoolean("Servers.OverWorld 3.LoadPlayerData");
                                }
                            } else if ((plugin.getConfiguration().getInt("Servers.OverWorld 4.From.X") < x) && (x < plugin.getConfiguration().getInt("Servers.OverWorld 4.To.X"))) {
                                if ((plugin.getConfiguration().getInt("Servers.OverWorld 4.From.Z") < z) && (z < plugin.getConfiguration().getInt("Servers.OverWorld 4.To.Z"))) {
                                    targetStr = plugin.getConfiguration().getString("Servers.OverWorld 4.Name");
                                    loadPlayerData = plugin.getConfiguration().getBoolean("Servers.OverWorld 4.LoadPlayerData");
                                }
                            }
                            PortalRequest portalRequest = new PortalRequest(
                                    player,
                                    x,
                                    y,
                                    z,
                                    "world",
                                    ProxyServer.getInstance().getServerInfo(targetStr),
                                    loadPlayerData);
                            player.connect(portalRequest.getServer(), (result, error) -> {
                                if (result) {
                                    portalRequest.startTimer(20);
                                }
                            });
                            break;
                        }
                        case "nether": {
                            PortalRequest portalRequest = new PortalRequest(
                                    player,
                                    x,
                                    y,
                                    z,
                                    "world_nether",
                                    ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.Nether.Name")),
                                    plugin.getConfiguration().getBoolean("Servers.Nether.LoadPlayerData"));
                            player.connect(portalRequest.getServer(), (result, error) -> {
                                if (result) {
                                    portalRequest.startTimer(20);
                                }
                            });
                            break;
                        }
                        case "end": {
                            PortalRequest portalRequest = new PortalRequest(
                                    player,
                                    x,
                                    y,
                                    z,
                                    "world_the_end",
                                    ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.End.Name")),
                                    plugin.getConfiguration().getBoolean("Servers.End.LoadPlayerData"));
                            player.connect(portalRequest.getServer(), (result, error) -> {
                                if (result) {
                                    portalRequest.startTimer(20);
                                }
                            });
                            break;
                        }
                        case "lobby": {
                            TeleportDataRequest telDataRequest = new TeleportDataRequest(
                                    player,
                                    ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.Lobby.Name")));
                            player.connect(telDataRequest.getServer(), (result, error) -> {
                                if (result) {
                                    telDataRequest.startTimer(20);
                                }
                            });
                            break;
                        }
                    }
                    break;

                case "mens:survey-create":
                    survey = new Anketa();
                    survey.create(stream.readUTF(), ProxyServer.getInstance().getPlayer(stream.readUTF()));
                    break;

                case "mens:survey-add":
                    player = ProxyServer.getInstance().getPlayer(stream.readUTF());
                    if(survey != null) {
                        survey.add(player, stream.readUTF());
                    } else {
                        surveyNotCreated.toPlayer(player);
                    }
                    break;

                case "mens:survey-start":
                    player = ProxyServer.getInstance().getPlayer(stream.readUTF());
                    if(survey != null) {
                        survey.start(player, stream.readInt(), stream.readUTF());
                    } else {
                        surveyNotCreated.toPlayer(player);
                    }
                    break;

                case "mens:survey-stop":
                    player = ProxyServer.getInstance().getPlayer(stream.readUTF());
                    if(survey != null) {
                        survey.stop(player);
                    } else {
                        surveyNotCreated.toPlayer(player);
                    }
                    break;

                case "mens:survey-vote":
                    player = ProxyServer.getInstance().getPlayer(stream.readUTF());
                    if(survey != null) {
                        survey.vote(player, stream.readInt());
                    } else {
                        surveyNotCreated.toPlayer(player);
                    }
                    break;

                case "mens:surveyPermissionResponse":
                    survey.addPermissedPlayers(stream.readUTF());
                    break;
                case "mens:broadcast-json":
                    String json = stream.readUTF();
                    for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                        onlinePlayer.sendMessage(ComponentSerializer.parse(json));
                    }
                    break;
                case "mens:servers-info-request":
                    servers = new StringBuilder();
                    player = ProxyServer.getInstance().getPlayer(stream.readUTF());
                    target = (ServerInfo) plugin.getProxy().getServers().values().toArray()[0];
                    for(ServerInfo server : plugin.getProxy().getServers().values()) {
                        servers.append(server.getName());
                        servers.append(";");
                        if(player.getServer().getInfo().getName().equals(server.getName())) {
                            target = ProxyServer.getInstance().getServerInfo(server.getName());
                        }
                    }
                    servers.substring(0, servers.length() - 1);
                    messageChannel.sendToServer(target, "mens:servers-info-response", servers.toString(), target.getName());
                    break;
                case "mens:teleport-request":
                    TeleportRequest teleportRequest = new TeleportRequest(
                            ProxyServer.getInstance().getPlayer(stream.readUTF()),
                            stream.readFloat(),
                            stream.readFloat(),
                            stream.readFloat(),
                            stream.readUTF(),
                            ProxyServer.getInstance().getServerInfo(stream.readUTF()),
                            stream.readBoolean());
                    player.connect(teleportRequest.getServer(), (result, error) -> {
                        if(result) {
                            teleportRequest.startTimer(10000);
                        }
                    });
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
