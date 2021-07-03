package com.mens.mutility.bungeecord.messages;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class MessageChannel {

    MUtilityBungeeCord plugin = MUtilityBungeeCord.getInstance();

    public  void sendToServer(ServerInfo server, String channel, String message) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);

        try {
            output.writeUTF(channel);
            output.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData("mens:mutility", stream.toByteArray());
    }

    public  void sendToServer(ServerInfo server, String channel, String message, String message2) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);

        try {
            output.writeUTF(channel);
            output.writeUTF(message);
            output.writeUTF(message2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData("mens:mutility", stream.toByteArray());
    }

    public void sendPortalInfoToServer(ProxiedPlayer player, String subchannel, ServerInfo server, double x, double y, double z)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        try {
            output.writeUTF(subchannel);
            output.writeDouble(x);
            output.writeDouble(y);
            output.writeDouble(z);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData( "mens:mutility", stream.toByteArray());
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

    public void sendTeleportRequest(ServerInfo server, String player, double x, double y, double z, String world) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        try {
            output.writeUTF("mens:teleport-request");
            output.writeUTF(player);
            output.writeDouble(x);
            output.writeDouble(y);
            output.writeDouble(z);
            output.writeUTF(world);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData( "mens:mutility", stream.toByteArray());
    }
}
