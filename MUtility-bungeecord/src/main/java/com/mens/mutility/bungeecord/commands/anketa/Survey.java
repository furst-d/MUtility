package com.mens.mutility.bungeecord.commands.anketa;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import com.mens.mutility.bungeecord.chat.PluginColors;
import com.mens.mutility.bungeecord.chat.Prefix;
import com.mens.mutility.bungeecord.chat.json.JsonBuilder;
import com.mens.mutility.bungeecord.utils.bossbar.BarColor;
import com.mens.mutility.bungeecord.utils.bossbar.BarStyle;
import com.mens.mutility.bungeecord.utils.bossbar.BossBar;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Survey {
    private MUtilityBungeeCord plugin;
    private final PluginColors colors;
    private String surveyName;
    private final List<Option> options;
    private final List<BossBar> bossBars;
    private final List<Vote> votes;
    private final List<String> permissedPlayers;
    private boolean isRunning;
    private int maxIndex;

        private int currentTime;
    private ScheduledTask st;

    public Survey() {
        plugin = MUtilityBungeeCord.getInstance();
        colors = new PluginColors();
        surveyName = "";
        options = new ArrayList<>();
        bossBars = new ArrayList<>();
        votes = new ArrayList<>();
        permissedPlayers = new ArrayList<>();
        isRunning = false;
        maxIndex = 0;
    }

    public MUtilityBungeeCord getPlugin() {
        return plugin;
    }

    public void setPlugin(MUtilityBungeeCord plugin) {
        this.plugin = plugin;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public List<Option> getOptions() {
        return options;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public List<String> getPermissedPlayers() {
        return permissedPlayers;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    public void setMaxIndex(int maxIndex) {
        this.maxIndex = maxIndex;
    }

    public void start(int time, String unit) {
        isRunning = true;
        showBossbars(time, unit);
        sendSurvey();
    }

    private void showBossbars(int time, String unit) {
        BossBar timer = new BossBar(ChatColor.GREEN + surveyName, BarColor.GREEN, BarStyle.SOLID);
        for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
            timer.addPlayer(onlinePlayer);
        }
        switch (unit.toLowerCase()) {
            case "min":
                time *= 60;
                break;
            case "h":
                time *= 3600;
                break;
        }
        timer(time, timer);

        for (int i = 0; i < options.size(); i++) {
            bossBars.add(new BossBar(ChatColor.AQUA + options.get(i).getOption(), BarColor.BLUE, BarStyle.SOLID));
            bossBars.get(i).setProgress(0);
            for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                bossBars.get(i).addPlayer(onlinePlayer);
            }
        }
    }

    public void vote(int id, ProxiedPlayer player) {
        Prefix prefix = new Prefix();
        PluginColors colors = new PluginColors();
        String option = "";
        votes.add(new Vote(id, player));
        for (Option value : options) {
            if (value.getId() == id) {
                value.setNumberOfVotes(value.getNumberOfVotes() + 1);
                option = value.getOption();
            }
        }
        new JsonBuilder()
                .addJsonSegment(prefix.getAnketaPrefix(true, true))
                .text(" Hlasoval jsi pro ")
                .color(colors.getSecondaryColorHEX())
                .text(option)
                .color(colors.getPrimaryColorHEX())
                .toPlayer(player);
        countResults();
    }

    public boolean isID(int id) {
        for (Option option : options) {
            if (option.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVoted(ProxiedPlayer player) {
        for (Vote vote : votes) {
            if (vote.getPlayer().equals(player)) {
                return false;
            }
        }
        return true;
    }

    private void timer(int time, BossBar bar) {
        currentTime = time;
        st = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            currentTime--;
            // Pošli každé 2 minuty a 20 sekund před koncem
            if(((currentTime % 120 == 0) && (time - currentTime > 120)) || ((currentTime == 20) && (time - currentTime > 60))) {
                sendSurvey();
            }
            bar.setProgress(((float)currentTime / (float)time));
            if((currentTime == 0) || (!isRunning)) {
                st.cancel();
                if(isRunning) {
                    showResults();
                }
                bar.removeAll();
                for (BossBar bossBar : bossBars) {
                    bossBar.removeAll();
                }
                bossBars.clear();
                votes.clear();
                isRunning = false;
                for (Option option : options) {
                    option.setNumberOfVotes(0);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void sendSurvey() {
        for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
            if(hasVoted(onlinePlayer)) {
                JsonBuilder jb = new JsonBuilder("\n                                                  \n")
                        .color(colors.getSecondaryColorHEX())
                        .effect(JsonBuilder.Effects.STRIKETHROUGH)
                        .text("\n  " + surveyName + "\n")
                        .color(colors.getPrimaryColorHEX())
                        .effect(JsonBuilder.Effects.BOLD);
                for (Option option : options) {
                    String voteHover = new JsonBuilder(">> Kliknutím zahlasuj pro ")
                            .color(colors.getSecondaryColorHEX())
                            .text(option.getOption())
                            .color(colors.getPrimaryColorHEX())
                            .text(" <<")
                            .color(colors.getSecondaryColorHEX())
                            .toString();
                    jb.text("\n   [")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, voteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/anketa vote " + option.getId())
                            .text("✔")
                            .color(ChatColor.GREEN)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, voteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/anketa vote " + option.getId())
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, voteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/anketa vote " + option.getId())
                            .text(" - " + option.getOption())
                            .color(colors.getSecondaryColorHEX())
                            .getJsonSegments();
                }
                jb.text("\n                                                  \n")
                        .color(colors.getSecondaryColorHEX())
                        .effect(JsonBuilder.Effects.STRIKETHROUGH)
                        .toPlayer(onlinePlayer);
            }
        }
    }

    private void countResults() {
        int maxNumberOfVotes = votes.size();
        int numberOfVotes;
        for (int i = 0; i < bossBars.size(); i++) {
            numberOfVotes = options.get(i).getNumberOfVotes();
            bossBars.get(i).setProgress((float)numberOfVotes / (float)maxNumberOfVotes);
        }
    }

    private void showResults() {
        options.sort(Comparator.comparing(Option::getNumberOfVotes));
        for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
            JsonBuilder jb = new JsonBuilder("\n                                                  \n")
                    .color(colors.getSecondaryColorHEX())
                    .effect(JsonBuilder.Effects.STRIKETHROUGH)
                    .text("\n  " + surveyName + "\n")
                    .color(colors.getPrimaryColorHEX())
                    .effect(JsonBuilder.Effects.BOLD);

            for (int i = 0; i < options.size(); i++) {
                if(permissedPlayers.contains(onlinePlayer.getName())) {
                    boolean empty = true;
                    JsonBuilder votesHover = new JsonBuilder();
                    JsonBuilder temp = new JsonBuilder();
                    for (Vote vote : votes) {
                        if (i == 0) {
                            if (options.get(options.size() - 1 - i).getNumberOfVotes() == options.get(options.size() - 2 - i).getNumberOfVotes()) {
                                temp = new JsonBuilder()
                                        .text("\n   " + (i + 1) + ". - " + options.get(options.size() - 1 - i).getOption())
                                        .color(colors.getSecondaryColorHEX())
                                        .text(" (➥" + options.get(options.size() - 1 - i).getNumberOfVotes() + ")")
                                        .color(colors.getSecondaryColorHEX());
                                if (vote.getId() == options.get(options.size() - 1 - i).getId()) {
                                    if (empty) {
                                        empty = false;
                                        votesHover.text("- " + vote.getPlayer().getName())
                                                .color(colors.getSecondaryColorHEX());
                                    } else {
                                        votesHover.text("\n- " + vote.getPlayer().getName())
                                                .color(colors.getSecondaryColorHEX());
                                    }
                                }
                            } else {
                                temp = new JsonBuilder()
                                        .text("\n   " + (i + 1) + ". - ")
                                        .color(colors.getSecondaryColorHEX())
                                        .text(options.get(options.size() - 1 - i).getOption())
                                        .color(ChatColor.GREEN)
                                        .text(" (➥" + options.get(options.size() - 1 - i).getNumberOfVotes() + ")")
                                        .color(ChatColor.GREEN);
                                if (vote.getId() == options.get(options.size() - 1 - i).getId()) {
                                    if (empty) {
                                        empty = false;
                                        votesHover.text("- " + vote.getPlayer().getName())
                                                .color(ChatColor.GREEN);
                                    } else {
                                        votesHover.text("\n- " + vote.getPlayer().getName())
                                                .color(ChatColor.GREEN);
                                    }
                                }
                            }
                        } else {
                            temp = new JsonBuilder()
                                    .text("\n   " + (i + 1) + ". - " + options.get(options.size() - 1 - i).getOption())
                                    .color(colors.getSecondaryColorHEX())
                                    .text(" (➥" + options.get(options.size() - 1 - i).getNumberOfVotes() + ")")
                                    .color(colors.getSecondaryColorHEX());
                            if (vote.getId() == options.get(options.size() - 1 - i).getId()) {
                                if (empty) {
                                    empty = false;
                                    votesHover.text("- " + vote.getPlayer().getName())
                                            .color(colors.getSecondaryColorHEX());
                                } else {
                                    votesHover.text("\n- " + vote.getPlayer().getName())
                                            .color(colors.getSecondaryColorHEX());
                                }
                            }
                        }
                    }
                    if(votes.size() > 0) {
                        if(!empty) {
                            temp.hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, votesHover.toString(), true);
                        }
                        jb.addJsonSegment(temp.getJsonSegments());
                    } else {
                        jb.text("\n   Nikdo nehlasoval")
                                .color(colors.getSecondaryColorHEX());
                        break;
                    }
                } else {
                    if(i == 0) {
                        //remiza
                        if(options.get(options.size() - 1 - i).getNumberOfVotes() == options.get(options.size() - 2 - i).getNumberOfVotes()) {
                            jb.text("\n   " + (i + 1) + ". - " + options.get(options.size() - 1 - i).getOption() + " (" + options.get(options.size() - 1 - i).getNumberOfVotes() + ")")
                                    .color(colors.getSecondaryColorHEX());
                        } else {
                            jb.text("\n   " + (i + 1) + ". - ")
                                    .color(colors.getSecondaryColorHEX())
                                    .text(options.get(options.size() - 1 - i).getOption() + " (" + options.get(options.size() - 1 - i).getNumberOfVotes() + ")")
                                    .color(ChatColor.GREEN);
                        }
                    } else {
                        jb.text("\n   " + (i + 1) + ". - " + options.get(options.size() - 1 - i).getOption() + " (" + options.get(options.size() - 1 - i).getNumberOfVotes() + ")")
                                .color(colors.getSecondaryColorHEX());
                    }
                }
            }
            jb.text("\n                                                  \n")
                    .color(colors.getSecondaryColorHEX())
                    .effect(JsonBuilder.Effects.STRIKETHROUGH)
                    .toPlayer(onlinePlayer);
        }
    }
}
