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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Survey {
    private MUtilityBungeeCord plugin;
    private final Prefix prefix;
    private final PluginColors colors;
    private String surveyName;
    private List<Option> options;
    private List<BossBar> bossBars;
    private List<Vote> votes;
    private List<String> permissedPlayers;
    private boolean isRunning;
    private int maxIndex;

    private int minute;
    private int second;
    private int tick;
    private int number;
    private int hour;
    private int currentTime;
    private ScheduledTask st;

    public Survey() {
        plugin = MUtilityBungeeCord.getInstance();
        prefix = new Prefix();
        colors = new PluginColors();
        surveyName = "";
        options = new ArrayList<>();
        bossBars = new ArrayList<>();
        votes = new ArrayList<>();
        permissedPlayers = new ArrayList<>();
        isRunning = false;
        maxIndex = 0;
        tick = 20;
    }

    public MUtilityBungeeCord getPlugin() {
        return plugin;
    }

    public void setPlugin(MUtilityBungeeCord plugin) {
        this.plugin = plugin;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    public PluginColors getColors() {
        return colors;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public List<BossBar> getBossBars() {
        return bossBars;
    }

    public void setBossBars(List<BossBar> bossBars) {
        this.bossBars = bossBars;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public List<String> getPermissedPlayers() {
        return permissedPlayers;
    }

    public void setPermissedPlayers(List<String> permissedPlayers) {
        this.permissedPlayers = permissedPlayers;
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

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public ScheduledTask getSt() {
        return st;
    }

    public void setSt(ScheduledTask st) {
        this.st = st;
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
        for (int i = 0; i < votes.size(); i++) {
            if(votes.get(i).getPlayer().equals(player)) {
                return true;
            }
        }
        return false;
    }

    private void timer(int time, BossBar bar) {
        number = time;
        currentTime = time;
        st = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            //Převedu si zadanou hodnotu v sekundách na dny/hodiny/minuty/sekundy
            while(number > 0) {
                if(number / 3600 > 0) {
                    number -= 3600;
                    hour++;
                }
                else if(number / 60 > 0) {
                    number -= 60;
                    minute++;
                }
                else {
                    second++;
                    number--;
                }
            }

            second--;
            currentTime--;
            if(second < 0) {
                minute--;
                second = 59;

                if(minute < 0) {
                    hour--;
                    minute = 59;
                }
            }
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
            if(!hasVoted(onlinePlayer)) {
                JsonBuilder jb = new JsonBuilder("\n                                                  \n")
                        .color(colors.getSecondaryColorHEX())
                        .effect(JsonBuilder.Effects.STRIKETHROUGH)
                        .text("\n  " + surveyName + "\n")
                        .color(colors.getPrimaryColorHEX())
                        .effect(JsonBuilder.Effects.BOLD);
                for (int i = 0; i < options.size(); i++) {
                    String voteHover = new JsonBuilder(">> Kliknutím zahlasuj pro ")
                            .color(colors.getSecondaryColorHEX())
                            .text(options.get(i).getOption())
                            .color(colors.getPrimaryColorHEX())
                            .text(" <<")
                            .color(colors.getSecondaryColorHEX())
                            .toString();
                    jb.text("\n   [")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, voteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/anketa vote " + options.get(i).getId())
                            .text("✔")
                            .color(ChatColor.GREEN)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, voteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/anketa vote " + options.get(i).getId())
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, voteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/anketa vote " + options.get(i).getId())
                            .text(" - " + options.get(i).getOption())
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
                    for (int j = 0; j < votes.size(); j++) {
                        if(i == 0) {
                            if(options.get(options.size() - 1 - i).getNumberOfVotes() == options.get(options.size() - 2 - i).getNumberOfVotes()) {
                                temp = new JsonBuilder()
                                        .text("\n   " + (i+1) + ". - " + options.get(options.size() - 1 - i).getOption())
                                        .color(colors.getSecondaryColorHEX())
                                        .text(" (➥" + options.get(options.size() - 1 - i).getNumberOfVotes() + ")")
                                        .color(colors.getSecondaryColorHEX());
                                if(votes.get(j).getId() == options.get(options.size() - 1 - i).getId()) {
                                    if (empty) {
                                        empty = false;
                                        votesHover.text("- " + votes.get(j).getPlayer().getName())
                                                .color(colors.getSecondaryColorHEX());
                                    } else {
                                        votesHover.text("\n- " + votes.get(j).getPlayer().getName())
                                                .color(colors.getSecondaryColorHEX());
                                    }
                                }
                            } else {
                                temp = new JsonBuilder()
                                        .text("\n   " + (i+1) + ". - ")
                                        .color(colors.getSecondaryColorHEX())
                                        .text(options.get(options.size() - 1 - i).getOption())
                                        .color(ChatColor.GREEN)
                                        .text(" (➥" + options.get(options.size() - 1 - i).getNumberOfVotes() + ")")
                                        .color(ChatColor.GREEN);
                                if(votes.get(j).getId() == options.get(options.size() - 1 - i).getId()) {
                                    if (empty) {
                                        empty = false;
                                        votesHover.text("- " + votes.get(j).getPlayer().getName())
                                                .color(ChatColor.GREEN);
                                    } else {
                                        votesHover.text("\n- " + votes.get(j).getPlayer().getName())
                                                .color(ChatColor.GREEN);
                                    }
                                }
                            }
                        } else {
                            temp = new JsonBuilder()
                                    .text("\n   " + (i+1) + ". - " + options.get(options.size() - 1 - i).getOption())
                                    .color(colors.getSecondaryColorHEX())
                                    .text(" (➥" + options.get(options.size() - 1 - i).getNumberOfVotes() + ")")
                                    .color(colors.getSecondaryColorHEX());
                            if(votes.get(j).getId() == options.get(options.size() - 1 - i).getId()) {
                                if (empty) {
                                    empty = false;
                                    votesHover.text("- " + votes.get(j).getPlayer().getName())
                                            .color(colors.getSecondaryColorHEX());
                                } else {
                                    votesHover.text("\n- " + votes.get(j).getPlayer().getName())
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
