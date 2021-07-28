package com.mens.mutility.spigot.chat;

import com.mens.mutility.spigot.MUtilitySpigot;
import net.md_5.bungee.api.ChatColor;

import java.util.Objects;

public class PluginColors {
    private final MUtilitySpigot plugin;

    public PluginColors() {
        plugin = MUtilitySpigot.getInstance();
    }

    public ChatColor getPrimaryColor() {
        return ChatColor.of(Objects.requireNonNull(plugin.getConfig().getString("Colors.Chat.Primary")));
    }

    public ChatColor getSecondaryColor() {
        return ChatColor.of(Objects.requireNonNull(plugin.getConfig().getString("Colors.Chat.Secondary")));
    }

    public String getPrimaryColorHEX() {
        return plugin.getConfig().getString("Colors.Chat.Primary");
    }

    public String getSecondaryColorHEX() {
        return plugin.getConfig().getString("Colors.Chat.Secondary");
    }

    public String getThirdColorHEX() {
        return plugin.getConfig().getString("Colors.Chat.Third");
    }

    public String getDisableColorHEX() {
        return plugin.getConfig().getString("Colors.Chat.Disable");
    }

    public ChatColor getConsolePrimaryColor() {
        return ChatColor.getByChar(Objects.requireNonNull(plugin.getConfig().getString("Colors.Console.Primary")).charAt(0));
    }

    public ChatColor getConsoleSecondaryColor() {
        return ChatColor.getByChar(Objects.requireNonNull(plugin.getConfig().getString("Colors.Console.Secondary")).charAt(0));
    }
}
