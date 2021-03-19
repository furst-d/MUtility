package com.mens.mutility.spigot.chat;

import com.mens.mutility.spigot.MUtilitySpigot;
import org.bukkit.Bukkit;

public class Prefix {
    private String prefixName;
    PluginColors colors = new PluginColors();

    public String getPrefixName() {
        return prefixName;
    }

    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

    private String getPrefix() {
        return colors.getSecondaryColor() + "[" +
                colors.getPrimaryColor() + getPrefixName() +
                colors.getSecondaryColor() + "]" +
                colors.getSecondaryColor() + " ";
    }

    public String getMutilityPrefix() {
        setPrefixName("M-Utility");
        return getPrefix();
    }

    public String getAnketaPrefix() {
        setPrefixName("Anketa");
        return getPrefix();
    }

    public String getEventPrefix() {
        setPrefixName("Event");
        return getPrefix();
    }

    public String getInventoryPrefix() {
        setPrefixName("M-Inventory");
        return getPrefix();
    }

    public String getResidencePrefix() {
        setPrefixName("M-Residence");
        return getPrefix();
    }

    public String getStavbaPrefix() {
        setPrefixName("M-Stavba");
        return getPrefix();
    }

    public String getNavrhPrefix() {
        setPrefixName("Navrh");
        return getPrefix();
    }

    public String getNavrhyPrefix() {
        setPrefixName("Navrhy");
        return getPrefix();
    }

    public String getZalohyPrefix() {
        setPrefixName("Zalohy");
        return getPrefix();
    }

    public String getTablePrefix(MUtilitySpigot plugin) {
        return plugin.getConfig().getString("MYSQL.Table prefix");
    }
}
