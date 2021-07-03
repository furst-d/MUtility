package com.mens.mutility.bungeecord;

import com.google.common.io.ByteStreams;
import com.mens.mutility.bungeecord.commands.Lobby;
import com.mens.mutility.bungeecord.events.JoinEvent;
import com.mens.mutility.bungeecord.utils.teleport.TeleportRequest;
import com.mens.mutility.bungeecord.messages.MessageChannelListener;
import com.mens.mutility.bungeecord.portal.PortalRequest;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class MUtilityBungeeCord extends Plugin {

    private static MUtilityBungeeCord instance;
    private MessageChannelListener channelListener;
    private Configuration configuration;
    public static List<PortalRequest> portalQueue;

    /**
     * Spousteci metoda
     */
    @Override
    public void onEnable() {
        setInstance(this);
        getLogger().info("Plugin spusten!");

        channelListener = new MessageChannelListener(this);
        portalQueue = new ArrayList<>();

        loadConfig("config.yml");
        loadCommands();
        loadEvents();
        registerChannels();
    }

    /**
     * Ukoncovaci metoda
     */
    @Override
    public void onDisable() {
        getLogger().info("Plugin vypnut!");
    }

    /**
     * Getter pro instanci
     * @return Instance Main tridy
     */
    public static MUtilityBungeeCord getInstance() {
        return instance;
    }

    /**
     * Setter pro instanci
     * @param instance Nova instance
     */
    public static void setInstance(MUtilityBungeeCord instance) {
        MUtilityBungeeCord.instance = instance;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Metoda pro registraci prikazu
     */
    private void loadCommands() {
        getProxy().getPluginManager().registerCommand(this, new Lobby());
    }

    /**
     * Metoda pro registraci eventu
     */
    private void loadEvents() {
        getProxy().getPluginManager().registerListener(this, new JoinEvent());
        getProxy().getPluginManager().registerListener(this, new MessageChannelListener(this));
    }

    public void loadConfig(String resource) {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(
                    loadFile(resource));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File loadFile(String resource) {
        File folder = getDataFolder();
        if (!folder.exists())
            folder.mkdir();
        File resourceFile = new File(folder, resource);
        try {
            if (!resourceFile.exists()) {
                resourceFile.createNewFile();
                try (InputStream in = getResourceAsStream(resource);
                     OutputStream out = new FileOutputStream(resourceFile)) {
                    ByteStreams.copy(in, out);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceFile;
    }


    /**
     * Metoda pro registraci kanalu pro komunikaci mezi pluginy na ruznych serverech v ramci BungeeCord
     */
    private void registerChannels() {
        getProxy().registerChannel("mens:mutility");
    }
}
