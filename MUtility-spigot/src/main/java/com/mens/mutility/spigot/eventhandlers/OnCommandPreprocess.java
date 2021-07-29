package com.mens.mutility.spigot.eventhandlers;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.messages.MessageChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class OnCommandPreprocess implements Listener {
    private final MUtilitySpigot plugin;
    MessageChannel messageChannel;

    public OnCommandPreprocess(MUtilitySpigot plugin) {
        this.plugin = plugin;
        messageChannel = new MessageChannel();
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String[] array = message.split(" ");
        Player sender = event.getPlayer();
        if(array.length == 2) {
            if(((array[0].equalsIgnoreCase("/v") || (array[0].equalsIgnoreCase("/pv"))) && array[1].equals("login"))) {
                if(plugin.getJoinEffects().getData().contains("JoinEffects." + sender.getName())) {
                    if(plugin.getJoinEffects().getData().getBoolean("JoinEffects." + sender.getName() + ".Enable")) {
                        List<String> list = plugin.getJoinEffects().getData().getStringList("JoinEffects." + sender.getName() + ".Effects");
                        if(!list.isEmpty()) {
                            for (String s : list) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
                            }
                            list.clear();
                        }
                    }
                }
            }
        }
    }
}
