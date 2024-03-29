package com.mens.mutility.spigot.commands.commands.mresidence;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.chat.json.JsonBuilder;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandHelp;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.messages.MessageChannel;
import com.mens.mutility.spigot.utils.PageList;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class MResidence extends CommandHelp {
    private final MUtilitySpigot plugin;
    private PageList helpList;
    private final MessageChannel messageChannel;
    private final Prefix prefix;
    private final PluginColors colors;

    public MResidence(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        helpList = new PageList(10, prefix.getResidencePrefix(true, true).replace("]", " - nápověda]"), "/mresidence");
        messageChannel = new MessageChannel();
        colors = new PluginColors();
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public final CommandData create() {
        final CommandData residence = new CommandData("mresidence", "mres", "M-Residence", "mutility.residence.help", CommandExecutors.BOTH, t -> {
            JsonBuilder jb = new JsonBuilder();
            jb.addJsonSegment(prefix.getKostkujPrefix(true, true))
                    .text(": Residence lze v případě zájmu samostatně dokoupit na našem ")
                    .color(colors.getSecondaryColorHEX())
                    .text("➥Shopu")
                    .color(colors.getPrimaryColorHEX())
                    .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, new JsonBuilder(">> Klikni pro otevření ")
                            .color(colors.getSecondaryColorHEX())
                            .text("Shopu")
                            .color(colors.getPrimaryColorHEX())
                            .text(" <<")
                            .color(colors.getSecondaryColorHEX()).toString(), true)
                    .clickEvent(JsonBuilder.ClickAction.OPEN_URL, "https://kostkuj.cz/shop")
                    .text(", avšak nejsou zde potřebné. Na serveru se nachází plugin, díky kterému jsou Moderátoři schopni navrátit veškeré škody a viníka potrestat. Stačí pouze kontaktovat některého ")
                    .color(colors.getSecondaryColorHEX())
                    .text("[")
                    .text("Mod")
                    .color(ChatColor.DARK_GREEN)
                    .text("]")
                    .text(".")
                    .color(colors.getSecondaryColorHEX());
            messageChannel.broadcastJson(jb.toString());
        });

        // 1. stupeň
        final CommandData help = new CommandData(ArgumentTypes.DEFAULT, "help", TabCompleterTypes.DEFAULT, "mutility.residence.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1, null).toPlayer((Player) t.getSender());
        });
        final CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);

        // 2. stupeň
        final CommandData helpHelpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.residence.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1]), null).toPlayer((Player) t.getSender());
        });

        // 3. stupeň
        final CommandData helpHelpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.residence.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[2]), null).toPlayer((Player) t.getSender());
        });

        residence.setDescription("Informační zpráva ohledně residencí");

        help.setDescription("Nápověda k příkazu");
        help.setSyntax("/mresidence " + help.getSubcommand() + "\n/mres " + help.getSubcommand());

        residence.link(help);
        residence.link(helpPage);

        help.link(helpHelpPage);
        helpPage.link(helpPageID);

        helpHelpPage.link(helpHelpPageID);

        return residence;
    }
}
