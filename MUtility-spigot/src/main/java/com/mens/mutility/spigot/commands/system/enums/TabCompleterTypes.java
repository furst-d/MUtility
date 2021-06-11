package com.mens.mutility.spigot.commands.system.enums;

public enum TabCompleterTypes {
    DEFAULT(null),
    CUSTOM(null),
    DATE_NOW("[<Datum>]"),
    DATE_PLUS_7("[<Datum>]"),
    BLOCKS("[<Blok>]"),
    ITEMS("[<Item>]"),
    ONLINE_PLAYERS("[< Hráč>]"),
    POSX("[<Souřadnice X>]"),
    POSY("[<Souřadnice Y>]"),
    POSZ("[<Souřadnice Z>]"),
    NONE(null);

    private final String description;

    TabCompleterTypes(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
