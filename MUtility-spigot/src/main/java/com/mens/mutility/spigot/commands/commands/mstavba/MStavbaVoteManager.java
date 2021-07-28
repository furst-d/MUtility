package com.mens.mutility.spigot.commands.commands.mstavba;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.chat.json.JsonBuilder;
import com.mens.mutility.spigot.database.Database;
import com.mens.mutility.spigot.database.DatabaseTables;
import com.mens.mutility.spigot.discord.DiscordManager;
import com.mens.mutility.spigot.utils.*;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MStavbaVoteManager {
    private static boolean active;
    private static int seasonId;
    private static final Timer timer = new Timer();

    private final MUtilitySpigot plugin;
    private final Database db;
    private final DatabaseTables tables;
    private final PlayerManager playerManager;
    private final MyStringUtils strUt;
    private final Prefix prefix;
    private final PluginColors colors;

    public MStavbaVoteManager(MUtilitySpigot plugin) {
        this.plugin = plugin;
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
                list.getList(1).toPlayer(player);
                Timer timer = new Timer();
                timer.setOnFinish((sec, tt) -> deletePlayerKey(player));
                timer.startTimer(300);
            }
        }
    }

    public void startTimer() {
        timer.setOnRunning((sec, tt) -> {
            if(sec % 900 == 0) {
                if(!stillActive()) {
                    active = false;
                    endCompetition();
                    tt.cancel();
                }
            }
            if(sec % 3600 == 0) {
                Bukkit.getOnlinePlayers().forEach(this::createVoteLink);
            }
        });
        timer.startTimer(-1);
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
        } catch (CommunicationsException e) {
            db.openConnection();
            return getMaxSeason();
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
        } catch (CommunicationsException e) {
            db.openConnection();
            return getPlayerVoteNumber(player, seasonId);
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
        } catch (CommunicationsException e) {
            db.openConnection();
            deletePlayerKey(player);
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
        } catch (CommunicationsException e) {
            db.openConnection();
            insertPlayerKey(player, key);
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            LocalDateTime close_date = LocalDateTime.parse(closeDate, formatter);
            LocalDateTime now = LocalDateTime.now();
            active = now.isBefore(close_date);
        } catch (CommunicationsException e) {
            db.openConnection();
            synchronizeActive();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteKeys() {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("DELETE FROM " + tables.getStavbaKeysTable());
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            deleteKeys();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private boolean stillActive() {
        String closeDateStr = "";
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        LocalDateTime closeDate = null;
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT close_date FROM " + tables.getStavbaSeasonsTable() + " WHERE active = 1");
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                closeDateStr = rs.getString(1);
            }
            closeDate = LocalDateTime.parse(closeDateStr, formatter);
        } catch (CommunicationsException e) {
            db.openConnection();
            return stillActive();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assert closeDate != null;
        return now.isBefore(closeDate);
    }

    public void endCompetition() {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT building_id i, building_name, user_id, image, (SELECT COUNT(id) FROM " + tables.getStavbaVotesTable() + " WHERE stavba_id = i AND season_id = ?) hlasy, forum_id FROM " + tables.getStavbaCompetitorsTable() + " WHERE season_id = ? GROUP BY building_id ORDER BY hlasy DESC");
            int season = getMaxSeason();
            String seasonDesc = getBuildingDesc(season);
            stm.setInt(1, season);
            stm.setInt(2, season);
            ResultSet rs =  stm.executeQuery();
            sendToDiscord(rs, seasonDesc);
            rs.beforeFirst();
            addCC(rs);
            setReward();
            rs.beforeFirst();
            createWebNews(rs, seasonDesc);
            setWebNews();
        } catch (CommunicationsException e) {
            db.openConnection();
            endCompetition();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void createWebNews(ResultSet rs, String seasonDesc) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            int buildingId;
            String buildingName;
            int userId;
            String image;
            int votes;
            int forumId;
            StringBuilder webBuilder = new StringBuilder();
            int i = 1;
            while(rs.next()) {
                buildingId = rs.getInt(1);
                buildingName = rs.getString(2);
                userId = rs.getInt(3);
                image = rs.getString(4);
                votes = rs.getInt(5);
                forumId = rs.getInt(6);
                webBuilder.append("<table align=\"center\" style=\"margin-bottom: 75px\"><tr><td valign=\"top\" colspan=\"2\" style=\"padding:0 15px\"><h2 align=\"center\">");
                webBuilder.append("<b>").append(i).append(". místo </b></h2></td></tr><tr><td valign=\"top\" style=\"padding:0 15px\"><h3>Stavba číslo: ");
                webBuilder.append("<b>").append(buildingId).append("</b></h3></td><td rowspan=\"9\" style=\"padding:0 15px\"><img src=\"").append(image).append("\" width=\"400px\">");
                webBuilder.append("</td></tr><tr><td style=\"padding:0 15px\"><h3>Název stavby: <b>").append(buildingName).append("</b></h3></td></tr>\n").append("                                <tr><td style=\"padding:0 15px\"><h3>Autor: <b>").append(playerManager.getUsername(userId)).append("</b></h3></td></tr><tr><td style=\"padding:0 15px\">\n").append("                                        <h3>Přihláška <b><a href=\"https://kostkuj.cz/forum/").append(forumId).append("\" \n").append("                                            target=\"_blank\">Zde</a></b></h3></td></tr>\n").append("                                <tr><td style=\"padding:0 15px\">&nbsp;</td></tr><tr><td style=\"padding:0 15px\">&nbsp;</td></tr>\n").append("                                <tr><td style=\"padding:0 15px\">&nbsp;</td></tr>\n").append("                                <tr><td style=\"padding:0 15px\"><h3>Počet hlasů: <b>").append(votes).append("</b>\n").append("                                        </h3></td></tr><tr><td>&nbsp;</td></tr></table>");
                i++;
            }
            webBuilder.append("<h3 align=\"center\">Výherci byla připsána odměna</h3>\n" +
                    "                                        <h3 align=\"center\">Své štěstí můžete zkusit v dalším kole, přihlášky lze zasílat již nyní</h3>");
            PreparedStatement stm = db.getCon().prepareStatement("INSERT INTO web_news (user_id, title, body, img, created_date, description) VALUES (?, ?, ?, ?, ?, ?)");
            stm.setInt(1, 11);
            stm.setString(2, "Vyhlášení stavby měsíce");
            stm.setString(3, webBuilder.toString());
            stm.setString(4, "stavba_mesice12020-06-30_21-24-42.png");
            stm.setString(5, strUt.getCurrentFormattedDate());
            stm.setString(6, seasonDesc);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            createWebNews(rs, seasonDesc);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private String getBuildingDesc(int seasonId) {
        String desc = "";
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT description FROM "+ tables.getStavbaSeasonsTable() + " WHERE season_id = ?");
            stm.setInt(1, seasonId);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                desc = rs.getString(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            return getBuildingDesc(seasonId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return desc;
    }

    private void sendToDiscord(ResultSet rs, String seasonDesc) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            String buildingName;
            int userId;
            String image;
            int votes;
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Vyhlášení Stavby měsíce za " + seasonDesc);
            builder.setColor(Color.decode(colors.getPrimaryColorHEX()));
            int i = 1;
            while(rs.next()) {
                buildingName = rs.getString(2);
                userId = rs.getInt(3);
                image = rs.getString(4);
                votes = rs.getInt(5);

                String suffix = " hlasů";
                switch(votes) {
                    case 1:
                        suffix = " hlas";
                        break;
                    case 2:
                    case 3:
                    case 4:
                        suffix = " hlasy";
                        break;
                }

                builder.addField(i + ". místo", playerManager.getUsername(userId) + " se stavbou " + buildingName + " (" + votes + suffix + ")", false);
                if(i == 1) {
                    builder.setImage(image);
                }
                i++;
            }
            builder.addField("", "Gratulujeme a připisujeme výhru 2000 CC.", false);
            builder.setFooter("Nestihli jste minulé kolo? Nevadí! Již nyní lze zasílat přihlášky za tento měsíc.");
            DiscordManager manager = new DiscordManager();
            String channelName = plugin.getConfig().getString("Discord.Rooms.Infoline");
            manager.sendEmbedMessage(manager.getChannelByName(channelName), builder.build());
        } catch (CommunicationsException e) {
            db.openConnection();
            sendToDiscord(rs, seasonDesc);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void addCC(ResultSet rs) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            int userId;
            if(rs.next()) {
                userId = rs.getInt(3);
                CraftCoinManager ccManager = new CraftCoinManager(plugin);
                ccManager.addCC(2000, playerManager.getUsername(userId), 13);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            addCC(rs);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void setReward() {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("UPDATE " + tables.getStavbaSeasonsTable() + " SET reward = 1 WHERE active = 1");
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            setReward();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void setWebNews() {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("UPDATE " + tables.getStavbaSeasonsTable() + " SET web_new = 1 WHERE active = 1");
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            setReward();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
