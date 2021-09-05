package com.mens.mutility.spigot.commands.commands.mparticle.enums;

import java.util.Arrays;

public enum Styles {
    MIC("mic", "ball"),
    DEST("dest", "rain"),
    STROM("strom", "tree"),
    HELIX("helix", "helix"),
    PRSTEN("prsten", "ring"),
    SPIRALA("spirala", "spiral"),
    STOPA("stopa", "trail"),
    VLNA("vlna", "wave"),
    VLASTNI("vlastni", "custom");

    private final String name;
    private final String englishName;

    Styles(String name, String englishName) {
        this.name = name;
        this.englishName = englishName;
    }

    public String getName() { return this.name;}

    public String getEnglishName() {
        return englishName;
    }

    public static String getEnglishNameFromName(String name) {
        return Styles.valueOf(name).getEnglishName();
    }

    public static Styles getStyleEnumByName(String name) {
        return Arrays.stream(Styles.values()).filter(s -> s.getName().equals(name)).findFirst().orElse(null);
    }
}
