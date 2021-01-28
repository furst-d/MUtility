package by.mens.mutility.bungeecord.messages;

import net.md_5.bungee.api.config.ServerInfo;
import by.mens.mutility.bungeecord.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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

    public void sendToServer(String channel, String message) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        try {
            output.writeUTF(channel);
            output.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        plugin.getProxy().getServers().values().stream().forEach((server) -> {
            server.sendData("mens:mutility", stream.toByteArray());
        });
    }
}
