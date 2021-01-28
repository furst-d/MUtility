package by.mens.mutility.spigot.messages;

import by.mens.mutility.spigot.MUtilitySpigot;
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

    /**
     * Metoda zajistujici posilani zprav mezi pluginy na serverech v ramci BungeeCord
     * @param player Odesilatel zpravy
     * @param channel Identifikace kanalu na posilani zprav
     * @param message Zprava, ktera ma byt odeslana
     */
    public void sendToBungeeCord(Player player, String channel, String message) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);

        try {
            output.writeUTF(channel);
            output.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }

    /**
     * Metoda zajistujici posilani zprav mezi pluginy na serverech v ramci BungeeCord
     * @param player Odesilatel zpravy
     * @param target Zprava, ktera ma byt odeslana
     */
    public void sendToBungeeCord(Player player, String target) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);

        try {
            output.writeUTF("Connect");
            output.writeUTF(target);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }
}
