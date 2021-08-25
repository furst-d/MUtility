package com.mens.mutility.bungeecord.utils;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import com.mens.mutility.bungeecord.database.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerManager {
    private final Database db;

    public PlayerManager() {
        db = MUtilityBungeeCord.getInstance().getDb();
    }

    public int getUserId(String username) {
        int id = 0;
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id FROM web_users WHERE username = ?");
            stm.setString(1, username);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                id = rs.getInt(1);
            }
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }
}
