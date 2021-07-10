package com.mens.mutility.spigot.utils;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.database.Database;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerManager {
    private final MUtilitySpigot plugin;
    private Database db;
    private final Prefix prefix;

    public PlayerManager(MUtilitySpigot plugin) {
        this.plugin = plugin;
        db = plugin.getDb();
        prefix = new Prefix();
    }

    public int getUserId(String username) {
        int id = 0;
        try {
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id FROM web_users WHERE username = ?");
            stm.setString(1, username);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                id = rs.getInt(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            getUserId(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public String getUsername(int id) {
        String username = "";
        try {
            PreparedStatement stm = db.getCon().prepareStatement("SELECT username FROM web_users WHERE id = ?");
            stm.setInt(1, id);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                username = rs.getString(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            getUserId(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }
}
