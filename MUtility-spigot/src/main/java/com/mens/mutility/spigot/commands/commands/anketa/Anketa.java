package com.mens.mutility.spigot.commands.commands.anketa;

import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;

public class Anketa {

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public static CommandData create() {
        PluginColors colors = new PluginColors();
        CommandData anketa = new CommandData("anketa", colors.getSecondaryColor() + "["
                + colors.getPrimaryColor() + "Anketa"
                + colors.getSecondaryColor() + "] "
                + colors.getSecondaryColor(),"mutility.anketa.help", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Anketa");
        });

        // 1. stupeň
        CommandData vytvor = new CommandData(ArgumentTypes.DEFAULT, "vytvor", TabCompleterTypes.DEFAULT, "mutility.anketa.create");
        CommandData pridej = new CommandData(ArgumentTypes.DEFAULT, "pridej", TabCompleterTypes.DEFAULT, "mutility.anketa.add");
        CommandData start = new CommandData(ArgumentTypes.DEFAULT, "start", TabCompleterTypes.DEFAULT, "mutility.anketa.run");
        CommandData vote = new CommandData(ArgumentTypes.DEFAULT, "vote", TabCompleterTypes.NONE);
        CommandData stop = new CommandData(ArgumentTypes.DEFAULT, "stop", TabCompleterTypes.DEFAULT, "mutility.anketa.run", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Stop");
        });

        // 2. stupeň
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

        anketa.link(vytvor);
        anketa.link(pridej);
        anketa.link(start);
        anketa.link(vote);
        anketa.link(stop);

        vytvor.link(nazevAnkety);
        pridej.link(nazevMoznosti);
        start.link(cas);
        vote.link(voteID);

        start.link(sec);
        start.link(min);
        start.link(hod);

        return anketa;
    }
}
