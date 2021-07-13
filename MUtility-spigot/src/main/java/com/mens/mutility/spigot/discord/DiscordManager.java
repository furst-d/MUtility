package com.mens.mutility.spigot.discord;

import com.mens.mutility.spigot.MUtilitySpigot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class DiscordManager {
    private static JDA discordBot;
    private MUtilitySpigot plugin;
    private static List<MyEmote> emotes = new ArrayList<>();

    public static JDA getDiscordBot() {
        return discordBot;
    }

    public static List<MyEmote> getEmotes() {
        return emotes;
    }

    public static void startBot(MUtilitySpigot plugin) {
        try {
            discordBot = JDABuilder.createDefault(plugin.getConfig().getString("Discord.Token"))
                    .addEventListeners(new DiscordEventListener(plugin))
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(MessageChannel channel, String message) {
        channel.sendMessage(message).queue();
        channel.sendMessage("```fix\n" + message +  "\n```").queue();
        channel.sendMessage("```css\n" + message +  "\n```").queue();
    }

    public void sendEmbedMessage(MessageChannel channel, MessageEmbed message) {
        channel.sendMessageEmbeds(message).queue();
    }

    public void sendVoteEmbedMessage(MessageChannel channel, MessageEmbed message) {
        channel.sendMessageEmbeds(message).queue(messageCallback -> {
            messageCallback.addReaction(emotes.get(0).getEmote()).queue();
            messageCallback.addReaction(emotes.get(1).getEmote()).queue();
        });
    }

    public void sendPrivateMessage(User user, String message) {
        user.openPrivateChannel().queue((channel) ->
                channel.sendMessage(message).queue());
    }

    public MessageChannel getChannelByName(String name) {
        return discordBot.getTextChannelsByName(name, true).get(0);
    }
}
