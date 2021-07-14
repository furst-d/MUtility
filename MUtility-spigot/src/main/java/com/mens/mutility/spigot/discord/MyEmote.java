package com.mens.mutility.spigot.discord;

import net.dv8tion.jda.api.entities.Emote;

public class MyEmote {
    private final String name;
    private final Emote emote;

    public MyEmote(String name, Emote emote) {
        this.name = name;
        this.emote = emote;
    }

    public String getName() {
        return name;
    }

    public Emote getEmote() {
        return emote;
    }
}
