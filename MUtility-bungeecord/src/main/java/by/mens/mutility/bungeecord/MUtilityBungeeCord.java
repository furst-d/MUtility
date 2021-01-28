package by.mens.mutility.bungeecord;

import by.mens.mutility.bungeecord.commands.Lobby;
import by.mens.mutility.bungeecord.events.PortalEvent;
import net.md_5.bungee.api.plugin.Plugin;

public final class MUtilityBungeeCord extends Plugin {

    private static MUtilityBungeeCord instance;

    /**
     * Spousteci metoda
     */
    @Override
    public void onEnable() {
        setInstance(this);
        getLogger().info("Plugin spusten!");
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
        getProxy().getPluginManager().registerListener(this, new PortalEvent());
    }

    /**
     * Metoda pro registraci kanalu pro komunikaci mezi pluginy na ruznych serverech v ramci BungeeCord
     */
    private void registerChannels() {
        getProxy().registerChannel("mens:mutility");
    }
}
