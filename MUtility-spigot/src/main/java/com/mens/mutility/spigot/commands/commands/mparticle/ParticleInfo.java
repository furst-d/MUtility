package com.mens.mutility.spigot.commands.commands.mparticle;

import com.mens.mutility.spigot.commands.commands.mparticle.enums.CustomStyles;
import com.mens.mutility.spigot.commands.commands.mparticle.enums.Particles;
import com.mens.mutility.spigot.commands.commands.mparticle.enums.Styles;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParticleInfo {
    private int id;
    private int recordID;
    private Player player;
    private Particles particle;
    private Styles style;
    private CustomStyles customStyle;
    private RGB color;
    private Location location;
    private String server;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Particles getParticle() {
        return particle;
    }

    public void setParticle(Particles particle) {
        this.particle = particle;
    }

    public Styles getStyle() {
        return style;
    }

    public void setStyle(Styles style) {
        this.style = style;
    }

    public CustomStyles getCustomStyle() {
        return customStyle;
    }

    public void setCustomStyle(CustomStyles customStyle) {
        this.customStyle = customStyle;
    }

    public RGB getColor() {
        return color;
    }

    public void setColor(RGB color) {
        this.color = color;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
