package com.mens.mutility.bungeecord.utils;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CraftCoinManager {
    private final MUtilityBungeeCord plugin;
    private final Connection con;

    public CraftCoinManager() {
        this.plugin = MUtilityBungeeCord.getInstance();
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
}
