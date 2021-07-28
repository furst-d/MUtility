package com.mens.mutility.spigot.inventory;

import com.google.gson.JsonObject;
import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.database.Database;
import com.mens.mutility.spigot.database.DatabaseTables;
import com.mens.mutility.spigot.utils.MyStringUtils;
import com.mens.mutility.spigot.utils.PlayerManager;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class TeleportDataManager {
    private final MUtilitySpigot plugin;
    private final Database db;
    private final DatabaseTables tables;
    private final PlayerManager playerManager;
    private final MyStringUtils strUt;

    public TeleportDataManager() {
        plugin = MUtilitySpigot.getInstance();
        db = plugin.getDb();
        tables = new DatabaseTables();
        playerManager = new PlayerManager();
        strUt = new MyStringUtils();
    }

    public void saveData(Player player, double x, double y, double z, String world) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            TeleportData data = new TeleportData(plugin, player, x, y, z, world);
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("INSERT INTO " + tables.getTeleportDataTable() + " (user_id, inventory, fromX, fromY, fromZ, fromWorld, fromServer, gamemode, exp, level, hunger, health, fly, effects, created_date, completed) VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stm.setInt(1, data.getUserId());
            stm.setObject(2, data.getInventory());
            stm.setDouble(3, data.getFromX());
            stm.setDouble(4, data.getFromY());
            stm.setDouble(5, data.getFromZ());
            stm.setString(6, data.getFromWorld());
            stm.setString(7, data.getFromServer());
            stm.setString(8, data.getGamemode());
            stm.setFloat(9, data.getExp());
            stm.setInt(10, data.getLevel());
            stm.setInt(11, data.getFoodLevel());
            stm.setDouble(12, data.getHealth());
            stm.setInt(13, data.isAllowFlight() ? 1 : 0);
            stm.setObject(14, data.getEffectsToString());
            stm.setString(15, strUt.getCurrentFormattedDate());
            stm.setInt(16, 0);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            saveData(player, x, y, z, world);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateData(int id, double x, double y, double z, String world, String server) {
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
            updateData(id, x, y, z, world, server);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteOldPlayerData(Player player, int days) {
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
            deleteOldPlayerData(player, days);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public TeleportData loadNewestPlayerData(Player player) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("SELECT id, inventory, gamemode, exp, level, hunger, health, fly, effects FROM " + tables.getTeleportDataTable() + " WHERE user_id = ?  AND completed = 0 ORDER BY created_date DESC LIMIT 1");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            ResultSet rs =  stm.executeQuery();
            return getData(rs);
        } catch (CommunicationsException e) {
            db.openConnection();
            loadNewestPlayerData(player);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public TeleportData loadDataById(int id) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("SELECT id, inventory, gamemode, exp, level, hunger, health, fly, effects FROM " + tables.getTeleportDataTable() + " WHERE id = ?");
            stm.setInt(1, id);
            ResultSet rs =  stm.executeQuery();
            return getData(rs);
        } catch (CommunicationsException e) {
            db.openConnection();
            loadDataById(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private TeleportData getData(ResultSet rs) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            int health;
            boolean fly;
            Collection<PotionEffect> effects;
            if(rs.next()) {
                effects = new ArrayList<>();
                TeleportData data = new TeleportData(plugin);
                data.setId(rs.getInt(1));
                data.setInventory(rs.getString(2));
                data.setGamemode(rs.getString(3));
                data.setExp(rs.getFloat(4));
                data.setLevel(rs.getInt(5));
                data.setFoodLevel(rs.getInt(6));
                data.setHealth(rs.getDouble(7));
                data.setAllowFlight(rs.getInt(8) == 1);
                String effectsStr = rs.getString(9);
                String[] effectsSplitted = effectsStr == null ? new String[]{} : effectsStr.split(";");
                for(String effect : effectsSplitted) {
                    String[] attributes = effect.split(":");
                    effects.add(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(attributes[0])),
                            Integer.parseInt(attributes[1]),
                            Integer.parseInt(attributes[2]),
                            attributes[3].equalsIgnoreCase("true"),
                            attributes[4].equalsIgnoreCase("true"),
                            attributes[5].equalsIgnoreCase("true")));
                }
                data.setActivePotionEffects(effects);
                return data;
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            getData(rs);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void applyData(Player player, TeleportData data, double x, double y, double z, String world) {
        InventoryManager manager = new InventoryManager();
        int inventoryId = data.getId();
        JsonObject inventory = manager.toJsonObject(data.getInventory());
        String server = plugin.getCurrentServer();
        updateData(inventoryId, x, y, z, world, server);
        manager.loadInventory(player, inventory);
        player.setGameMode(GameMode.valueOf(data.getGamemode()));
        player.setLevel(data.getLevel());
        player.setExp(data.getExp());
        player.setFoodLevel(data.getFoodLevel());
        player.setHealth(data.getHealth());
        player.setAllowFlight(data.isAllowFlight());
        player.addPotionEffects(data.getActivePotionEffects());
    }
}
