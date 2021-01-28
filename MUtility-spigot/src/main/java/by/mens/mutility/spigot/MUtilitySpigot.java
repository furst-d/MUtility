package by.mens.mutility.spigot;

import by.mens.mutility.spigot.commands.commands.mstavba.Mstavba;
import by.mens.mutility.spigot.commands.system.CommandData;
import by.mens.mutility.spigot.commands.system.CommandListener;
import by.mens.mutility.spigot.messages.MessageChannelListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class MUtilitySpigot extends JavaPlugin {
    private List<CommandData> commands;

    /**
     * Spousteci metoda
     */
    @Override
    public void onEnable() {
        getLogger().info("Plugin spusten!");
        loadCommand();
        loadEvent();
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
    private void loadCommand() {
        commands = new ArrayList<>();
        commands.add(Mstavba.create());

        getCommand("mstavba").setExecutor(new CommandListener(this));
    }

    /**
     * Metoda pro registraci eventu
     */
    private void loadEvent() {

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
