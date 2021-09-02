package com.mens.mutility.bungeecord.chat;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;

public class Prefix {
    private String prefixName;
    PluginColors colors = new PluginColors();

    public String getPrefixName() {
        return prefixName;
    }

    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

    private String getPrefix(boolean hexColor, boolean json) {
        if(hexColor) {
            if(json) {
                return "{\"text\":\"[\",\"color\":\"" + colors.getSecondaryColorHEX() + "\"},{\"text\":\"" + getPrefixName() + "\",\"color\":\"" + colors.getPrimaryColorHEX() + "\"},{\"text\":\"]\",\"color\":\"" + colors.getSecondaryColorHEX() + "\"}";
            } else {
                return colors.getSecondaryColor() + "[" +
                        colors.getPrimaryColor() + getPrefixName() +
                        colors.getSecondaryColor() + "]" +
                        colors.getSecondaryColor() + " ";
            }
        } else {
            if(json) {
                return "{\"text\":\"[\",\"color\":\"" + colors.getSecondaryColor().getName() + "\"},{\"text\":\"" + getPrefixName() + "\",\"color\":\"" + colors.getPrimaryColor().getName() + "\"},{\"text\":\"]\",\"color\":\"" + colors.getSecondaryColor().getName() + "\"}";
            } else {
                return colors.getConsoleSecondaryColor() + "[" +
                        colors.getConsolePrimaryColor() + getPrefixName() +
                        colors.getConsoleSecondaryColor() + "]" +
                        colors.getConsoleSecondaryColor() + " ";
            }
        }
    }

    public String getMutilityPrefix(boolean hexColor, boolean json) {
        setPrefixName("M-Utility");
        return getPrefix(hexColor, json);
    }

    public String getAnketaPrefix(boolean hexColor, boolean json) {
        setPrefixName("Anketa");
        return getPrefix(hexColor, json);
    }

    public String getCustomPrefix(String prefix, boolean hexColor, boolean json) {
        setPrefixName(prefix);
        return getPrefix(hexColor, json);
    }

    public String getTablePrefix() {
        return MUtilityBungeeCord.getInstance().getConfiguration().getString("MYSQL.Table prefix");
    }
}
