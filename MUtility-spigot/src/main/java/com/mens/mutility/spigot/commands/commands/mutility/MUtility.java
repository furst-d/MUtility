package com.mens.mutility.spigot.commands.commands.mutility;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandHelp;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.utils.PageList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MUtility extends CommandHelp {
    private final MUtilitySpigot plugin;
    private final Prefix prefix;
    private PageList helpList;
    private PageList pluginHelpList;

    public MUtility(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        helpList = new PageList(10, prefix.getMutilityPrefix(true, true).replace("]", " - nápověda]"), "/mutility");
        pluginHelpList = new PageList(10, prefix.getMutilityPrefix(true, true).replace("]", " - nápověda]"), "/mutility help");
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public final CommandData create() {
        final CommandData mutility = new CommandData("mutility", "M-Utility","mutility.help", CommandExecutors.PLAYER, t -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1, null).toPlayer((Player) t.getSender());
        });

        // 1. stupeň
        final CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData reload = new CommandData(ArgumentTypes.DEFAULT, "reload", TabCompleterTypes.DEFAULT, "mutility.reload", CommandExecutors.BOTH, (t) -> {
            Bukkit.getConsoleSender().sendMessage(prefix.getMutilityPrefix(false, false) + "Znovu načítám configy...");
            t.getSender().sendMessage(prefix.getMutilityPrefix(true, false) + "Znovu načítám configy...");
            plugin.getEvents().reload();
            plugin.getJoinEffects().reload();
            plugin.reloadConfig();
            Bukkit.getConsoleSender().sendMessage(prefix.getMutilityPrefix(false, false) + "Configy načteny");
            if(t.getSender() instanceof Player) {
                t.getSender().sendMessage(prefix.getMutilityPrefix(true, false) + "Configy načteny");
            }
        });
        final CommandData pluginHelp = new CommandData(ArgumentTypes.DEFAULT, "help", TabCompleterTypes.DEFAULT, "mutility.help", CommandExecutors.PLAYER, (t) -> {
            pluginHelpList = getMainHelp(plugin, t.getSender(), pluginHelpList);
            pluginHelpList.getList(1, null).toPlayer((Player) t.getSender());
        });

        // 2. stupeň
        final CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1]), null).toPlayer((Player) t.getSender());
        });
        final CommandData pluginHelpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);

        // 3. stupeň
        final CommandData pluginHelpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.help", CommandExecutors.PLAYER, (t) -> {
            pluginHelpList = getMainHelp(plugin, t.getSender(), pluginHelpList);
            pluginHelpList.getList(Integer.parseInt(t.getArgs()[2]), null).toPlayer((Player) t.getSender());
        });

        mutility.setDescription("Správa pluginu");

        reload.setDescription("Opětovně načte data z configu");
        reload.setSyntax("/mutility " + reload.getSubcommand());

        pluginHelp.setDescription("Nápověda pluginu");
        pluginHelp.setSyntax("/mutility " + pluginHelp.getSubcommand());

        mutility.link(helpPage);
        mutility.link(reload);
        mutility.link(pluginHelp);

        helpPage.link(helpPageID);
        pluginHelp.link(pluginHelpPage);

        pluginHelpPage.link(pluginHelpPageID);

        return mutility;
    }
}
