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
    private final DatabaseTables tables;

    public Database(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        colors = new PluginColors();
        tables = new DatabaseTables();
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
            createTablesIfNotExists();
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



    private void createTablesIfNotExists() throws SQLException {
        PreparedStatement stm;
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getEventsTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, event_name varchar(255) NOT NULL, tpX double NOT NULL, tpY double NOT NULL, tpZ double NOT NULL, world varchar(255) NOT NULL, server varchar(255) NOT NULL, objective varchar(255), note varchar(255))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getInventoryTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, user_id int(8) NOT NULL, id_user_record int(4) NOT NULL, inventory_name varchar(255), inventory longtext NOT NULL, FOREIGN KEY (user_id) REFERENCES web_users(id))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getNavrhyTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, user_id int(8) NOT NULL, record_id int(4) NOT NULL, content longtext NOT NULL, rejected int(1) NOT NULL, rejected_reason varchar(255), accepted int(1) NOT NULL, admin_id int(8), create_date datetime NOT NULL, update_date datetime, FOREIGN KEY (user_id) REFERENCES web_users(id), FOREIGN KEY (admin_id) REFERENCES web_users(id))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getStavbaCompetitorsTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, season_id int(4) NOT NULL, building_id int(4) NOT NULL, forum_id int(12) NOT NULL, user_id int(8) NOT NULL, building_name varchar(255) NOT NULL, image varchar(255) NOT NULL, FOREIGN KEY (season_id) REFERENCES " + tables.getStavbaSeasonsTable() + "(id), FOREIGN KEY (forum_id) REFERENCES web_forum_posts(id), FOREIGN KEY (user_id) REFERENCES web_users(id))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getStavbaKeysTable() + "(user_id int(8) NOT NULL, confirm_key varchar(255) NOT NULL, FOREIGN KEY (user_id) REFERENCES web_users(id))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getStavbaSeasonsTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, season_id int(4) NOT NULL, admin_id int(8) NOT NULL, start_date datetime NOT NULL, close_date datetime NOT NULL, reward int(1) NOT NULL, web_new int(1) NOT NULL, active int(1) NOT NULL, description varchar(255) NOT NULL, FOREIGN KEY (admin_id) REFERENCES web_users(id))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getStavbaVotesTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, username varchar(255) NOT NULL, season_id int(5) NOT NULL, stavba_id int(5) NOT NULL, FOREIGN KEY (season_id) REFERENCES " + tables.getStavbaSeasonsTable() + "(season_id))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getZalohyTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, user_id int(8) NOT NULL, record_id int(4) NOT NULL, building_name varchar(255) NOT NULL, note varchar(255), rejected int(1) NOT NULL, rejected_reason varchar(255), completed int(1) NOT NULL, admin_id int(8) NOT NULL, world varchar(255) NOT NULL, posX double NOT NULL, posY double NOT NULL, posZ double NOT NULL, create_date date NOT NULL, update_date date, FOREIGN KEY (user_id) REFERENCES web_users(id), FOREIGN KEY (admin_id) REFERENCES web_users(id))");
        stm.execute();
        stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tables.getTeleportDataTable() + "(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, user_id int(8) NOT NULL, inventory longtext NOT NULL, fromX double NOT NULL, fromY double NOT NULL, fromZ double NOT NULL, fromWorld varchar(255) NOT NULL, fromServer varchar(255) NOT NULL, toX double, toY double, toZ double, toWorld varchar(255), toServer varchar(255), gamemode varchar(30) NOT NULL, exp float NOT NULL, level int(8) NOT NULL, hunger int(2) NOT NULL, health double NOT NULL, fly int(1) NOT NULL, effects longtext, created_date datetime NOT NULL, completed int(1) NOT NULL, FOREIGN KEY (user_id) REFERENCES web_users(id))");
        stm.execute();
        Bukkit.getConsoleSender().sendMessage(prefix.getMutilityPrefix(false, false) + "Chybejici tabulky vytvoreny!");
    }
}
