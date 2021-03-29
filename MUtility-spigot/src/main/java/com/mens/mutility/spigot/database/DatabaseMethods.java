package com.mens.mutility.spigot.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.mens.mutility.spigot.MUtilitySpigot.db;

public class DatabaseMethods {
    public ResultSet sqlSelect(PreparedStatement stm) {
        try {
            return stm.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public String getUserFromID(int id) {
        String username = "";
        try {
            PreparedStatement stm = db.getCon().prepareStatement("SELECT username FROM web_users WHERE id = ?");
            stm.setInt(1, id);
            ResultSet rs =  sqlSelect(stm);
            if(rs.next()) {
                username = rs.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return username;
    }
}
