package com.mens.mutility.spigot.database;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import org.bukkit.Bukkit;

import java.sql.*;

public class Database {
    private Connection con;
    private final MUtilitySpigot plugin;
    private final Prefix prefix;
    private final PluginColors colors;
    private final DatabaseTables tables;

    public Database(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        colors = new PluginColors();
        tables = new DatabaseTables(plugin);
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
            Bukkit.getConsoleSender().sendMessage(prefix.getMutilityPrefix(false, false) + " Databaze " + colors.getConsolePrimaryColor() + "MYSQL" + colors.getConsoleSecondaryColor() + " pripojena!");
            createTablesIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(prefix.getMutilityPrefix(false, false) + "Pripojeni k " + colors.getConsolePrimaryColor() + "MYSQL" + colors.getConsoleSecondaryColor() + " se nezdarilo!");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    private void createTablesIfNotExists() throws SQLException {
        PreparedStatement stm;
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getEventsTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, event_name varchar(255), tpX double, tpY double, tpZ double, world varchar(255), server varchar(255), necessaryItems varchar(255), forbiddenItems varchar(255), objective varchar(255), note varchar(255))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getInventoryTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, user_id int(4), id_user_record int(4), inventory_name varchar(255), inventory longtext)");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getNavrhyTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, username varchar(255), content varchar(255), create_date date, active int(1))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getStavbaCompetitorsTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, season_id int(4), building_id int(4), forum_id int(4), user_id int(8), building_name varchar(255), image varchar(255))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getStavbaKeysTable() + "(username varchar(255), confirm_key varchar(255))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getStavbaSeasonsTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, season_id int(4), admin_id int(4), start_date datetime, close_date datetime, reward int(1), web_new int(1), active int(1), description varchar(255))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getStavbaVotesTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, username varchar(255), season_id int(5), stavba_id int(5))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getZalohyTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, user_id int(11), record_id int(11), building_name varchar(255), note varchar(255), rejected int(11), rejected_reason varchar(255), completed int(11), admin_id int(11), world varchar(255), posX double, posY double, posZ double, create_date date, update_date date)");
        stm.execute();
        Bukkit.getConsoleSender().sendMessage(prefix.getMutilityPrefix(false, false) + "Chybejici tabulky vytvoreny!");
    }
}
