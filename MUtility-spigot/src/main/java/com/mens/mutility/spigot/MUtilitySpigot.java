/**
 * TODO
 * Příkazy - řešit, jestli to odeslal hráč nebo console
 * TabCompleter na hráče nejspíše nebude fungovat pro bungee, bude zobrazovat pouze hráče na jednom serveru
 */
package com.mens.mutility.spigot;

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
import com.mens.mutility.spigot.messages.MessageChannelListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MUtilitySpigot extends JavaPlugin {
    private List<CommandData> commands;

    /**
     * Spousteci metoda
     */
    @Override
    public void onEnable() {
        getLogger().info("Plugin spusten!");
        loadCommands();
        loadEvents();
        loadConfig();
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
     * Metoda pro registraci prikazu
     */
    private void loadCommands() {
        commands = new ArrayList<>();
        commands.add(MUtility.create());
        commands.add(MStavba.create());
        commands.add(Event.create());
        commands.add(MResidence.create());
        commands.add(MInv.create());
        commands.add(Anketa.create());
        commands.add(Zalohy.create());
        commands.add(Navrh.create());
        commands.add(Navrhy.create());

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

    }

    /**
     * Metoda pro nacteni konfiguracniho souboru
     */
    private void loadConfig() {
      /*  getConfig().options().copyDefaults(true);
        saveDefaultConfig(); */
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
     * @return List prikazu
     */
    public List<CommandData> getCommands() {
        return commands;
    }
}
