package com.mens.mutility.spigot.commands.commands.mparticle;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticlePlayer {
    private static final List<ParticlePlayer> playerParticleList = new ArrayList<>();

    private final Player player;
    private final int recordId;

    public ParticlePlayer(Player player, int recordId) {
        this.player = player;
        this.recordId = recordId;
    }

    public Player getPlayer() {
        return player;
    }

    public int getRecordId() {
        return recordId;
    }

    public static void registerPlayer(Player player, int recordId){
        playerParticleList.add(new ParticlePlayer(player, recordId));
    }

    public static void unregisterPlayer(Player player) {
        playerParticleList.removeIf(pp -> pp.getPlayer().getName().equals(player.getName()));
    }

    public static boolean containsPlayerAndRecord(Player player, int recordId) {
        return playerParticleList.stream().anyMatch(pp -> pp.getPlayer().getName().equals(player.getName()) && pp.getRecordId() == recordId);
    }

    public static int getRunningId(Player player) {
        Optional<ParticlePlayer> optParticle = playerParticleList.stream().filter(pp -> pp.getPlayer().getName().equals(player.getName())).findFirst();
        return optParticle.map(ParticlePlayer::getRecordId).orElse(0);
    }
}
