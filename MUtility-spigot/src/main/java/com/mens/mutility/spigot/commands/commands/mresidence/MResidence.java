package com.mens.mutility.spigot.commands.commands.mresidence;

import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;

public class MResidence {

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public static CommandData create() {
        PluginColors colors = new PluginColors();
        CommandData res = new CommandData("mresidence", "mres", colors.getSecondaryColor() + "["
                + colors.getPrimaryColor() + "M-Residence"
                + colors.getSecondaryColor() + "] "
                + colors.getSecondaryColor(), "mutility.residence", CommandExecutors.BOTH, t -> {
            System.out.println("Res");
        });
        return res;
    }
}
