package com.mens.mutility.spigot.commands.commands.mresidence;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;

public class MResidence {
    private MUtilitySpigot plugin;

    public MResidence(MUtilitySpigot plugin) {
        this.plugin = plugin;
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public CommandData create() {
        Prefix prefix = new Prefix();
        return new CommandData("mresidence", "mres", prefix.getResidencePrefix(), "mutility.residence", CommandExecutors.BOTH, t -> {
            System.out.println("Res");
        });
    }
}
