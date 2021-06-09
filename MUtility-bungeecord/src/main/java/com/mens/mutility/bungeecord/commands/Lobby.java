package com.mens.mutility.bungeecord.commands;

import com.mens.mutility.bungeecord.messages.MessageChannel;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Lobby extends Command {

    MessageChannel msgChannel = new MessageChannel();

    public Lobby()  {
        super("Lobby");
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if ((sender instanceof ProxiedPlayer)) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            player.sendMessage(new TextComponent(" BungeeCord >> Info command sended"));
            //msgChannel.sendToServer("mens:mutility-data", player.getServer().getInfo().getName());


           /* player.sendMessage(new ComponentBuilder("Connecting you to the lobby!").color(ChatColor.RED).create());
            player.connect(ProxyServer.getInstance().getServerInfo("lobby")); */
        }
    }
}
