package com.mens.mutility.spigot.chat;

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

    public String errWrongArgument(String arg) {
        return "Chybný argument " +getColors().getPrimaryColor() + arg + getColors().getSecondaryColor() + "!";
    }

    public String errWrongArgumentNumber(String arg) {
        return "Chybný argument " +getColors().getPrimaryColor() + arg + getColors().getSecondaryColor()
                + " - argument musí být číslo!";
    }

    public String errWrongArgumentBoolean(String arg) {
        return "Chybný argument " +getColors().getPrimaryColor() + arg + getColors().getSecondaryColor()
                + " - argument musí být True nebo False!";
    }

    public String errWrongArgumentDate(String arg) {
        return "Chybný argument " +getColors().getPrimaryColor() + arg + getColors().getSecondaryColor()
                + " - argument musí být datum!";
    }

    public String errWrongArgumentOnlinePlayer(String arg) {
        return "Chybný argument " +getColors().getPrimaryColor() + arg + getColors().getSecondaryColor()
                + " - argument musí být jméno online hráče!";
    }

    public String errWrongArgumentPositiveNumber(String arg) {
        return "Chybný argument " +getColors().getPrimaryColor() + arg + getColors().getSecondaryColor()
                + " - argument musí být kladné číslo!";
    }

    public String errNotInGame() {
        return "Tento příkaz musí být zadán ve hře!";
    }

    public String errNoPermission() {
        return "Na použití tohoto příkazu nemáte dostatečná oprávnění!";
    }

    public String errWrongNick() {
        return "Hráč nebyl nalezen!";
    }

    public String errDenied() {
        return "Přístup zamítnut!";
    }

}
