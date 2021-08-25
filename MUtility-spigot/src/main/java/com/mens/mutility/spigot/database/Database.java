package com.mens.mutility.spigot.database;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import org.bukkit.Bukkit;

import java.sql.*;

public class Database {
    private static Connection con;
    private final MUtilitySpigot plugin;
    private final Prefix prefix;
    private final PluginColors colors;

    public Database(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        colors = new PluginColors();
    }

    public Connection getCon() {
        return con;
    }

    public void openFirstConnection() {
        String HOST = plugin.getConfig().getString("MYSQL.Host");
        String PORT = plugin.getConfig().getString("MYSQL.Port");
        String DATABASE = plugin.getConfig().getString("MYSQL.Database");
        String USER = plugin.getConfig().getString("MYSQL.User");
        String PASSWORD = plugin.getConfig().getString("MYSQL.Password");

        String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?autoReconnect=true&useSSL=false&useUnicode=yes&characterEncoding=UTF-8&connectTimeout=30000&socketTimeout=30000&waitTimeout=30000&interactiveTimeout=30000";
        try {
            DriverManager.setLoginTimeout(2);
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            Bukkit.getConsoleSender().sendMessage(prefix.getMutilityPrefix(false, false) + "Databaze " + colors.getConsolePrimaryColor() + "MYSQL" + colors.getConsoleSecondaryColor() + " pripojena!");
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(prefix.getMutilityPrefix(false, false) + "Pripojeni k " + colors.getConsolePrimaryColor() + "MYSQL" + colors.getConsoleSecondaryColor() + " se nezdarilo!");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    public void openConnection() {
        String HOST = plugin.getConfig().getString("MYSQL.Host");
        String PORT = plugin.getConfig().getString("MYSQL.Port");
        String DATABASE = plugin.getConfig().getString("MYSQL.Database");
        String USER = plugin.getConfig().getString("MYSQL.User");
        String PASSWORD = plugin.getConfig().getString("MYSQL.Password");

        String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?autoReconnect=true&useSSL=false&useUnicode=yes&characterEncoding=UTF-8&connectTimeout=30000&socketTimeout=30000&waitTimeout=30000&interactiveTimeout=30000";
        try {
            DriverManager.setLoginTimeout(2);
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }
}
