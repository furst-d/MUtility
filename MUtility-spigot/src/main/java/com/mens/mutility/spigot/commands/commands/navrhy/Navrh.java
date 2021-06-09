package com.mens.mutility.spigot.commands.commands.navrhy;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.chat.json.JsonBuilder;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.utils.PageList2;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class Navrh {
    private MUtilitySpigot plugin;

    public Navrh(MUtilitySpigot plugin) {
        this.plugin = plugin;
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public CommandData create() {
        Prefix prefix = new Prefix();
        PageList2 helpList = new PageList2(10, prefix.getNavrhPrefix(true, false), "/navrh");

        CommandData navrh = new CommandData("navrh", prefix.getNavrhPrefix(true, false),"mutility.navrh.help", CommandExecutors.BOTH, t -> {
            //TODO

            //t.getSender().spigot().sendMessage(helpList.getList(1).create());

        });

        // 1. stupeň
        CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData add = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Tvůj návrh >]", "mutility.navrh.add", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Pridat");
        });

        // 2. stupeň
        CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.navrh.help", CommandExecutors.BOTH, (t) -> {
            t.getSender().spigot().sendMessage(helpList.getList(Integer.parseInt(t.getArgs()[1])).create());
        });

        navrh.link(helpPage);
        navrh.link(add);

        helpPage.link(helpPageID);

        return navrh;
    }
}
