package com.mens.mutility.bungeecord.messages;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import com.mens.mutility.bungeecord.chat.PluginColors;
import com.mens.mutility.bungeecord.chat.Prefix;
import com.mens.mutility.bungeecord.chat.json.JsonBuilder;
import com.mens.mutility.bungeecord.commands.anketa.Anketa;
import com.mens.mutility.bungeecord.portal.PortalRequest;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MessageChannelListener implements Listener {
    private final MUtilityBungeeCord plugin;
    private final MessageChannel messageChannel;
    private final Prefix prefix;
    private final PluginColors colors;
    private Anketa survey;
    JsonBuilder notCreated;

    public MessageChannelListener(MUtilityBungeeCord plugin) {
        this.plugin = plugin;
        messageChannel = new MessageChannel();
        prefix = new Prefix();
        colors = new PluginColors();
        notCreated = new JsonBuilder()
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
                case "mens:send-to-overworld":
                    double x = stream.readDouble();
                    double y = stream.readDouble();
                    double z = stream.readDouble();
                    System.out.println("Souřadnice: " + x + ", " + y + ", " + z);

                    String targetStr = "";
                    if ((plugin.getConfiguration().getInt("Servers.OverWorld 1.From.X") < x) && (x < plugin.getConfiguration().getInt("Servers.OverWorld 1.To.X"))) {
                        if ((plugin.getConfiguration().getInt("Servers.OverWorld 1.From.Z") < z) && (z < plugin.getConfiguration().getInt("Servers.OverWorld 1.To.Z"))) {
                            System.out.println("Ano 1");
                            targetStr = plugin.getConfiguration().getString("Servers.OverWorld 1.Name");
                        }
                    }
                    if ((plugin.getConfiguration().getInt("Servers.OverWorld 2.From.X") < x) && (x < plugin.getConfiguration().getInt("Servers.OverWorld 2.To.X"))) {
                        if ((plugin.getConfiguration().getInt("Servers.OverWorld 2.From.Z") < z) && (z < plugin.getConfiguration().getInt("Servers.OverWorld 2.To.Z"))) {
                            System.out.println("Ano 2");
                            targetStr = plugin.getConfiguration().getString("Servers.OverWorld 2.Name");
                        }
                    }
                    if ((plugin.getConfiguration().getInt("Servers.OverWorld 3.From.X") < x) && (x < plugin.getConfiguration().getInt("Servers.OverWorld 3.To.X"))) {
                        if ((plugin.getConfiguration().getInt("Servers.OverWorld 3.From.Z") < z) && (z < plugin.getConfiguration().getInt("Servers.OverWorld 3.To.Z"))) {
                            System.out.println("Ano 3");
                            targetStr = plugin.getConfiguration().getString("Servers.OverWorld 3.Name");
                        }
                    }
                    if ((plugin.getConfiguration().getInt("Servers.OverWorld 4.From.X") < x) && (x < plugin.getConfiguration().getInt("Servers.OverWorld 4.To.X"))) {
                        if ((plugin.getConfiguration().getInt("Servers.OverWorld 4.From.Z") < z) && (z < plugin.getConfiguration().getInt("Servers.OverWorld 4.To.Z"))) {
                            System.out.println("Ano 4");
                            targetStr = plugin.getConfiguration().getString("Servers.OverWorld 4.Name");
                        }
                    }
                    ServerInfo target = ProxyServer.getInstance().getServerInfo(targetStr);
                    MUtilityBungeeCord.portalQueue.add(new PortalRequest(player, target, "mens:send-to-overworld", x, y, z));
                    player.connect(target);
                    break;

                case "mens:send-to-nether":
                    x = stream.readDouble();
                    y = stream.readDouble();
                    z = stream.readDouble();
                    targetStr = "nether";
                    target = ProxyServer.getInstance().getServerInfo(targetStr);
                    MUtilityBungeeCord.portalQueue.add(new PortalRequest(player, target, "mens:send-to-nether", x, y, z));
                    player.connect(target);
                    break;

                case "mens:join-confirmation":
                    System.out.println("Dostal jsem potvrzení");
                    for (PortalRequest pr : MUtilityBungeeCord.portalQueue) {
                        if (pr.getPlayer().getName().equals(player.getName())) {
                            System.out.println("Ano, jména sedí");
                            messageChannel.sendPortalInfoToServer(player, pr.getSubChannel(), pr.getTarget(), pr.getX(), pr.getY(), pr.getZ());
                            MUtilityBungeeCord.portalQueue.remove(pr);
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
                        notCreated.toPlayer(player);
                    }
                    break;

                case "mens:survey-start":
                    player = ProxyServer.getInstance().getPlayer(stream.readUTF());
                    if(survey != null) {
                        survey.start(player, stream.readInt(), stream.readUTF());
                    } else {
                        notCreated.toPlayer(player);
                    }
                    break;

                case "mens:survey-stop":
                    player = ProxyServer.getInstance().getPlayer(stream.readUTF());
                    if(survey != null) {
                        survey.stop(player);
                    } else {
                        notCreated.toPlayer(player);
                    }
                    break;

                case "mens:survey-vote":
                    player = ProxyServer.getInstance().getPlayer(stream.readUTF());
                    if(survey != null) {
                        survey.vote(player, stream.readInt());
                    } else {
                        notCreated.toPlayer(player);
                    }
                    break;

                case "mens:surveyPermissionResponse":
                    survey.addPermissedPlayers(stream.readUTF());
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
