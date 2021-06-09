package com.mens.mutility.spigot.chat;

import net.md_5.bungee.api.ChatColor;

public class PluginColors {
    String primaryColor = "#1eb0ff";
    String secondaryColor = "#aaaaaa";
    ChatColor consolePrimaryColor = ChatColor.DARK_AQUA;
    ChatColor consoleSecondaryColor = ChatColor.GRAY;

    public ChatColor getPrimaryColor() {
        return ChatColor.of(primaryColor);
    }

    public ChatColor getSecondaryColor() {
        return ChatColor.of(secondaryColor);
    }

    public String getPrimaryColorHEX() {
        return primaryColor;
    }

    public String getSecondaryColorHEX() {
        return secondaryColor;
    }

    public ChatColor getConsolePrimaryColor() {
        return consolePrimaryColor;
    }

    public ChatColor getConsoleSecondaryColor() {
        return consoleSecondaryColor;
    }
}
