package com.mens.mutility.spigot.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Checker {
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

    public boolean checkPermissions(String playerName, String permission) {
        Player player = Bukkit.getServer().getPlayer(playerName);
        return player.hasPermission(permission);
    }
}
