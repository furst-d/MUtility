package com.mens.mutility.spigot.commands.commands.mparticle.enums;

public enum Styles {
    MIC("mic"),
    DEST("dest"),
    STROM("strom"),
    HELIX("helix"),
    PRSTEN("prsten"),
    SPIRALA("spirala"),
    STOPA("stopa"),
    VLNA("vlna"),
    VLASTNI("vlastni");

    private final String name;

    Styles(String name) {
        this.name = name;
    }

    public String getName() { return this.name;}
}
