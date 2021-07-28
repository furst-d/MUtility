package com.mens.mutility.spigot.messages;

import com.google.common.collect.Iterables;
import com.mens.mutility.spigot.MUtilitySpigot;
import org.bukkit.Bukkit;
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
    private ByteArrayOutputStream stream;
    private DataOutputStream output;

    /**
     * Konstruktor tridy
     */
    public MessageChannel() {
        plugin = MUtilitySpigot.getInstance();
    }

    public void sendServer(Player player, String server) {
        stream = new ByteArrayOutputStream();
        output = new DataOutputStream(stream);
        try {
            output.writeUTF("Connect");
            output.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }

    public void sendPortalInfoToBungeeCord(Player player, String channel, String world, double x, double y, double z) {
        stream = new ByteArrayOutputStream();
        output = new DataOutputStream(stream);
        try {
            output.writeUTF(channel);
            output.writeUTF(world);
            output.writeDouble(x);
            output.writeDouble(y);
            output.writeDouble(z);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }

    public void sendToBungeeCord(Player player, String channel) {
        stream = new ByteArrayOutputStream();
        output = new DataOutputStream(stream);
        try {
            output.writeUTF(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }

    public void sendToBungeeCord(Player player, String channel, String value) {
        stream = new ByteArrayOutputStream();
        output = new DataOutputStream(stream);
        try {
            output.writeUTF(channel);
            output.writeUTF(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }

    public void sendSurveyCreateSignalToBungeecord(Player player, String surveyName) {
        stream = new ByteArrayOutputStream();
        output = new DataOutputStream(stream);
        try {
            output.writeUTF("mens:survey-create");
            output.writeUTF(surveyName);
            output.writeUTF(player.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }

    public void sendSurveyAddSignalToBungeecord(Player player, String option) {
        stream = new ByteArrayOutputStream();
        output = new DataOutputStream(stream);
        try {
            output.writeUTF("mens:survey-add");
            output.writeUTF(player.getName());
            output.writeUTF(option);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }

    public void sendSurveyStartSignalToBungeecord(Player player, int time, String unit) {
        stream = new ByteArrayOutputStream();
        output = new DataOutputStream(stream);
        try {
            output.writeUTF("mens:survey-start");
            output.writeUTF(player.getName());
            output.writeInt(time);
            output.writeUTF(unit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }

    public void sendSurveyStopSignalToBungeecord(Player player) {
        stream = new ByteArrayOutputStream();
        output = new DataOutputStream(stream);
        try {
            output.writeUTF("mens:survey-stop");
            output.writeUTF(player.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }

    public void sendSurveyVoteSignalToBungeecord(Player player, int id) {
        stream = new ByteArrayOutputStream();
        output = new DataOutputStream(stream);
        try {
            output.writeUTF("mens:survey-vote");
            output.writeUTF(player.getName());
            output.writeInt(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }

    public void broadcastJson(String json) {
        stream = new ByteArrayOutputStream();
        output = new DataOutputStream(stream);
        try {
            output.writeUTF("mens:broadcast-json");
            output.writeUTF(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if(player != null) {
            player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
        }
    }

    public void sendTeleportRequest(Player player, float x, float y, float z, String world, String server, boolean loadTeleportData) {
        stream = new ByteArrayOutputStream();
        output = new DataOutputStream(stream);
        try {
            output.writeUTF("mens:teleport-request");
            output.writeUTF(player.getName());
            output.writeFloat(x);
            output.writeFloat(y);
            output.writeFloat(z);
            output.writeUTF(world);
            output.writeUTF(server);
            output.writeBoolean(loadTeleportData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }
}
