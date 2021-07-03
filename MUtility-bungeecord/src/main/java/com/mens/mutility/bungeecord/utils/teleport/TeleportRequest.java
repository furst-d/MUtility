package com.mens.mutility.bungeecord.utils.teleport;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import com.mens.mutility.bungeecord.messages.MessageChannel;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class TeleportRequest {
    private final ProxiedPlayer player;
    private final double x;
    private final double y;
    private final double z;
    private final String world;
    private final ServerInfo server;
    private final MUtilityBungeeCord plugin;
    private ScheduledTask st;
    private int seconds;
    private final MessageChannel messageChannel;

    public TeleportRequest(ProxiedPlayer player, double x, double y, double z, String world, ServerInfo server) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
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
                    messageChannel.sendTeleportRequest(server, player.getName(), x, y, z, world);
                    st.cancel();
                }
            }
            System.out.println(seconds);
            seconds++;
        }, 0, 1, TimeUnit.SECONDS);
    }
}
