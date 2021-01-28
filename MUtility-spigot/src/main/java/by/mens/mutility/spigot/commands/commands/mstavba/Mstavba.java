package by.mens.mutility.spigot.commands.commands.mstavba;

import by.mens.mutility.spigot.commands.system.CommandData;
import by.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import by.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import by.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;

/**
 * Trida reprezentujici prikaz /mstavba
 */
public class Mstavba {
    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public static CommandData create() {
        CommandData stavba = new CommandData("mstavba");

        // 1. stupeň
        CommandData add = new CommandData(ArgumentTypes.DEFAULT, "add", TabCompleterTypes.DEFAULT, "mstavba.add", CommandExecutors.PLAYER, (t) -> {
            System.out.println("Zobrazeno");
        });
        CommandData create = new CommandData(ArgumentTypes.DEFAULT, "create", TabCompleterTypes.DEFAULT, "mstavba.create");

        // 2. stupeň
        CommandData startDate = new CommandData(ArgumentTypes.DATE, TabCompleterTypes.DATE_NOW, "mstavba.create;mstavba.cosi");

        //3. stupeň
        CommandData endDate = new CommandData(ArgumentTypes.DATE, TabCompleterTypes.DATE_PLUS_10, "mstavba.create");

        //4. stupeň
        CommandData popis = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Popis >]", "mstavba.create", CommandExecutors.PLAYER, (t) -> {
            System.out.println("Vytvořeno");
        });

        stavba.link(add);
        stavba.link(create);

        create.link(startDate);

        startDate.link(endDate);

        endDate.link(popis);

        return stavba;
    }
}
