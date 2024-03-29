package com.mens.mutility.bungeecord.messages;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessageChannel {

    MUtilityBungeeCord plugin = MUtilityBungeeCord.getInstance();

    public void sendToServer(ServerInfo server, String channel, String... messages) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        try {
            output.writeUTF(channel);
            for(String message : messages) {
                output.writeUTF(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData("mens:mutility", stream.toByteArray());
    }

    public void sendToServer(ProxiedPlayer player, String channel, String... messages) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        try {
            output.writeUTF(channel);
            for(String message : messages) {
                output.writeUTF(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.getServer().sendData("mens:mutility", stream.toByteArray());
    }

    public void broadcastMessage(String channel, String... messages) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        try {
            output.writeUTF(channel);
            for(String message : messages) {
                output.writeUTF(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.getProxy().getServers().values().forEach((server) -> server.sendData("mens:mutility", stream.toByteArray()));
    }

    public void sendPortalInfoToServer(ProxiedPlayer player, String subchannel, double x, double y, double z, boolean loadInventory) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        try {
            output.writeUTF(subchannel);
            output.writeBoolean(loadInventory);
            output.writeDouble(x);
            output.writeDouble(y);
            output.writeDouble(z);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.getServer().sendData( "mens:mutility", stream.toByteArray());
    }

    public void sendPermissionRequestBroadcast(String subChannel, String permission, String returnChannel) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        try {
            output.writeUTF(subChannel);
            output.writeUTF(permission);
            output.writeUTF(returnChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.getProxy().getServers().values().forEach((server) -> server.sendData("mens:mutility", stream.toByteArray()));
    }

    public void sendTeleportRequest(ProxiedPlayer player, double x, double y, double z, boolean loadTeleportData, String world) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        try {
            output.writeUTF("mens:teleport-request");
            output.writeDouble(x);
            output.writeDouble(y);
            output.writeDouble(z);
            output.writeBoolean(loadTeleportData);
            output.writeUTF(world);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.getServer().sendData( "mens:mutility", stream.toByteArray());
    }

    public void sendRandomTeleportRequest(ProxiedPlayer player, double centerX, double centerZ, int radius, boolean loadTeleportData) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        try {
            output.writeUTF("mens:random-teleport-request");
            output.writeDouble(centerX);
            output.writeDouble(centerZ);
            output.writeInt(radius);
            output.writeBoolean(loadTeleportData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.getServer().sendData( "mens:mutility", stream.toByteArray());
    }
}
