package com.mens.mutility.bungeecord.eventhandlers;

import com.mens.mutility.bungeecord.utils.Response;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;


public class OnPlayerDisconnectEvent implements Listener {
    private final Response response;

    public OnPlayerDisconnectEvent() {
        response = new Response();
    }

    @EventHandler
    public void OnPlayerDisconnect(PlayerDisconnectEvent event) {
        response.broadcastPlayersInfo(event.getPlayer(), true);
    }

}
