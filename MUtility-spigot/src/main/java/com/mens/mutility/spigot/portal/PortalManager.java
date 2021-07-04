package com.mens.mutility.spigot.portal;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.BlockUtil.Rectangle;
import net.minecraft.server.v1_16_R3.EnumDirection;
import net.minecraft.server.v1_16_R3.PortalTravelAgent;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PortalManager {
    private Player player;
    private final Location searchLocation;
    private final PortalTravelAgent portalAgent;
    private final int createRadius;
    private Optional<Rectangle> portalRect;
    private final Location portalLocation;

    public PortalManager(Player player, Location searchLocation) {
        this.player = player;
        this.searchLocation = searchLocation;
        portalAgent = new PortalTravelAgent(((CraftWorld)player.getWorld()).getHandle());
        createRadius = 16;
        portalRect = Optional.empty();
        portalLocation = player.getLocation();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getCreateRadius() {
        return createRadius;
    }

    public Location getPortalLocation() {
        return portalLocation;
    }

    public void setPortalRect(Optional<Rectangle> portalRect) {
        this.portalRect = portalRect;
    }

    public void findPortal() {
        setPortalRect(portalAgent.findPortal(new BlockPosition(searchLocation.getX(), searchLocation.getY(), searchLocation.getZ()), 128));
    }

    public void createPortal() {
        int createRadius = getCreateRadius();
        while(!portalRect.isPresent() && createRadius <= 512) {
            portalRect = portalAgent.createPortal(new BlockPosition(searchLocation.getX(), searchLocation.getY(), searchLocation.getZ()), EnumDirection.EnumAxis.X, ((CraftPlayer) player).getHandle(), createRadius);
            if(!portalRect.isPresent()) {
                portalRect = portalAgent.createPortal(new BlockPosition(searchLocation.getX(), searchLocation.getY(), searchLocation.getZ()), EnumDirection.EnumAxis.Z, ((CraftPlayer) player).getHandle(), createRadius);
            }
            System.out.println("Zvysuji radius na: " + createRadius);
            createRadius += 16;
        }
        setPortalRect(portalRect);
    }

    public boolean isPrepared() {
        if(portalRect.isPresent()) {
            portalLocation.setX(portalRect.get().origin.getX() + 0.5);
            portalLocation.setY(portalRect.get().origin.getY());
            portalLocation.setZ(portalRect.get().origin.getZ() + 0.5);
            portalLocation.setWorld(searchLocation.getWorld());
            return true;
        }
        return false;
    }
}
