package com.mens.mutility.spigot.commands.commands.mresidence;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandHelp;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.utils.PageList;
import org.bukkit.entity.Player;

public class MResidence extends CommandHelp {
    private final MUtilitySpigot plugin;
    private PageList helpList;

    public MResidence(MUtilitySpigot plugin) {
        this.plugin = plugin;
        Prefix prefix = new Prefix();
        helpList = new PageList(10, prefix.getResidencePrefix(true, true).replace("]", " - nápověda]"), "/mresidence");
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public CommandData create() {
        Prefix prefix = new Prefix();
        CommandData residence = new CommandData("mresidence", "mres", prefix.getResidencePrefix(true, false), "mutility.residence.help", CommandExecutors.BOTH, t -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1).toPlayer((Player) t.getSender());
        });

        // 1. stupeň
        CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData posli = new CommandData(ArgumentTypes.DEFAULT, "posli", TabCompleterTypes.DEFAULT, "mutility.residence.send", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Res");
        });

        // 2. stupeň
        CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.residence.help", CommandExecutors.BOTH, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1])).toPlayer((Player) t.getSender());
        });

        residence.setDescription("Informační zpráva ohledně residencí");

        posli.setDescription("Poslání informační zprávy o residencí do chatu");
        posli.setSyntax("/mres " + posli.getSubcommand() + "\n/mresidence " + posli.getSubcommand());

        residence.link(helpPage);
        residence.link(posli);

        helpPage.link(helpPageID);

        return residence;
    }
}
