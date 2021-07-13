package com.mens.mutility.spigot.discord;

import com.mens.mutility.spigot.MUtilitySpigot;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordEventListener extends ListenerAdapter {
    private final MUtilitySpigot plugin;

    public DiscordEventListener(MUtilitySpigot plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        DiscordManager.getEmotes().add(new MyEmote(":schvaleno:", event.getGuild().getEmoteById(plugin.getConfig().getLong("Discord.Emotes.Accepted"))));
        DiscordManager.getEmotes().add(new MyEmote(":zamitnuto:", event.getGuild().getEmoteById(plugin.getConfig().getLong("Discord.Emotes.Rejected"))));
    }

}
