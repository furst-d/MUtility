package com.mens.mutility.spigot.commands.system;

import org.bukkit.command.CommandSender;

public class CommandParams {
    private CommandSender sender;
    private String[] args;

    public CommandParams(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String[] getArgs() {
        return args;
    }
}
