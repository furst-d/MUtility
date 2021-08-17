package com.mens.mutility.spigot.eventhandlers;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.commands.commands.tpdata.Tpdata;
import com.mens.mutility.spigot.messages.MessageChannel;

import com.mens.mutility.spigot.utils.Timer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class OnLeavingEndEvent implements Listener {
    private final MUtilitySpigot plugin;
    private final MessageChannel messageChannel;
    private final Tpdata teleportDataManager;
    private final List<Player> conectingPlayers;
    private final LinkedList<Player> players;

    public OnLeavingEndEvent(MUtilitySpigot plugin) {
        this.plugin = plugin;
        messageChannel = new MessageChannel();
        teleportDataManager = new Tpdata(plugin);
        conectingPlayers = new ArrayList<>();
        players = new LinkedList<>();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if(players.remove(player)){
            if(!plugin.getServers().isEmpty()) {
                if(conectingPlayers.stream().noneMatch(playerLoc -> playerLoc.getName().equals(player.getName()))) {
                    conectingPlayers.add(player);
                    Location loc = player.getLocation();
                    e.setRespawnLocation(loc);
                    Timer timer = new Timer();
                    timer.setOnFinish((sec, tt) -> conectingPlayers.removeIf(playerLoc -> playerLoc.getName().equals(player.getName())));
                    teleportDataManager.saveData(player, loc.getX(), loc.getY(), loc.getZ(), Objects.requireNonNull(loc.getWorld()).getName());
                    teleportDataManager.deleteOldPlayerData(player, 30);
                    messageChannel.sendPortalInfoToBungeeCord(player, "mens:portalRequest", "lobby", 0, 0, 0);
                    timer.startTimer(5);
                }
            }
        }
    }

    @EventHandler
    public void onPortal(EntityPortalEnterEvent e) {
        if(e.getEntity() instanceof Player
                && e.getLocation().getBlock().getType() == Material.END_PORTAL
                && Objects.requireNonNull(e.getLocation().getWorld()).getName().equals("world_the_end")) {
            players.add((Player) e.getEntity());
        }
    }
}
