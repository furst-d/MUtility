package com.mens.mutility.spigot.commands.system;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.json.JsonBuilder;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.utils.PageList;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CommandHelp {
    private final PluginColors colors;

    public CommandHelp() {
        colors = new PluginColors();
    }

    public PageList getMainHelp(MUtilitySpigot plugin, CommandSender sender, PageList list) {
        list.clear();
        String command = list.getCommand().replace("/", "");
        for (CommandData commandData : plugin.getCommands()) {
            if(commandData.getCommandName().equalsIgnoreCase(command)) {
                list.setHead(new JsonBuilder("Příkazy:")
                        .color(colors.getSecondaryColorHEX())
                );
            }
            if(!(sender instanceof Player) || (sender.hasPermission(commandData.getPermission()))) {
                String hoverSubcommand;
                if(commandData.getAlias() == null) {
                    hoverSubcommand = new JsonBuilder("Popis:\n")
                            .color(colors.getSecondaryColorHEX())
                            .text(commandData.getDescription())
                            .color(colors.getPrimaryColorHEX())
                            .toString();
                } else {
                    hoverSubcommand = new JsonBuilder("Popis:\n")
                            .color(colors.getSecondaryColorHEX())
                            .text(commandData.getDescription())
                            .color(colors.getPrimaryColorHEX())
                            .text("\nAlias: ")
                            .color(colors.getSecondaryColorHEX())
                            .text("/" + commandData.getAlias())
                            .color(colors.getPrimaryColorHEX())
                            .toString();
                }
                list.add(new JsonBuilder(" ➥ ")
                        .color(colors.getSecondaryColorHEX())
                        .text(commandData.getCommandName())
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverSubcommand, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/" + commandData.getCommandName())
                        .getJsonSegments());
            }
        }
        return list;
    }

    public PageList getCommandHelp(MUtilitySpigot plugin, CommandSender sender, PageList list) {
        list.clear();
        boolean isEmpty = true;
        String command = list.getCommand().replace("/", "");
        for (CommandData commandData : plugin.getCommands()) {
            if(commandData.getCommandName().equalsIgnoreCase(command)) {
                if(commandData.getAlias() == null) {
                    list.setHead(new JsonBuilder(commandData.getDescription())
                            .color(colors.getPrimaryColor())
                            .text("\n Podpříkazy:")
                            .color(colors.getSecondaryColorHEX())
                    );
                } else {
                    list.setHead(new JsonBuilder(commandData.getDescription())
                            .color(colors.getPrimaryColor())
                            .text("\nAlias: ")
                            .color(colors.getSecondaryColorHEX())
                            .text("/" + commandData.getAlias())
                            .color(colors.getPrimaryColorHEX())
                            .text("\n Podpříkazy:")
                            .color(colors.getSecondaryColorHEX())
                    );
                }
                for (CommandData subCommand : commandData.getNext()) {
                    if(subCommand.getTc() != TabCompleterTypes.NONE) {
                        if(!(sender instanceof Player) || (sender.hasPermission(subCommand.getPermission()))) {
                            isEmpty = false;
                            String subCommandName = subCommand.getSubcommand();
                            if(subCommandName == null) {
                                if(subCommand.getTc() == TabCompleterTypes.CUSTOM) {
                                    subCommandName = subCommand.getTcCustom().replace("[< ", "[<").replace(" >]", ">]");
                                } else {
                                    subCommandName = subCommand.getTc().getDescription();
                                }
                            }
                              String hoverSubcommand = new JsonBuilder("Popis:\n")
                                    .color(colors.getSecondaryColorHEX())
                                    .text(subCommand.getDescription())
                                    .color(colors.getPrimaryColorHEX())
                                    .text("\n\nPoužití:\n")
                                    .color(colors.getSecondaryColorHEX())
                                    .text(subCommand.getSyntax())
                                    .color(ChatColor.YELLOW)
                                    .toString();
                            list.add(new JsonBuilder(" ➥ ")
                                    .color(colors.getSecondaryColorHEX())
                                    .text(subCommandName)
                                    .color(colors.getPrimaryColorHEX())
                                    .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverSubcommand, true)
                                    .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, list.getCommand() + " " + subCommand.getSubcommand() + " ")
                                    .getJsonSegments());
                        }
                    }
                }
            }
        }
        if(isEmpty) {
            list.add(new JsonBuilder(" Nemáte povolení na žádné podpříkazy!")
                    .color(colors.getSecondaryColorHEX())
                    .getJsonSegments());
        }
        return list;
    }
}
