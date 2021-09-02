package com.mens.mutility.spigot.eventhandlers;

import com.google.common.collect.Iterables;
import com.mens.mutility.spigot.messages.MessageChannel;
import com.mens.mutility.spigot.utils.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;

import java.util.Objects;

public class OnEntityPortalEvent implements Listener {
    private final MessageChannel messageChannel;

    public OnEntityPortalEvent() {
        messageChannel = new MessageChannel();
    }

    @EventHandler
    public void onEntityPortalEvent(EntityPortalEvent event) {
        event.setCancelled(true);
        Entity entity = event.getEntity();
        Location location = entity.getLocation();
        String entityTypeName = entity.getType().name();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        String world = Objects.requireNonNull(event.getFrom().getWorld()).getName();
        NBTEditor editor = new NBTEditor();
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if(player != null) {
            if(world.equalsIgnoreCase("world") && Objects.requireNonNull(Objects.requireNonNull(event.getTo()).getWorld()).getName().equals("world_nether")) {
                String nbt = editor.getNBT(entity).replace("Pos:[" + location.getX() + "d," + location.getY() + "d," + location.getZ() + "d]", "Pos:[" + (x / 8) + "d," + y + "d," + (z / 8) + "d]");
                messageChannel.sendToBungeeCord(player,"mens:entity-portal-request", "nether", String.valueOf(x / 8), String.valueOf(y), String.valueOf(z / 8), entityTypeName, nbt);
            } else if(world.equalsIgnoreCase("world_nether") && Objects.requireNonNull(Objects.requireNonNull(event.getTo()).getWorld()).getName().equals("world")) {
                String nbt = editor.getNBT(entity).replace("Pos:[" + location.getX() + "d," + location.getY() + "d," + location.getZ() + "d]", "Pos:[" + (x * 8) + "d," + y + "d," + (z * 8) + "d]");
                messageChannel.sendToBungeeCord(player, "mens:entity-portal-request", "overworld", String.valueOf(x * 8), String.valueOf(y), String.valueOf(z * 8), entityTypeName, nbt);
            }
            entity.remove();
        }
    }


}
