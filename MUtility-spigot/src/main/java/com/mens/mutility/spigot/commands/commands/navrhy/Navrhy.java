package com.mens.mutility.spigot.commands.commands.navrhy;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandHelp;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.utils.PageList;
import org.bukkit.entity.Player;

public class Navrhy extends CommandHelp {
    private final MUtilitySpigot plugin;
    private final Prefix prefix;
    private PageList helpList;

    public Navrhy(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        helpList = new PageList(10, prefix.getNavrhyPrefix(true, true).replace("]", " - nápověda]"), "/navrhy");
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public CommandData create() {
        CommandData navrhy = new CommandData("navrhy", prefix.getNavrhyPrefix(true, false),"mutility.navrhy.help", CommandExecutors.BOTH, t -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1).toPlayer((Player) t.getSender());
        });

        // 1. stupeň
        CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData admin = new CommandData(ArgumentTypes.DEFAULT, "admin", TabCompleterTypes.DEFAULT, "mutility.navrhy.get", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Admin");
        });
        CommandData zobraz = new CommandData(ArgumentTypes.DEFAULT, "zobraz", TabCompleterTypes.DEFAULT, "mutility.navrhy.show", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Zobraz");
        });
        CommandData schvalit = new CommandData(ArgumentTypes.DEFAULT, "schvalit", TabCompleterTypes.NONE);
        CommandData zamitnout = new CommandData(ArgumentTypes.DEFAULT, "zamitnout", TabCompleterTypes.NONE);
        CommandData vratit = new CommandData(ArgumentTypes.DEFAULT, "vratit", TabCompleterTypes.NONE);
        CommandData delete = new CommandData(ArgumentTypes.DEFAULT, "delete", TabCompleterTypes.NONE);

        // 2. stupeň
        CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.navrhy.help", CommandExecutors.BOTH, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1])).toPlayer((Player) t.getSender());
        });
        CommandData adminVse = new CommandData(ArgumentTypes.DEFAULT, "vse", TabCompleterTypes.DEFAULT, "mutility.navrhy.getall", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Dej vse");
        });
        CommandData adminZamitnute = new CommandData(ArgumentTypes.DEFAULT, "zamitnute", TabCompleterTypes.DEFAULT, "mutility.navrhy.getrejected", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Dej zamitnute");
        });
        CommandData adminSchvalene = new CommandData(ArgumentTypes.DEFAULT, "schvalene", TabCompleterTypes.DEFAULT, "mutility.navrhy.getaccepted", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Dej schvalene");
        });
        CommandData adminPage= new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData zobrazPage= new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData acceptID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.navrhy.accept", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Accept");
        });
        CommandData rejectID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE);
        CommandData returnID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.navrhy.return", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Return");
        });
        CommandData deleteID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.navrhy.delete", CommandExecutors.PLAYER, (t) -> {
            //TODO
            System.out.println("Delete ID");
        });

        // 3. stupeň
        CommandData rejectReason = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Důvod zamítnutí >]", "mutility.navrhy.reject", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Reject");
        });

        CommandData adminPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.navrhy.getrejected", CommandExecutors.BOTH, (t) -> {
            //TODO
            System.out.println("Admin page ID");
        });
        CommandData zobrazPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.navrhy.show", CommandExecutors.BOTH, (t) -> {
            //TODO
            System.out.println("Zobraz page ID");
        });

        CommandData adminVsePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData adminZamitnutePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData adminSchvalenePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);

        //4. stupeň
        CommandData adminVsePageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.navrhy.getall", CommandExecutors.BOTH, (t) -> {
            //TODO
            System.out.println("Admin vse page ID");
        });

        CommandData adminZamitnutePageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.navrhy.getrejected", CommandExecutors.BOTH, (t) -> {
            //TODO
            System.out.println("Admin zamitnute page ID");
        });

        CommandData adminSchvalenePageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.navrhy.getaccepted", CommandExecutors.BOTH, (t) -> {
            //TODO
            System.out.println("Admin schvalene page ID");
        });

        navrhy.setDescription("Systém pro správu návrhů");

        admin.setDescription("Zobrazí seznam návrhů a jejich správu.\nRovněž umožňuje návrhy vyfiltrovat dle jejich stavu\nNespecifikovaný stav v příkazu vrátí seznam nerozhodnutých návrhů.");
        admin.setSyntax("/navrhy " + admin.getSubcommand() + "\n/navrhy " + admin.getSubcommand() + " schvalene"+ "\n/navrhy " + admin.getSubcommand() + " vse" + "\n/navrhy " + admin.getSubcommand() + " zamitnute");

        zobraz.setDescription("Zobrazí vaše návrhy, jejich stav a umožní návrhy smazat");
        zobraz.setSyntax("/navrhy " + zobraz.getSubcommand());

        navrhy.link(helpPage);
        navrhy.link(admin);
        navrhy.link(zobraz);
        navrhy.link(schvalit);
        navrhy.link(zamitnout);
        navrhy.link(vratit);
        navrhy.link(delete);

        helpPage.link(helpPageID);
        admin.link(adminVse);
        admin.link(adminZamitnute);
        admin.link(adminSchvalene);
        admin.link(adminPage);
        zobraz.link(zobrazPage);
        schvalit.link(acceptID);
        zamitnout.link(rejectID);
        vratit.link(returnID);
        delete.link(deleteID);

        adminVse.link(adminVsePage);
        adminZamitnute.link(adminZamitnutePage);
        adminSchvalene.link(adminSchvalenePage);
        zobrazPage.link(zobrazPageID);
        adminPage.link(adminPageID);
        rejectID.link(rejectReason);

        adminVsePage.link(adminVsePageID);
        adminZamitnutePage.link(adminZamitnutePageID);
        adminSchvalenePage.link(adminSchvalenePageID);

        return navrhy;
    }
}
