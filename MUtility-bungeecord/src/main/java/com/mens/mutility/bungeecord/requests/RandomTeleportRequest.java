package com.mens.mutility.bungeecord.requests;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import com.mens.mutility.bungeecord.messages.MessageChannel;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class RandomTeleportRequest {
    private final ProxiedPlayer player;
    private final double centerX;
    private final double centerZ;
    private final int radius;
    private final ServerInfo server;
    private final boolean loadTeleportData;
    private final MUtilityBungeeCord plugin;
    private ScheduledTask st;
    private int seconds;
    private final MessageChannel messageChannel;

    public RandomTeleportRequest(ProxiedPlayer player, double centerX, double centerZ, int radius, ServerInfo server, boolean loadTeleportData) {
        this.player = player;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.radius = radius;
        this.server = server;
        this.loadTeleportData = loadTeleportData;
        plugin = MUtilityBungeeCord.getInstance();
        seconds = 0;
        messageChannel = new MessageChannel();
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterZ() {
        return centerZ;
    }

    public int getRadius() {
        return radius;
    }

    public ServerInfo getServer() {
        return server;
    }

    public boolean isLoadTeleportData() {
        return loadTeleportData;
    }

    public void startTimer(int timeInSec) {
        st = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            if(seconds == timeInSec) {
                st.cancel();
            }
            for (ProxiedPlayer onlinePlayer : server.getPlayers()) {
                if(onlinePlayer.getName().equals(player.getName()) && player.isConnected()) {
                    messageChannel.sendRandomTeleportRequest(server, player.getName(), centerX, centerZ, radius, loadTeleportData);
                    st.cancel();
                }
            }
            seconds++;
        }, 0, 1, TimeUnit.SECONDS);
    }
}
