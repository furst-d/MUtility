/**
 * TODO
 * Příkazy - výpisy chyb
 * Příkazy - řešit, jestli to odeslal hráč nebo console
 * TabCompleter na hráče nejspíše nebude fungovat pro bungee, bude zobrazovat pouze hráče na jednom serveru
 */
package com.mens.mutility.spigot;

import com.mens.mutility.spigot.commands.commands.mstavba.Mstavba;
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
        commands.add(Mstavba.create());

        Objects.requireNonNull(getCommand("mstavba")).setExecutor(new CommandListener(this));
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
