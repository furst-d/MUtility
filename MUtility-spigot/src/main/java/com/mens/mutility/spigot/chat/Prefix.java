package com.mens.mutility.spigot.chat;

import com.mens.mutility.spigot.MUtilitySpigot;

public class Prefix {
    private String prefixName;
    PluginColors colors;

    public Prefix() {
        colors = new PluginColors();
    }

    public String getPrefixName() {
        return prefixName;
    }

    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

    public String getPrefix(boolean hexColor, boolean json) {
        if(hexColor) {
            if(json) {
                return "{\"text\":\"[\",\"color\":\"" + colors.getSecondaryColorHEX() + "\"},{\"text\":\"" + getPrefixName() + "\",\"color\":\"" + colors.getPrimaryColorHEX() + "\"},{\"text\":\"]\",\"color\":\"" + colors.getSecondaryColorHEX() + "\"}";
            } else {
                return colors.getSecondaryColor() + "[" +
                        colors.getPrimaryColor() + getPrefixName() +
                        colors.getSecondaryColor() + "]: ";
            }
        } else {
            if(json) {
                return "{\"text\":\"[\",\"color\":\"" + colors.getSecondaryColor().getName() + "\"},{\"text\":\"" + getPrefixName() + "\",\"color\":\"" + colors.getPrimaryColor().getName() + "\"},{\"text\":\"]\",\"color\":\"" + colors.getSecondaryColor().getName() + "\"}";
            } else {
                return colors.getConsoleSecondaryColor() + "[" +
                        colors.getConsolePrimaryColor() + getPrefixName() +
                        colors.getConsoleSecondaryColor() + "]: ";
            }
        }
    }

    public String getKostkujPrefix(boolean hexColor, boolean json) {
        setPrefixName("Kostkuj");
        return getPrefix(hexColor, json);
    }

    public String getMutilityPrefix(boolean hexColor, boolean json) {
        setPrefixName("M-Utility");
        return getPrefix(hexColor, json);
    }

    public String getAnketaPrefix(boolean hexColor, boolean json) {
        setPrefixName("Anketa");
        return getPrefix(hexColor, json);
    }

    public String getEventPrefix(boolean hexColor, boolean json) {
        setPrefixName("Event");
        return getPrefix(hexColor, json);
    }

    public String getInventoryPrefix(boolean hexColor, boolean json) {
        setPrefixName("M-Inventory");
        return getPrefix(hexColor, json);
    }

    public String getResidencePrefix(boolean hexColor, boolean json) {
        setPrefixName("M-Residence");
        return getPrefix(hexColor, json);
    }

    public String getStavbaPrefix(boolean hexColor, boolean json) {
        setPrefixName("M-Stavba");
        return getPrefix(hexColor, json);
    }

    public String getNavrhyPrefix(boolean hexColor, boolean json) {
        setPrefixName("Návrhy");
        return getPrefix(hexColor, json);
    }

    public String getZalohyPrefix(boolean hexColor, boolean json) {
        setPrefixName("Zálohy");
        return getPrefix(hexColor, json);
    }

    public String getTpDataPrefix(boolean hexColor, boolean json) {
        setPrefixName("TP-Data");
        return getPrefix(hexColor, json);
    }

    public String getRandomTeleportPrefix(boolean hexColor, boolean json) {
        setPrefixName("Náhodný teleport");
        return getPrefix(hexColor, json);
    }

    public String getCCPrefix(boolean hexColor, boolean json) {
        setPrefixName("Craft-Coin");
        return getPrefix(hexColor, json);
    }

    public String getMParticlePrefix(boolean hexColor, boolean json) {
        setPrefixName("M-Particle");
        return getPrefix(hexColor, json);
    }

    public String getCustomPrefix(String prefix, boolean hexColor, boolean json) {
        setPrefixName(prefix);
        return getPrefix(hexColor, json);
    }

    public String getTablePrefix() {
        return MUtilitySpigot.getInstance().getConfig().getString("MYSQL.Table prefix");
    }
}
