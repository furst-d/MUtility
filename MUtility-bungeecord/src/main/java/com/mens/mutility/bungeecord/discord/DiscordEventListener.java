package com.mens.mutility.bungeecord.discord;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordEventListener extends ListenerAdapter {
    private final MUtilityBungeeCord plugin;

    public DiscordEventListener(MUtilityBungeeCord plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        DiscordManager.getEmotes().add(new MyEmote(":schvaleno:", event.getGuild().getEmoteById(plugin.getConfiguration().getLong("Discord.Emotes.Accepted"))));
        DiscordManager.getEmotes().add(new MyEmote(":zamitnuto:", event.getGuild().getEmoteById(plugin.getConfiguration().getLong("Discord.Emotes.Rejected"))));
    }

}
