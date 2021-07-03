package com.mens.mutility.spigot.database;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.mens.mutility.spigot.MUtilitySpigot.db;

public class DatabaseMethods {
    public ResultSet sqlSelect(PreparedStatement stm) {
        try {
            return stm.executeQuery();
        } catch(CommunicationsException e) {
            db.openConnection();
            sqlSelect(stm);
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
        } catch(CommunicationsException e) {
            db.openConnection();
            getUserFromID(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return username;
    }
}
