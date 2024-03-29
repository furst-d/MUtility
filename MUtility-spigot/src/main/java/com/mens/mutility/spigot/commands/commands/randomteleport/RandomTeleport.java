package com.mens.mutility.spigot.commands.commands.randomteleport;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.commands.tpdata.Tpdata;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandHelp;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.messages.MessageChannel;
import com.mens.mutility.spigot.utils.PageList;
import org.bukkit.entity.Player;

import java.util.Objects;

public class RandomTeleport extends CommandHelp {
    private final MUtilitySpigot plugin;
    private PageList helpList;
    private final MessageChannel messageChannel;
    private final Tpdata teleportDataManager;

    public RandomTeleport(MUtilitySpigot plugin) {
        this.plugin = plugin;
        Prefix prefix = new Prefix();
        helpList = new PageList(10, prefix.getRandomTeleportPrefix(true, true).replace("]", " - nápověda]"), "/randomteleport");
        messageChannel = new MessageChannel();
        teleportDataManager = new Tpdata(plugin);
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public final CommandData create() {
        final CommandData rt = new CommandData("randomteleport", "rt", "Random teleport", "mutility.rt.help", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            teleportDataManager.saveData(player, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), Objects.requireNonNull(player.getLocation().getWorld()).getName());
            teleportDataManager.deleteOldData(30);
            messageChannel.sendToBungeeCord(player, "mens:random-teleport", player.getName());
        });

        // 1. stupeň
        final CommandData help = new CommandData(ArgumentTypes.DEFAULT, "help", TabCompleterTypes.DEFAULT, "mutility.rt.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1, null).toPlayer((Player) t.getSender());
        });
        final CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);

        // 2. stupeň
        final CommandData helpHelpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.rt.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1]), null).toPlayer((Player) t.getSender());
        });

        // 3. stupeň
        final CommandData helpHelpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.rt.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[2]), null).toPlayer((Player) t.getSender());
        });

        rt.setDescription("Náhodný teleport");

        help.setDescription("Nápověda k příkazu");
        help.setSyntax("/randomteleport " + help.getSubcommand() + "\n/rt " + help.getSubcommand());

        rt.link(help);
        rt.link(helpPage);

        help.link(helpHelpPage);
        helpPage.link(helpPageID);

        helpHelpPage.link(helpHelpPageID);

        return rt;
    }
}
