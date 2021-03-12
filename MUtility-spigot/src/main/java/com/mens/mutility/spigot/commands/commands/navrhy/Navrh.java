package com.mens.mutility.spigot.commands.commands.navrhy;

import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;

public class Navrh {

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public static CommandData create() {
        PluginColors colors = new PluginColors();
        CommandData navrh = new CommandData("navrh", colors.getSecondaryColor() + "["
                + colors.getPrimaryColor() + "Návrh"
                + colors.getSecondaryColor() + "] "
                + colors.getSecondaryColor(),"mutility.navrh.help", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Navrh");
        });

        // 1. stupeň
        CommandData add = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Tvůj návrh >]", "mutility.navrh.add", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Pridat");
        });

        navrh.link(add);

        return navrh;
    }
}
