package com.mens.mutility.bungeecord.commands.anketa;

import com.mens.mutility.bungeecord.chat.Errors;
import com.mens.mutility.bungeecord.chat.PluginColors;
import com.mens.mutility.bungeecord.chat.Prefix;
import com.mens.mutility.bungeecord.chat.json.JsonBuilder;
import com.mens.mutility.bungeecord.messages.MessageChannel;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Anketa {
    private final Survey survey;
    private final Prefix prefix;
    private final PluginColors colors;
    private final Errors errors;

    public Anketa() {
        prefix = new Prefix();
        colors = new PluginColors();
        errors = new Errors();
        survey = new Survey();
    }

    public void create(String surveyName, ProxiedPlayer player) {
        survey.setSurveyName(surveyName);
        survey.getOptions().clear();
        survey.getVotes().clear();
        new JsonBuilder()
                .addJsonSegment(prefix.getAnketaPrefix(true, true))
                .text(" Anketa vytvořena")
                .color(colors.getSecondaryColorHEX())
                .toPlayer(player);
    }

    public void add(ProxiedPlayer player, String option) {
        if(!survey.isRunning()) {
            int id = survey.getMaxIndex();
            survey.setMaxIndex(id + 1);
            survey.getOptions().add(new Option(id, option, 0));
            new JsonBuilder()
                    .addJsonSegment(prefix.getAnketaPrefix(true, true))
                    .text(" Přidali jste možnost: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(option)
                    .color(colors.getPrimaryColorHEX())
                    .toPlayer(player);
        } else {
            new JsonBuilder()
                    .addJsonSegment(prefix.getAnketaPrefix(true, true))
                    .text(" Anketa je spuštěná, nelze přidávat další možnosti")
                    .color(colors.getSecondaryColorHEX())
                    .toPlayer(player);
        }
    }

    public void start(ProxiedPlayer player, int time, String unit) {
        if(survey.getOptions().size() < 2) {
            new JsonBuilder()
                    .addJsonSegment(prefix.getAnketaPrefix(true, true))
                    .text(" Anketa musí mít alespoň 2 možnosti, použijte ")
                    .color(colors.getSecondaryColorHEX())
                    .text("/anketa pridej [<Název možnosti>]")
                    .color(colors.getPrimaryColorHEX())
                    .toPlayer(player);
        } else {
            if(survey.isRunning()) {
                new JsonBuilder()
                        .addJsonSegment(prefix.getAnketaPrefix(true, true))
                        .text(" Anketa již běží")
                        .color(colors.getSecondaryColorHEX())
                        .toPlayer(player);
            } else {
                MessageChannel channel = new MessageChannel();
                channel.sendPermissionRequestBroadcast("mens:permissionRequest", "mutility.anketa.run", "mens:surveyPermissionResponse");
                survey.start(time, unit);
                new JsonBuilder()
                        .addJsonSegment(prefix.getAnketaPrefix(true, true))
                        .text(" Anketa byla spuštěna")
                        .color(colors.getSecondaryColorHEX())
                        .toPlayer(player);
            }
        }
    }

    public void stop(ProxiedPlayer player) {
        if(!survey.isRunning()) {
            new JsonBuilder()
                    .addJsonSegment(prefix.getAnketaPrefix(true, true))
                    .text(" Nelze zastavit anketu, která není aktivní")
                    .color(colors.getSecondaryColorHEX())
                    .toPlayer(player);
        } else {
            survey.setRunning(false);
            new JsonBuilder()
                    .addJsonSegment(prefix.getAnketaPrefix(true, true))
                    .text(" Anketa byla zastavena")
                    .color(colors.getSecondaryColorHEX())
                    .toPlayer(player);
        }
    }

    public void vote(ProxiedPlayer player, int id) {
        if(!survey.isRunning()) {
            new JsonBuilder()
                    .addJsonSegment(prefix.getAnketaPrefix(true, true))
                    .text(" Anketa již není aktivní")
                    .color(colors.getSecondaryColorHEX())
                    .toPlayer(player);
        } else {
            if(survey.isID(id)) {
                if(!survey.hasVoted(player)) {
                    survey.vote(id, player);
                } else {
                    new JsonBuilder()
                            .addJsonSegment(prefix.getAnketaPrefix(true, true))
                            .text(" Již jsi hlasoval")
                            .color(colors.getSecondaryColorHEX())
                            .toPlayer(player);
                }
            } else {
                new JsonBuilder()
                        .addJsonSegment(prefix.getAnketaPrefix(true, true))
                        .addJsonSegment(errors.errWrongArgument(String.valueOf(id), true))
                        .color(colors.getSecondaryColorHEX())
                        .toPlayer(player);
            }
        }
    }

    public void addPermissedPlayers(String permissedPlayers) {
        String[] permPlayers = permissedPlayers.split(";");
        for (String permPlayer : permPlayers) {
            survey.getPermissedPlayers().add(permPlayer);
        }
    }
}
