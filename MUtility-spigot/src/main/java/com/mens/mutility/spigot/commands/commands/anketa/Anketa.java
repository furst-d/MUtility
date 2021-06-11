package com.mens.mutility.spigot.commands.commands.anketa;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandHelp;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.utils.PageList;
import org.bukkit.entity.Player;

public class Anketa extends CommandHelp {
    private final MUtilitySpigot plugin;
    private final Prefix prefix;
    private PageList helpList;

    public Anketa(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        helpList = new PageList(10, prefix.getAnketaPrefix(true, true).replace("]", " - nápověda]"), "/anketa");
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public CommandData create() {
        CommandData anketa = new CommandData("anketa", prefix.getAnketaPrefix(true, false),"mutility.anketa.help", CommandExecutors.BOTH, t -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1).toPlayer((Player) t.getSender());
        });

        // 1. stupeň
        CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData vytvor = new CommandData(ArgumentTypes.DEFAULT, "vytvor", TabCompleterTypes.DEFAULT, "mutility.anketa.create");
        CommandData pridej = new CommandData(ArgumentTypes.DEFAULT, "pridej", TabCompleterTypes.DEFAULT, "mutility.anketa.add");
        CommandData start = new CommandData(ArgumentTypes.DEFAULT, "start", TabCompleterTypes.DEFAULT, "mutility.anketa.run");
        CommandData vote = new CommandData(ArgumentTypes.DEFAULT, "vote", TabCompleterTypes.NONE);
        CommandData stop = new CommandData(ArgumentTypes.DEFAULT, "stop", TabCompleterTypes.DEFAULT, "mutility.anketa.run", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Stop");
        });

        // 2. stupeň
        CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.anketa.help", CommandExecutors.BOTH, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1])).toPlayer((Player) t.getSender());
        });
        CommandData nazevAnkety = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název ankety >]" ,"mutility.anketa.create", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Název ankety");
        });
        CommandData nazevMoznosti = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název možnosti >]" ,"mutility.anketa.add", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Název možnosti");
        });
        CommandData cas = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.CUSTOM, "[< Čas (číslo) >]", "mutility.anketa.run");
        CommandData voteID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE,"mutility.anketa.vote", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Vote ID");
        });

        // 3. stupeň
        CommandData sec = new CommandData(ArgumentTypes.DEFAULT, "sec", TabCompleterTypes.DEFAULT,"mutility.anketa.run", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Sec");
        });
        CommandData min = new CommandData(ArgumentTypes.DEFAULT, "min", TabCompleterTypes.DEFAULT,"mutility.anketa.run", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Min");
        });
        CommandData hod = new CommandData(ArgumentTypes.DEFAULT, "h", TabCompleterTypes.DEFAULT,"mutility.anketa.run", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("H");
        });

        anketa.setDescription("Systém pro správu anket");

        vytvor.setDescription("Vytvoření nové ankety");
        vytvor.setSyntax("/anketa " + vytvor.getSubcommand() + " [<Název ankety>]");

        pridej.setDescription("Přidání možnosti volby v aktuálně vytvořené anketě");
        pridej.setSyntax("/anketa " + pridej.getSubcommand() + " [<Název volby>]");

        start.setDescription("Zapnutí ankety");
        start.setSyntax("/anketa " + start.getSubcommand() + " [<Čas(číslo)>] [<Jednotka>]");

        stop.setDescription("Vypnutí ankety");
        stop.setSyntax("/anketa " + stop.getSubcommand());

        anketa.link(helpPage);
        anketa.link(vytvor);
        anketa.link(pridej);
        anketa.link(start);
        anketa.link(vote);
        anketa.link(stop);

        helpPage.link(helpPageID);
        vytvor.link(nazevAnkety);
        pridej.link(nazevMoznosti);
        start.link(cas);
        vote.link(voteID);

        cas.link(sec);
        cas.link(min);
        cas.link(hod);

        return anketa;
    }
}
