package com.mens.mutility.spigot.commands.commands.navrhy;

import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;

public class Navrhy {

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public static CommandData create() {
        PluginColors colors = new PluginColors();
        CommandData navrhy = new CommandData("navrhy", colors.getSecondaryColor() + "["
                + colors.getPrimaryColor() + "Návrhy"
                + colors.getSecondaryColor() + "] "
                + colors.getSecondaryColor(),"mutility.navrhy.help", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Navrhy");
        });

        // 1. stupeň
        CommandData dej = new CommandData(ArgumentTypes.DEFAULT, "dej", TabCompleterTypes.DEFAULT, "mutility.navrhy.get", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Dej");
        });
        CommandData dejVse = new CommandData(ArgumentTypes.DEFAULT, "dejVse", TabCompleterTypes.DEFAULT, "mutility.navrhy.getall", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Dej vse");
        });
        CommandData dejZamitnute = new CommandData(ArgumentTypes.DEFAULT, "dejZamitnute", TabCompleterTypes.DEFAULT, "mutility.navrhy.getrejected", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Dej zamitnute");
        });
        CommandData dejSchvalene = new CommandData(ArgumentTypes.DEFAULT, "dejSchvalene", TabCompleterTypes.DEFAULT, "mutility.navrhy.getaccepted", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Dej schvalene");
        });
        CommandData schvalit = new CommandData(ArgumentTypes.DEFAULT, "schvalit", TabCompleterTypes.NONE);
        CommandData zamitnout = new CommandData(ArgumentTypes.DEFAULT, "zamitnout", TabCompleterTypes.NONE);
        CommandData vratit = new CommandData(ArgumentTypes.DEFAULT, "vratit", TabCompleterTypes.NONE);

        // 2. stupeň
        CommandData dejPage= new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData dejVsePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData dejZamitnutePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData dejSchvalenePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData acceptID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.navrhy.accept", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Accept");
        });
        CommandData rejectID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE);
        CommandData returnID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.navrhy.return", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Return");
        });

        // 3. stupeň
        CommandData rejectReason = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Důvod zamítnutí >]", "mutility.navrhy.reject", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Reject");
        });

        navrhy.link(dej);
        navrhy.link(dejVse);
        navrhy.link(dejZamitnute);
        navrhy.link(dejSchvalene);
        navrhy.link(schvalit);
        navrhy.link(zamitnout);
        navrhy.link(vratit);

        dej.link(dejPage);
        dejVse.link(dejVsePage);
        dejZamitnute.link(dejZamitnutePage);
        dejSchvalene.link(dejSchvalenePage);
        schvalit.link(acceptID);
        zamitnout.link(rejectID);
        vratit.link(returnID);

        rejectID.link(rejectReason);

        return navrhy;
    }
}
