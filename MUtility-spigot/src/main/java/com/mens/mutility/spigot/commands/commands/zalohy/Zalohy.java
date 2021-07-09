package com.mens.mutility.spigot.commands.commands.zalohy;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.MyComp;
import com.mens.mutility.spigot.chat.Errors;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandHelp;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.database.Database;
import com.mens.mutility.spigot.database.DatabaseTables;
import com.mens.mutility.spigot.utils.MyStringUtils;
import com.mens.mutility.spigot.utils.PageList;
import com.mens.mutility.spigot.utils.PageList2;
import com.mens.mutility.spigot.utils.PlayerManager;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


public class Zalohy extends CommandHelp {
    private final MUtilitySpigot plugin;
    private PageList helpList;
    private final Database db;
    private final DatabaseTables tables;

    public Zalohy(MUtilitySpigot plugin) {
        this.plugin = plugin;
        db = plugin.getDb();
        Prefix prefix = new Prefix();
        helpList = new PageList(10, prefix.getZalohyPrefix(true, true).replace("]", " - nápověda]"), "/zalohy");
        tables = new DatabaseTables(plugin);
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public CommandData create() {
        Prefix prefix = new Prefix();
        PluginColors colors = new PluginColors();
        Errors errors = new Errors();
        MyStringUtils utils = new MyStringUtils();
        PageList2 showList = new PageList2(10, prefix.getZalohyPrefix(true, false), "/zalohy zobraz");
        PageList2 manageList = new PageList2(10, prefix.getZalohyPrefix(true, false), "/zalohy manage");
        PageList2 adminList = new PageList2(10, prefix.getZalohyPrefix(true, false), "/zalohy admin");
        PageList2 adminUserList = new PageList2(10, prefix.getZalohyPrefix(true, false), null);

        CommandData zalohy = new CommandData("zalohy", prefix.getZalohyPrefix(true, false),"mutility.zalohy.help", CommandExecutors.BOTH, t -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1).toPlayer((Player) t.getSender());
        });

        // 1. stupeň
        CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData pridej = new CommandData(ArgumentTypes.DEFAULT, "pridej", TabCompleterTypes.DEFAULT, "mutility.zalohy.create");
        CommandData zobraz = new CommandData(ArgumentTypes.DEFAULT, "zobraz", TabCompleterTypes.DEFAULT, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            showList.clear();
            try {
                Player player = (Player) t.getSender();
                PreparedStatement stm = db.getCon().prepareStatement("SELECT record_id, building_name, note, rejected, rejected_reason, completed, admin_id, posX, posY, posZ, world, create_date, update_date FROM "+ tables.getZalohyTable() + " WHERE user_id = (SELECT id FROM web_users WHERE username = ?) ORDER BY record_id");
                stm.setString(1, player.getName());
                ResultSet rs =  stm.executeQuery();
                int record_id;
                String building_name;
                String note;
                int rejected;
                String rejected_reason;
                int completed;
                int admin_id;
                float x;
                float y;
                float z;
                String world;
                String date;
                String updateDate;
                while(rs.next()) {
                    record_id = rs.getInt(1);
                    building_name = rs.getString(2);
                    note = rs.getString(3);
                    rejected = rs.getInt(4);
                    rejected_reason = rs.getString(5);
                    completed = rs.getInt(6);
                    admin_id = rs.getInt(7);
                    x = rs.getFloat(8);
                    y = rs.getFloat(9);
                    z = rs.getFloat(10);
                    world = rs.getString(11);
                    date = String.valueOf(rs.getDate(12));
                    updateDate = String.valueOf(rs.getDate(13));
                    MyComp manageMyComp = new MyComp(HoverEvent.Action.SHOW_TEXT, ClickEvent.Action.RUN_COMMAND);
                    MyComp deleteMyComp = new MyComp(HoverEvent.Action.SHOW_TEXT, ClickEvent.Action.RUN_COMMAND);
                    MyComp textMyComp = new MyComp(HoverEvent.Action.SHOW_TEXT);
                    manageMyComp.setText(colors.getSecondaryColor() + " [" + ChatColor.DARK_GRAY + "•••" + colors.getSecondaryColor() + "]");
                    manageMyComp.setHover(colors.getSecondaryColor() + ">> " + ChatColor.GRAY + "Tuto zálohu již "+ ChatColor.DARK_RED + "nelze " + ChatColor.GRAY + "upravit "+ colors.getSecondaryColor() + "<<");
                    deleteMyComp.setText(colors.getSecondaryColor() + " [" + ChatColor.DARK_GRAY + "✖" + colors.getSecondaryColor() + "]");
                    deleteMyComp.setHover(colors.getSecondaryColor() + ">> " + ChatColor.GRAY + "Tuto zálohu již "+ ChatColor.DARK_RED + "nelze " + ChatColor.GRAY + "smazat "+ colors.getSecondaryColor() + "<<");
                    if(rejected == 1) {
                        String admin = getUserFromID(admin_id);
                        textMyComp.setText(" " + colors.getSecondaryColor() +"- ["+ colors.getPrimaryColor() + record_id + colors.getSecondaryColor() + "] "+ ChatColor.DARK_RED + building_name);
                        textMyComp.setHover(
                                colors.getSecondaryColor() + "X: "+ colors.getPrimaryColor() + x + "\n" +
                                        colors.getSecondaryColor() + "Y: "+ colors.getPrimaryColor() + y + "\n" +
                                        colors.getSecondaryColor() + "Z: "+ colors.getPrimaryColor() + z + "\n" +
                                        colors.getSecondaryColor() + "Svět: "+ colors.getPrimaryColor() + world + "\n" +
                                        colors.getSecondaryColor() + "Vytvořeno: "+ colors.getPrimaryColor() + date + "\n" +
                                        colors.getSecondaryColor() + "Poznámka: "+ colors.getPrimaryColor() + note + "\n" +
                                        colors.getSecondaryColor() + "Stav: "+ ChatColor.DARK_RED + "Zamítnuto\n" +
                                        colors.getSecondaryColor() + "Datum: "+ ChatColor.DARK_RED + updateDate + "\n" +
                                        colors.getSecondaryColor() + "Zamítnul/a: "+ ChatColor.DARK_RED + admin + "\n" +
                                        colors.getSecondaryColor() + "Důvod zamítnutí: "+ ChatColor.DARK_RED + rejected_reason);
                    } else if(completed == 1) {
                        String admin = getUserFromID(admin_id);
                        textMyComp.setText(" " + colors.getSecondaryColor() +"- ["+ colors.getPrimaryColor() + record_id + colors.getSecondaryColor() + "] "+ ChatColor.GREEN + building_name);
                        textMyComp.setHover(
                                colors.getSecondaryColor() + "X: "+ colors.getPrimaryColor() + x + "\n" +
                                        colors.getSecondaryColor() + "Y: "+ colors.getPrimaryColor() + y + "\n" +
                                        colors.getSecondaryColor() + "Z: "+ colors.getPrimaryColor() + z + "\n" +
                                        colors.getSecondaryColor() + "Svět: "+ colors.getPrimaryColor() + world + "\n" +
                                        colors.getSecondaryColor() + "Vytvořeno: "+ colors.getPrimaryColor() + date + "\n" +
                                        colors.getSecondaryColor() + "Poznámka: "+ colors.getPrimaryColor() + note + "\n" +
                                        colors.getSecondaryColor() + "Stav: "+ ChatColor.GREEN + "Přesunuto\n" +
                                        colors.getSecondaryColor() + "Datum: "+ ChatColor.GREEN + updateDate + "\n" +
                                        colors.getSecondaryColor() + "Přesunul/a: "+ ChatColor.GREEN + admin);
                    } else {
                        manageMyComp.setText(colors.getSecondaryColor() + " [" + ChatColor.GOLD + "•••" + colors.getSecondaryColor() + "]");
                        manageMyComp.setHover(colors.getSecondaryColor() + ">> " + ChatColor.GRAY + "Klikni pro "+ ChatColor.GOLD + "úpravu " + ChatColor.GRAY + "zálohy "+ colors.getSecondaryColor() + "<<");
                        manageMyComp.setCommand("/zalohy manage " + record_id);

                        deleteMyComp.setText(colors.getSecondaryColor() + " [" + ChatColor.DARK_RED + "✖" + colors.getSecondaryColor() + "]");
                        deleteMyComp.setHover(colors.getSecondaryColor() + ">> " + ChatColor.GRAY + "Klikni pro "+ ChatColor.DARK_RED + "smazání " + ChatColor.GRAY + "zálohy "+ colors.getSecondaryColor() + "<<");
                        deleteMyComp.setCommand("/zalohy delete " + record_id);

                        textMyComp.setText(" " + colors.getSecondaryColor() +"- ["+ colors.getPrimaryColor() + record_id + colors.getSecondaryColor() + "] "+ ChatColor.GOLD + building_name);
                        textMyComp.setHover(
                                colors.getSecondaryColor() + "X: "+ colors.getPrimaryColor() + x + "\n" +
                                        colors.getSecondaryColor() + "Y: "+ colors.getPrimaryColor() + y + "\n" +
                                        colors.getSecondaryColor() + "Z: "+ colors.getPrimaryColor() + z + "\n" +
                                        colors.getSecondaryColor() + "Svět: "+ colors.getPrimaryColor() + world + "\n" +
                                        colors.getSecondaryColor() + "Vytvořeno: "+ colors.getPrimaryColor() + date + "\n" +
                                        colors.getSecondaryColor() + "Poznámka: "+ colors.getPrimaryColor() + note + "\n" +
                                        colors.getSecondaryColor() + "Stav: "+ ChatColor.GOLD + "Čeká na přesun");
                    }
                    showList.add(manageMyComp, deleteMyComp, textMyComp);
                }
                t.getSender().spigot().sendMessage(showList.getList(1).create());

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        CommandData manage = new CommandData(ArgumentTypes.DEFAULT, "manage", TabCompleterTypes.NONE);
        CommandData delete = new CommandData(ArgumentTypes.DEFAULT, "delete", TabCompleterTypes.NONE);
        CommandData admin = new CommandData(ArgumentTypes.DEFAULT, "admin", TabCompleterTypes.DEFAULT, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            adminList.clear();
            try {
                int statCompleted = 0;
                int statRejected = 0;
                int statTotal = 0;
                float statPercent = 0;
                PreparedStatement stm = db.getCon().prepareStatement("SELECT (SELECT count(completed) FROM "+ tables.getZalohyTable() + " WHERE completed = 1),(SELECT count(completed) FROM "+ tables.getZalohyTable() + "zalohy WHERE rejected = 1),(SELECT count(completed) FROM "+ tables.getZalohyTable() + "zalohy) FROM "+ tables.getZalohyTable() + "zalohy LIMIT 1");
                ResultSet rs =  stm.executeQuery();
                if(rs.next()) {
                    statCompleted = rs.getInt(1);
                    statRejected = rs.getInt(2);
                    statTotal = rs.getInt(3);
                    statPercent = 100 / (float)statTotal * (statCompleted + statRejected);
                }
                stm = db.getCon().prepareStatement("SELECT (SELECT username FROM web_users WHERE id = user_id), SUM(rejected) AS zamitnute, SUM(completed) AS presunute, count(user_id) AS celkem, CASE WHEN (SUM(rejected)+SUM(completed)) = count(user_id) THEN 1 ELSE 0 END AS hotovo FROM " + tables.getZalohyTable() + " GROUP BY user_id ORDER BY hotovo");
                rs =  stm.executeQuery();
                String username;
                int rejected;
                int completed;
                int total;
                int done;
                MyComp statsMyComp = new MyComp(HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + "Celkem: "+ colors.getPrimaryColor() + statTotal + "\n" +
                        colors.getSecondaryColor() + "Přesunuto: " + ChatColor.GREEN + statCompleted + "\n" +
                        colors.getSecondaryColor() + "Zamítnuto: "+ ChatColor.DARK_RED + statRejected + "\n" +
                        colors.getSecondaryColor() + "Zbývá: "+ ChatColor.GOLD + (statTotal - (statCompleted + statRejected)));
                if(statPercent <= 33) {
                    statsMyComp.setText(colors.getSecondaryColor() + "Celkem přesunuto: " + ChatColor.DARK_RED + String.format("%1.1f", statPercent) + colors.getSecondaryColor() + " %");
                } else if(statPercent <= 66) {
                    statsMyComp.setText(colors.getSecondaryColor() + "Celkem přesunuto: " + ChatColor.GOLD + String.format("%1.1f", statPercent) + colors.getSecondaryColor() + " %");
                } else {
                    statsMyComp.setText(colors.getSecondaryColor() + "Celkem přesunuto: " + ChatColor.GREEN + String.format("%1.1f", statPercent) + colors.getSecondaryColor() + " %");
                }
                adminList.setHead(statsMyComp);
                while(rs.next()) {
                    username = rs.getString(1);
                    rejected = rs.getInt(2);
                    completed = rs.getInt(3);
                    total = rs.getInt(4);
                    done = rs.getInt(5);
                    MyComp userStatsMyComp = new MyComp(ClickEvent.Action.RUN_COMMAND,"/zalohy admin " + username);
                    userStatsMyComp.setHoverAction(HoverEvent.Action.SHOW_TEXT);
                    if(done == 0) {
                        userStatsMyComp.setText(colors.getSecondaryColor() + "- "+ ChatColor.GOLD + "➥" + username);
                        userStatsMyComp.setHover(
                                colors.getSecondaryColor() + "Stav: "+ ChatColor.GOLD + "Nekompletní\n" +
                                        colors.getSecondaryColor() + "Celkem: "+ colors.getPrimaryColor() + total + "\n" +
                                        colors.getSecondaryColor() + "Přesunuto: "+ ChatColor.GREEN + completed + "\n" +
                                        colors.getSecondaryColor() + "Zamítnuto: "+ ChatColor.DARK_RED + rejected + "\n" +
                                        colors.getSecondaryColor() + "Zbývá: " + ChatColor.GOLD + (total-(rejected+completed)));
                    } else {
                        userStatsMyComp.setText(colors.getSecondaryColor() + "- "+ ChatColor.GREEN + "➥" + username);
                        userStatsMyComp.setHover(
                                colors.getSecondaryColor() + "Stav: "+ ChatColor.GREEN + "Kompletní\n" +
                                        colors.getSecondaryColor() + "Celkem: "+ colors.getPrimaryColor() + total + "\n" +
                                        colors.getSecondaryColor() + "Přesunuto: "+ ChatColor.GREEN + completed + "\n" +
                                        colors.getSecondaryColor() + "Zamítnuto: "+ ChatColor.DARK_RED + rejected + "\n" +
                                        colors.getSecondaryColor() + "Zbývá: " + ChatColor.GOLD + (total-(rejected+completed)));
                    }
                    adminList.add(userStatsMyComp);
                }
                t.getSender().spigot().sendMessage(adminList.getList(1).create());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        CommandData tp = new CommandData(ArgumentTypes.DEFAULT, "tp", TabCompleterTypes.NONE);
        CommandData complete = new CommandData(ArgumentTypes.DEFAULT, "complete", TabCompleterTypes.NONE);
        CommandData reject = new CommandData(ArgumentTypes.DEFAULT, "reject", TabCompleterTypes.NONE);
        CommandData returnZaloha = new CommandData(ArgumentTypes.DEFAULT, "return", TabCompleterTypes.NONE);

        // 2. stupeň
        CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.zalohy.help", CommandExecutors.BOTH, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1])).toPlayer((Player) t.getSender());
        });
        CommandData pridejX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.zalohy.create");
        CommandData zobrazPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData manageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            manageList.clear();
            try {
                Player player = (Player) t.getSender();
                int id = Integer.parseInt(t.getArgs()[1]);
                if(isZaloha(player, id, false, tables.getZalohyTable())) {
                    PreparedStatement stm = db.getCon().prepareStatement("SELECT rejected, completed FROM " + tables.getZalohyTable() + " WHERE record_id= ? AND user_id = ?");
                    stm.setInt(1, id);
                    stm.setString(2, player.getName());
                    stm.setInt(3, new PlayerManager(plugin).getUserId(player.getName()));
                    ResultSet rs =  stm.executeQuery();
                    int rejected = 0;
                    int completed = 0;
                    if(rs.next()) {
                        rejected = rs.getInt(1);
                        completed = rs.getInt(2);
                    }
                    if(rejected == 0 && completed == 0) {
                        stm = db.getCon().prepareStatement("SELECT building_name, posX, posY, posZ, note FROM " + tables.getZalohyTable() + " WHERE user_id = (SELECT id FROM web_users WHERE username = ?) AND record_id = ?");
                        stm.setString(1, player.getName());
                        stm.setInt(2, id);
                        rs =  stm.executeQuery();
                        String name;
                        float tpX;
                        float tpY;
                        float tpZ;
                        String note;
                        if(rs.next()) {
                            name = rs.getString(1);
                            tpX = rs.getFloat(2);
                            tpY = rs.getFloat(3);
                            tpZ = rs.getFloat(4);
                            note = rs.getString(5);
                            manageList.add(new MyComp(colors.getSecondaryColor() + "ID: " + colors.getPrimaryColor() + id));
                            manageList.add(new MyComp(colors.getSecondaryColor() + "Název: " + colors.getPrimaryColor() + name), new MyComp(colors.getSecondaryColor() + " [" + ChatColor.GOLD + "•••" + colors.getSecondaryColor() + "]", HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + ">> Klikni pro úpravu " + colors.getPrimaryColor() + "Názvu " + colors.getSecondaryColor() + "<<", ClickEvent.Action.SUGGEST_COMMAND, "/zalohy manage "+ id +" setName "));
                            manageList.add(new MyComp(colors.getSecondaryColor() + "Souřadnice: "));
                            manageList.add(new MyComp(colors.getSecondaryColor() + "   - X = " + colors.getPrimaryColor() + tpX), new MyComp(colors.getSecondaryColor() + " [" + ChatColor.GOLD + "•••" + colors.getSecondaryColor() + "]", HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + ">> Klikni pro úpravu souřadnice " + colors.getPrimaryColor() + "X " + colors.getSecondaryColor() + "<<", ClickEvent.Action.SUGGEST_COMMAND, "/zalohy manage "+ id +" setX "));
                            manageList.add(new MyComp(colors.getSecondaryColor() + "   - Y = " + colors.getPrimaryColor() + tpY), new MyComp(colors.getSecondaryColor() + " [" + ChatColor.GOLD + "•••" + colors.getSecondaryColor() + "]", HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + ">> Klikni pro úpravu souřadnice " + colors.getPrimaryColor() + "Y " + colors.getSecondaryColor() + "<<", ClickEvent.Action.SUGGEST_COMMAND, "/zalohy manage "+ id +" setY "));
                            manageList.add(new MyComp(colors.getSecondaryColor() + "   - Z = " + colors.getPrimaryColor() + tpZ), new MyComp(colors.getSecondaryColor() + " [" + ChatColor.GOLD + "•••" + colors.getSecondaryColor() + "]", HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + ">> Klikni pro úpravu souřadnice " + colors.getPrimaryColor() + "Z " + colors.getSecondaryColor() + "<<", ClickEvent.Action.SUGGEST_COMMAND, "/zalohy manage "+ id +" setZ "));
                            manageList.add(new MyComp(colors.getSecondaryColor() + "Note: " + colors.getPrimaryColor() + note), new MyComp(colors.getSecondaryColor() + " [" + ChatColor.GOLD + "•••" + colors.getSecondaryColor() + "]", HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + ">> Klikni pro úpravu " + colors.getPrimaryColor() + "Poznámky " + colors.getSecondaryColor() + "<<", ClickEvent.Action.SUGGEST_COMMAND, "/zalohy manage "+ id +" setNote "));
                        }
                        player.spigot().sendMessage(manageList.getList(1).create());
                    } else {
                        player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errDenied(true, false));
                    }
                } else {
                    player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        CommandData deleteID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.delete", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            Player player = (Player) t.getSender();
            try {
                if(isZaloha(player, id, false, tables.getZalohyTable())) {
                    PreparedStatement stm = db.getCon().prepareStatement("SELECT rejected, completed FROM "+ tables.getZalohyTable() + " WHERE record_id= ? and user_id = (SELECT id FROM web_users WHERE username = ?)");
                    stm.setInt(1, id);
                    stm.setString(2, player.getName());
                    ResultSet rs =  stm.executeQuery();
                    int rejected = 0;
                    int completed = 0;
                    if(rs.next()) {
                        rejected = rs.getInt(1);
                        completed = rs.getInt(2);
                    }
                    if(rejected == 0 && completed == 0) {
                        int max_record_id = 1;
                        String name = "";
                        stm = db.getCon().prepareStatement("SELECT building_name FROM "+ tables.getZalohyTable() + " WHERE user_id = (SELECT id FROM web_users WHERE username = ?) AND record_id = ?");
                        stm.setString(1, player.getName());
                        stm.setInt(2, id);
                        rs = stm.executeQuery();
                        if(rs.next()) {
                            name = rs.getString(1);
                        }
                        stm = db.getCon().prepareStatement("DELETE FROM "+ tables.getZalohyTable() + " WHERE user_id = (SELECT id FROM web_users WHERE username = ?) AND record_id = ?");
                        stm.setString(1, player.getName());
                        stm.setInt(2, id);
                        stm.executeUpdate();
                        stm = db.getCon().prepareStatement("SELECT MAX(record_id) FROM "+ tables.getZalohyTable() + " WHERE user_id = (SELECT id FROM web_users WHERE username = ?)");
                        stm.setString(1, player.getName());
                        rs = stm.executeQuery();
                        if(rs.next()) {
                            max_record_id = rs.getInt(1);
                        }
                        while(id <= max_record_id) {
                            int currentId = id - 1;
                            stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET record_id = ? WHERE user_id = (SELECT id FROM web_users WHERE username = ?) AND record_id = ?");
                            stm.setInt(1, currentId);
                            stm.setString(2, player.getName());
                            stm.setInt(3, id);
                            stm.execute();
                            stm.close();
                            id++;
                        }
                        player.sendMessage(prefix.getZalohyPrefix(true, false) + "Záloha "+ colors.getPrimaryColor() + name + " "+ colors.getSecondaryColor() + "byla smazána!");
                    } else {
                        player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errDenied(true, false));
                    }
                } else {
                    player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        CommandData adminPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData adminName = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.ONLINE_PLAYERS, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            String username = t.getArgs()[1];
            adminUserList.clear();
            adminUserList.setCommand("/zalohy admin " + username);
            try {
                PreparedStatement stm = db.getCon().prepareStatement("SELECT id, record_id, building_name, note, rejected, rejected_reason, completed, admin_id, posX, posY, posZ, world, create_date, update_date FROM "+ tables.getZalohyTable() +" WHERE user_id = (SELECT id FROM web_users WHERE username = ?) ORDER BY record_id");
                stm.setString(1, username);
                ResultSet rs =  stm.executeQuery();
                int id;
                int record_id;
                String building_name;
                String note;
                int rejected;
                String rejected_reason;
                int completed;
                int admin_id;
                float x;
                float y;
                float z;
                String world;
                String date;
                String updateDate;
                adminUserList.setHead(new MyComp(colors.getSecondaryColor() + "Hráč: " + colors.getPrimaryColor() + username));
                while(rs.next()) {
                    id = rs.getInt(1);
                    record_id = rs.getInt(2);
                    building_name = rs.getString(3);
                    note = rs.getString(4);
                    rejected = rs.getInt(5);
                    rejected_reason = rs.getString(6);
                    completed = rs.getInt(7);
                    admin_id = rs.getInt(8);
                    x = rs.getFloat(9);
                    y = rs.getFloat(10);
                    z = rs.getFloat(11);
                    world = rs.getString(12);
                    date = String.valueOf(rs.getDate(13));
                    updateDate = String.valueOf(rs.getDate(14));

                    MyComp backupMyComp = new MyComp(HoverEvent.Action.SHOW_TEXT);
                    if(rejected == 1) {
                        String adminUsername = getUserFromID(admin_id);
                        backupMyComp.setText(colors.getSecondaryColor() + "- ["+ colors.getPrimaryColor() + record_id + colors.getSecondaryColor() + "] "+ ChatColor.DARK_RED + building_name);
                        backupMyComp.setHover(
                                colors.getSecondaryColor() + "X: "+ colors.getPrimaryColor() + x + "\n" +
                                        colors.getSecondaryColor() + "Y: "+ colors.getPrimaryColor() + y + "\n" +
                                        colors.getSecondaryColor() + "Z: "+ colors.getPrimaryColor() + z + "\n" +
                                        colors.getSecondaryColor() + "Svět: "+ colors.getPrimaryColor() + world + "\n" +
                                        colors.getSecondaryColor() + "Vytvořeno: "+ colors.getPrimaryColor() + date + "\n" +
                                        colors.getSecondaryColor() + "Poznámka: "+ colors.getPrimaryColor() + note + "\n" +
                                        colors.getSecondaryColor() + "Stav: "+ ChatColor.DARK_RED + "Zamítnuto \n" +
                                        colors.getSecondaryColor() + "Datum: "+ ChatColor.DARK_RED + updateDate + "\n" +
                                        colors.getSecondaryColor() + "Zamítnul/a: "+ ChatColor.DARK_RED + adminUsername + "\n" +
                                        colors.getSecondaryColor() + "Důvod zamítnutí: "+ ChatColor.DARK_RED + rejected_reason);
                    } else if(completed == 1) {
                        String adminUsername = getUserFromID(admin_id);
                        backupMyComp.setText(colors.getSecondaryColor() + "- ["+ colors.getPrimaryColor() + record_id + colors.getSecondaryColor() + "] "+ ChatColor.GREEN + building_name);
                        backupMyComp.setHover(
                                colors.getSecondaryColor() + "X: "+ colors.getPrimaryColor() + x + "\n" +
                                        colors.getSecondaryColor() + "Y: "+ colors.getPrimaryColor() + y + "\n" +
                                        colors.getSecondaryColor() + "Z: "+ colors.getPrimaryColor() + z + "\n" +
                                        colors.getSecondaryColor() + "Svět: "+ colors.getPrimaryColor() + world + "\n" +
                                        colors.getSecondaryColor() + "Vytvořeno: "+ colors.getPrimaryColor() + date + "\n" +
                                        colors.getSecondaryColor() + "Poznámka: "+ colors.getPrimaryColor() + note + "\n" +
                                        colors.getSecondaryColor() + "Stav: "+ ChatColor.GREEN + "Přesunuto \n" +
                                        colors.getSecondaryColor() + "Datum: "+ ChatColor.GREEN + updateDate + "\n" +
                                        colors.getSecondaryColor() + "Přesunul/a: "+ ChatColor.GREEN + adminUsername);
                    } else {
                        backupMyComp.setText(colors.getSecondaryColor() + "- ["+ colors.getPrimaryColor() + record_id + colors.getSecondaryColor() + "] "+ ChatColor.GOLD + building_name);
                        backupMyComp.setHover(
                                colors.getSecondaryColor() + "X: "+ colors.getPrimaryColor() + x + "\n" +
                                        colors.getSecondaryColor() + "Y: "+ colors.getPrimaryColor() + y + "\n" +
                                        colors.getSecondaryColor() + "Z: "+ colors.getPrimaryColor() + z + "\n" +
                                        colors.getSecondaryColor() + "Svět: "+ colors.getPrimaryColor() + world + "\n" +
                                        colors.getSecondaryColor() + "Vytvořeno: "+ colors.getPrimaryColor() + date + "\n" +
                                        colors.getSecondaryColor() + "Poznámka: "+ colors.getPrimaryColor() + note + "\n" +
                                        colors.getSecondaryColor() + "Stav: "+ ChatColor.GOLD + "Čeká na přesun");
                    }
                    MyComp completeMyComp = new MyComp(colors.getSecondaryColor() + "["+ ChatColor.GREEN + "✔"+ colors.getSecondaryColor() + "]", HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + ">> Klikni pro "+ ChatColor.GREEN + "Dokončení "+ colors.getSecondaryColor() + "zálohy <<", ClickEvent.Action.RUN_COMMAND, "/zalohy complete " + id);
                    MyComp rejectMyComp = new MyComp(colors.getSecondaryColor() + "["+ ChatColor.DARK_RED + "✖"+ colors.getSecondaryColor() + "]", HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + ">> Klikni pro "+ ChatColor.DARK_RED + "Zamítnutí "+ colors.getSecondaryColor() + "zálohy <<", ClickEvent.Action.SUGGEST_COMMAND, "/zalohy reject " + id + " ");
                    MyComp returnMyComp = new MyComp(colors.getSecondaryColor() + "["+ ChatColor.GOLD + "◀"+ colors.getSecondaryColor() + "]", HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + ">> Klikni pro "+ ChatColor.GOLD + "Vrácení "+ colors.getSecondaryColor() + "zálohy mezi nepřesunuté <<", ClickEvent.Action.RUN_COMMAND, "/zalohy return " + id);
                    MyComp tpMyComp = new MyComp(colors.getSecondaryColor() + "["+ ChatColor.DARK_AQUA + "☄"+ colors.getSecondaryColor() + "]", HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + ">> Klikni pro "+ ChatColor.DARK_AQUA + "Teleportaci "+ colors.getSecondaryColor() + "na místo zálohy <<", ClickEvent.Action.RUN_COMMAND, "/zalohy tp " + id);
                    if(rejected == 1 || completed == 1) {
                        adminUserList.add(new MyComp("    "), returnMyComp, tpMyComp, backupMyComp);
                    } else {
                        adminUserList.add(completeMyComp, rejectMyComp, tpMyComp, backupMyComp);
                    }
                }
                t.getSender().spigot().sendMessage(adminUserList.getList(1).create());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        CommandData tpID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            try {
                Player player = (Player) t.getSender();
                int id = Integer.parseInt(t.getArgs()[1]);
                if(isZaloha(player, id, true, tables.getZalohyTable())) {
                    PreparedStatement stm = db.getCon().prepareStatement("SELECT building_name, posX, posY, posZ FROM "+ tables.getZalohyTable() + " WHERE id= ?");
                    stm.setInt(1, id);
                    ResultSet rs =  stm.executeQuery();
                    float x = 0;
                    float y = 0;
                    float z = 0;
                    String name = "";
                    if(rs.next()) {
                        name = rs.getString(1);
                        x = rs.getFloat(2);
                        y = rs.getFloat(3);
                        z = rs.getFloat(4);
                    }
                    Location destination = player.getLocation();
                    destination.setX(x);
                    destination.setY(y);
                    destination.setZ(z);
                    player.teleport(destination);
                    player.sendMessage(prefix.getZalohyPrefix(true, false) + "Byl jsi teleportován k záloze " + colors.getPrimaryColor() + name + colors.getSecondaryColor() + "!");
                } else {
                    player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        CommandData completeID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            Player player = (Player) t.getSender();
            if(isZaloha(player, id, true, tables.getZalohyTable())) {
                String name = "";
                try {
                    PreparedStatement stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET rejected = 0, completed = 1, rejected_reason = null, admin_id = (SELECT id FROM web_users WHERE username = ?), update_date = ? WHERE id= ? ");
                    stm.setString(1, player.getName());
                    stm.setDate(2, Date.valueOf(LocalDate.now()));
                    stm.setInt(3, id);
                    stm.execute();
                    stm = db.getCon().prepareStatement("SELECT building_name FROM " + tables.getZalohyTable() + " WHERE id = ?");
                    stm.setInt(1, id);
                    ResultSet rs = stm.executeQuery();
                    if(rs.next()) {
                        name = rs.getString(1);
                    }
                    player.sendMessage(prefix.getZalohyPrefix(true, false) + "Záloha "+ colors.getPrimaryColor() + name + colors.getSecondaryColor() + " byla nastavena jako "+ ChatColor.GREEN + "dokončená" + colors.getSecondaryColor() + "!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
            }
        });
        CommandData rejectID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE);
        CommandData returnID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            Player player = (Player) t.getSender();
            if(isZaloha(player, id, true, tables.getZalohyTable())) {
                String name = "";
                try {
                    PreparedStatement stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET rejected = 0, completed = 0, rejected_reason = null, admin_id = null, update_date = null WHERE id= ? ");
                    stm.setInt(1, id);
                    stm.execute();
                    stm = db.getCon().prepareStatement("SELECT building_name FROM " + tables.getZalohyTable() + " WHERE id = ?");
                    stm.setInt(1, id);
                    ResultSet rs = stm.executeQuery();
                    if(rs.next()) {
                        name = rs.getString(1);
                    }
                    player.sendMessage(prefix.getZalohyPrefix(true, false) + "Záloha "+ colors.getPrimaryColor() + name + colors.getSecondaryColor() + " byla vrácena mezi "+ ChatColor.GOLD + "nepřesunuté" + colors.getSecondaryColor() + "!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
            }
        });

        // 3. stupeň
        CommandData pridejY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.zalohy.create");
        CommandData zobrazPageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.manage", CommandExecutors.PLAYER, t-> t.getSender().spigot().sendMessage(showList.getList(Integer.parseInt(t.getArgs()[2])).create()));
        CommandData setX = new CommandData(ArgumentTypes.DEFAULT, "setx", TabCompleterTypes.NONE);
        CommandData setY = new CommandData(ArgumentTypes.DEFAULT, "sety", TabCompleterTypes.NONE);
        CommandData setZ = new CommandData(ArgumentTypes.DEFAULT, "setz", TabCompleterTypes.NONE);
        CommandData setNote = new CommandData(ArgumentTypes.DEFAULT, "setnote", TabCompleterTypes.NONE);
        CommandData setName = new CommandData(ArgumentTypes.DEFAULT, "setname", TabCompleterTypes.NONE);
        CommandData adminPageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> t.getSender().spigot().sendMessage(adminList.getList(Integer.parseInt(t.getArgs()[2])).create()));
        CommandData adminNamePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData rejectReason = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Důvod zamítnutí >]", "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            String reason = utils.getStringFromArgs(t.getArgs(), 2);
            Player player = (Player) t.getSender();
            if(isZaloha(player, id, true, tables.getZalohyTable())) {
                try {
                    String name = "";
                    PreparedStatement stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET rejected = 1, completed = 0, rejected_reason = ?, admin_id = (SELECT id FROM web_users WHERE username = ?), update_date = ? WHERE id= ? ");
                    stm.setString(1, reason);
                    stm.setString(2, player.getName());
                    stm.setDate(3, Date.valueOf(LocalDate.now()));
                    stm.setInt(4, id);
                    stm.execute();
                    stm = db.getCon().prepareStatement("SELECT building_name FROM " + tables.getZalohyTable() + " WHERE id = ?");
                    stm.setInt(1, id);
                    ResultSet rs = stm.executeQuery();
                    if(rs.next()) {
                        name = rs.getString(1);
                    }
                    player.sendMessage(prefix.getZalohyPrefix(true, false) + "Záloha "+ colors.getPrimaryColor() + name + colors.getSecondaryColor() + " byla nastavena jako "+ ChatColor.DARK_RED + "zamítnutá" + colors.getSecondaryColor() + "!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
            }
        });

        // 4. stupeň
        CommandData pridejZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.zalohy.create");
        CommandData setXX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            float x = Float.parseFloat(t.getArgs()[3]);
            Player player = (Player) t.getSender();
            if(isZaloha(player, id, false, tables.getZalohyTable())) {
                try {
                    PreparedStatement stm = db.getCon().prepareStatement("SELECT rejected, completed FROM "+ tables.getZalohyTable() + " WHERE record_id= ? and user_id = (SELECT id FROM web_users WHERE username = ?)");
                    stm.setInt(1, id);
                    stm.setString(2, player.getName());
                    ResultSet rs =  stm.executeQuery();
                    int rejected = 0;
                    int completed = 0;
                    if(rs.next()) {
                        rejected = rs.getInt(1);
                        completed = rs.getInt(2);
                    }
                    if(rejected == 0 && completed == 0) {
                        stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET posX = ? WHERE record_id = ? AND user_id = (SELECT id FROM web_users WHERE username = ?)");
                        stm.setFloat(1, x);
                        stm.setInt(2, id);
                        stm.setString(3, player.getName());
                        stm.execute();
                        player.sendMessage(prefix.getZalohyPrefix(true, false) + "Souřadnice "+ colors.getPrimaryColor() + "X "+ colors.getSecondaryColor() + "byla aktualizována na " + colors.getPrimaryColor() +  x + colors.getSecondaryColor() + "!");
                    } else {
                        player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errDenied(true, false));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
            }
        });
        CommandData setYY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            float y = Float.parseFloat(t.getArgs()[3]);
            Player player = (Player) t.getSender();
            if(isZaloha(player, id, false, tables.getZalohyTable())) {
                try {
                    PreparedStatement stm = db.getCon().prepareStatement("SELECT rejected, completed FROM "+ tables.getZalohyTable() + " WHERE record_id= ? and user_id = (SELECT id FROM web_users WHERE username = ?)");
                    stm.setInt(1, id);
                    stm.setString(2, player.getName());
                    ResultSet rs =  stm.executeQuery();
                    int rejected = 0;
                    int completed = 0;
                    if(rs.next()) {
                        rejected = rs.getInt(1);
                        completed = rs.getInt(2);
                    }
                    if(rejected == 0 && completed == 0) {
                        stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET posY = ? WHERE record_id = ? AND user_id = (SELECT id FROM web_users WHERE username = ?)");
                        stm.setFloat(1, y);
                        stm.setInt(2, id);
                        stm.setString(3, player.getName());
                        stm.execute();
                        player.sendMessage(prefix.getZalohyPrefix(true, false) + "Souřadnice "+ colors.getPrimaryColor() + "Y "+ colors.getSecondaryColor() + "byla aktualizována na " + colors.getPrimaryColor() +  y + colors.getSecondaryColor() + "!");
                    } else {
                        player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errDenied(true, false));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
            }
        });
        CommandData setZZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            float z = Float.parseFloat(t.getArgs()[3]);
            Player player = (Player) t.getSender();
            if(isZaloha(player, id, false, tables.getZalohyTable())) {
                try {
                    PreparedStatement stm = db.getCon().prepareStatement("SELECT rejected, completed FROM "+ tables.getZalohyTable() + " WHERE record_id= ? and user_id = (SELECT id FROM web_users WHERE username = ?)");
                    stm.setInt(1, id);
                    stm.setString(2, player.getName());
                    ResultSet rs =  stm.executeQuery();
                    int rejected = 0;
                    int completed = 0;
                    if(rs.next()) {
                        rejected = rs.getInt(1);
                        completed = rs.getInt(2);
                    }
                    if(rejected == 0 && completed == 0) {
                        stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET posZ = ? WHERE record_id = ? AND user_id = (SELECT id FROM web_users WHERE username = ?)");
                        stm.setFloat(1, z);
                        stm.setInt(2, id);
                        stm.setString(3, player.getName());
                        stm.execute();
                        player.sendMessage(prefix.getZalohyPrefix(true, false) + "Souřadnice "+ colors.getPrimaryColor() + "Z "+ colors.getSecondaryColor() + "byla aktualizována na " + colors.getPrimaryColor() +  z + colors.getSecondaryColor() + "!");
                    } else {
                        player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errDenied(true, false));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
            }
        });
        CommandData setNoteNote = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Poznámka >]", "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            String note = utils.getStringFromArgs(t.getArgs(), 3);
            Player player = (Player) t.getSender();
            if(isZaloha(player, id, false, tables.getZalohyTable())) {
                try {
                    PreparedStatement stm = db.getCon().prepareStatement("SELECT rejected, completed FROM "+ tables.getZalohyTable() + " WHERE record_id= ? and user_id = (SELECT id FROM web_users WHERE username = ?)");
                    stm.setInt(1, id);
                    stm.setString(2, player.getName());
                    ResultSet rs =  stm.executeQuery();
                    int rejected = 0;
                    int completed = 0;
                    if(rs.next()) {
                        rejected = rs.getInt(1);
                        completed = rs.getInt(2);
                    }
                    if(rejected == 0 && completed == 0) {
                        stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET note = ? WHERE record_id = ? AND user_id = (SELECT id FROM web_users WHERE username = ?)");
                        stm.setString(1, note);
                        stm.setInt(2, id);
                        stm.setString(3, player.getName());
                        stm.execute();
                        player.sendMessage(prefix.getZalohyPrefix(true, false) + "Poznámka byla aktualizována na " + colors.getPrimaryColor() +  note + colors.getSecondaryColor() + "!");
                    } else {
                        player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errDenied(true, false));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
            }
        });
        CommandData setNameName = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název stavby/staveb >]", "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            String name = utils.getStringFromArgs(t.getArgs(), 3);
            Player player = (Player) t.getSender();
            if(isZaloha(player, id, false, tables.getZalohyTable())) {
                try {
                    PreparedStatement stm = db.getCon().prepareStatement("SELECT rejected, completed FROM "+ tables.getZalohyTable() + " WHERE record_id= ? and user_id = (SELECT id FROM web_users WHERE username = ?)");
                    stm.setInt(1, id);
                    stm.setString(2, player.getName());
                    ResultSet rs =  stm.executeQuery();
                    int rejected = 0;
                    int completed = 0;
                    if(rs.next()) {
                        rejected = rs.getInt(1);
                        completed = rs.getInt(2);
                    }
                    if(rejected == 0 && completed == 0) {
                        stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET building_name = ? WHERE record_id = ? AND user_id = (SELECT id FROM web_users WHERE username = ?)");
                        stm.setString(1, name);
                        stm.setInt(2, id);
                        stm.setString(3, player.getName());
                        stm.execute();
                        player.sendMessage(prefix.getZalohyPrefix(true, false) + "Název byl aktualizován na " + colors.getPrimaryColor() +  name + colors.getSecondaryColor() + "!");
                    } else {
                        player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errDenied(true, false));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
            }
        });
        CommandData adminNamePageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> t.getSender().spigot().sendMessage(adminUserList.getList(Integer.parseInt(t.getArgs()[3])).create()));

        // 5. stupeň
        CommandData pridejName = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název stavby/staveb >]", "mutility.zalohy.create", CommandExecutors.PLAYER, t -> {
            float x = Float.parseFloat(t.getArgs()[1]);
            float y = Float.parseFloat(t.getArgs()[2]);
            float z = Float.parseFloat(t.getArgs()[3]);
            String name = utils.getStringFromArgs(t.getArgs(), 4);
            Player player = (Player) t.getSender();
            try {
                PreparedStatement stm = db.getCon().prepareStatement("SELECT COALESCE(MAX(record_id), 0) FROM "+ tables.getZalohyTable() + " WHERE user_id = (SELECT id FROM web_users WHERE username = ?)");
                stm.setString(1, player.getName());
                ResultSet rs = stm.executeQuery();
                int max_record_id = 0;
                if(rs.next()) {
                    max_record_id = rs.getInt(1);
                }
                stm = db.getCon().prepareStatement("INSERT INTO "+ tables.getZalohyTable() + " (user_id, record_id, building_name, note, rejected, completed, posX, posY, posZ, world, create_date) " +
                        "VALUE ((SELECT id FROM web_users WHERE username = ?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                stm.setString(1, player.getName());
                stm.setInt(2, (max_record_id + 1));
                stm.setString(3, name);
                stm.setString(4, "-");
                stm.setInt(5, 0);
                stm.setInt(6, 0);
                stm.setDouble(7, x);
                stm.setDouble(8, y);
                stm.setDouble(9, z);
                stm.setString(10, player.getWorld().getName());
                stm.setDate(11, Date.valueOf(LocalDate.now()));
                stm.execute();
                player.sendMessage(prefix.getZalohyPrefix(true, false) + "Záloha "+ colors.getPrimaryColor() + name + colors.getSecondaryColor() +" úspěšně vytvořena!");
                player.spigot().sendMessage(new TextComponent(colors.getSecondaryColor() + "Kliknutím "), new MyComp(colors.getPrimaryColor() + "➥Zde", HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + ">> "+ colors.getPrimaryColor() + "Klikni "+ colors.getSecondaryColor() +"<<", ClickEvent.Action.SUGGEST_COMMAND, "/zalohy manage "+ (max_record_id + 1) + " setNote ").getComp(), new TextComponent(colors.getSecondaryColor() + " můžete přidat poznámku pro moderátory."));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        zalohy.setDescription("Systém pro správu záloh");

        admin.setDescription("Zobrazí seznam všech hráčů, kteří si zažádali o zálohu a jejich statistiky.\nSeznam staveb jednotlivého hráče lze specifikovat dalším parametrem");
        admin.setSyntax("/zalohy " + admin.getSubcommand() + "\n/zalohy " + admin.getSubcommand() + " [<Jméno hráče>]");

        pridej.setDescription("Vytvoření nové žádosti na přesun staveb");
        pridej.setSyntax("/zalohy " + pridej.getSubcommand() + " [<X>] [<Y>] [<Z>] [<Název stavby/staveb>] na zadaných souřadnicích");

        zobraz.setDescription("Zobrazí uživateli jeho žádosti na přesun staveb");
        zobraz.setSyntax("/zalohy " + zobraz.getSubcommand());

        zalohy.link(helpPage);
        zalohy.link(pridej);
        zalohy.link(zobraz);
        zalohy.link(manage);
        zalohy.link(delete);
        zalohy.link(admin);
        zalohy.link(tp);
        zalohy.link(complete);
        zalohy.link(reject);
        zalohy.link(returnZaloha);

        helpPage.link(helpPageID);
        pridej.link(pridejX);
        zobraz.link(zobrazPage);
        manage.link(manageID);
        delete.link(deleteID);
        admin.link(adminPage);
        admin.link(adminName);
        tp.link(tpID);
        complete.link(completeID);
        reject.link(rejectID);
        returnZaloha.link(returnID);

        pridejX.link(pridejY);
        zobrazPage.link(zobrazPageID);
        manageID.link(setX);
        manageID.link(setY);
        manageID.link(setZ);
        manageID.link(setNote);
        manageID.link(setName);
        adminPage.link(adminPageID);
        adminName.link(adminNamePage);
        rejectID.link(rejectReason);

        pridejY.link(pridejZ);
        setX.link(setXX);
        setY.link(setYY);
        setZ.link(setZZ);
        setNote.link(setNoteNote);
        setName.link(setNameName);
        adminNamePage.link(adminNamePageID);

        pridejZ.link(pridejName);

        return zalohy;
    }

    private boolean isZaloha(Player player, int id, boolean global, String tablePrefix) {
        int count = 0;
        PreparedStatement stm;
        try {
            if(global) {
                stm = db.getCon().prepareStatement("SELECT count(id) FROM "+ tablePrefix + " WHERE id = ?");
                stm.setInt(1, id);
            } else {
                stm = db.getCon().prepareStatement("SELECT count(record_id) FROM "+ tablePrefix + " WHERE user_id= (SELECT id FROM web_users WHERE username = ?) AND record_id = ?");
                stm.setString(1, player.getName());
                stm.setInt(2, id);
            }
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                count = rs.getInt(1);
            }
        } catch (CommunicationsException e)  {
            db.openConnection();
            isZaloha(player, id, global, tablePrefix);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (count != 0);
    }

    private String getUserFromID(int id) {
        String username = "";
        try {
            PreparedStatement stm = db.getCon().prepareStatement("SELECT username FROM web_users WHERE id = ?");
            stm.setInt(1, id);
            ResultSet rs =  stm.executeQuery();
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
