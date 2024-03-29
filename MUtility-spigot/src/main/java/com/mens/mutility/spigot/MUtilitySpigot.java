package com.mens.mutility.spigot;

import com.mens.mutility.spigot.commands.commands.cc.CC;
import com.mens.mutility.spigot.commands.commands.event.programmed.finder.eventhandlers.OnPlayerInteractEvent;
import com.mens.mutility.spigot.commands.commands.event.programmed.graveyard.eventhandlers.OnBlockBreakEvent;
import com.mens.mutility.spigot.commands.commands.mparticle.MParticle;
import com.mens.mutility.spigot.commands.commands.mstavba.MStavbaVoteManager;
import com.mens.mutility.spigot.commands.commands.anketa.Anketa;
import com.mens.mutility.spigot.commands.commands.minv.MInv;
import com.mens.mutility.spigot.commands.commands.mresidence.MResidence;
import com.mens.mutility.spigot.commands.commands.mstavba.MStavba;
import com.mens.mutility.spigot.commands.commands.event.Event;
import com.mens.mutility.spigot.commands.commands.mutility.MUtility;
import com.mens.mutility.spigot.commands.commands.navrhy.Navrhy;
import com.mens.mutility.spigot.commands.commands.randomteleport.RandomTeleport;
import com.mens.mutility.spigot.commands.commands.tpdata.Tpdata;
import com.mens.mutility.spigot.commands.commands.zalohy.Zalohy;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandListener;
import com.mens.mutility.spigot.database.Database;
import com.mens.mutility.spigot.eventhandlers.*;
import com.mens.mutility.spigot.messages.MessageChannelListener;
import com.mens.mutility.spigot.utils.ServerInfo;
import com.mens.mutility.spigot.utils.YamlFile;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MUtilitySpigot extends JavaPlugin {
    private static MUtilitySpigot instance;
    private List<CommandData> commands;
    private List<ServerInfo> servers;
    private List<String> playerNames;
    private Database db;
    private PluginManager pm;

    private YamlFile events;
    private YamlFile joinEffects;

    /**
     * Spousteci metoda
     */
    @Override
    public void onEnable() {
        setInstance(this);
        getLogger().info("Plugin spusten!");
        pm = Bukkit.getPluginManager();
        db = new Database(this);
        db.openFirstConnection();
        loadCommands();
        loadEvents();
        loadConfig();
        loadFiles();
        registerChannels();
        servers = new ArrayList<>();
        playerNames = new ArrayList<>();
        setMstavba();
    }

    /**
     * Ukoncovaci metoda
     */
    @Override
    public void onDisable() {
        getLogger().info("Plugin vypnut!");
    }

    public static MUtilitySpigot getInstance() {
        return instance;
    }

    public static void setInstance(MUtilitySpigot instance) {
        MUtilitySpigot.instance = instance;
    }

    public PluginManager getPm() {
        return pm;
    }

    public Database getDb() {
        return db;
    }

    public YamlFile getEvents() {
        return events;
    }

    public YamlFile getJoinEffects() {
        return joinEffects;
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
        commands.add(new Navrhy(this).create());
        commands.add(new Tpdata(this).create());
        commands.add(new RandomTeleport(this).create());
        commands.add(new CC(this).create());
        commands.add(new MParticle(this).create());

        CommandListener commandListener = new CommandListener(this);
        Objects.requireNonNull(getCommand("mutility")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("mstavba")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("event")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("mresidence")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("mres")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("minv")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("anketa")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("zalohy")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("navrhy")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("tpdata")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("randomteleport")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("rt")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("cc")).setExecutor(commandListener);
        Objects.requireNonNull(getCommand("mparticle")).setExecutor(commandListener);
    }

    /**
     * Metoda pro registraci eventu
     */
    private void loadEvents() {
        pm.registerEvents(new OnEntityPortalEvent(), this);
        pm.registerEvents(new OnPlayerPortalEvent(this), this);
        pm.registerEvents(new OnPlayerJoinEvent(this), this);
        pm.registerEvents(new OnPlayerInteractEvent(this), this);
        pm.registerEvents(new OnBlockBreakEvent(this), this);
        pm.registerEvents(new OnLeavingEndEvent(this), this);
        pm.registerEvents(new OnCommandPreprocessEvent(this), this);
        pm.registerEvents(new OnPlayerMoveEvent(this), this);
        pm.registerEvents(new OnPlayerQuitEvent(), this);
    }

    /**
     * Metoda pro nacteni konfiguracniho souboru
     */
    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    private void loadFiles() {
        events = new YamlFile(this, "/events.yml");
        joinEffects = new YamlFile(this, "/joineffects.yml");
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

    public ServerInfo getCurrentServer() {
        for(ServerInfo server : servers) {
            if(server.isThis()) {
                return server;
            }
        }
        return null;
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    private void setMstavba() {
        MStavbaVoteManager manager = new MStavbaVoteManager(this);
        manager.synchronizeActive();
    }
}
