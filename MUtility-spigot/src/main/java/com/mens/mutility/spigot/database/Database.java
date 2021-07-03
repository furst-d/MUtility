package com.mens.mutility.spigot.database;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.chat.json.JsonBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;

import java.sql.*;

public class Database {
    private Connection con;
    private final MUtilitySpigot plugin;
    private final String tablePrefix;
    Prefix prefix = new Prefix();
    PluginColors colors = new PluginColors();

    public Database(MUtilitySpigot plugin) {
        this.plugin = plugin;
        tablePrefix = prefix.getTablePrefix(plugin);
    }

    public Connection getCon() {
        return con;
    }

    public void openConnection() {
        String HOST = plugin.getConfig().getString("MYSQL.Host");
        String PORT = plugin.getConfig().getString("MYSQL.Port");
        String DATABASE = plugin.getConfig().getString("MYSQL.Database");
        String USER = plugin.getConfig().getString("MYSQL.User");
        String PASSWORD = plugin.getConfig().getString("MYSQL.Password");

        String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?autoReconnect=true&useSSL=false&useUnicode=yes&characterEncoding=UTF-8";
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);

            JsonBuilder jb = new JsonBuilder();
            jb.text("Databáze ")
                    .color(ChatColor.AQUA)
                    .text("MYSQL")
                    .color(colors.getPrimaryColorHEX())
                    .text(" pripojena!")
                    .color(colors.getSecondaryColorHEX());

            Bukkit.getConsoleSender().spigot().sendMessage(ComponentSerializer.parse(jb.toString()));


            Bukkit.getConsoleSender().sendMessage(prefix.getMutilityPrefix(true, false) + " Databaze " + colors.getPrimaryColor() + "MYSQL" + colors.getSecondaryColor() + " pripojena!");
            new JsonBuilder()
                    .addJsonSegment(prefix.getMutilityPrefix(true, true))
                    .text("Databáze ")
                    .color(colors.getSecondaryColorHEX())
                    .text("MySQL")
                    .color(colors.getPrimaryColorHEX())
                    .text(" připojena!")
                    .color(colors.getSecondaryColorHEX())
                    .toConsole();

            createTablesIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(prefix.getMutilityPrefix(true, false) + "Pripojeni k " + colors.getPrimaryColor() + "MYSQL" + colors.getSecondaryColor() + " se nezdarilo!");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    private void createTablesIfNotExists() throws SQLException {
        PreparedStatement stm;
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tablePrefix + "events(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, event_name varchar(255), tpX double, tpY double, tpZ double, world varchar(255), server varchar(255), necessaryItems varchar(255), forbiddenItems varchar(255), objective varchar(255), note varchar(255))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tablePrefix + "inventory(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, username varchar(255), id_user_record int(4), inventory_name varchar(255), inventory longtext)");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tablePrefix + "navrhy(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, username varchar(255), content varchar(255), create_date date, active int(1))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tablePrefix + "stavba_competitors(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, season_id int(4), building_id int(4), forum_id int(4), user_id int(8), building_name varchar(255), image varchar(255))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tablePrefix + "stavba_keys(username varchar(255), confirm_key varchar(255))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tablePrefix + "stavba_seasons(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, season_id int(4), admin_id int(4), start_date datetime, close_date datetime, reward int(1), web_new int(1), active int(1), description varchar(255))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tablePrefix + "stavba_votes(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, username varchar(255), season_id int(5), stavba_id int(5))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tablePrefix + "zalohy(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, user_id int(11), record_id int(11), building_name varchar(255), note varchar(255), rejected int(11), rejected_reason varchar(255), completed int(11), admin_id int(11), world varchar(255), posX double, posY double, posZ double, create_date date, update_date date)");
        stm.execute();
        Bukkit.getConsoleSender().sendMessage(prefix.getMutilityPrefix(true, false) + "Chybejici tabulky vytvoreny!");
    }
}
