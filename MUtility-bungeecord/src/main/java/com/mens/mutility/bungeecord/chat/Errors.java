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

    public String errTooMuchArguments() {
        return "Příliš mnoho argumentů!";
    }

    public String errNotEnoughArguments() {
        return "Nedostatek argumentů!";
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

    public String errWrongArgumentNumber(String arg, boolean json) {
        if(json) {
            return new JsonBuilder(" Chybný argument ")
                    .color(colors.getSecondaryColorHEX())
                    .text(arg)
                    .color(colors.getPrimaryColorHEX())
                    .text(" - argument musí být číslo!")
                    .color(colors.getSecondaryColorHEX())
                    .getJsonSegments();
        }
        return "Chybný argument " +getColors().getPrimaryColor() + arg + getColors().getSecondaryColor()
                + " - argument musí být číslo!";
    }

    public String errWrongArgumentBoolean(String arg, boolean json) {
        if(json) {
            return new JsonBuilder(" Chybný argument ")
                    .color(colors.getSecondaryColorHEX())
                    .text(arg)
                    .color(colors.getPrimaryColorHEX())
                    .text(" - argument musí být True nebo False!")
                    .color(colors.getSecondaryColorHEX())
                    .getJsonSegments();
        }
        return "Chybný argument " +getColors().getPrimaryColor() + arg + getColors().getSecondaryColor()
                + " - argument musí být True nebo False!";
    }

    public String errWrongArgumentDate(String arg, boolean json) {
        if(json) {
            return new JsonBuilder(" Chybný argument ")
                    .color(colors.getSecondaryColorHEX())
                    .text(arg)
                    .color(colors.getPrimaryColorHEX())
                    .text(" - argument musí být datum!")
                    .color(colors.getSecondaryColorHEX())
                    .getJsonSegments();
        }
        return "Chybný argument " +getColors().getPrimaryColor() + arg + getColors().getSecondaryColor()
                + " - argument musí být datum!";
    }

    public String errWrongArgumentOnlinePlayer(String arg, boolean json) {
        if(json) {
            return new JsonBuilder(" Chybný argument ")
                    .color(colors.getSecondaryColorHEX())
                    .text(arg)
                    .color(colors.getPrimaryColorHEX())
                    .text(" - argument musí být jméno online hráče!")
                    .color(colors.getSecondaryColorHEX())
                    .getJsonSegments();
        }
        return "Chybný argument " +getColors().getPrimaryColor() + arg + getColors().getSecondaryColor()
                + " - argument musí být jméno online hráče!";
    }

    public String errWrongArgumentPositiveNumber(String arg, boolean json) {
        if(json) {
            return new JsonBuilder(" Chybný argument ")
                    .color(colors.getSecondaryColorHEX())
                    .text(arg)
                    .color(colors.getPrimaryColorHEX())
                    .text(" - argument musí být kladné číslo!")
                    .color(colors.getSecondaryColorHEX())
                    .getJsonSegments();
        }
        return "Chybný argument " +getColors().getPrimaryColor() + arg + getColors().getSecondaryColor()
                + " - argument musí být kladné číslo!";
    }

    public String errNotInGame(boolean json) {
        if(json) {
            return new JsonBuilder(" Tento příkaz musí být zadán ve hře! ")
                    .getJsonSegments();
        }
        return "Tento příkaz musí být zadán ve hře!";
    }

    public String errNotInConsole(boolean json) {
        if(json) {
            return new JsonBuilder(" Tento příkaz musí být zadán z konzole! ")
                    .getJsonSegments();
        }
        return "Tento příkaz musí být zadán z konzole!";
    }

    public String errNoPermission(boolean json) {
        if(json) {
            return new JsonBuilder(" Na použití tohoto příkazu nemáte dostatečná oprávnění! ")
                    .getJsonSegments();
        }
        return "Na použití tohoto příkazu nemáte dostatečná oprávnění!";
    }

    public String errWrongNick(boolean json) {
        if(json) {
            return new JsonBuilder(" Hráč nebyl nalezen! ")
                    .getJsonSegments();
        }
        return "Hráč nebyl nalezen!";
    }

    public String errDenied(boolean json) {
        if(json) {
            return new JsonBuilder(" Přístup zamítnut! ")
                    .getJsonSegments();
        }
        return "Přístup zamítnut!";
    }

}
