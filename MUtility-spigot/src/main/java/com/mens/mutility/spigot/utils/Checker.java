package com.mens.mutility.spigot.utils;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.commands.commands.mparticle.enums.CustomStyles;
import com.mens.mutility.spigot.commands.commands.mparticle.enums.Particles;
import com.mens.mutility.spigot.commands.commands.mparticle.enums.Styles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Objects;

public class Checker {
    private final MUtilitySpigot plugin;

    public Checker() {
        this.plugin = MUtilitySpigot.getInstance();
    }

    public boolean checkInt(String number) {
        try {
            Integer.valueOf(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean checkDouble(String number) {
        try {
            Double.valueOf(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean checkFloat(String number) {
        try {
            Float.valueOf(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean checkDate(String date) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.BASIC_ISO_DATE;
        try {
            date = date.replace("-", "");
            LocalDate.parse(date, dateFormatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public boolean checkOnlinePlayer(String playerName) {
        Player player = Bukkit.getServer().getPlayer(playerName);
        return player != null;
    }

    public boolean checkPermissions(CommandSender sender, String permission) {
        if(sender instanceof Player) {
            Player player = Bukkit.getServer().getPlayer(sender.getName());
            if(permission.contains(";")) {
                String[] permissions = permission.split(";");
                for (String perm : permissions) {
                    if (Objects.requireNonNull(player).hasPermission(perm)) {
                        return true;
                    }
                }
                return false;
            } else {
                return Objects.requireNonNull(player).hasPermission(permission);
            }
        }
        return true;
    }

    public boolean checkPositiveInt(String number) {
        try {
            int numberInt = Integer.parseInt(number);
            return numberInt > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean chechEventLocation(Location location, String event) {
        int posX1 = plugin.getEvents().getData().getInt(event + ".PosX1");
        int posX2 = plugin.getEvents().getData().getInt(event + ".PosX2");
        int posY1 = plugin.getEvents().getData().getInt(event + ".PosY1");
        int posY2 = plugin.getEvents().getData().getInt(event + ".PosY2");
        int posZ1 = plugin.getEvents().getData().getInt(event + ".PosZ1");
        int posZ2 = plugin.getEvents().getData().getInt(event + ".PosZ2");
        String world = plugin.getEvents().getData().getString(event + ".World");
        int pom;
        if(posX1 > posX2) {
            pom = posX1;
            posX1 = posX2;
            posX2 = pom;
        }
        if(posY1 > posY2) {
            pom = posY1;
            posY1 = posY2;
            posY2 = pom;
        }
        if(posZ1 > posZ2) {
            pom = posZ1;
            posZ1 = posZ2;
            posZ2 = pom;
        }
        return (location.getX() >= posX1)
                && (location.getX() <= posX2)
                && (location.getY() >= posY1)
                && (location.getY() <= posY2)
                && (location.getZ() >= posZ1)
                && (location.getZ() <= posZ2)
                && (Objects.requireNonNull(location.getWorld()).getName().equals(world));
    }

    public boolean checkBlock(String block) {
        return Arrays.stream(Material.values()).anyMatch(material -> material.toString().toLowerCase().equalsIgnoreCase(block));
    }

    public boolean checkWorld(String worldName) {
        return Bukkit.getWorlds().stream().anyMatch(world -> world.getName().equalsIgnoreCase(worldName));
    }

    public boolean checkServer(String serverName) {
        return plugin.getServers().stream().anyMatch(server -> server.getName().equalsIgnoreCase(serverName));
    }

    public boolean checkParticleStyle(String style) {
        return Arrays.stream(Styles.values()).anyMatch(styleLoc -> styleLoc.getName().equalsIgnoreCase(style));
    }

    public boolean checkParticle(String particle) {
        return Arrays.stream(Particles.values()).anyMatch(particleLoc -> particleLoc.getName().equalsIgnoreCase(particle));
    }

    public boolean checkCustomStyle(String style) {
        return Arrays.stream(CustomStyles.values()).anyMatch(styleLoc -> styleLoc.getName().equalsIgnoreCase(style));
    }
}
