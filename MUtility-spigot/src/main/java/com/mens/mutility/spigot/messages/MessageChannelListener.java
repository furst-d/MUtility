package com.mens.mutility.spigot.messages;

import com.mens.mutility.spigot.MUtilitySpigot;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Trida zajistujici komunikaci mezi pluginy na serverech v ramci BungeeCord
 */
public class MessageChannelListener implements PluginMessageListener {

    private final MUtilitySpigot plugin;

    /**
     * Konstruktor tridy
     * @param plugin Odkaz na main tridu
     */
    public MessageChannelListener(MUtilitySpigot plugin) {
        this.plugin = plugin;
    }

    /**
     * Listener zachytavajici prichozi zpravy
     * @param channel Kanal, na kterem byla zprava vysilana
     * @param player Jmeno hrace, kteremu byla zprava adresovana
     * @param message Obsah zpravy
     */
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        try {
            DataInputStream stream = new DataInputStream(new ByteArrayInputStream(message));
            String subChannel = stream.readUTF();

            if(subChannel.equals("mens:mutility-data")) {
                String input = stream.readUTF();
                player.sendMessage(" ");
                player.sendMessage(" Server >> Info from Bungee");
                player.sendMessage(" Server >> Info: " + input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
