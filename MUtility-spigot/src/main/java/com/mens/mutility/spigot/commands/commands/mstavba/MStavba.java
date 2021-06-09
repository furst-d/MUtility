package com.mens.mutility.spigot.commands.commands.mstavba;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.MyComp;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.utils.PageList2;

/**
 * Trida reprezentujici prikaz /mstavba
 */
public class MStavba {
    private MUtilitySpigot plugin;

    public MStavba(MUtilitySpigot plugin) {
        this.plugin = plugin;
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public CommandData create() {
        Prefix prefix = new Prefix();
        PageList2 helpList = new PageList2(10, prefix.getStavbaPrefix(true, false), "/mstavba");

        CommandData stavba = new CommandData("mstavba", prefix.getStavbaPrefix(true, false), "mutility.stavba.help", CommandExecutors.PLAYER, t -> {
            //TODO
            helpList.clear();
            for (int i = 0; i < 25; i++) {
                helpList.add(new MyComp("Ahoj " + i));
            }
            t.getSender().spigot().sendMessage(helpList.getList(1).create());
        });

        // 1. stupeň
        CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData zobraz = new CommandData(ArgumentTypes.DEFAULT, "zobraz", TabCompleterTypes.DEFAULT, "mutility.stavba.manage", CommandExecutors.BOTH, (t) -> {
            //TODO
            System.out.println("Zobraz");
        });
        CommandData vytvor = new CommandData(ArgumentTypes.DEFAULT, "vytvor", TabCompleterTypes.DEFAULT, "mutility.stavba.create");
        CommandData hlasuj = new CommandData(ArgumentTypes.DEFAULT, "hlasuj", TabCompleterTypes.DEFAULT, "mutility.stavba.vote", CommandExecutors.PLAYER, (t) -> {
            //TODO
            System.out.println("Hlasuj");
        });

        // 2. stupeň
        CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.stavba.help", CommandExecutors.BOTH, (t) -> {
            t.getSender().spigot().sendMessage(helpList.getList(Integer.parseInt(t.getArgs()[1])).create());
        });
        CommandData zobrazPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData startDate = new CommandData(ArgumentTypes.DATE, TabCompleterTypes.DATE_NOW, "mutility.stavba.create");
        CommandData accept = new CommandData(ArgumentTypes.DEFAULT, "accept", TabCompleterTypes.NONE);
        CommandData reject = new CommandData(ArgumentTypes.DEFAULT, "accept", TabCompleterTypes.NONE);

        // 3. stupeň
        CommandData zobrazPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.stavba.help", CommandExecutors.BOTH, (t) -> {
            //TODO
            System.out.println("Zobraz page ID");
        });
        CommandData endDate = new CommandData(ArgumentTypes.DATE, TabCompleterTypes.DATE_PLUS_7, "mutility.stavba.create");
        CommandData acceptID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.stavba.manage", CommandExecutors.BOTH, (t) -> {
            //TODO
            System.out.println("Accept ID");
        });
        CommandData rejectID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.stavba.manage", CommandExecutors.BOTH, (t) -> {
            //TODO
            System.out.println("Reject ID");
        });

        // 4. stupeň
        CommandData popis = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Popis >]", "mutility.stavba.create", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Popis");
        });

        stavba.link(helpPage);
        stavba.link(zobraz);
        stavba.link(vytvor);
        stavba.link(hlasuj);

        helpPage.link(helpPageID);
        zobraz.link(zobrazPage);
        zobraz.link(accept);
        zobraz.link(reject);
        vytvor.link(startDate);

        zobraz.link(zobrazPageID);
        accept.link(acceptID);
        reject.link(rejectID);
        startDate.link(endDate);

        endDate.link(popis);

        return stavba;
    }
}
