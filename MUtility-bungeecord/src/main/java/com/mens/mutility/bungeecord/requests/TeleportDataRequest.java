package com.mens.mutility.bungeecord.requests;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import com.mens.mutility.bungeecord.messages.MessageChannel;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class TeleportDataRequest {
    private final ProxiedPlayer player;
    private final ServerInfo server;
    private final MUtilityBungeeCord plugin;
    private ScheduledTask st;
    private int seconds;
    private final MessageChannel messageChannel;

    public TeleportDataRequest(ProxiedPlayer player, ServerInfo server) {
        this.player = player;
        this.server = server;
        plugin = MUtilityBungeeCord.getInstance();
        seconds = 0;
        messageChannel = new MessageChannel();
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public ServerInfo getServer() {
        return server;
    }

    public void startTimer(int timeInSec) {
        st = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            if(seconds == timeInSec) {
                st.cancel();
            }
            for (ProxiedPlayer onlinePlayer : getServer().getPlayers()) {
                if(onlinePlayer.getName().equals(getPlayer().getName()) && player.isConnected()) {
                    messageChannel.sendToServer(server, "mens:teleport-data-request", player.getName());
                    st.cancel();
                }
            }
            seconds++;
        }, 0, 1, TimeUnit.SECONDS);
    }
}
