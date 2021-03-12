package com.mens.mutility.spigot.commands.commands.mstavba;

import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;

/**
 * Trida reprezentujici prikaz /mstavba
 */
public class MStavba {

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public static CommandData create() {
        PluginColors colors = new PluginColors();
        CommandData stavba = new CommandData("mstavba", colors.getSecondaryColor() + "["
                + colors.getPrimaryColor() + "M-Stavba"
                + colors.getSecondaryColor() + "] "
                + colors.getSecondaryColor(), "mutility.stavba.help", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Stavba");
        });

        // 1. stupeň
        CommandData zobraz = new CommandData(ArgumentTypes.DEFAULT, "zobraz", TabCompleterTypes.DEFAULT, "mutility.stavba.manage", CommandExecutors.BOTH, (t) -> {
            //TODO
            System.out.println("Zobrazeno");
        });
        CommandData vytvor = new CommandData(ArgumentTypes.DEFAULT, "vytvor", TabCompleterTypes.DEFAULT, "mutility.stavba.create");
        CommandData hlasuj = new CommandData(ArgumentTypes.DEFAULT, "hlasuj", TabCompleterTypes.DEFAULT, "mutility.stavba.vote", CommandExecutors.PLAYER, (t) -> {
            //TODO
            System.out.println("Hlasuj");
        });

        // 2. stupeň
        CommandData startDate = new CommandData(ArgumentTypes.DATE, TabCompleterTypes.DATE_NOW, "mutility.stavba.create");
        CommandData accept = new CommandData(ArgumentTypes.DEFAULT, "accept", TabCompleterTypes.NONE);
        CommandData reject = new CommandData(ArgumentTypes.DEFAULT, "accept", TabCompleterTypes.NONE);

        // 3. stupeň
        CommandData endDate = new CommandData(ArgumentTypes.DATE, TabCompleterTypes.DATE_PLUS_7, "mutility.stavba.create");
        CommandData acceptID = new CommandData(ArgumentTypes.INTEGER,  TabCompleterTypes.NONE, "mutility.stavba.manage", CommandExecutors.BOTH, (t) -> {
            //TODO
            System.out.println("Accept ID");
        });
        CommandData rejectID = new CommandData(ArgumentTypes.INTEGER,  TabCompleterTypes.NONE, "mutility.stavba.manage", CommandExecutors.BOTH, (t) -> {
            //TODO
            System.out.println("Reject ID");
        });

        // 4. stupeň
        CommandData popis = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Popis >]", "mutility.stavba.create", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Popis");
        });

        stavba.link(zobraz);
        stavba.link(vytvor);
        stavba.link(hlasuj);

        zobraz.link(accept);
        zobraz.link(reject);
        vytvor.link(startDate);

        accept.link(acceptID);
        reject.link(rejectID);
        startDate.link(endDate);

        endDate.link(popis);

        return stavba;
    }
}
