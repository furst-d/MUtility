package com.mens.mutility.spigot.commands.system;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Errors;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.utils.Checker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CommandListener implements CommandExecutor, TabCompleter {
    private final MUtilitySpigot plugin;
    private final Checker checker;
    private final Errors errors;
    private String errorMessage;
    private boolean error;

    public CommandListener(MUtilitySpigot plugin) {
        this.plugin = plugin;
        checker = new Checker();
        errors = new Errors();
        error = false;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isError() {
        return error;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        setError(false);
        List<CommandData> commands = getPlugin().getCommands();
        for (CommandData commandData: commands) {
            if((command.getName().equalsIgnoreCase(commandData.getCommandName()))
                    || (command.getName().equalsIgnoreCase(commandData.getAlias()))) {
                //Prikaz nalezen
                if(args.length == 0) {
                    if(commandData.getExecute() != null) {
                        if (checkLast(sender, args, commandData, checkCommandSender(commandData.getPrefix(), sender, commandData), commandData.getPermission(), commandData.getExecute()))
                            return true;
                    } else {
                        setError(true);
                        setErrorMessage(commandData.getPrefix() + getErrors().errNotEnoughArguments());
                    }
                }
                List<CommandData> subcommands = commandData.getNext();
                //Projed vsechny parametry v prikazu
                for (int i = 0; i < args.length; i++) {
                    if(subcommands.size() == 0) {
                        setError(true);
                        setErrorMessage(commandData.getPrefix() + getErrors().errTooMuchArguments());
                    }
                    for (CommandData subcommand: subcommands) {
                        if(checkSubcommand(args, commandData.getPrefix(), subcommand, i)) {
                            if(subcommand.getArgumentType() == ArgumentTypes.STRINGINF) {
                                i = args.length -1;
                            }
                            // Pokud se jedná o poslední podpříkaz
                            if(i == args.length -1) {
                                if(subcommand.getExecute() != null) {
                                    if (checkLast(sender, args, commandData, checkCommandSender(commandData.getPrefix(), sender, subcommand), subcommand.getPermission(), subcommand.getExecute()))
                                        return true;
                                } else {
                                    if(!isError()) {
                                        setError(true);
                                        setErrorMessage(commandData.getPrefix() + getErrors().errNotEnoughArguments());
                                    }
                                }
                            }
                            subcommands = subcommand.getNext();
                            break;
                        }
                    }
                    if(isError()) {
                        sender.sendMessage(getErrorMessage());
                        return false;
                    }
                }
            }
        }
        if(isError()) {
            sender.sendMessage(getErrorMessage());
        }
        return false;
    }

    private boolean checkLast(CommandSender sender, String[] args, CommandData commandData, boolean commandSender, String permission, Consumer<CommandParams> execute) {
        if(commandSender) {
            if(getChecker().checkPermissions(sender, permission)) {
                commandData.setSender(sender);
                execute.accept(new CommandParams(sender, args));
                return true;
            } else {
                setError(true);
                setErrorMessage(commandData.getPrefix() + getErrors().errNoPermission());
            }
        }
        return false;
    }

    private boolean checkSubcommand(String[] args, String prefix, CommandData subcommand, int i) {
        switch(subcommand.getArgumentType()) {
            case DEFAULT:
                if(subcommand.getSubcommand().equalsIgnoreCase(args[i])) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    setErrorMessage(prefix + getErrors().errWrongArgument(args[i]));
                }
                break;
            case INTEGER:
                if(getChecker().checkInt(args[i])) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    setErrorMessage(prefix + getErrors().errWrongArgumentNumber(args[i]));
                }
                break;
            case DOUBLE:
                if(getChecker().checkDouble(args[i])) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    setErrorMessage(prefix + getErrors().errWrongArgumentNumber(args[i]));
                }
                break;
            case FLOAT:
                if(getChecker().checkFloat(args[i])) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    setErrorMessage(prefix + getErrors().errWrongArgumentNumber(args[i]));
                }
                break;
            case STRING:
            case STRINGINF:
                setError(false);
                return true;
            case DATE:
                if(getChecker().checkDate(args[i])) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    setErrorMessage(prefix + getErrors().errWrongArgumentDate(args[i]));
                }
                break;
            case ONLINE_PLAYER:
                if(getChecker().checkOnlinePlayer(subcommand.getSubcommand())) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    setErrorMessage(prefix + getErrors().errWrongArgumentOnlinePlayer(args[i]));
                }
                break;
            case POSITIVE_INTEGER:
                if(getChecker().checkPositiveInt(args[i])) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    setErrorMessage(prefix + getErrors().errWrongArgumentPositiveNumber(args[i]));
                }
                break;
        }
        return false;
    }

    private boolean checkCommandSender(String prefix, CommandSender sender, CommandData subcommand) {
        switch (subcommand.getExecutor()) {
            case PLAYER:
                if(sender instanceof Player) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    setErrorMessage(prefix + getErrors().errNotInGame());
                }
                break;
            case CONSOLE:
                if(!(sender instanceof Player)) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    setErrorMessage(prefix + getErrors().errNotInConsole());
                }
                break;
            case BOTH:
                setError(false);
                return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        List<String> arguments = new ArrayList<>();
        Player player = (Player) sender;
        List<CommandData> commands = plugin.getCommands();
        for (CommandData commandData: commands) {
            if(command.getName().equalsIgnoreCase(commandData.getCommandName())) {
                List<CommandData> subcommands = commandData.getNext();
                for (int i = 0; i < args.length; i++) {
                    for (CommandData subcommand: subcommands) {
                        if(checkSubcommand(args, commandData.getPrefix(), subcommand, i)) {
                            if(subcommand.getArgumentType() == ArgumentTypes.STRINGINF) {
                                i = args.length -1;
                            } else {
                                subcommands = subcommand.getNext();
                            }
                        }
                        // Pokud se jedná o poslední podpříkaz
                        if(i == args.length -1) {
                            if(subcommand.getTc() != TabCompleterTypes.NONE) {
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
        }
        return arguments;
    }
}
