package com.mens.mutility.spigot.commands.system;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Errors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.commands.mparticle.enums.Colors;
import com.mens.mutility.spigot.commands.commands.mparticle.enums.CustomStyles;
import com.mens.mutility.spigot.commands.commands.mparticle.enums.Particles;
import com.mens.mutility.spigot.commands.commands.mparticle.enums.Styles;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.utils.Checker;
import com.mens.mutility.spigot.utils.ServerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
    private final Prefix prefix;
    private String errorMessage;
    private boolean error;

    public CommandListener(MUtilitySpigot plugin) {
        this.plugin = plugin;
        checker = new Checker(plugin);
        errors = new Errors();
        error = false;
        prefix = new Prefix();
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
                prefix.setPrefixName(commandData.getPrefix());
                String prefixStr;
                if(sender instanceof Player) {
                    prefixStr = prefix.getPrefix(true, false);
                } else {
                    prefixStr = prefix.getPrefix(false, false);
                }
                //Prikaz nalezen
                if(args.length == 0) {
                    if(commandData.getExecute() != null) {
                        if (checkLast(prefixStr, sender, args, commandData, checkCommandSender(prefixStr, sender, commandData), commandData.getPermission(), commandData.getExecute()))
                            return true;
                    } else {
                        setError(true);
                        if(sender instanceof Player) {
                            setErrorMessage(prefixStr + getErrors().errNotEnoughArguments(true, false));
                        } else {
                            setErrorMessage(prefixStr + getErrors().errNotEnoughArguments(false, false));
                        }
                    }
                }
                List<CommandData> subcommands = commandData.getNext();
                //Projed vsechny parametry v prikazu
                for (int i = 0; i < args.length; i++) {
                    if(subcommands.size() == 0) {
                        setError(true);
                        if(sender instanceof Player) {
                            setErrorMessage(prefixStr + getErrors().errTooMuchArguments(true, false));
                        } else {
                            setErrorMessage(prefixStr + getErrors().errTooMuchArguments(false, false));
                        }
                    }
                    for (CommandData subcommand: subcommands) {
                        if(checkSubcommand(sender, args, prefixStr, subcommand, i)) {
                            if(subcommand.getArgumentType() == ArgumentTypes.STRINGINF) {
                                i = args.length -1;
                            }
                            // Pokud se jedná o poslední podpříkaz
                            if(i == args.length -1) {
                                if(subcommand.getExecute() != null) {
                                    if (checkLast(prefixStr, sender, args, commandData, checkCommandSender(prefixStr, sender, subcommand), subcommand.getPermission(), subcommand.getExecute()))
                                        return true;
                                } else {
                                    if(!isError()) {
                                        setError(true);
                                        if(sender instanceof Player) {
                                            setErrorMessage(prefixStr + getErrors().errNotEnoughArguments(true, false));
                                        } else {
                                            setErrorMessage(prefixStr + getErrors().errNotEnoughArguments(false, false));
                                        }
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

    private boolean checkLast(String prefix, CommandSender sender, String[] args, CommandData commandData, boolean commandSender, String permission, Consumer<CommandParams> execute) {
        if(commandSender) {
            if(getChecker().checkPermissions(sender, permission)) {
                commandData.setSender(sender);
                execute.accept(new CommandParams(sender, args));
                return true;
            } else {
                setError(true);
                if(sender instanceof Player) {
                    setErrorMessage(prefix + getErrors().errNoPermission(true, false));
                } else {
                    setErrorMessage(prefix + getErrors().errNoPermission(false, false));
                }
            }
        }
        return false;
    }

    private boolean checkSubcommand(CommandSender sender, String[] args, String prefix, CommandData subcommand, int i) {
        switch(subcommand.getArgumentType()) {
            case DEFAULT:
                if(subcommand.getSubcommand().equalsIgnoreCase(args[i])) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    if(sender instanceof Player) {
                        setErrorMessage(prefix + getErrors().errWrongArgument(args[i], true, false));
                    } else {
                        setErrorMessage(prefix + getErrors().errWrongArgument(args[i], false, false));
                    }
                }
                break;
            case INTEGER:
                if(getChecker().checkInt(args[i])) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    if(sender instanceof Player) {
                        setErrorMessage(prefix + getErrors().errWrongArgumentNumber(args[i], true, false));
                    } else {
                        setErrorMessage(prefix + getErrors().errWrongArgumentNumber(args[i], false, false));
                    }
                }
                break;
            case DOUBLE:
                if(getChecker().checkDouble(args[i])) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    if(sender instanceof Player) {
                        setErrorMessage(prefix + getErrors().errWrongArgumentNumber(args[i], true, false));
                    } else {
                        setErrorMessage(prefix + getErrors().errWrongArgumentNumber(args[i], false, false));
                    }
                }
                break;
            case FLOAT:
                if(getChecker().checkFloat(args[i])) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    if(sender instanceof Player) {
                        setErrorMessage(prefix + getErrors().errWrongArgumentNumber(args[i], true, false));
                    } else {
                        setErrorMessage(prefix + getErrors().errWrongArgumentNumber(args[i], false, false));
                    }
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
                    if(sender instanceof Player) {
                        setErrorMessage(prefix + getErrors().errWrongArgumentDate(args[i], true, false));
                    } else {
                        setErrorMessage(prefix + getErrors().errWrongArgumentDate(args[i], false, false));
                    }
                }
                break;
            case ONLINE_PLAYER:
                if(getChecker().checkOnlinePlayer(subcommand.getSubcommand())) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    if(sender instanceof Player) {
                        setErrorMessage(prefix + getErrors().errWrongArgumentOnlinePlayer(args[i], true, false));
                    } else {
                        setErrorMessage(prefix + getErrors().errWrongArgumentOnlinePlayer(args[i], false, false));
                    }
                }
                break;
            case POSITIVE_INTEGER:
                if(getChecker().checkPositiveInt(args[i])) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    if(sender instanceof Player) {
                        setErrorMessage(prefix + getErrors().errWrongArgumentPositiveNumber(args[i], true, false));
                    } else {
                        setErrorMessage(prefix + getErrors().errWrongArgumentPositiveNumber(args[i], false, false));
                    }
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
                    setErrorMessage(prefix + getErrors().errNotInGame(false, false));
                }
                break;
            case CONSOLE:
                if(!(sender instanceof Player)) {
                    setError(false);
                    return true;
                } else {
                    setError(true);
                    setErrorMessage(prefix + getErrors().errNotInConsole(true, false));
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
        boolean found = false;
        List<String> arguments = new ArrayList<>();
        Player player = (Player) sender;
        List<CommandData> commands = plugin.getCommands();
        for (CommandData commandData: commands) {
            if(command.getName().equalsIgnoreCase(commandData.getCommandName())
                    || command.getName().equalsIgnoreCase(commandData.getAlias())) {
                prefix.setPrefixName(commandData.getPrefix());
                String prefixStr = prefix.getPrefix(true, false);
                List<CommandData> subcommands = commandData.getNext();
                for (int i = 0; i < args.length; i++) {
                    for (CommandData subcommand: subcommands) {
                            if(checkSubcommand(sender, args, prefixStr, subcommand, i)) {
                                if(subcommand.getArgumentType() == ArgumentTypes.STRINGINF) {
                                    i = args.length -1;
                                } else {
                                    subcommands = subcommand.getNext();
                                    found = true;
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
                                        case LOCAL_ONLINE_PLAYERS:
                                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                                if(onlinePlayer.getName().contains(args[i])) {
                                                    arguments.add(onlinePlayer.getName());
                                                }
                                            }
                                            break;
                                        case GLOBAL_ONLINE_PLAYERS:
                                            for (String playerName : plugin.getPlayerNames()) {
                                                if(playerName.contains(args[i])) {
                                                    arguments.add(playerName);
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
                                        case SERVERS:
                                            for(ServerInfo server: plugin.getServers()) {
                                                if(server.getName().contains(args[i])) {
                                                    arguments.add(server.getName());
                                                }
                                            }
                                            break;
                                        case WORLDS:
                                            for(World world: Bukkit.getServer().getWorlds()) {
                                                if(world.getName().contains(args[i])) {
                                                    arguments.add(world.getName());
                                                }
                                            }
                                            break;
                                        case PARTICLES:
                                            for(Particles particle : Particles.values()) {
                                                if(!particle.getName().equalsIgnoreCase("redstone") && particle.getName().contains(args[i])) {
                                                    arguments.add(particle.getName());
                                                }
                                            }
                                            break;
                                        case PARTICLE_STYLES:
                                            for(Styles style : Styles.values()) {
                                                if(!style.getName().equalsIgnoreCase("vlastni") && style.getName().contains(args[i])) {
                                                    arguments.add(style.getName());
                                                }
                                            }
                                            break;
                                        case PARTICLE_COLORS:
                                            for(Colors color : Colors.values()) {
                                                if(color.getName().contains(args[i])) {
                                                    arguments.add(color.getName());
                                                }
                                            }
                                            break;
                                        case CUSTOM_STYLES:
                                            for(CustomStyles style : CustomStyles.values()) {
                                                if(style.getName().contains(args[i])) {
                                                    arguments.add(style.getName());
                                                }
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                        if(found) {
                            found = false;
                            break;
                        }
                    }
                }
            }
        }
        return arguments;
    }
}
