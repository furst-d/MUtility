package com.mens.mutility.spigot.commands.commands.event.programmed.questions;

import org.bukkit.entity.Player;

public class PlayerVote {
    private final Player player;
    private boolean voted;

    public PlayerVote(Player player, boolean voted) {
        this.player = player;
        this.voted = voted;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }


}
