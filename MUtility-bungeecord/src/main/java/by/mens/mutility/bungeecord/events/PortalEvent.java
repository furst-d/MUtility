package by.mens.mutility.bungeecord.events;

import by.mens.mutility.bungeecord.MUtilityBungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PortalEvent implements Listener {

    private MUtilityBungeeCord plugin = MUtilityBungeeCord.getInstance();

    @EventHandler
    public void onPortalEvent(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        player.sendMessage(new TextComponent(ChatColor.DARK_AQUA + "Ahoj " + player.getName() + ", vítej na serveru"));

    }
}
