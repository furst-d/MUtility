package com.mens.mutility.bungeecord.utils.randomteleport;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RandomTeleport {
    private final MUtilityBungeeCord plugin;

    public RandomTeleport() {
        plugin = MUtilityBungeeCord.getInstance();
    }

    public ServerData findServer() {
        List<ServerData> servers = getServersData();
        servers.sort(Comparator.comparingInt(ServerData::getPlayerCount));
        int firstAcc = 10;
        int secAcc = 25;
        int thirdAcc = 55;
        int acc = (int) (Math.random() * 100);
        if(acc <= firstAcc) {
            return servers.get(3);
        } else if(acc <= secAcc) {
            return servers.get(2);
        } else if(acc <= thirdAcc) {
            return servers.get(1);
        } else {
            return servers.get(0);
        }
    }

    private List<ServerData> getServersData() {
        List<ServerData> servers = new ArrayList<>();
        ServerInfo ow1 = ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.OverWorld 1.Name"));
        ServerInfo ow2 = ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.OverWorld 2.Name"));
        ServerInfo ow3 = ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.OverWorld 3.Name"));
        ServerInfo ow4 = ProxyServer.getInstance().getServerInfo(plugin.getConfiguration().getString("Servers.OverWorld 4.Name"));
        servers.add(new ServerData(ow1, plugin.getConfiguration().getString("Servers.OverWorld 1.Name"), ow1.getPlayers().size(), plugin.getConfiguration().getInt("Servers.OverWorld 1.Center.X"), plugin.getConfiguration().getInt("Servers.OverWorld 1.Center.Z"), plugin.getConfiguration().getInt("Servers.OverWorld 1.Radius") - 5000, true));
        servers.add(new ServerData(ow2, plugin.getConfiguration().getString("Servers.OverWorld 2.Name"), ow2.getPlayers().size(), plugin.getConfiguration().getInt("Servers.OverWorld 2.Center.X"), plugin.getConfiguration().getInt("Servers.OverWorld 2.Center.Z"), plugin.getConfiguration().getInt("Servers.OverWorld 2.Radius") - 5000, true));
        servers.add(new ServerData(ow3, plugin.getConfiguration().getString("Servers.OverWorld 3.Name"), ow3.getPlayers().size(), plugin.getConfiguration().getInt("Servers.OverWorld 3.Center.X"), plugin.getConfiguration().getInt("Servers.OverWorld 3.Center.Z"), plugin.getConfiguration().getInt("Servers.OverWorld 3.Radius") - 5000, true));
        servers.add(new ServerData(ow4, plugin.getConfiguration().getString("Servers.OverWorld 4.Name"), ow4.getPlayers().size(), plugin.getConfiguration().getInt("Servers.OverWorld 4.Center.X"), plugin.getConfiguration().getInt("Servers.OverWorld 4.Center.Z"), plugin.getConfiguration().getInt("Servers.OverWorld 4.Radius") - 5000, true));
        return servers;
    }
}
