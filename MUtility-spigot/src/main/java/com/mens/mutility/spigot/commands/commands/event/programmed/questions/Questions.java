package com.mens.mutility.spigot.commands.commands.event.programmed.questions;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.chat.json.JsonBuilder;
import com.mens.mutility.spigot.utils.Checker;
import com.mens.mutility.spigot.utils.PageList;
import com.mens.mutility.spigot.utils.Timer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Questions {
    private final List<PlayerVote> stillPlaying;
    private final List<Player> badAnswer;
    private final Checker checker;
    private final PluginColors colors;
    private final Prefix prefix;

    private String answer;
    private boolean activeQuestion;
    private boolean firstQuestion;
    private int questionNumber;

    private int correctAnswers;
    private int badAnswers;
    private String correctPlayers;
    private String badPlayers;

    public Questions(MUtilitySpigot plugin) {
        stillPlaying = new ArrayList<>();
        badAnswer = new ArrayList<>();
        checker = new Checker(plugin);
        colors = new PluginColors();
        prefix = new Prefix();

        answer = "";
        activeQuestion = false;
        firstQuestion = true;
        questionNumber = 0;

        correctAnswers = 0;
        badAnswers = 0;
        correctPlayers = "";
        badPlayers = "";
    }

    public void run(String question, String answer) {
        this.answer = answer;
        questionNumber++;
        PageList questPageList = new PageList(10, prefix.getCustomPrefix("Otázka", true, true), null);
        JsonBuilder hoverYes = new JsonBuilder(">> Kliknutím zahlasujete pro ")
                .color(colors.getSecondaryColorHEX())
                .text("Ano")
                .color(ChatColor.GREEN)
                .text(" <<")
                .color(colors.getSecondaryColorHEX());
        JsonBuilder hoverNo = new JsonBuilder(">> Kliknutím zahlasujete pro ")
                .color(colors.getSecondaryColorHEX())
                .text("Ne")
                .color(ChatColor.DARK_RED)
                .text(" <<")
                .color(colors.getSecondaryColorHEX());
        Bukkit.getOnlinePlayers().forEach(player -> {
            JsonBuilder text = new JsonBuilder("Otázka číslo ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.valueOf(questionNumber))
                    .color(colors.getPrimaryColorHEX())
                    .text(" zní:\n\n  ")
                    .color(colors.getSecondaryColorHEX())
                    .text(question)
                    .color(colors.getPrimaryColorHEX())
                    .effect(JsonBuilder.Effects.BOLD)
                    .text("\n\n  ");
            if(player.hasPermission("mutility.eventy.otazky.vote")) {
                if(isRegistered(player)) {
                    setVoted(player, false);
                }
                if(checker.chechEventLocation(player.getLocation(), "Otazky")) {
                    if(firstQuestion) {
                        registerPlayer(player);
                    }
                    if(isRegistered(player)) {
                        text.text("[")
                                .color(colors.getSecondaryColorHEX())
                                .effect(JsonBuilder.Effects.BOLD)
                                .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverYes.toString(), true)
                                .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event spust otazky vote ano")
                                .text("Ano")
                                .color(ChatColor.GREEN)
                                .effect(JsonBuilder.Effects.BOLD)
                                .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverYes.toString(), true)
                                .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event spust otazky vote ano")
                                .text("]")
                                .color(colors.getSecondaryColorHEX())
                                .effect(JsonBuilder.Effects.BOLD)
                                .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverYes.toString(), true)
                                .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event spust otazky vote ano")
                                .text("  [")
                                .color(colors.getSecondaryColorHEX())
                                .effect(JsonBuilder.Effects.BOLD)
                                .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverNo.toString(), true)
                                .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event spust otazky vote ne")
                                .text("Ne")
                                .color(ChatColor.DARK_RED)
                                .effect(JsonBuilder.Effects.BOLD)
                                .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverNo.toString(), true)
                                .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event spust otazky vote ne")
                                .text("]\n")
                                .color(colors.getSecondaryColorHEX())
                                .effect(JsonBuilder.Effects.BOLD)
                                .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverNo.toString(), true)
                                .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event spust otazky vote ne");
                    } else {
                        text.text("Již nemůžeš hlasovat!\n")
                                .color(ChatColor.DARK_RED);
                    }
                    questPageList.clear();
                    questPageList.add(text.getJsonSegments());
                    questPageList.getList(1).toPlayer(player);
                }
            }
        });
        unbregisterBad();
        runTimer();
        firstQuestion = false;
    }

    public void resetEvent() {
        firstQuestion = true;
        questionNumber = 0;
        unregisterEveryone();
    }

    public void resetKolo() {
        registerEveryoneBad();
        setEveryoneVoteTrue();
    }

    private void registerPlayer(Player player) {
        stillPlaying.add(new PlayerVote(player, false));
    }

    private void unregisterPlayer(Player player) {
        stillPlaying.removeIf(x -> x.getPlayer().getName().equals(player.getName()));
    }

    public boolean isRegistered(Player player) {
        return stillPlaying.stream().anyMatch(x -> x.getPlayer().getName().equals(player.getName()));
    }

    private boolean isRegisteredBadAnswer(Player player) {
        return badAnswer.stream().anyMatch(x -> x.getName().equals(player.getName()));
    }

    private void unbregisterBad() {
        badAnswer.clear();
    }

    private void unregisterUnvoted() {
        stillPlaying.removeIf(x -> !x.isVoted());
    }

    private void unregisterEveryone() {
        stillPlaying.clear();
        badAnswer.clear();
    }

    private void unregisterWrongPos(Player player) {
        stillPlaying.removeIf(x -> checker.chechEventLocation(player.getLocation(), "Otazky"));
        badAnswer.removeIf(x -> checker.chechEventLocation(player.getLocation(), "Otazky"));
    }

    private void setVoted(Player player, boolean voted) {
        stillPlaying.stream().filter(x -> x.getPlayer().getName().equals(player.getName())).forEach(x -> x.setVoted(voted));
    }

    private boolean didVoted(Player player) {
        return stillPlaying.stream().anyMatch(x -> (x.getPlayer().getName().equals(player.getName()) && x.isVoted()));
    }

    private boolean checkUnvoted() {
        if(!stillPlaying.isEmpty()) {
            return stillPlaying.stream().noneMatch(PlayerVote::isVoted);
        }
        return true;
    }

    private void setEveryoneVoteTrue() {
        stillPlaying.stream().filter(x -> !x.isVoted()).forEach(x -> setVoted(x.getPlayer(), true));
    }

    private void registerEveryoneBad() {
        badAnswer.forEach(x -> {
            registerPlayer(x.getPlayer());
            setVoted(x.getPlayer(), true);
        });
        badAnswer.clear();
    }

    public void vote(Player player, String answer) {
        if(activeQuestion) {
            if(didVoted(player)) {
                player.sendMessage(prefix.getEventPrefix(true, false) + "Již jsi hlasoval!");
            } else {
                if(answer.equalsIgnoreCase(this.answer)) {
                    setVoted(player, true);
                } else {
                    unregisterPlayer(player);
                    badAnswer.add(player);
                }
                player.sendMessage(prefix.getEventPrefix(true, false) + "Odpověď byla zaznamenána");
                player.sendMessage("\n");
            }
        }
    }

    private void runTimer() {
        activeQuestion = true;
        getTitle(String.valueOf(10));
        Timer timer = new Timer();
        timer.setOnFinish((sec, tt) -> {
            activeQuestion = false;
            getAnswer();
        });
        timer.setOnRunning((sec, tt) -> {
            if(sec < 10) {
                getTitle(String.valueOf(10 - sec));
            }
        });
        timer.startTimer(10);
    }

    private void getTitle(String title) {
        Bukkit.getOnlinePlayers().forEach(player -> getTitle(player, title, "§3Hlasuj kliknutím v chatu"));
    }

    private void getTitle(Player player, String title, String subtitle) {
        if(checker.chechEventLocation(player.getLocation(), "Otazky")) {
            player.sendTitle("§b§l" + title, subtitle, 0, 100, 0);
        }
    }

    private void getAnswer() {
        correctAnswers = 0;
        badAnswers = 0;
        correctPlayers = "";
        badPlayers = "";
        stillPlaying.forEach(playerVote -> {
            String subtitle = "";
            if(answer.equalsIgnoreCase("ano")) {
                subtitle = "§bSprávná odpověď byla: §aANO";
            } else if(answer.equalsIgnoreCase("ne")) {
                subtitle = "§bSprávná odpověď byla: §4NE";
            }
            if(playerVote.isVoted()) {
                getTitle(playerVote.getPlayer(), "§a§lSprávně", subtitle);
                if(correctAnswers == 0) {
                    correctPlayers += ChatColor.GREEN + "- " + playerVote.getPlayer().getName();
                } else {
                    correctPlayers += "\n" + ChatColor.GREEN + "- " + playerVote.getPlayer().getName();
                }
                correctAnswers++;
            } else {
                getTitle(playerVote.getPlayer(), "§4§lVypršel ti čas!", subtitle);
            }
        });

        badAnswer.forEach(player -> {
            String subtitle = "";
            if(answer.equalsIgnoreCase("ano")) {
                subtitle = "§bSprávná odpověď byla: §aANO";
            } else if(answer.equalsIgnoreCase("ne")) {
                subtitle = "§bSprávná odpověď byla: §4NE";
            }
            getTitle(player, "§4§lŠpatně", subtitle);
            if(badAnswers == 0) {
                badPlayers += ChatColor.DARK_RED + "- " + player.getName();
            } else {
                badPlayers += "\n" + ChatColor.DARK_RED + "- " + player.getName();
            }
            badAnswers++;
        });

        TextComponent TCcorrectAnswers = new TextComponent("§a➥Správné odpovědi: §3" + correctAnswers);
        TextComponent TCbadAnswers = new TextComponent("\n§4➥Špatné odpovědi: §3" + badAnswers + "\n");

        Bukkit.getOnlinePlayers().forEach(player -> {
            if(checker.chechEventLocation(player.getLocation(), "Otazky")) {
                if(player.hasPermission("mutility.eventy.otazky.vote")) {
                    if(!isRegistered(player)) {
                        if(!isRegisteredBadAnswer(player)) {
                            String subtitle;
                            if(answer.equalsIgnoreCase("ano")) {
                                subtitle = "§bSprávná odpověď byla: §aANO";
                            } else {
                                subtitle = "§bSprávná odpověď byla: §4NE";
                            }
                            getTitle(player, "", subtitle);
                        }
                    }
                    TCcorrectAnswers.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(correctPlayers)));
                    TCbadAnswers.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(badPlayers)));
                    player.spigot().sendMessage(TCcorrectAnswers, TCbadAnswers);
                }
            } else {
                unregisterWrongPos(player);
            }
        });
        if(stillPlaying.isEmpty() || checkUnvoted()) {
            registerEveryoneBad();
            setEveryoneVoteTrue();
            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> checker.chechEventLocation(player.getLocation(), "Otazky"))
                    .filter(player -> player.hasPermission("mutility.eventy.otazky.vote"))
                    .forEach(player -> player.sendMessage(prefix.getEventPrefix(true, false) + "Žádná správná odpověď, kolo se bude opakovat!"));
        }
        unregisterUnvoted();
        if(stillPlaying.size() == 1 && !firstQuestion) {
            String winner = stillPlaying.get(0).getPlayer().getName();
            PageList winnerPL = new PageList(10, prefix.getCustomPrefix("Výherce", true, true), null);
            winnerPL.add(new JsonBuilder("Výhercem se stává: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(winner)
                    .color(ChatColor.GREEN)
                    .effect(JsonBuilder.Effects.BOLD)
                    .getJsonSegments());
            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> checker.chechEventLocation(player.getLocation(), "Otazky"))
                    .filter(player -> player.hasPermission("mutility.eventy.otazky.vote"))
                    .forEach(player -> winnerPL.getList(1).toPlayer(player));
            resetEvent();
        }
    }

}
