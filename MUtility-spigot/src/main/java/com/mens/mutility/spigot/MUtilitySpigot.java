/*
 * TODO
 * Příkazy - řešit, jestli to odeslal hráč nebo console
 * TabCompleter na hráče nejspíše nebude fungovat pro bungee, bude zobrazovat pouze hráče na jednom serveru
 * Vyřešit timeout databáze
 */
package com.mens.mutility.spigot;

import com.mens.mutility.spigot.messages.MessageChannel;
import com.mens.mutility.spigot.commands.commands.anketa.Anketa;
import com.mens.mutility.spigot.commands.commands.minv.MInv;
import com.mens.mutility.spigot.commands.commands.mresidence.MResidence;
import com.mens.mutility.spigot.commands.commands.mstavba.MStavba;
import com.mens.mutility.spigot.commands.commands.event.Event;
import com.mens.mutility.spigot.commands.commands.mutility.MUtility;
import com.mens.mutility.spigot.commands.commands.navrhy.Navrh;
import com.mens.mutility.spigot.commands.commands.navrhy.Navrhy;
import com.mens.mutility.spigot.commands.commands.zalohy.Zalohy;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandListener;
import com.mens.mutility.spigot.database.Database;
import com.mens.mutility.spigot.eventhandlers.OnEntityPortalEvent;
import com.mens.mutility.spigot.eventhandlers.OnPlayerJoinEvent;
import com.mens.mutility.spigot.eventhandlers.OnPlayerPortalEvent;
import com.mens.mutility.spigot.messages.MessageChannelListener;
import com.mens.mutility.spigot.utils.ServerInfo;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MUtilitySpigot extends JavaPlugin {
    private List<CommandData> commands;
    private List<ServerInfo> servers;
    public static Database db;
    private PluginManager pm;
    private MessageChannel messageChannel;

    /**
     * Spousteci metoda
     */
    @Override
    public void onEnable() {
        getLogger().info("Plugin spusten!");
        pm = Bukkit.getPluginManager();
        loadCommands();
        loadEvents();
        loadConfig();
        registerChannels();
        db = new Database(this);
        db.openConnection();
        messageChannel = new MessageChannel(this);
        servers = new ArrayList<>();
    }

    /**
     * Ukoncovaci metoda
     */
    @Override
    public void onDisable() {
        getLogger().info("Plugin vypnut!");
    }

    /**
     * Metoda pro registraci prikazu
     */
    private void loadCommands() {
        commands = new ArrayList<>();
        commands.add(new MUtility(this).create());
        commands.add(new MStavba(this).create());
        commands.add(new Event(this).create());
        commands.add(new MResidence(this).create());
        commands.add(new MInv(this).create());
        commands.add(new Anketa(this).create());
        commands.add(new Zalohy(this).create());
        commands.add(new Navrh(this).create());
        commands.add(new Navrhy(this).create());

        CommandListener commandListener = new CommandListener(this);
        Objects.requireNonNull(getCommand("mutility")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("mstavba")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("event")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("mresidence")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("mres")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("minv")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("anketa")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("zalohy")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("navrh")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("navrhy")).setExecutor(commandListener);
    }

    /**
     * Metoda pro registraci eventu
     */
    private void loadEvents() {
        pm.registerEvents(new OnEntityPortalEvent(this), this);
        pm.registerEvents(new OnPlayerPortalEvent(this), this);
        pm.registerEvents(new OnPlayerJoinEvent(this), this);
    }

    /**
     * Metoda pro nacteni konfiguracniho souboru
     */
    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    /**
     * Metoda pro registraci kanalu pro komunikaci mezi pluginy na ruznych serverech v ramci BungeeCord
     */
    private void registerChannels() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "mens:mutility", new MessageChannelListener(this));
    }

    /**
     * Getter na List prikazu
     * @return list prikazu
     */
    public List<CommandData> getCommands() {
        return commands;
    }

    /**
     * Getter na List serveru
     * @return list serveru
     */
    public List<ServerInfo> getServers() {
        return servers;
    }

    public String getCurrentServer() {
        for(ServerInfo server : servers) {
            if(server.isThis()) {
                return server.getName();
            }
        }
        return null;
    }
}
