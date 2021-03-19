package com.mens.mutility.spigot.commands.commands.mutility;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.utils.PageList;

public class MUtility {
    private MUtilitySpigot plugin;

    public MUtility(MUtilitySpigot plugin) {
        this.plugin = plugin;
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public CommandData create() {
        Prefix prefix = new Prefix();
        PageList helpList = new PageList(10, prefix.getMutilityPrefix(), "/mutility");

        CommandData mutility = new CommandData("mutility", prefix.getMutilityPrefix(),"mutility.help", CommandExecutors.BOTH, t -> {
            //TODO
            t.getSender().spigot().sendMessage(helpList.getList(1).create());
        });

        // 1. stupeň
        CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData reload = new CommandData(ArgumentTypes.DEFAULT, "reload", TabCompleterTypes.DEFAULT, "mutility.reload", CommandExecutors.BOTH, (t) -> {
            //TODO
            System.out.println("Reload");
        });

        // 2. stupeň
        CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.help", CommandExecutors.BOTH, (t) -> {
            t.getSender().spigot().sendMessage(helpList.getList(Integer.parseInt(t.getArgs()[1])).create());
        });

        mutility.link(helpPage);
        mutility.link(reload);

        helpPage.link(helpPageID);

        return mutility;
    }
}
