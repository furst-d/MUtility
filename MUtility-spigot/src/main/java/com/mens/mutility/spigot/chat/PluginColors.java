package com.mens.mutility.spigot.chat;

import net.md_5.bungee.api.ChatColor;

public class PluginColors {
    String primaryColor = "#1eb0ff";
    String secondaryColor = "#B5C2C5";
    String thirdColor = "#55FFFF";
    String disableColor = "#637C7E";
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

    public String getThirdColorHEX() {
        return thirdColor;
    }

    public String getDisableColorHEX() {
        return disableColor;
    }

    public ChatColor getConsolePrimaryColor() {
        return consolePrimaryColor;
    }

    public ChatColor getConsoleSecondaryColor() {
        return consoleSecondaryColor;
    }
}
