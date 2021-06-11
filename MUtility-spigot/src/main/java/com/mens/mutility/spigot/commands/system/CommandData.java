package com.mens.mutility.spigot.commands.system;

import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CommandData {
    private String commandName;
    private String alias;
    private String prefix;
    private ArgumentTypes argumentType;
    private String subcommand;
    private TabCompleterTypes tc;
    private String tcCustom;
    private String permission;
    private CommandExecutors executor;
    private Consumer<CommandParams> execute;
    private final List<CommandData> next;
    private CommandSender sender;
    private String description;
    private String syntax;

    public CommandData(String commandName, String prefix) {
        this.commandName = commandName;
        this.prefix = prefix;
        next = new ArrayList<>();
    }

    public CommandData(String commandName, String alias, String prefix) {
        this.commandName = commandName;
        this.alias = alias;
        this.prefix = prefix;
        next = new ArrayList<>();
    }

    public CommandData(String commandName, String prefix, String permission, CommandExecutors executor, Consumer<CommandParams> execute) {
        this.commandName = commandName;
        this.prefix = prefix;
        this.permission = permission;
        this.executor = executor;
        this.execute = execute;
        next = new ArrayList<>();
    }

    public CommandData(String commandName, String alias, String prefix, String permission, CommandExecutors executor, Consumer<CommandParams> execute) {
        this.commandName = commandName;
        this.alias = alias;
        this.prefix = prefix;
        this.permission = permission;
        this.executor = executor;
        this.execute = execute;
        next = new ArrayList<>();
    }

    public CommandData(ArgumentTypes argumentType, TabCompleterTypes tc) {
        this.argumentType = argumentType;
        this.tc = tc;
        next = new ArrayList<>();
    }

    public CommandData(ArgumentTypes argumentType, TabCompleterTypes tc, String permission) {
        this.argumentType = argumentType;
        this.tc = tc;
        this.permission = permission;
        next = new ArrayList<>();
    }

    public CommandData(ArgumentTypes argumentType, TabCompleterTypes tc, String tcCustom, String permission) {
        this.argumentType = argumentType;
        this.tc = tc;
        this.tcCustom = tcCustom;
        this.permission = permission;
        next = new ArrayList<>();
    }

    public CommandData(ArgumentTypes argumentType, TabCompleterTypes tc, String permission, CommandExecutors executor, Consumer<CommandParams> execute) {
        this.argumentType = argumentType;
        this.tc = tc;
        this.permission = permission;
        this.executor = executor;
        this.execute = execute;
        next = new ArrayList<>();
    }

    public CommandData(ArgumentTypes argumentType, TabCompleterTypes tc, String tcCustom, String permission, CommandExecutors executor, Consumer<CommandParams> execute) {
        this.argumentType = argumentType;
        this.tc = tc;
        this.tcCustom = tcCustom;
        this.permission = permission;
        this.executor = executor;
        this.execute = execute;
        next = new ArrayList<>();
    }

    public CommandData(ArgumentTypes argumentType, String subcommand, TabCompleterTypes tc) {
        this.argumentType = argumentType;
        this.subcommand= subcommand;
        this.tc = tc;
        next = new ArrayList<>();
    }

    public CommandData(ArgumentTypes argumentType, String subcommand, TabCompleterTypes tc, String permission) {
        this.argumentType = argumentType;
        this.subcommand= subcommand;
        this.tc = tc;
        this.permission = permission;
        next = new ArrayList<>();
    }

    public CommandData(ArgumentTypes argumentType, String subcommand, TabCompleterTypes tc, String tcCustom, String permission) {
        this.argumentType = argumentType;
        this.subcommand= subcommand;
        this.tc = tc;
        this.tcCustom = tcCustom;
        this.permission = permission;
        next = new ArrayList<>();
    }

    public CommandData(ArgumentTypes argumentType, String subcommand, TabCompleterTypes tc, String permission, CommandExecutors executor, Consumer<CommandParams> execute) {
        this.argumentType = argumentType;
        this.subcommand = subcommand;
        this.tc = tc;
        this.permission = permission;
        this.executor = executor;
        this.execute = execute;
        next = new ArrayList<>();
    }

    public CommandData(ArgumentTypes argumentType, String subcommand, TabCompleterTypes tc, String tcCustom, String permission, CommandExecutors executor, Consumer<CommandParams> execute) {
        this.argumentType = argumentType;
        this.subcommand = subcommand;
        this.tc = tc;
        this.tcCustom = tcCustom;
        this.permission = permission;
        this.executor = executor;
        this.execute = execute;
        next = new ArrayList<>();
    }

    public ArgumentTypes getArgumentType() {
        return argumentType;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getAlias() {
        return alias;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSubcommand() {
        return subcommand;
    }

    public TabCompleterTypes getTc() {
        return tc;
    }

    public String getTcCustom() {
        return tcCustom;
    }

    public String getPermission() {
        return permission;
    }

    public CommandExecutors getExecutor() {
        return executor;
    }

    public Consumer<CommandParams> getExecute() {
        return execute;
    }

    public List<CommandData> getNext() {
        return next;
    }

    public String getDescription() {
        return description;
    }

    public String getSyntax() {
        return syntax;
    }

    public void link(CommandData data) {
        next.add(data);
    }

    public CommandSender getSender() {
        return sender;
    }

    public void setSender(CommandSender sender) {
        this.sender = sender;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }
}
