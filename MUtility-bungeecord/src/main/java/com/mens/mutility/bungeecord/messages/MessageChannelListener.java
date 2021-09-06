package com.mens.mutility.bungeecord.messages;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import com.mens.mutility.bungeecord.chat.PluginColors;
import com.mens.mutility.bungeecord.chat.Prefix;
import com.mens.mutility.bungeecord.chat.json.JsonBuilder;
import com.mens.mutility.bungeecord.commands.anketa.Anketa;
import com.mens.mutility.bungeecord.commands.mparticle.MParticle;
import com.mens.mutility.bungeecord.commands.mstavba.MStavbaVoteManager;
import com.mens.mutility.bungeecord.discord.DiscordManager;
import com.mens.mutility.bungeecord.requests.*;
import com.mens.mutility.bungeecord.utils.randomteleport.RandomTeleport;
import com.mens.mutility.bungeecord.utils.randomteleport.ServerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.event.EventHandler;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MessageChannelListener implements Listener {
    private final MUtilityBungeeCord plugin;
    private final MessageChannel messageChannel;
    private final PluginColors colors;
    private final DiscordManager discordManager;
    private Anketa survey;
    JsonBuilder surveyNotCreated;

    public static List<PortalRequest> portalRequests;
    public static List<TeleportRequest> teleportRequests;
    public static List<RandomTeleportRequest> rtRequests;
    public static List<TeleportDataRequest> teleportDataRequests;
    public static List<EntityPortalRequest> entityPortalRequests;
    public static List<ParticleUpdateRequest> particleUpdateRequest;

    public MessageChannelListener(MUtilityBungeeCord plugin) {
        this.plugin = plugin;
        messageChannel = new MessageChannel();
        colors = new PluginColors();
        discordManager = new DiscordManager();
        Prefix prefix = new Prefix();
        surveyNotCreated = new JsonBuilder()
                .addJsonSegment(prefix.getAnketaPrefix(true, true))
                .text(" Nejprve je nutné vytvořit anketu, použijte ")
                .color(colors.getSecondaryColorHEX())
                .text("/anketa vytvor [<Název ankety>]")
                .color(colors.getPrimaryColorHEX());

        portalRequests = new ArrayList<>();
        teleportRequests = new ArrayList<>();
        rtRequests = new ArrayList<>();
        teleportDataRequests = new ArrayList<>();
        entityPortalRequests = new ArrayList<>();
        particleUpdateRequest = new ArrayList<>();
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
                            ServerInfo target = getServerByCoords(x, z);
                            if(target != null) {
                                boolean loadPlayerData = isLoadData(target);
                                PortalRequest portalRequest = new PortalRequest(
                                        player,
                                        target,
                                        x,
                                        y,
                                        z,
                                        "world",
                                        loadPlayerData);
                                player.connect(portalRequest.getServer());
                                portalRequests.add(portalRequest);
                            }
                            break;
                        }
                        case "nether": {
                            PortalRequest portalRequest = new PortalRequest(
                                    player,
                                    ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.Nether.Name")),
                                    x,
                                    y,
                                    z,
                                    "world_nether",
                                    plugin.getConfiguration().getBoolean("Servers.Nether.LoadPlayerData"));
                            player.connect(portalRequest.getServer());
                            portalRequests.add(portalRequest);
                            break;
                        }
                        case "end": {
                            PortalRequest portalRequest = new PortalRequest(
                                    player,
                                    ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.End.Name")),
                                    x,
                                    y,
                                    z,
                                    "world_the_end",
                                    plugin.getConfiguration().getBoolean("Servers.End.LoadPlayerData"));
                            player.connect(portalRequest.getServer());
                            portalRequests.add(portalRequest);
                            break;
                        }
                        case "lobby": {
                            TeleportDataRequest telDataRequest = new TeleportDataRequest(
                                    player,
                                    ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.Lobby.Name")));
                            player.connect(telDataRequest.getServer());
                            teleportDataRequests.add(telDataRequest);
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

                case "mens:teleport-request":
                    player = ProxyServer.getInstance().getPlayer(stream.readUTF());
                    x = stream.readFloat();
                    y = stream.readFloat();
                    z = stream.readFloat();
                    world = stream.readUTF();
                    String serverName = stream.readUTF();
                    ServerInfo server;
                    if(serverName.equals("null")) {
                        server = getServerByCoords(x, z);
                    } else {
                        server = ProxyServer.getInstance().getServerInfo(serverName);
                    }
                    TeleportRequest teleportRequest = new TeleportRequest(
                            player,
                            server,
                            x,
                            y,
                            z,
                            world,
                            stream.readBoolean());
                    player.connect(teleportRequest.getServer());
                    teleportRequests.add(teleportRequest);
                    break;

                case "mens:random-teleport":
                    RandomTeleport rt = new RandomTeleport();
                    ServerData data = rt.findServer();
                    RandomTeleportRequest randomTeleportRequest = new RandomTeleportRequest(
                            ProxyServer.getInstance().getPlayer(stream.readUTF()),
                            data.getServer(),
                            data.getCenterX(),
                            data.getCenterZ(),
                            data.getRadius(),
                            data.isLoadTeleportData());
                    if(data.getServer().getPlayers().contains(randomTeleportRequest.getPlayer())) {
                        messageChannel.sendRandomTeleportRequest(randomTeleportRequest.getPlayer(), randomTeleportRequest.getCenterX(), randomTeleportRequest.getCenterZ(), randomTeleportRequest.getRadius(), randomTeleportRequest.isLoadTeleportData());
                    } else {
                        player.connect(randomTeleportRequest.getServer());
                        rtRequests.add(randomTeleportRequest);
                    }
                    break;

                case "mens:discord-navrh-create":
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Hráč " + stream.readUTF() + " přidal nový návrh");
                    embedBuilder.setDescription(stream.readUTF());
                    embedBuilder.setColor(Color.decode(colors.getPrimaryColorHEX()));
                    embedBuilder.setFooter("Hlasujte kliknutím na jednu z reakcí");
                    embedBuilder.setAuthor(stream.readUTF());
                    discordManager.sendVoteEmbedMessage(discordManager.getChannelByName(plugin.getConfiguration().getString("Discord.Rooms.Vote")), embedBuilder.build());
                    break;

                case "mens:discord-navrh-edit":
                    int id = Integer.parseInt(stream.readUTF());
                    String adminNameNullable = stream.readUTF();
                    String adminName = adminNameNullable.equals("null") ? null : adminNameNullable;
                    String rejectReasonNullable = stream.readUTF();
                    String rejectReason = rejectReasonNullable.equals("null") ? null : rejectReasonNullable;
                    boolean returned = Boolean.parseBoolean(stream.readUTF());
                    Color color;
                    try {
                        color = (Color)Class.forName("java.awt.Color").getField(stream.readUTF()).get(null);
                        editStatusNavrhyDiscordEmbed(discordManager.getChannelByName(plugin.getConfiguration().getString("Discord.Rooms.Vote")), id, color, rejectReason, adminName, returned);
                    } catch (Exception ignored) {
                    }
                    break;

                case "mens:discord-navrh-delete":
                    deleteNavrhyDiscordEmbed(discordManager.getChannelByName(plugin.getConfiguration().getString("Discord.Rooms.Vote")), Integer.parseInt(stream.readUTF()));
                    break;

                case "mens:discord-navrh-update":
                    editNavrhyDiscordEmbed(discordManager.getChannelByName(plugin.getConfiguration().getString("Discord.Rooms.Vote")), Integer.parseInt(stream.readUTF()), stream.readUTF(), stream.readUTF());
                    break;

                case "mens:start-mstavba":
                    MStavbaVoteManager manager = new MStavbaVoteManager(plugin);
                    manager.setActive(true);
                    manager.setSeasonId(Integer.parseInt(stream.readUTF()));
                    manager.startTimer();
                    break;

                case "mens:entity-portal-request":
                    String eWorld = stream.readUTF();
                    double eX = Double.parseDouble(stream.readUTF());
                    double eY = Double.parseDouble(stream.readUTF());
                    double eZ = Double.parseDouble(stream.readUTF());
                    String eType = stream.readUTF();
                    String eNBT = stream.readUTF();
                    switch(eWorld) {
                        case "overworld":
                            ServerInfo target = getServerByCoords(eX, eZ);
                            if(target != null) {
                                EntityPortalRequest request = new EntityPortalRequest(
                                        eType,
                                        eNBT,
                                        target,
                                        eX,
                                        eY,
                                        eZ,
                                        "world");
                                if(request.getServer().getPlayers().isEmpty()) {
                                    entityPortalRequests.add(request);
                                } else {
                                    messageChannel.sendToServer(request.getServer(), "mens:send-entity-to-overworld", String.valueOf(eX), String.valueOf(eY), String.valueOf(eZ), eType, eNBT);
                                }
                            }
                            break;

                        case "nether":
                            EntityPortalRequest request = new EntityPortalRequest(
                                    eType,
                                    eNBT,
                                    ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.Nether.Name")),
                                    eX,
                                    eY,
                                    eZ,
                                    "world_nether");
                            if(request.getServer().getPlayers().isEmpty()) {
                                entityPortalRequests.add(request);
                            } else {
                                messageChannel.sendToServer(request.getServer(), "mens:send-entity-to-nether", String.valueOf(eX), String.valueOf(eY), String.valueOf(eZ), eType, eNBT);
                            }
                            break;
                    }
                    break;

                case "mens:particle-place-request":
                    int particleId = Integer.parseInt(stream.readUTF());
                    boolean startParticle = Boolean.parseBoolean(stream.readUTF());
                    MParticle mParticle = new MParticle();
                    String sName = mParticle.getServerFromId(particleId);
                    ProxyServer.getInstance().getServers().values().forEach(s -> {
                        ParticleUpdateRequest request = new ParticleUpdateRequest(particleId, s, startParticle);
                        if(s.getName().equals(sName)) {
                            request.setRunClass(true);
                        }
                        if(s.getPlayers().isEmpty()) {
                            particleUpdateRequest.add(request);
                        } else {
                            messageChannel.sendToServer(s, "mens:particle-place-request", String.valueOf(request.getId()), String.valueOf(request.isStart()), String.valueOf(request.isRunClass()));
                        }
                    });
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ServerInfo getServerByCoords(double x, double z) {
        ServerInfo server = null;
        if ((((plugin.getConfiguration().getInt("Servers.OverWorld 1.Center.X") - (plugin.getConfiguration().getInt("Servers.OverWorld 1.Radius"))) < x) && (x < (plugin.getConfiguration().getInt("Servers.OverWorld 1.Center.X") + (plugin.getConfiguration().getInt("Servers.OverWorld 1.Radius"))))) && ((((plugin.getConfiguration().getInt("Servers.OverWorld 1.Center.Z") - (plugin.getConfiguration().getInt("Servers.OverWorld 1.Radius"))) < z) && (z < (plugin.getConfiguration().getInt("Servers.OverWorld 1.Center.Z") + (plugin.getConfiguration().getInt("Servers.OverWorld 1.Radius"))))))) {
            server = ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.OverWorld 1.Name"));
        } else if ((((plugin.getConfiguration().getInt("Servers.OverWorld 2.Center.X") - (plugin.getConfiguration().getInt("Servers.OverWorld 2.Radius"))) < x) && (x < (plugin.getConfiguration().getInt("Servers.OverWorld 2.Center.X") + (plugin.getConfiguration().getInt("Servers.OverWorld 2.Radius"))))) && ((((plugin.getConfiguration().getInt("Servers.OverWorld 2.Center.Z") - (plugin.getConfiguration().getInt("Servers.OverWorld 2.Radius"))) < z) && (z < (plugin.getConfiguration().getInt("Servers.OverWorld 2.Center.Z") + (plugin.getConfiguration().getInt("Servers.OverWorld 2.Radius"))))))) {
            server = ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.OverWorld 2.Name"));
        } else if ((((plugin.getConfiguration().getInt("Servers.OverWorld 3.Center.X") - (plugin.getConfiguration().getInt("Servers.OverWorld 3.Radius"))) < x) && (x < (plugin.getConfiguration().getInt("Servers.OverWorld 3.Center.X") + (plugin.getConfiguration().getInt("Servers.OverWorld 3.Radius"))))) && ((((plugin.getConfiguration().getInt("Servers.OverWorld 3.Center.Z") - (plugin.getConfiguration().getInt("Servers.OverWorld 3.Radius"))) < z) && (z < (plugin.getConfiguration().getInt("Servers.OverWorld 3.Center.Z") + (plugin.getConfiguration().getInt("Servers.OverWorld 3.Radius"))))))) {
            server = ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.OverWorld 3.Name"));
        } else if ((((plugin.getConfiguration().getInt("Servers.OverWorld 4.Center.X") - (plugin.getConfiguration().getInt("Servers.OverWorld 4.Radius"))) < x) && (x < (plugin.getConfiguration().getInt("Servers.OverWorld 4.Center.X") + (plugin.getConfiguration().getInt("Servers.OverWorld 4.Radius"))))) && ((((plugin.getConfiguration().getInt("Servers.OverWorld 4.Center.Z") - (plugin.getConfiguration().getInt("Servers.OverWorld 4.Radius"))) < z) && (z < (plugin.getConfiguration().getInt("Servers.OverWorld 4.Center.Z") + (plugin.getConfiguration().getInt("Servers.OverWorld 4.Radius"))))))) {
            server = ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.OverWorld 4.Name"));
        }
        return server;
    }

    private boolean isLoadData(ServerInfo server) {
        if(server.getName().equalsIgnoreCase(plugin.getConfiguration().getString("Servers.OverWorld 1.Name"))) {
            return plugin.getConfiguration().getBoolean("Servers.OverWorld 1.LoadPlayerData");
        } else if(server.getName().equalsIgnoreCase(plugin.getConfiguration().getString("Servers.OverWorld 2.Name"))) {
            return plugin.getConfiguration().getBoolean("Servers.OverWorld 2.LoadPlayerData");
        } else if(server.getName().equalsIgnoreCase(plugin.getConfiguration().getString("Servers.OverWorld 3.Name"))) {
            return plugin.getConfiguration().getBoolean("Servers.OverWorld 3.LoadPlayerData");
        } else if(server.getName().equalsIgnoreCase(plugin.getConfiguration().getString("Servers.OverWorld 4.Name"))) {
            return plugin.getConfiguration().getBoolean("Servers.OverWorld 4.LoadPlayerData");
        } else if(server.getName().equalsIgnoreCase(plugin.getConfiguration().getString("Servers.Lobby.Name"))) {
            return plugin.getConfiguration().getBoolean("Servers.Lobby.LoadPlayerData");
        } else if(server.getName().equalsIgnoreCase(plugin.getConfiguration().getString("Servers.Nether.Name"))) {
            return plugin.getConfiguration().getBoolean("Servers.Nether.LoadPlayerData");
        } else if(server.getName().equalsIgnoreCase(plugin.getConfiguration().getString("Servers.End.Name"))) {
            return plugin.getConfiguration().getBoolean("Servers.End.LoadPlayerData");
        } else if(server.getName().equalsIgnoreCase(plugin.getConfiguration().getString("Servers.Event.Name"))) {
            return plugin.getConfiguration().getBoolean("Servers.Event.LoadPlayerData");
        } else {
            return false;
        }
    }

    private void editStatusNavrhyDiscordEmbed(net.dv8tion.jda.api.entities.MessageChannel channel, int id, Color color, String rejectReason, String adminName, boolean returned) {
        Optional<Message> messageOpt = channel.getIterableHistory().stream().filter(x -> !x.getEmbeds().isEmpty()
                && x.getEmbeds().get(0).getAuthor() != null
                && Objects.equals(Objects.requireNonNull(x.getEmbeds().get(0).getAuthor()).getName(), String.valueOf(id))).findAny();
        if(messageOpt.isPresent()) {
            Message message = messageOpt.get();
            MessageEmbed origEmbed = message.getEmbeds().get(0);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor(Objects.requireNonNull(origEmbed.getAuthor()).getName());
            builder.setDescription(origEmbed.getDescription());
            builder.setTitle(origEmbed.getTitle());
            builder.setColor(color);
            List<MessageEmbed.Field> fields = origEmbed.getFields();
            if(!fields.isEmpty()) {
                fields.forEach(field -> {
                    if(!Objects.equals(field.getName(), "Status")
                            && !Objects.equals(field.getName(), "Důvod zamítnutí")
                            && !Objects.equals(field.getName(), "Zamítnul(a)")
                            && !Objects.equals(field.getName(), "Schválil(a)")) {
                        builder.addField(field.getName(), field.getValue(), field.isInline());
                    }
                });
            }
            if(origEmbed.getFooter() != null) {
                builder.setFooter(origEmbed.getFooter().getText());
            }
            if(!returned) {
                if(rejectReason != null) {
                    builder.addField("Status", "Zamítnuto", true);
                    builder.addField("Důvod zamítnutí", rejectReason, true);
                    builder.addField("Zamítnul(a)", adminName, false);
                } else {
                    builder.addField("Status", "Schváleno", true);
                    builder.addField("Schválil(a)", adminName, true);
                }
            }
            message.editMessageEmbeds(builder.build()).queue();
        }
    }

    private void editNavrhyDiscordEmbed(net.dv8tion.jda.api.entities.MessageChannel channel, int id, String content, String name) {
        Optional<Message> messageOpt = channel.getIterableHistory().stream().filter(x -> !x.getEmbeds().isEmpty()
                && x.getEmbeds().get(0).getAuthor() != null
                && Objects.equals(Objects.requireNonNull(x.getEmbeds().get(0).getAuthor()).getName(), String.valueOf(id))).findAny();
        if(messageOpt.isPresent()) {
            Message message = messageOpt.get();
            MessageEmbed origEmbed = message.getEmbeds().get(0);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor(Objects.requireNonNull(origEmbed.getAuthor()).getName());
            builder.setDescription(content);
            builder.setTitle("Hráč " + name + " upravil návrh!");
            builder.setColor(Color.ORANGE);
            builder.addField("Původní návrh", origEmbed.getDescription(), false);
            if(origEmbed.getFooter() != null) {
                builder.setFooter(origEmbed.getFooter().getText());
            }
            message.editMessageEmbeds(builder.build()).queue();
        }
    }

    private void deleteNavrhyDiscordEmbed(net.dv8tion.jda.api.entities.MessageChannel channel, int id) {
        Optional<Message> messageOpt = channel.getIterableHistory().stream().filter(x -> !x.getEmbeds().isEmpty()
                && x.getEmbeds().get(0).getAuthor() != null
                && Objects.equals(Objects.requireNonNull(x.getEmbeds().get(0).getAuthor()).getName(), String.valueOf(id))).findAny();
        if(messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.delete().queue();
        }
    }
}
