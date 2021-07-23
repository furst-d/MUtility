package com.mens.mutility.bungeecord.utils.portal;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import com.mens.mutility.bungeecord.messages.MessageChannel;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class PortalRequest {
    private final ProxiedPlayer player;
    private final double x;
    private final double y;
    private final double z;
    private final String world;
    private final ServerInfo server;
    private final boolean loadTeleportData;
    private final MUtilityBungeeCord plugin;
    private ScheduledTask st;
    private int seconds;
    private final MessageChannel messageChannel;

    public PortalRequest(ProxiedPlayer player, double x, double y, double z, String world, ServerInfo server, boolean loadTeleportData) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.server = server;
        this.loadTeleportData = loadTeleportData;
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public void startTimer(int timeInSec) {
        st = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            if(seconds == timeInSec) {
                st.cancel();
            }
            for (ProxiedPlayer onlinePlayer : getServer().getPlayers()) {
                if(onlinePlayer.getName().equals(getPlayer().getName())) {
                    String subChannel = "mens:send-to-";
                    switch (world) {
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
                    messageChannel.sendPortalInfoToServer(player, subChannel, server, x, y, z, loadTeleportData);
                    st.cancel();
                }
            }
            seconds++;
        }, 0, 1, TimeUnit.SECONDS);
    }
}
