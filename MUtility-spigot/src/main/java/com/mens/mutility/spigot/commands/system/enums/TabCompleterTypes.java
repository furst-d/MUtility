package com.mens.mutility.spigot.commands.system.enums;

public enum TabCompleterTypes {
    DEFAULT(null),
    CUSTOM(null),
    DATE_NOW("[<Datum>]"),
    DATE_PLUS_7("[<Datum>]"),
    BLOCKS("[<Blok>]"),
    ITEMS("[<Item>]"),
    GLOBAL_ONLINE_PLAYERS("[<Hráč>]"),
    LOCAL_ONLINE_PLAYERS("[<Hráč>]"),
    POSX("[<Souřadnice X>]"),
    POSY("[<Souřadnice Y>]"),
    POSZ("[<Souřadnice Z>]"),
    SERVERS("[<Server>]"),
    WORLDS("[<Svět>]"),
    PARTICLES("[<Particle>]"),
    PARTICLE_STYLES("[<Styl particlu>]"),
    PARTICLE_COLORS("[<Barva particlu>]"),
    CUSTOM_STYLES("[<Styl particlu>]"),
    NONE(null);

    private final String description;

    TabCompleterTypes(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
