package com.mens.mutility.bungeecord.eventhandlers;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import com.mens.mutility.bungeecord.commands.mstavba.MStavbaVoteManager;
import com.mens.mutility.bungeecord.messages.MessageChannel;
import com.mens.mutility.bungeecord.messages.MessageChannelListener;
import com.mens.mutility.bungeecord.requests.*;
import com.mens.mutility.bungeecord.utils.Response;
import com.mens.mutility.bungeecord.utils.Timer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class OnServerSwitchEvent implements Listener {
    private final MUtilityBungeeCord plugin;
    private final MessageChannel messageChannel;
    private final Response response;
    private ScheduledTask st;

    public OnServerSwitchEvent() {
        plugin = MUtilityBungeeCord.getInstance();
        messageChannel = new MessageChannel();
        response = new Response();
    }

    @EventHandler
    public void OnServerSwitch(ServerSwitchEvent event) {
        response.sendServerInfoResponse(event.getPlayer());
        response.broadcastPlayersInfo(event.getPlayer(), false);

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

            List<EntityPortalRequest> requests = MessageChannelListener.entityPortalRequests.stream().filter(request -> request.getServer().getName().equals(event.getPlayer().getServer().getInfo().getName())).collect(Collectors.toList());
            requests.forEach(request -> {
                String subChannel = "mens:send-entity-to-";
                switch (request.getWorld()) {
                    case "world":
                        subChannel += "overworld";
                        break;
                    case "world_nether":
                        subChannel += "nether";
                        break;
                }
                messageChannel.sendToServer(request.getServer(), subChannel, String.valueOf(request.getX()), String.valueOf(request.getY()), String.valueOf(request.getZ()), request.getEntityTypeName(), request.getNbt());
                MessageChannelListener.entityPortalRequests.remove(request);
            });
        } else {
            MStavbaVoteManager manager = new MStavbaVoteManager(plugin);
            if(manager.isActive()) {
                Timer timer = new Timer();
                timer.setOnFinish((sec, tt) -> manager.createVoteLink(event.getPlayer()));
                timer.startTimer(20);
            }
            messageChannel.sendToServer(event.getPlayer(), "mens:mstavba-request");
        }

        List<ParticleUpdateRequest> requests = MessageChannelListener.particleUpdateRequest.stream().filter(request -> request.getServer().getName().equals(event.getPlayer().getServer().getInfo().getName())).collect(Collectors.toList());
        requests.forEach(request -> {
            messageChannel.sendToServer(request.getServer(), "mens:particle-place-request", String.valueOf(request.getId()), String.valueOf(request.isStart()), String.valueOf(request.isRunClass()));
            MessageChannelListener.particleUpdateRequest.remove(request);
        });
    }
}
