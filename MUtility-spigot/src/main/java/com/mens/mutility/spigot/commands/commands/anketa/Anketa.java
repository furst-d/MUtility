package com.mens.mutility.spigot.commands.commands.anketa;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandHelp;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.messages.MessageChannel;
import com.mens.mutility.spigot.utils.MyStringUtils;
import com.mens.mutility.spigot.utils.PageList;
import org.bukkit.entity.Player;

public class Anketa extends CommandHelp {
    private final MUtilitySpigot plugin;
    private final MessageChannel channel;
    private final MyStringUtils strUt;
    private PageList helpList;

    public Anketa(MUtilitySpigot plugin) {
        this.plugin = plugin;
        Prefix prefix = new Prefix();
        channel = new MessageChannel();
        strUt = new MyStringUtils();
        helpList = new PageList(10, prefix.getAnketaPrefix(true, true).replace("]", " - nápověda]"), "/anketa");
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public CommandData create() {
        final CommandData anketa = new CommandData("anketa", "Anketa","mutility.anketa.help", CommandExecutors.PLAYER, t -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1, null).toPlayer((Player) t.getSender());
        });

        // 1. stupeň
        final CommandData help = new CommandData(ArgumentTypes.DEFAULT, "help", TabCompleterTypes.DEFAULT, "mutility.anketa.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1, null).toPlayer((Player) t.getSender());
        });
        final CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData vytvor = new CommandData(ArgumentTypes.DEFAULT, "vytvor", TabCompleterTypes.DEFAULT, "mutility.anketa.create");
        final CommandData pridej = new CommandData(ArgumentTypes.DEFAULT, "pridej", TabCompleterTypes.DEFAULT, "mutility.anketa.add");
        final CommandData start = new CommandData(ArgumentTypes.DEFAULT, "start", TabCompleterTypes.DEFAULT, "mutility.anketa.run");
        final CommandData vote = new CommandData(ArgumentTypes.DEFAULT, "vote", TabCompleterTypes.NONE);
        final CommandData stop = new CommandData(ArgumentTypes.DEFAULT, "stop", TabCompleterTypes.DEFAULT, "mutility.anketa.run", CommandExecutors.PLAYER, t -> channel.sendSurveyStopSignalToBungeecord((Player) t.getSender()));

        // 2. stupeň
        final CommandData helpHelpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.anketa.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1]), null).toPlayer((Player) t.getSender());
        });
        final CommandData nazevAnkety = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název ankety >]" ,"mutility.anketa.create", CommandExecutors.PLAYER, t -> {
            String name = strUt.getStringFromArgs(t.getArgs(), 1);
            channel.sendSurveyCreateSignalToBungeecord((Player) t.getSender(), name);

        });
        final CommandData nazevMoznosti = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název možnosti >]" ,"mutility.anketa.add", CommandExecutors.PLAYER, t -> {
            String option = strUt.getStringFromArgs(t.getArgs(), 1);
            channel.sendSurveyAddSignalToBungeecord((Player) t.getSender(), option);
        });
        final CommandData cas = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.CUSTOM, "[< Čas (číslo) >]", "mutility.anketa.run");
        final CommandData voteID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE,"mutility.anketa.vote", CommandExecutors.PLAYER, t -> channel.sendSurveyVoteSignalToBungeecord((Player) t.getSender(), Integer.parseInt(t.getArgs()[1])));

        // 3. stupeň
        final CommandData helpHelpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.anketa.help", CommandExecutors.BOTH, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[2]), null).toPlayer((Player) t.getSender());
        });
        final CommandData sec = new CommandData(ArgumentTypes.DEFAULT, "sec", TabCompleterTypes.DEFAULT,"mutility.anketa.run", CommandExecutors.PLAYER, t -> channel.sendSurveyStartSignalToBungeecord((Player) t.getSender(), Integer.parseInt(t.getArgs()[1]), "sec"));
        final CommandData min = new CommandData(ArgumentTypes.DEFAULT, "min", TabCompleterTypes.DEFAULT,"mutility.anketa.run", CommandExecutors.PLAYER, t -> channel.sendSurveyStartSignalToBungeecord((Player) t.getSender(), Integer.parseInt(t.getArgs()[1]), "min"));
        final CommandData hod = new CommandData(ArgumentTypes.DEFAULT, "h", TabCompleterTypes.DEFAULT,"mutility.anketa.run", CommandExecutors.PLAYER, t -> channel.sendSurveyStartSignalToBungeecord((Player) t.getSender(), Integer.parseInt(t.getArgs()[1]), "h"));

        anketa.setDescription("Systém pro správu anket");

        help.setDescription("Nápověda k příkazu");
        help.setSyntax("/anketa " + help.getSubcommand());

        vytvor.setDescription("Vytvoření nové ankety");
        vytvor.setSyntax("/anketa " + vytvor.getSubcommand() + " [<Název ankety>]");

        pridej.setDescription("Přidání možnosti volby v aktuálně vytvořené anketě");
        pridej.setSyntax("/anketa " + pridej.getSubcommand() + " [<Název volby>]");

        start.setDescription("Zapnutí ankety");
        start.setSyntax("/anketa " + start.getSubcommand() + " [<Čas(číslo)>] [<Jednotka>]");

        stop.setDescription("Vypnutí ankety");
        stop.setSyntax("/anketa " + stop.getSubcommand());

        anketa.link(help);
        anketa.link(helpPage);
        anketa.link(vytvor);
        anketa.link(pridej);
        anketa.link(start);
        anketa.link(vote);
        anketa.link(stop);

        help.link(helpHelpPage);
        helpPage.link(helpPageID);
        vytvor.link(nazevAnkety);
        pridej.link(nazevMoznosti);
        start.link(cas);
        vote.link(voteID);

        helpHelpPage.link(helpHelpPageID);
        cas.link(sec);
        cas.link(min);
        cas.link(hod);

        return anketa;
    }
}
