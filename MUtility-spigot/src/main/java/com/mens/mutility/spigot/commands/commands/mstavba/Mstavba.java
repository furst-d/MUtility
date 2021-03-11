package com.mens.mutility.spigot.commands.commands.mstavba;

import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.utils.StringUtils;

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
        CommandData add = new CommandData(ArgumentTypes.DEFAULT, "add", TabCompleterTypes.DEFAULT, "mstavba.add", CommandExecutors.PLAYER, (t) -> System.out.println("Zobrazeno"));
        CommandData create = new CommandData(ArgumentTypes.DEFAULT, "create", TabCompleterTypes.DEFAULT, "mstavba.create");

        // 2. stupeň
        CommandData startDate = new CommandData(ArgumentTypes.DATE, TabCompleterTypes.DATE_NOW, "mstavba.create;mstavba.cosi");
        CommandData test1 = new CommandData(ArgumentTypes.DEFAULT, "test1", TabCompleterTypes.DEFAULT, "mstavba.create");

        //3. stupeň
        CommandData endDate = new CommandData(ArgumentTypes.DATE, TabCompleterTypes.DATE_PLUS_7, "mstavba.create");
        CommandData test2 = new CommandData(ArgumentTypes.DEFAULT, "test2", TabCompleterTypes.DEFAULT, "mstavba.create");

        //4. stupeň
        CommandData popis = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Popis >]", "mstavba.create", CommandExecutors.PLAYER, t -> {
            System.out.println(t[0]);
            System.out.println(t[1]);
            System.out.println(t[2]);
            System.out.println(new StringUtils().getStringFromArgs(t, 3));
        });

        stavba.link(add);
        stavba.link(create);

        create.link(startDate);

        create.link(test1);
        test1.link(test2);

        startDate.link(endDate);

        endDate.link(popis);

        return stavba;
    }
}
