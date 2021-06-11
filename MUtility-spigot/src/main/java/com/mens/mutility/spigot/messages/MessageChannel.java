package com.mens.mutility.spigot.messages;

import com.mens.mutility.spigot.MUtilitySpigot;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Trida zajistujici komunikaci mezi pluginy na serverech v ramci BungeeCord
 */
public class MessageChannel implements Listener {

    private final MUtilitySpigot plugin;

    /**
     * Konstruktor tridy
     * @param plugin Odkaz na main tridu
     */
    public MessageChannel(MUtilitySpigot plugin) {
        this.plugin = plugin;
    }

    public void sendServer(Player player, String server) {
        ByteArrayOutputStream byteArrayOutpusStream = new ByteArrayOutputStream();
        DataOutputStream dataOutpusStream = new DataOutputStream(byteArrayOutpusStream);
        try {
            dataOutpusStream.writeUTF("Connect");
            dataOutpusStream.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", byteArrayOutpusStream.toByteArray());
    }

    public void sendPortalInfoToBungeeCord(Player player, String channel, double x, double y, double z) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        try {
            output.writeUTF(channel);
            output.writeDouble(x);
            output.writeDouble(y);
            output.writeDouble(z);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }

    public void sendToBungeeCord(Player player, String channel) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        try {
            output.writeUTF(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }
}
