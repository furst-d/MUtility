package com.mens.mutility.spigot.commands.system;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Errors;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.utils.Checker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CommandListener implements CommandExecutor, TabCompleter {
    private final MUtilitySpigot plugin;
    private final Checker checker;
    private final Errors errors;
    private final Prefix prefix;
    private final PluginColors colors;
    private String errorMessage;

    public CommandListener(MUtilitySpigot plugin) {
        this.plugin = plugin;
        checker = new Checker();
        errors = new Errors();
        prefix = new Prefix();
        colors = new PluginColors();
    }

    public MUtilitySpigot getPlugin() {
        return plugin;
    }

    public Checker getChecker() {
        return checker;
    }

    public Errors getErrors() {
        return errors;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    public PluginColors getColors() {
        return colors;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<CommandData> commands = getPlugin().getCommands();
        for (CommandData commandData: commands) {
            if(command.getName().equalsIgnoreCase(commandData.getCommandName())) {
                //Prikaz nalezen
                List<CommandData> subcommands = commandData.next;
                //Projed vsechny parametry v prikazu
                for (int i = 0; i < args.length; i++) {
                    for (CommandData subcommand: subcommands) {
                        if(checkSubcommand(args, subcommand, i)) {
                            if(subcommand.getArgumentType() == ArgumentTypes.STRINGINF) {
                                i = args.length -1;
                            } else {
                                subcommands = subcommand.next;
                            }
                        } else {
                            sender.sendMessage(getErrorMessage());
                        }
                        // Pokud se jedná o poslední podpříkaz
                        if(i == args.length -1) {
                                if(getChecker().checkPermissions(sender.getName(), subcommand.getPermission())) {
                                subcommand.getExecute().accept(args);
                                return true;
                            }
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean checkSubcommand(String[] args, CommandData subcommand, int i) {
        switch(subcommand.getArgumentType()) {
            case DEFAULT:
                if(subcommand.getSubcommand().equalsIgnoreCase(args[i])) {
                    return true;
                } else {
                    setErrorMessage(getPrefix().getMainPrefix() + getErrors().errWrongArgument()
                            + getColors().getPrimaryColor() + args[i]);
                }
                break;
            case INTEGER:
                if(getChecker().checkInt(args[i])) {
                    return true;
                } else {

                }
                break;
            case DOUBLE:
                if(getChecker().checkDouble(args[i])) {
                    return true;
                } else {

                }
                break;
            case FLOAT:
                if(getChecker().checkFloat(args[i])) {
                    return true;
                } else {

                }
                break;
            case STRING:
            case STRINGINF:
                return true;
            case DATE:
                if(getChecker().checkDate(args[i])) {
                    return true;
                } else {

                }
                break;
            case ONLINE_PLAYER:
                if(getChecker().checkOnlinePlayer(subcommand.getSubcommand())) {
                    return true;
                } else {

                }
                break;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> arguments = new ArrayList<>();
        Player player = (Player) sender;
        List<CommandData> commands = plugin.getCommands();
        for (CommandData commandData: commands) {
            if(command.getName().equalsIgnoreCase(commandData.getCommandName())) {
                List<CommandData> subcommands = commandData.next;
                for (int i = 0; i < args.length; i++) {
                    for (CommandData subcommand: subcommands) {
                        if(checkSubcommand(args, subcommand, i)) {
                            if(subcommand.getArgumentType() == ArgumentTypes.STRINGINF) {
                                i = args.length -1;
                            } else {
                                subcommands = subcommand.next;
                            }
                        }
                        // Pokud se jedná o poslední podpříkaz
                        if(i == args.length -1) {
                            if(player.hasPermission(subcommand.getPermission())) {
                                switch (subcommand.getTc()) {
                                    case DEFAULT:
                                        if(subcommand.getSubcommand().contains(args[i])) {
                                            arguments.add(subcommand.getSubcommand());
                                        }
                                        break;
                                    case CUSTOM:
                                        if(subcommand.getTcCustom().contains(args[i])) {
                                            arguments.add(subcommand.getTcCustom());
                                        }
                                        arguments.add(subcommand.getTcCustom());
                                        break;
                                    case DATE_NOW:
                                        LocalDate start = LocalDate.now();
                                        arguments.add(String.valueOf(String.valueOf(start)));
                                        break;
                                    case DATE_PLUS_7:
                                        LocalDate end = LocalDate.now().plusDays(6);
                                        arguments.add(String.valueOf(end));
                                        break;
                                    case BLOCKS:
                                        Material[] blocks = Material.values();
                                        for (Material block : blocks) {
                                            if (block.isBlock() && block.toString().toLowerCase().contains(args[i])) {
                                                arguments.add(block.toString().toLowerCase());
                                            }
                                        }
                                        break;
                                    case ITEMS:
                                        Material[] items = Material.values();
                                        for (Material item : items) {
                                            if (item.toString().toLowerCase().contains(args[i])) {
                                                arguments.add(item.toString().toLowerCase());
                                            }
                                        }
                                        break;
                                    case ONLINE_PLAYERS:
                                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                            if(onlinePlayer.getName().contains(args[i])) {
                                                arguments.add(onlinePlayer.getName());
                                            }
                                        }
                                        break;
                                    case POSX:
                                        Location destination = player.getLocation();
                                        float x = (int) destination.getX();
                                        x += 0.5;
                                        if(String.valueOf(x).contains(args[i])) {
                                            arguments.add(String.valueOf(x));
                                        }
                                        break;
                                    case POSY:
                                        destination = player.getLocation();
                                        float y = (int) destination.getY();
                                        if(String.valueOf(y).contains(args[i])) {
                                            arguments.add(String.valueOf(y));
                                        }
                                        break;
                                    case POSZ:
                                        destination = player.getLocation();
                                        float z = (int) destination.getZ();
                                        z += 0.5;
                                        if(String.valueOf(z).contains(args[i])) {
                                            arguments.add(String.valueOf(z));
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return arguments;
    }
}
