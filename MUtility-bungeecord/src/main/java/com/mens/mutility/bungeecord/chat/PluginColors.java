package com.mens.mutility.bungeecord.chat;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import net.md_5.bungee.api.ChatColor;

import java.util.Objects;

@SuppressWarnings("unused")
public class PluginColors {
    private final MUtilityBungeeCord plugin;

    public PluginColors() {
        plugin = MUtilityBungeeCord.getInstance();
    }

    public ChatColor getPrimaryColor() {
        return ChatColor.of(Objects.requireNonNull(plugin.getConfiguration().getString("Colors.Chat.Primary")));
    }

    public ChatColor getSecondaryColor() {
        return ChatColor.of(Objects.requireNonNull(plugin.getConfiguration().getString("Colors.Chat.Secondary")));
    }

    public String getPrimaryColorHEX() {
        return plugin.getConfiguration().getString("Colors.Chat.Primary");
    }

    public String getSecondaryColorHEX() {
        return plugin.getConfiguration().getString("Colors.Chat.Secondary");
    }

    public String getThirdColorHEX() {
        return plugin.getConfiguration().getString("Colors.Chat.Third");
    }

    public String getDisableColorHEX() {
        return plugin.getConfiguration().getString("Colors.Chat.Disable");
    }

    public ChatColor getConsolePrimaryColor() {
        return ChatColor.getByChar(Objects.requireNonNull(plugin.getConfiguration().getString("Colors.Console.Primary")).charAt(0));
    }

    public ChatColor getConsoleSecondaryColor() {
        return ChatColor.getByChar(Objects.requireNonNull(plugin.getConfiguration().getString("Colors.Console.Secondary")).charAt(0));
    }
}
