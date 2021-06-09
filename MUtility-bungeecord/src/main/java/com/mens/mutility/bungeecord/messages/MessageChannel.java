package com.mens.mutility.bungeecord.messages;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import com.mens.mutility.bungeecord.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

public class MessageChannel {

    MUtilityBungeeCord plugin = MUtilityBungeeCord.getInstance();

    public  void sendToServer(String channel, String message, ServerInfo server) {
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
        /*    plugin.getProxy().getServers().values().forEach((server) -> {
            server.sendData("mens:mutility", stream.toByteArray());
        }); */
    }
}
