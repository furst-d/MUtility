package com.mens.mutility.spigot.eventhandlers;

import com.google.common.collect.Iterables;
import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.messages.MessageChannel;
import com.mens.mutility.spigot.utils.NBTEditor;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class OnEntityPortalEvent implements Listener {
    private final MUtilitySpigot plugin;
    private final MessageChannel messageChannel;

    public OnEntityPortalEvent(MUtilitySpigot plugin) {
        this.plugin = plugin;
        messageChannel = new MessageChannel();
    }

    @EventHandler
    public void onEntityPortalEvent(EntityPortalEvent event) {
        event.setCancelled(true);
        Entity entity = event.getEntity();
        Location location = entity.getLocation();
        String entityTypeName = entity.getType().name();
        Optional<EntityType> entityTypeOpt = Arrays.stream(EntityType.values()).filter(et -> et.name().equals(entityTypeName)).findFirst();
        if(entityTypeOpt.isPresent()) {
            if(entity instanceof LivingEntity) {
                if(entityTypeOpt.get().getEntityClass() != null) {
                    /*entity.getWorld().spawn(location, entityTypeOpt.get().getEntityClass(), en -> {
                        editor.setNBT(en, nbt);
                    });*/
                    double x = location.getX();
                    double y = location.getY();
                    double z = location.getZ();
                    String world = Objects.requireNonNull(event.getFrom().getWorld()).getName();
                    NBTEditor editor = new NBTEditor();
                    if(world.equalsIgnoreCase("world") && Objects.requireNonNull(Objects.requireNonNull(event.getTo()).getWorld()).getName().equals("world_nether")) {
                        String nbt = editor.getNBT(entity).replace("Pos:[" + location.getX() + "d," + location.getY() + "d," + location.getZ() + "d]", "Pos:[" + (x / 8) + "d," + y + "d," + (z / 8) + "d]");
                        messageChannel.sendToBungeeCord(Iterables.getFirst(Bukkit.getOnlinePlayers(), null), "mens:entity-portal-request", "nether", String.valueOf(x / 8), String.valueOf(y), String.valueOf(z / 8), entityTypeName, nbt);
                    } else if(world.equalsIgnoreCase("world_nether") && Objects.requireNonNull(Objects.requireNonNull(event.getTo()).getWorld()).getName().equals("world")) {
                        String nbt = editor.getNBT(entity).replace("Pos:[" + location.getX() + "d," + location.getY() + "d," + location.getZ() + "d]", "Pos:[" + (x * 8) + "d," + y + "d," + (z * 8) + "d]");
                        messageChannel.sendToBungeeCord(Iterables.getFirst(Bukkit.getOnlinePlayers(), null), "mens:entity-portal-request", "nether", String.valueOf(x * 8), String.valueOf(y), String.valueOf(z * 8), entityTypeName, nbt);
                    }
                    entity.remove();
                }
            }
        }
    }


}
