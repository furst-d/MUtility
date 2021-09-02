package com.mens.mutility.bungeecord.chat;

import com.mens.mutility.bungeecord.chat.json.JsonBuilder;

public class Errors {
    private final PluginColors colors;

    public Errors() {
        colors = new PluginColors();
    }

    public PluginColors getColors() {
        return colors;
    }

    public String errWrongArgument(String arg, boolean json) {
        if(json) {
            return new JsonBuilder(" Chybný argument ")
                    .color(colors.getSecondaryColorHEX())
                    .text(arg)
                    .color(colors.getPrimaryColorHEX())
                    .text("!")
                    .color(colors.getSecondaryColorHEX())
                    .getJsonSegments();
        }
        return "Chybný argument " +getColors().getPrimaryColor() + arg + getColors().getSecondaryColor() + "!";
    }
}
