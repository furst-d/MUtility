package com.mens.mutility.spigot.commands.commands.mstavba;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.chat.json.JsonBuilder;
import com.mens.mutility.spigot.database.Database;
import com.mens.mutility.spigot.database.DatabaseTables;
import com.mens.mutility.spigot.utils.*;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MStavbaVoteManager {
    private static boolean active;
    private static int seasonId;

    private final Database db;
    private final DatabaseTables tables;
    private final PlayerManager playerManager;
    private final MyStringUtils strUt;
    private final Prefix prefix;
    private final PluginColors colors;

    public MStavbaVoteManager(MUtilitySpigot plugin) {
        db = plugin.getDb();
        tables = new DatabaseTables();
        seasonId = getMaxSeason();
        playerManager = new PlayerManager();
        strUt = new MyStringUtils();
        prefix = new Prefix();
        colors = new PluginColors();
    }

    public void setActive(boolean activeLoc) {
        active = activeLoc;
    }

    public void setSeasonId(int seasonIdLoc) {
        seasonId = seasonIdLoc;
    }

    public boolean isActive() {
        return active;
    }

    public void createVoteLink(Player player) {
        if(active) {
            int voteNumber = getPlayerVoteNumber(player, seasonId);
            if(voteNumber == 0) {
                String key = strUt.generateKey();
                insertPlayerKey(player, key);
                PageList list = new PageList(10, prefix.getCustomPrefix("Stavba měsíce", true, true), null);
                list.add(new JsonBuilder("Právě probíhá ")
                        .color(colors.getSecondaryColorHEX())
                        .text("Stavba měsíce")
                        .color(colors.getPrimaryColorHEX())
                        .text("!")
                        .color(colors.getSecondaryColorHEX())
                        .getJsonSegments());
                list.add(new JsonBuilder("Klikni ")
                        .color(colors.getSecondaryColorHEX())
                        .text("➥Zde")
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, new JsonBuilder(">> Klikni pro ")
                                .color(colors.getSecondaryColorHEX())
                                .text("Hlasování")
                                .color(colors.getPrimaryColorHEX())
                                .text(" o Stavbě měsíce <<")
                                .color(colors.getSecondaryColorHEX())
                                .toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.OPEN_URL, "https://kostkuj.menshons.cz/stavba_mesice/index.php?username=" + player.getName() + "&confirm_key=" + key)
                        .text(" a hlasuj pro stavbu, která se ti nejvíce líbí.")
                        .color(colors.getSecondaryColorHEX())
                        .getJsonSegments());
                list.add(new JsonBuilder("Za hlasování obdržíš ")
                        .color(colors.getSecondaryColorHEX())
                        .text("20 Craft-Coinů")
                        .color(colors.getPrimaryColorHEX())
                        .text(".")
                        .color(colors.getSecondaryColorHEX())
                        .getJsonSegments());
                list.getList(1, null).toPlayer(player);
                Timer timer = new Timer();
                timer.setOnFinish((sec, tt) -> deletePlayerKey(player));
                timer.startTimer(300);
            }
        }
    }

    private int getMaxSeason() {
        int season_id = 0;
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT COALESCE(MAX(season_id), 0) FROM "+ tables.getStavbaSeasonsTable());
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                season_id = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return season_id;
    }

    private int getPlayerVoteNumber(Player player, int seasonId) {
        int votes = 0;
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT COUNT(username) FROM " + tables.getStavbaVotesTable() + " WHERE username = ? AND season_id = ?");
            stm.setString(1, player.getName());
            stm.setInt(2, seasonId);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                votes = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return votes;
    }

    private void deletePlayerKey(Player player) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("DELETE FROM " + tables.getStavbaKeysTable() + " WHERE user_id = ?");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            stm.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void insertPlayerKey(Player player, String key) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("INSERT INTO " + tables.getStavbaKeysTable() + " (user_id, confirm_key) VALUES (?, ?)");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            stm.setString(2, key);
            stm.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void synchronizeActive() {
        String closeDate = "1900-01-01 00:00:00.0";
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT close_date FROM " + tables.getStavbaSeasonsTable() + " WHERE active = 1");
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                closeDate = rs.getString(1);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime close_date = LocalDateTime.parse(closeDate, formatter);
            LocalDateTime now = LocalDateTime.now();
            active = now.isBefore(close_date);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
