package com.mens.mutility.spigot.utils;

import com.mens.mutility.spigot.MUtilitySpigot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CraftCoinManager {
    private final MUtilitySpigot plugin;
    private final Connection con;

    public CraftCoinManager() {
        this.plugin = MUtilitySpigot.getInstance();
        con = plugin.getDb().getCon();
    }

    public void addCC(int amount, String username, int type) {
        try {
            if(!plugin.getDb().getCon().isValid(0)) {
                plugin.getDb().openConnection();
            }
            LocalDateTime myDateObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String date = myDateObj.format(myFormatObj);

            PreparedStatement stm = con.prepareStatement("SELECT id FROM web_users WHERE username = ?");
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                int id = rs.getInt(1);
                stm = con.prepareStatement("INSERT INTO shop_transactions (user_id, created_date, amount, type_id) VALUES (?, ?, ?, ?)");
                stm.setInt(1, id);
                stm.setString(2, date);
                stm.setInt(3, amount);
                stm.setInt(4, type);
                stm.execute();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void removeCC(int amount, String username, int type) {
        addCC(-amount, username, type);
    }

    public int getCC(String username) {
        try {
            if(!plugin.getDb().getCon().isValid(0)) {
                plugin.getDb().openConnection();
            }
            PreparedStatement stm = con.prepareStatement("SELECT SUM(amount) FROM shop_transactions WHERE user_id = ?");
            stm.setInt(1, new PlayerManager().getUserId(username));
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }
}
