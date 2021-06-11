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

public class Navrh extends CommandHelp {
    private final MUtilitySpigot plugin;
    private final Prefix prefix;
    private PageList helpList;

    public Navrh(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        helpList = new PageList(10, prefix.getNavrhPrefix(true, true).replace("]", " - nápověda]"), "/navrh");
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public CommandData create() {
        CommandData navrh = new CommandData("navrh", prefix.getNavrhPrefix(true, false),"mutility.navrh.help", CommandExecutors.BOTH, t -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1).toPlayer((Player) t.getSender());
        });

        // 1. stupeň
        CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData add = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Tvůj návrh >]", "mutility.navrh.add", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Pridat");
        });

        // 2. stupeň
        CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.navrh.help", CommandExecutors.BOTH, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1])).toPlayer((Player) t.getSender());
        });

        navrh.setDescription("Systém pro posílání návrhů na zlepšení serveru");

        add.setDescription("Popis vašeho návrhu");
        add.setSyntax("/navrh [<Tvůj návrh>]");

        navrh.link(helpPage);
        navrh.link(add);

        helpPage.link(helpPageID);

        return navrh;
    }
}
