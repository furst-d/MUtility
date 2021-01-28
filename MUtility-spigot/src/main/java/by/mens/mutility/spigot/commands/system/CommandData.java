package by.mens.mutility.spigot.commands.system;

import by.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import by.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import by.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CommandData {
    String commandName;
    ArgumentTypes ct;
    String subcommand;
    TabCompleterTypes tc;
    String tcCustom;
    String permission;
    CommandExecutors executor;
    Consumer execute;
    List<CommandData> next;

    public CommandData(String commandName) {
        this.commandName = commandName;
    }

    public CommandData(ArgumentTypes ct, TabCompleterTypes tc, String permission) {
        this.ct = ct;
        this.tc = tc;
        this.permission = permission;
        next = new ArrayList<>();
    }

    public CommandData(ArgumentTypes ct, TabCompleterTypes tc, String tcCustom, String permission) {
        this.ct = ct;
        this.tc = tc;
        this.tcCustom = tcCustom;
        this.permission = permission;
    }

    public CommandData(ArgumentTypes ct, TabCompleterTypes tc, String permission, Consumer execute) {
        this.ct = ct;
        this.tc = tc;
        this.permission = permission;
        this.execute = execute;
    }

    public CommandData(ArgumentTypes ct, TabCompleterTypes tc, String tcCustom, String permission, CommandExecutors executor, Consumer execute) {
        this.ct = ct;
        this.tc = tc;
        this.tcCustom = tcCustom;
        this.permission = permission;
        this.executor = executor;
        this.execute = execute;
    }

    public CommandData(ArgumentTypes ct, String subcommand, TabCompleterTypes tc, String permission) {
        this.ct = ct;
        this.subcommand= subcommand;
        this.tc = tc;
        this.permission = permission;
    }

    public CommandData(ArgumentTypes ct, String subcommand, TabCompleterTypes tc, String tcCustom, String permission) {
        this.ct = ct;
        this.subcommand= subcommand;
        this.tc = tc;
        this.tcCustom = tcCustom;
        this.permission = permission;
    }

    public CommandData(ArgumentTypes ct, String subcommand, TabCompleterTypes tc, String permission, CommandExecutors executor, Consumer execute) {
        this.ct = ct;
        this.subcommand = subcommand;
        this.tc = tc;
        this.permission = permission;
        this.executor = executor;
        this.execute = execute;
    }

    public CommandData(ArgumentTypes ct, String subcommand, TabCompleterTypes tc, String tcCustom, String permission, CommandExecutors executor, Consumer execute) {
        this.ct = ct;
        this.subcommand = subcommand;
        this.tc = tc;
        this.tcCustom = tcCustom;
        this.permission = permission;
        this.executor = executor;
        this.execute = execute;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getSubcommand() {
        return subcommand;
    }

    public void link(CommandData data) {
        next.add(data);
    }
}
