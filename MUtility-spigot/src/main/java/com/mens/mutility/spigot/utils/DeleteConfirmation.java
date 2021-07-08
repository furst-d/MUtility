package com.mens.mutility.spigot.utils;

import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.json.JsonBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class DeleteConfirmation {
    private final int id;
    private final Player player;
    private final Timer timer;
    private boolean finished;
    private final int TIME_IN_SEC;
    private JsonBuilder message;
    private final PluginColors colors;
    private final String command;

    public DeleteConfirmation(int id, Player player, String command) {
        this.id = id;
        this.player = player;
        this.command = command + " " + id;
        finished = false;
        TIME_IN_SEC = 60;
        colors = new PluginColors();
        timer = new Timer();
        timer.setOnFinish((sec, tt) -> {
            finished = true;
        });
    }

    public int getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
        if(finished) {
            timer.setRunning(false);
        }
    }

    public void setMessage(JsonBuilder message) {
        this.message = message;
    }

    public void startTimer() {
        timer.startTimer(TIME_IN_SEC);
        if(message == null) {
            message = new JsonBuilder();
        } else {
            message.text("\n");
        }
        message.text("Klikněte ")
               .color(colors.getSecondaryColorHEX())
               .text("➥Zde")
               .color(colors.getPrimaryColorHEX())
                .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT,
                        new JsonBuilder(">> Klikni pro ")
                                .color(colors.getSecondaryColorHEX())
                                .text("Potvrzení smazání")
                                .color(ChatColor.DARK_RED)
                                .text(".\nNa potvrzení máte ")
                                .color(colors.getSecondaryColorHEX())
                                .text(String.valueOf(TIME_IN_SEC))
                                .color(colors.getPrimaryColorHEX())
                                .text(" sekund <<")
                                .color(colors.getSecondaryColorHEX())
                                .toString(), true)
                .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, command)
               .text(" pro potvrzení smazání.")
               .color(colors.getSecondaryColorHEX())
               .toPlayer(player);
    }

}
