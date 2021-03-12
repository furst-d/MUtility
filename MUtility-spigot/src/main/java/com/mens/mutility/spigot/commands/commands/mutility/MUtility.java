package com.mens.mutility.spigot.commands.commands.mutility;

import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;

public class MUtility {

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public static CommandData create() {
        PluginColors colors = new PluginColors();
        CommandData mutility = new CommandData("mutility", colors.getSecondaryColor() + "["
                + colors.getPrimaryColor() + "M-Utility"
                + colors.getSecondaryColor() + "] "
                + colors.getSecondaryColor(),"mutility.help", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("MUtility");
        });

        // 1. stupeň
        CommandData reload = new CommandData(ArgumentTypes.DEFAULT, "reload", TabCompleterTypes.DEFAULT, "mutility.reload", CommandExecutors.BOTH, (t) -> {
            //TODO
            System.out.println("Reload");
        });

        mutility.link(reload);

        return mutility;
    }
}
