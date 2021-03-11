package com.mens.mutility.spigot.chat;

public class Prefix {
    PluginColors colors;

    public Prefix() {
        colors = new PluginColors();
    }

    public PluginColors getColors() {
        return colors;
    }

    public String getMainPrefix() {
        return getColors().getSecondaryColor() + "["
                + getColors().getPrimaryColor() + "M-Utility"
                + getColors().getSecondaryColor() + "] "
                + getColors().getSecondaryColor();
    }

    public String getEventPrefix() {
        return getColors().getSecondaryColor() + "["
                + getColors().getPrimaryColor() + "Event"
                + getColors().getSecondaryColor() + "] "
                + getColors().getSecondaryColor();
    }
}
