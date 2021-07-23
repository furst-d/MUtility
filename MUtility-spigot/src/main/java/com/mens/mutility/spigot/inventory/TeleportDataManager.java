package com.mens.mutility.spigot.inventory;

import com.google.gson.JsonObject;
import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.database.Database;
import com.mens.mutility.spigot.database.DatabaseTables;
import com.mens.mutility.spigot.utils.MyStringUtils;
import com.mens.mutility.spigot.utils.PlayerManager;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeleportDataManager {
    private final Database db;
    private final DatabaseTables tables;
    private final PlayerManager playerManager;
    private final MyStringUtils strUt;

    public TeleportDataManager(MUtilitySpigot plugin) {
        db = plugin.getDb();
        tables = new DatabaseTables(plugin);
        playerManager = new PlayerManager(plugin);
        strUt = new MyStringUtils();
    }

    public void saveInventory(Player player, JsonObject inventory, double x, double y, double z, String world, String server) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("INSERT INTO " + tables.getTeleportDataTable() + " (user_id, inventory, fromX, fromY, fromZ, fromWorld, fromServer, gamemode, created_date, completed) VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            stm.setObject(2, inventory.toString());
            stm.setDouble(3, x);
            stm.setDouble(4, y);
            stm.setDouble(5, z);
            stm.setString(6, world);
            stm.setString(7, server);
            stm.setString(8, player.getGameMode().name());
            stm.setString(9, strUt.getCurrentFormattedDate());
            stm.setInt(10, 0);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            saveInventory(player, inventory, x, y, z, world, server);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateInventory(int id, double x, double y, double z, String world, String server) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("UPDATE " + tables.getTeleportDataTable() + " SET toX = ?, toY = ?, toZ = ?, toWorld = ?, toServer = ?, completed = ? WHERE id = ?");
            stm.setDouble(1, x);
            stm.setDouble(2, y);
            stm.setDouble(3, z);
            stm.setString(4, world);
            stm.setString(5, server);
            stm.setInt(6, 1);
            stm.setInt(7, id);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            updateInventory(id, x, y, z, world, server);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteOldInventories(int days) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("DELETE FROM " + tables.getTeleportDataTable() + " WHERE created_date < NOW() - INTERVAL ? DAY");
            stm.setInt(1, days);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            deleteOldInventories(days);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public TeleportData loadNewestPlayerInventory(Player player) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("SELECT id, inventory, gamemode FROM " + tables.getTeleportDataTable() + " WHERE user_id = ?  AND completed = 0 ORDER BY created_date DESC LIMIT 1");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            ResultSet rs =  stm.executeQuery();
            int id;
            String inventory;
            String gamemode;
            if(rs.next()) {
                id = rs.getInt(1);
                inventory = rs.getString(2);
                gamemode = rs.getString(3);
                return new TeleportData(id, inventory, gamemode);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            loadNewestPlayerInventory(player);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
