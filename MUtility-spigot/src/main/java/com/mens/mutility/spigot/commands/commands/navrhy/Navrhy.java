package com.mens.mutility.spigot.commands.commands.navrhy;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Errors;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.chat.json.JsonBuilder;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandHelp;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.database.Database;
import com.mens.mutility.spigot.database.DatabaseTables;
import com.mens.mutility.spigot.discord.DiscordManager;
import com.mens.mutility.spigot.utils.DeleteConfirmation;
import com.mens.mutility.spigot.utils.MyStringUtils;
import com.mens.mutility.spigot.utils.PageList;
import com.mens.mutility.spigot.utils.PlayerManager;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Navrhy extends CommandHelp {
    private final MUtilitySpigot plugin;
    private final Prefix prefix;
    private PageList helpList;
    private final PageList adminList;
    private final PageList adminNameList;
    private final PageList showList;
    private final Database db;
    private final PluginColors colors;
    private final DatabaseTables tables;
    private final PlayerManager playerManager;
    private final MyStringUtils strUt;
    private final Errors errors;
    private final List<DeleteConfirmation> deleteConfirmationList;
    private final DiscordManager discordManager;

    public Navrhy(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        helpList = new PageList(10, prefix.getNavrhyPrefix(true, true).replace("]", " - nápověda]"), "/navrhy");
        adminList = new PageList(10, prefix.getNavrhyPrefix(true, true).replace("]", " - admin]"), "/navrhy admin");
        adminList.setEmptyMessage(" Nejsou vytvořené žádné návrhy!");
        adminNameList = new PageList(10, null, null);
        adminNameList.setEmptyMessage(" Hráč nemá vytvořené žádné návrhy!");
        showList = new PageList(10, prefix.getNavrhyPrefix(true, true).replace("]", " - seznam]"), "/navrhy zobraz");
        showList.setEmptyMessage(" Nemáš vytvořené žádné návrhy!\n Pro přidání návrhu použij /navrhy pridej [<Tvůj návrh>]");
        db = plugin.getDb();
        colors = new PluginColors();
        tables = new DatabaseTables(plugin);
        playerManager = new PlayerManager(plugin);
        strUt = new MyStringUtils();
        errors = new Errors();
        deleteConfirmationList = new ArrayList<>();
        discordManager = new DiscordManager();
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public CommandData create() {
        CommandData navrhy = new CommandData("navrhy", prefix.getNavrhyPrefix(true, false),"mutility.navrhy.help", CommandExecutors.BOTH, t -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1).toPlayer((Player) t.getSender());
        });

        // 1. stupeň
        CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData admin = new CommandData(ArgumentTypes.DEFAULT, "admin", TabCompleterTypes.DEFAULT, "mutility.navrhy.admin", CommandExecutors.PLAYER, t -> {
            loadAdminList();
            adminList.getList(1).toPlayer((Player) t.getSender());
        });
        CommandData show = new CommandData(ArgumentTypes.DEFAULT, "zobraz", TabCompleterTypes.DEFAULT, "mutility.navrhy.show", CommandExecutors.PLAYER, t -> {
            loadShowList((Player)t.getSender());
            showList.getList(1).toPlayer((Player) t.getSender());
        });
        CommandData add = new CommandData(ArgumentTypes.DEFAULT, "pridej", TabCompleterTypes.DEFAULT, "mutility.navrhy.admin");
        CommandData accept = new CommandData(ArgumentTypes.DEFAULT, "accept", TabCompleterTypes.NONE);
        CommandData reject = new CommandData(ArgumentTypes.DEFAULT, "reject", TabCompleterTypes.NONE);
        CommandData returnN = new CommandData(ArgumentTypes.DEFAULT, "return", TabCompleterTypes.NONE);
        CommandData delete = new CommandData(ArgumentTypes.DEFAULT, "delete", TabCompleterTypes.NONE);
        CommandData manage = new CommandData(ArgumentTypes.DEFAULT, "manage", TabCompleterTypes.NONE);

        // 2. stupeň
        CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.navrhy.help", CommandExecutors.BOTH, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1])).toPlayer((Player) t.getSender());
        });
        CommandData addText = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Tvůj návrh >]", "mutility.navrhy.add", CommandExecutors.PLAYER, t -> {
            String content = strUt.getStringFromArgs(t.getArgs(), 1);
            addNavrh(playerManager.getUserId(t.getSender().getName()), getMaxRecordId((Player)t.getSender()) + 1, content);
            t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false) + "Návrh byl přidán");
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Hráč " + t.getSender().getName() + " přidal nový návrh");
            embedBuilder.setDescription(content);
            embedBuilder.setColor(Color.decode(colors.getPrimaryColorHEX()));
            embedBuilder.setFooter("Hlasujte kliknutím na jednu z reakcí");
            discordManager.sendVoteEmbedMessage(discordManager.getChannelByName(plugin.getConfig().getString("Discord.Rooms.Vote")), embedBuilder.build());
        });
        CommandData adminName = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.ONLINE_PLAYERS, "mutility.navrhy.admin", CommandExecutors.PLAYER, t -> {
            loadAdminNameList(t.getArgs()[1]);
            adminNameList.getList(1).toPlayer((Player) t.getSender());
        });
        CommandData adminPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData showPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData acceptID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.navrhy.accept", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            if(isNavrh(id)) {
                acceptNavrh(id, playerManager.getUserId(t.getSender().getName()));
            } else {
                t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1], true, false));
            }
            t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false) + "Návrh byl schválen");
        });
        CommandData rejectID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE);
        CommandData returnID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.navrhy.return", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            if(isNavrh(id)) {
                returnNavrh(id);
            } else {
                t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1], true, false));
            }
            t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false) + "Návrh byl vrácen mezi neprojednané");
        });
        CommandData deleteID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.navrhy.delete", CommandExecutors.PLAYER, (t) -> {
            int recordId = Integer.parseInt(t.getArgs()[1]);
            if(isRecordId((Player)t.getSender(), recordId)) {
                if(!isCompleted((Player)t.getSender(), recordId)) {
                    DeleteConfirmation deleteConfirmation = new DeleteConfirmation(recordId, (Player) t.getSender(), "/navrhy delete confirm");
                    deleteConfirmation.setMessage(new JsonBuilder()
                            .addJsonSegment(prefix.getZalohyPrefix(true, true))
                            .text(": Opravdu si přejete odstranit tento návrh?")
                            .color(colors.getSecondaryColorHEX()));
                    if(deleteConfirmationList.stream().noneMatch(x -> (x.getId() == recordId
                            && x.getPlayer().getName().equals(t.getSender().getName())
                            && !x.isFinished()))) {
                        deleteConfirmation.startTimer();
                        deleteConfirmationList.add(deleteConfirmation);
                    } else {
                        t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false)
                                + colors.getSecondaryColor() + "Žádost o potvrzení již byla vytvořena!");
                    }
                } else {
                    t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false)
                            + colors.getSecondaryColor() + "Návrh již není možné smazat");
                }
            } else {
                t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1],true, false));
            }
        });
        CommandData deleteConfirm = new CommandData(ArgumentTypes.DEFAULT, "confirm", TabCompleterTypes.NONE);
        CommandData manageId = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.NONE);

        // 3. stupeň
        CommandData rejectReason = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Důvod zamítnutí >]", "mutility.navrhy.reject", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            String rejectedReason = strUt.getStringFromArgs(t.getArgs(), 2);
            if(isNavrh(id)) {
                rejectNavrh(id, playerManager.getUserId(t.getSender().getName()), rejectedReason);
            } else {
                t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1], true, false));
            }
            t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false) + "Návrh byl zamítnut");
        });
        CommandData adminPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.navrhy.admin", CommandExecutors.BOTH, (t) -> {
            loadAdminList();
            adminList.getList(Integer.parseInt(t.getArgs()[2])).toPlayer((Player) t.getSender());
        });
        CommandData adminNamePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData showPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.navrhy.show", CommandExecutors.PLAYER, (t) -> {
            loadShowList((Player)t.getSender());
            showList.getList(Integer.parseInt(t.getArgs()[2])).toPlayer((Player) t.getSender());
        });
        CommandData deleteConfirmID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.delete", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[2]);
            if(isRecordId((Player)t.getSender(), recordId)) {
                boolean valid = false;
                for (int i = deleteConfirmationList.size() - 1; i >= 0; i--) {
                    if(deleteConfirmationList.get(i).getId() == recordId
                            && deleteConfirmationList.get(i).getPlayer().getName().equals(t.getSender().getName())) {
                        if(!deleteConfirmationList.get(i).isFinished()) {
                            valid = true;
                            deleteConfirmationList.get(i).setFinished(true);
                            deleteNavrh((Player)t.getSender(), recordId);
                            t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false) + "Návrh byl smazán!");
                            break;
                        }
                    }
                }
                if(!valid) {
                    t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false)
                            + "Potvrzení o smazání návrhu není platné!");
                }
                deleteConfirmationList.removeIf(DeleteConfirmation::isFinished);
            } else {
                t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1],true, false));
            }
        });
        CommandData manageIdText = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Tvůj návrh >]", "mutility.navrhy.manage", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[1]);
            if(!isCompleted((Player)t.getSender(), recordId)) {
                String content = strUt.getStringFromArgs(t.getArgs(), 2);
                editNavrh((Player)t.getSender(), recordId, content);
                t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false) + "Návrh byl upraven");
            } else {
                t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false)
                        + colors.getSecondaryColor() + "Návrh již není možné upravit");
            }
        });

        //4. stupeň
        CommandData adminNamePageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.navrhy.admin", CommandExecutors.BOTH, (t) -> {
            loadAdminNameList(t.getArgs()[1]);
            adminNameList.getList(Integer.parseInt(t.getArgs()[3])).toPlayer((Player) t.getSender());
        });

        navrhy.setDescription("Systém pro správu návrhů");

        add.setDescription("Přidání nového návrhu");
        add.setSyntax("/navrhy " + show.getSubcommand() + " [<Tvůj návrh>]");

        show.setDescription("Zobrazí vaše návrhy, jejich stav a umožní vám je upravovat nebo smazat");
        show.setSyntax("/navrhy " + show.getSubcommand());

        admin.setDescription("Zobrazí seznam všech návrhů a jejich správu.\nPřidáním jména hráče za příkaz si můžete vyfiltrovat návrhy daného hráče");
        admin.setSyntax("/navrhy " + admin.getSubcommand() + "\n/navrhy " + admin.getSubcommand() + " [<Jméno hráče>]");

        navrhy.link(helpPage);
        navrhy.link(add);
        navrhy.link(show);
        navrhy.link(admin);
        navrhy.link(accept);
        navrhy.link(reject);
        navrhy.link(returnN);
        navrhy.link(delete);
        navrhy.link(manage);

        helpPage.link(helpPageID);
        add.link(addText);
        admin.link(adminPage);
        admin.link(adminName);
        show.link(showPage);
        accept.link(acceptID);
        reject.link(rejectID);
        returnN.link(returnID);
        delete.link(deleteID);
        delete.link(deleteConfirm);
        manage.link(manageId);

        adminName.link(adminNamePage);
        showPage.link(showPageID);
        adminPage.link(adminPageID);
        rejectID.link(rejectReason);
        deleteConfirm.link(deleteConfirmID);
        manageId.link(manageIdText);

        adminNamePage.link(adminNamePageID);

        return navrhy;
    }

    private void loadAdminList() {
        try {
            adminList.clear();
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id, user_id, content, rejected, rejected_reason, accepted, admin_id, create_date, update_date, (SUM(accepted+rejected)) FROM " + tables.getNavrhyTable() + " GROUP BY id ORDER BY (SUM(accepted+rejected)), create_date DESC");
            ResultSet rs =  stm.executeQuery();
            String acceptHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Schválení")
                    .color(ChatColor.GREEN)
                    .text(" návrhu <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String rejectHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Zamítnutí")
                    .color(ChatColor.DARK_RED)
                    .text(" návrhu <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String returnHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Vrácení")
                    .color(ChatColor.GOLD)
                    .text(" návrhu mezi neprojednané <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            int id;
            int userId;
            int recordId;
            String content;
            int rejected;
            String rejectedReason;
            int accepted;
            int adminId;
            String createDate;
            String updateDate;
            int completed;
            while(rs.next()) {
                id = rs.getInt(1);
                userId = rs.getInt(2);
                content = rs.getString(3);
                rejected = rs.getInt(4);
                rejectedReason = rs.getString(5);
                accepted = rs.getInt(6);
                adminId = rs.getInt(7);
                createDate = rs.getString(8);
                updateDate = rs.getString(9);
                completed = rs.getInt(10);
                JsonBuilder hoverInfo = getNavrhInfoHover(rejected, accepted, adminId, rejectedReason, content, createDate, updateDate);
                JsonBuilder jb = getAdminButtons(hoverInfo, completed, rejected, accepted, id, adminId, rejectedReason, content, createDate, updateDate, acceptHover, rejectHover, returnHover);
                jb.text(playerManager.getUsername(userId));
                if(accepted == 0 && rejected == 0) {
                    jb.color(ChatColor.GOLD);
                } else if(accepted == 1) {
                    jb.color(ChatColor.GREEN);
                } else {
                    jb.color(ChatColor.DARK_RED);
                }
                jb.hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true);
                adminList.add(jb.getJsonSegments());
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            loadAdminList();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void loadAdminNameList(String playerName) {
        try {
            adminNameList.clear();
            adminNameList.setCommand("/navrhy admin " + playerName);
            adminNameList.setTitleJson(prefix.getNavrhyPrefix(true, true).replace("]", " - " + playerName));
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id, content, rejected, rejected_reason, accepted, admin_id, create_date, update_date, (SUM(accepted+rejected)) FROM " + tables.getNavrhyTable() + " WHERE user_id = ? GROUP BY id ORDER BY (SUM(accepted+rejected)), create_date DESC");
            stm.setInt(1, playerManager.getUserId(playerName));
            ResultSet rs =  stm.executeQuery();
            String acceptHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Schválení")
                    .color(ChatColor.GREEN)
                    .text(" návrhu <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String rejectHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Zamítnutí")
                    .color(ChatColor.DARK_RED)
                    .text(" návrhu <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String returnHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Vrácení")
                    .color(ChatColor.GOLD)
                    .text(" návrhu mezi neprojednané <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            int id;
            int recordId;
            String content;
            int rejected;
            String rejectedReason;
            int accepted;
            int adminId;
            String createDate;
            String updateDate;
            int completed;
            while(rs.next()) {
                id = rs.getInt(1);
                content = rs.getString(2);
                rejected = rs.getInt(3);
                rejectedReason = rs.getString(4);
                accepted = rs.getInt(5);
                adminId = rs.getInt(6);
                createDate = rs.getString(7);
                updateDate = rs.getString(8);
                completed = rs.getInt(9);
                JsonBuilder hoverInfo = getNavrhInfoHover(rejected, accepted, adminId, rejectedReason, content, createDate, updateDate);
                JsonBuilder jb = getAdminButtons(hoverInfo, completed, rejected, accepted, id, adminId, rejectedReason, content, createDate, updateDate, acceptHover, rejectHover, returnHover);
                if(content.length() > 30) {
                    jb.text(content.substring(0, 29) + "...");
                } else {
                    jb.text(content);
                }
                if(accepted == 0 && rejected == 0) {
                    jb.color(ChatColor.GOLD);
                } else if(accepted == 1) {
                    jb.color(ChatColor.GREEN);
                } else {
                    jb.color(ChatColor.DARK_RED);
                }
                jb.hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true);
                adminNameList.add(jb.getJsonSegments());
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            loadAdminList();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void loadShowList(Player player) {
        try {
            showList.clear();
            PreparedStatement stm = db.getCon().prepareStatement("SELECT * FROM " + tables.getNavrhyTable() + " WHERE user_id = ?");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            ResultSet rs =  stm.executeQuery();
            String manageHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Úpravu")
                    .color(ChatColor.GOLD)
                    .text(" návrhu <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String deleteHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Smazání")
                    .color(ChatColor.DARK_RED)
                    .text(" návrhu <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String manageDisableHover = new JsonBuilder(">> ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Návrh již ")
                    .color(colors.getDisableColorHEX())
                    .text("nelze")
                    .color(ChatColor.DARK_RED)
                    .text(" upravit")
                    .color(colors.getDisableColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String deleteDisableHover = new JsonBuilder(">> ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Návrh již ")
                    .color(colors.getDisableColorHEX())
                    .text("nelze")
                    .color(ChatColor.DARK_RED)
                    .text(" smazat")
                    .color(colors.getDisableColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            int recordId;
            String content;
            int rejected;
            String rejectedReason;
            int accepted;
            int adminId;
            String createDate;
            String updateDate;
            while(rs.next()) {
                recordId = rs.getInt(3);
                content = rs.getString(4);
                rejected = rs.getInt(5);
                rejectedReason = rs.getString(6);
                accepted = rs.getInt(7);
                adminId = rs.getInt(8);
                createDate = rs.getString(9);
                updateDate = rs.getString(10);
                JsonBuilder jb = new JsonBuilder();
                JsonBuilder hoverInfo = getNavrhInfoHover(rejected, accepted, adminId, rejectedReason, content, createDate, updateDate);
                jb.text("[")
                        .color(colors.getSecondaryColorHEX());
                if(accepted == 0 && rejected == 0) {
                    jb.hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageHover, true)
                            .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/navrhy manage " + recordId + " ")
                            .text("•••")
                            .color(ChatColor.GOLD)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageHover, true)
                            .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/navrhy manage " + recordId + " ")
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageHover, true)
                            .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/navrhy manage " + recordId + " ")
                            .text(" ")
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/navrhy delete " + recordId)
                            .text("✖")
                            .color(ChatColor.DARK_RED)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/navrhy delete " + recordId)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/navrhy delete " + recordId);
                } else {
                    jb.hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageDisableHover, true)
                            .text("•••")
                            .color(colors.getDisableColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageDisableHover, true)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageDisableHover, true)
                            .text(" ")
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteDisableHover, true)
                            .text("✖")
                            .color(colors.getDisableColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteDisableHover, true)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteDisableHover, true);
                }
                jb.text(" - ")
                        .color(colors.getSecondaryColorHEX())
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                        .text(String.valueOf(recordId))
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                        .text("] ")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true);
                if(content.length() > 30) {
                    jb.text(content.substring(0, 29) + "...");
                } else {
                    jb.text(content);
                }
                if(accepted == 0 && rejected == 0) {
                    jb.color(ChatColor.GOLD);
                } else if(accepted == 1) {
                    jb.color(ChatColor.GREEN);
                } else {
                    jb.color(ChatColor.DARK_RED);
                }
                jb.hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true);
                showList.add(jb.getJsonSegments());
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            loadShowList(player);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void addNavrh(int userId, int recordId, String content) {
        try {
            PreparedStatement stm = db.getCon().prepareStatement("INSERT INTO " + tables.getNavrhyTable() + " (user_id, record_id, content, rejected, accepted, create_date) VALUES (?, ?, ?, ?, ?, ?)");
            stm.setInt(1, userId);
            stm.setInt(2, recordId);
            stm.setString(3, content);
            stm.setInt(4, 0);
            stm.setInt(5, 0);
            stm.setString(6, strUt.getCurrentFormattedDate());
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            addNavrh(userId, recordId, content);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private int getMaxRecordId(Player player) {
        int recordId = 0;
        try {
            PreparedStatement stm = db.getCon().prepareStatement("SELECT COALESCE(MAX(record_id), 0) FROM "+ tables.getNavrhyTable() + " WHERE user_id = ?");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                recordId = rs.getInt(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            getMaxRecordId(player);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return recordId;
    }

    private JsonBuilder getNavrhInfoHover(int rejected, int accepted, int adminId, String rejectedReason, String content, String createDate, String updateDate) {
        JsonBuilder hoverInfo = new JsonBuilder(content)
                .color(colors.getPrimaryColorHEX())
                .text("\n\nVytvořeno: ")
                .color(colors.getSecondaryColorHEX())
                .text(createDate)
                .color(colors.getPrimaryColorHEX())
                .text("\nStav: ")
                .color(colors.getSecondaryColorHEX());
        if(rejected == 0 && accepted == 0) {
            hoverInfo.text("Čeká na schválení")
                    .color(ChatColor.GOLD);
        } else if(accepted == 1) {
            hoverInfo.text("Schváleno")
                    .color(ChatColor.GREEN)
                    .text("\nDatum: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(updateDate)
                    .color(ChatColor.GREEN)
                    .text("\nSchválil/a: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(playerManager.getUsername(adminId))
                    .color(ChatColor.GREEN);
        } else {
            hoverInfo.text("Zamítnuto")
                    .color(ChatColor.DARK_RED)
                    .text("\nDatum: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(updateDate)
                    .color(ChatColor.DARK_RED)
                    .text("\nZamítnul/a: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(playerManager.getUsername(adminId))
                    .color(ChatColor.DARK_RED)
                    .text("\nDůvod zamítnutí: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(rejectedReason)
                    .color(ChatColor.DARK_RED);
        }
        return hoverInfo;
    }

    private JsonBuilder getAdminButtons(JsonBuilder hoverInfo, int completed, int rejected, int accepted, int id, int adminId, String rejectedReason, String content, String createDate, String updateDate, String acceptHover, String rejectHover, String returnHover) {
        JsonBuilder jb = new JsonBuilder();
        if(completed == 0) {
            jb.text("[")
                    .color(colors.getSecondaryColorHEX())
                    .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, acceptHover, true)
                    .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/navrhy accept " + id)
                    .text("✔")
                    .color(ChatColor.GREEN)
                    .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, acceptHover, true)
                    .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/navrhy accept " + id)
                    .text("]")
                    .color(colors.getSecondaryColorHEX())
                    .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, acceptHover, true)
                    .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/navrhy accept " + id)
                    .text(" ")
                    .text("[")
                    .color(colors.getSecondaryColorHEX())
                    .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, rejectHover, true)
                    .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/navrhy reject " + id + " ")
                    .text("✖")
                    .color(ChatColor.DARK_RED)
                    .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, rejectHover, true)
                    .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/navrhy reject " + id + " ")
                    .text("]")
                    .color(colors.getSecondaryColorHEX())
                    .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, rejectHover, true)
                    .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/navrhy reject " + id + " ");
        } else {
            jb.text("     [")
                    .color(colors.getSecondaryColorHEX())
                    .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, returnHover, true)
                    .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/navrhy return " + id)
                    .text("◀")
                    .color(ChatColor.GOLD)
                    .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, returnHover, true)
                    .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/navrhy return " + id)
                    .text("]")
                    .color(colors.getSecondaryColorHEX())
                    .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, returnHover, true)
                    .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/navrhy return " + id);
        }
        jb.text(" - ")
                .color(colors.getSecondaryColorHEX())
                .text("[")
                .color(colors.getSecondaryColorHEX())
                .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                .text(String.valueOf(id))
                .color(colors.getPrimaryColorHEX())
                .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                .text("] ")
                .color(colors.getSecondaryColorHEX())
                .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true);
        return jb;
    }

    private void acceptNavrh(int id, int adminId) {
        try {
            PreparedStatement stm = db.getCon().prepareStatement("UPDATE " + tables.getNavrhyTable() + " SET accepted = 1, admin_id = ?, update_date = ? WHERE id = ?");
            stm.setInt(1, adminId);
            stm.setString(2, strUt.getCurrentFormattedDate());
            stm.setInt(3, id);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            acceptNavrh(id, adminId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void rejectNavrh(int id, int adminId, String rejectedReason) {
        try {
            PreparedStatement stm = db.getCon().prepareStatement("UPDATE " + tables.getNavrhyTable() + " SET rejected = 1, admin_id = ?, update_date = ?, rejected_reason = ? WHERE id = ?");
            stm.setInt(1, adminId);
            stm.setString(2, strUt.getCurrentFormattedDate());
            stm.setString(3, rejectedReason);
            stm.setInt(4, id);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            rejectNavrh(id, adminId, rejectedReason);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void returnNavrh(int id) {
        try {
            PreparedStatement stm = db.getCon().prepareStatement("UPDATE " + tables.getNavrhyTable() + " SET rejected = 0, accepted = 0, admin_id = NULL, update_date = NULL, rejected_reason = NULL WHERE id = ?");
            stm.setInt(1, id);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            returnNavrh(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void editNavrh(Player player, int recordId, String content) {
        try {
            PreparedStatement stm = db.getCon().prepareStatement("UPDATE " + tables.getNavrhyTable() + " SET content = ? WHERE user_id = ? AND record_id = ?");
            stm.setString(1, content);
            stm.setInt(2, playerManager.getUserId(player.getName()));
            stm.setInt(3, recordId);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            editNavrh(player, recordId, content);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private boolean isNavrh( int id) {
        int count = 0;
        try {
            PreparedStatement stm = db.getCon().prepareStatement("SELECT count(id) FROM " + tables.getNavrhyTable() + " WHERE id = ?");
            stm.setInt(1, id);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                count = rs.getInt(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            isNavrh(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (count != 0);
    }

    private boolean isRecordId(Player player, int recordId) {
        return recordId <= getMaxRecordId(player);
    }

    private void deleteNavrh(Player player, int recordId) {
        try {
            PreparedStatement stm;
            ResultSet rs;
            stm = db.getCon().prepareStatement("DELETE FROM " + tables.getNavrhyTable() + " WHERE record_id = ? AND user_id = ?");
            stm.setInt(1, recordId);
            stm.setInt(2, playerManager.getUserId(player.getName()));
            stm.execute();
            stm = db.getCon().prepareStatement("UPDATE " + tables.getNavrhyTable() + " SET record_id = record_id - 1 WHERE record_id > ? AND user_id = ? ");
            stm.setInt(1, recordId);
            stm.setInt(2, playerManager.getUserId(player.getName()));
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            deleteNavrh(player, recordId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isCompleted(Player player, int recordId) {
        int sum = 0;
        try {
            PreparedStatement stm = db.getCon().prepareStatement("SELECT (accepted+rejected) FROM " + tables.getNavrhyTable() + " WHERE user_id = ? AND record_id = ?");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            stm.setInt(2, recordId);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                sum = rs.getInt(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            isCompleted(player, recordId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (sum != 0);
    }
}
