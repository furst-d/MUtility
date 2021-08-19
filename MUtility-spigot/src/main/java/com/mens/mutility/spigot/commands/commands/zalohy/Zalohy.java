package com.mens.mutility.spigot.commands.commands.zalohy;

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
import com.mens.mutility.spigot.utils.*;
import com.mens.mutility.spigot.utils.confirmations.Confirmation;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Zalohy extends CommandHelp {
    private final MUtilitySpigot plugin;
    private PageList helpList;
    private final Database db;
    private final DatabaseTables tables;
    private final Prefix prefix;
    private final PlayerManager playerManager;
    private final PluginColors colors;
    private final MyStringUtils strUt;
    private final Errors errors;
    private final PageList showList;
    private final PageList manageList;
    private final PageList adminList;
    private final PageList adminUserList;
    private final Checker checker;
    private final List<Confirmation> deleteConfirmationList;

    public Zalohy(MUtilitySpigot plugin) {
        this.plugin = plugin;
        db = plugin.getDb();
        prefix = new Prefix();
        helpList = new PageList(10, prefix.getZalohyPrefix(true, true).replace("]", " - nápověda]"), "/zalohy");
        showList = new PageList(10, prefix.getZalohyPrefix(true, true).replace("]", " - seznam]"), "/zalohy zobraz");
        showList.setEmptyMessage(" Nemáš vytvořené žádné zálohy!\n Pro přidání zálohy použij /zalohy pridej [<Parametry>]");
        manageList = new PageList(10, prefix.getZalohyPrefix(true, true).replace("]", " - úprava]"), "/zalohy manage");
        adminList = new PageList(10, prefix.getZalohyPrefix(true, true).replace("]", " - admin]"), "/zalohy admin");
        adminList.setEmptyMessage(" Nejsou vytvořené žádné zálohy!");
        adminUserList = new PageList(10, null, null);
        adminUserList.setEmptyMessage(" Hráč nemá vytvořené žádné zálohy!");
        tables = new DatabaseTables();
        playerManager = new PlayerManager();
        colors = new PluginColors();
        strUt = new MyStringUtils();
        errors = new Errors();
        checker = new Checker(plugin);
        deleteConfirmationList = new ArrayList<>();
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public final CommandData create() {
        final CommandData zalohy = new CommandData("zalohy", "Zálohy","mutility.zalohy.help", CommandExecutors.PLAYER, t -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1, null).toPlayer((Player) t.getSender());
        });

        // 1. stupeň
        final CommandData help = new CommandData(ArgumentTypes.DEFAULT, "help", TabCompleterTypes.DEFAULT, "mutility.zalohy.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1, null).toPlayer((Player) t.getSender());
        });
        final CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData pridej = new CommandData(ArgumentTypes.DEFAULT, "pridej", TabCompleterTypes.DEFAULT, "mutility.zalohy.create");
        final CommandData zobraz = new CommandData(ArgumentTypes.DEFAULT, "zobraz", TabCompleterTypes.DEFAULT, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> loadShowList((Player)t.getSender(), 1));
        final CommandData manage = new CommandData(ArgumentTypes.DEFAULT, "manage", TabCompleterTypes.NONE);
        final CommandData delete = new CommandData(ArgumentTypes.DEFAULT, "delete", TabCompleterTypes.NONE);
        final CommandData admin = new CommandData(ArgumentTypes.DEFAULT, "admin", TabCompleterTypes.DEFAULT, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> loadAdminList((Player)t.getSender(), 1));
        final CommandData tp = new CommandData(ArgumentTypes.DEFAULT, "tp", TabCompleterTypes.NONE);
        final CommandData complete = new CommandData(ArgumentTypes.DEFAULT, "complete", TabCompleterTypes.NONE);
        final CommandData reject = new CommandData(ArgumentTypes.DEFAULT, "reject", TabCompleterTypes.NONE);
        final CommandData returnZaloha = new CommandData(ArgumentTypes.DEFAULT, "return", TabCompleterTypes.NONE);

        // 2. stupeň
        final CommandData helpHelpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.zalohy.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1]), null).toPlayer((Player) t.getSender());
        });
        final CommandData pridejX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.zalohy.create");
        final CommandData zobrazPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData manageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[1]);
            if(!isCompleted((Player)t.getSender(), recordId)) {
                if(isZaloha((Player) t.getSender(), recordId, false)) {
                    loadManageList((Player)t.getSender(), recordId);
                } else {
                    t.getSender().sendMessage(prefix.getInventoryPrefix(true, false) + errors.errWrongArgument(String.valueOf(recordId),true, false));
                }

            } else {
                t.getSender().sendMessage(prefix.getZalohyPrefix(true, false)
                        + colors.getSecondaryColor() + "Zálohu již není možné upravit");
            }
        });
        final CommandData deleteID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.delete", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[1]);
            if(isZaloha((Player)t.getSender(), recordId, false)) {
                if(!isCompleted((Player)t.getSender(), recordId)) {
                    Confirmation deleteConfirmation = new Confirmation(recordId, (Player) t.getSender(), "/zalohy delete confirm");
                    deleteConfirmation.setMessage(new JsonBuilder()
                            .addJsonSegment(prefix.getZalohyPrefix(true, true))
                            .text(": Opravdu si přejete odstranit tuto zálohu?")
                            .color(colors.getSecondaryColorHEX()));
                    if(deleteConfirmationList.stream().noneMatch(x -> (x.getId() == recordId
                            && x.getPlayer().getName().equals(t.getSender().getName())
                            && !x.isFinished()))) {
                        deleteConfirmation.startTimer();
                        deleteConfirmationList.add(deleteConfirmation);
                    } else {
                        t.getSender().sendMessage(prefix.getZalohyPrefix(true, false)
                                + colors.getSecondaryColor() + "Žádost o potvrzení již byla vytvořena!");
                    }
                } else {
                    t.getSender().sendMessage(prefix.getZalohyPrefix(true, false)
                            + colors.getSecondaryColor() + "Zálohu již není možné smazat");
                }
            } else {
                t.getSender().sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1],true, false));
            }
        });
        final CommandData deleteConfirm = new CommandData(ArgumentTypes.DEFAULT, "confirm", TabCompleterTypes.NONE);
        final CommandData adminPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData adminName = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.GLOBAL_ONLINE_PLAYERS, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> loadAdminUserList(t.getArgs()[1], (Player) t.getSender(), 1));
        final CommandData tpID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            int id = Integer.parseInt(t.getArgs()[1]);
            if(isZaloha(player, id, true)) {
                teleportPlayer(player, id);
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
            }
        });
        final CommandData completeID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            Player player = (Player) t.getSender();
            if(isZaloha((Player)t.getSender(), id, true)) {
                completeZaloha(player, id);
                player.sendMessage(prefix.getZalohyPrefix(true, false)
                        + colors.getSecondaryColor() + "Záloha " + colors.getPrimaryColor() + getBuildingName(id) + colors.getSecondaryColor() + " byla nastavena jako dokončená");
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
            }
        });
        final CommandData rejectID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE);
        final CommandData returnID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            Player player = (Player) t.getSender();
            if(isZaloha((Player)t.getSender(), id, true)) {
                returnZaloha(id);
                player.sendMessage(prefix.getZalohyPrefix(true, false)
                        + colors.getSecondaryColor() + "Záloha " + colors.getPrimaryColor() + getBuildingName(id) + colors.getSecondaryColor() + " byla nastavena jako nedokončená");
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
            }
        });

        // 3. stupeň
        final CommandData helpHelpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.zalohy.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[2]), null).toPlayer((Player) t.getSender());
        });
        final CommandData pridejY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.zalohy.create");
        final CommandData zobrazPageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.manage", CommandExecutors.PLAYER, t-> loadShowList((Player)t.getSender(), Integer.parseInt(t.getArgs()[2])));
        final CommandData setX = new CommandData(ArgumentTypes.DEFAULT, "setx", TabCompleterTypes.NONE);
        final CommandData setY = new CommandData(ArgumentTypes.DEFAULT, "sety", TabCompleterTypes.NONE);
        final CommandData setZ = new CommandData(ArgumentTypes.DEFAULT, "setz", TabCompleterTypes.NONE);
        final CommandData setWorld = new CommandData(ArgumentTypes.DEFAULT, "setworld", TabCompleterTypes.NONE);
        final CommandData setNote = new CommandData(ArgumentTypes.DEFAULT, "setnote", TabCompleterTypes.NONE);
        final CommandData setName = new CommandData(ArgumentTypes.DEFAULT, "setname", TabCompleterTypes.NONE);
        final CommandData adminPageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> loadAdminList((Player) t.getSender(), Integer.parseInt(t.getArgs()[2])));
        final CommandData adminNamePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData rejectReason = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Důvod zamítnutí >]", "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            String reason = strUt.getStringFromArgs(t.getArgs(), 2);
            Player player = (Player) t.getSender();
            if(isZaloha((Player)t.getSender(), id, true)) {
                rejectZaloha(player, id, reason);
                player.sendMessage(prefix.getZalohyPrefix(true, false)
                        + colors.getSecondaryColor() + "Záloha " + colors.getPrimaryColor() + getBuildingName(id) + colors.getSecondaryColor() + " byla nastavena jako zamítnutá");
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(id), true, false));
            }
        });
        final CommandData deleteConfirmID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.delete", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[2]);
            if(isZaloha((Player)t.getSender(), recordId, false)) {
                boolean valid = false;
                for (int i = deleteConfirmationList.size() - 1; i >= 0; i--) {
                    if(deleteConfirmationList.get(i).getId() == recordId
                            && deleteConfirmationList.get(i).getPlayer().getName().equals(t.getSender().getName())) {
                        if(!deleteConfirmationList.get(i).isFinished()) {
                            valid = true;
                            deleteConfirmationList.get(i).setFinished(true);
                            deleteZaloha((Player)t.getSender(), recordId);
                            t.getSender().sendMessage(prefix.getZalohyPrefix(true, false) + "Záloha byla smazána!");
                            break;
                        }
                    }
                }
                if(!valid) {
                    t.getSender().sendMessage(prefix.getZalohyPrefix(true, false)
                            + "Potvrzení o smazání zálohy není platné!");
                }
                deleteConfirmationList.removeIf(Confirmation::isFinished);
            } else {
                t.getSender().sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(t.getArgs()[2],true, false));
            }
        });

        // 4. stupeň
        final CommandData pridejZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.zalohy.create");
        final CommandData setXX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[1]);
            float x = Float.parseFloat(t.getArgs()[3]);
            Player player = (Player) t.getSender();
            if(isZaloha(player, recordId, false)) {
                setX(player, recordId, x);
                player.sendMessage(prefix.getZalohyPrefix(true, false)
                        + colors.getSecondaryColor() + "Souřadnice " + colors.getPrimaryColor() + "X " + colors.getSecondaryColor() + "byla nastavena na " + colors.getPrimaryColor() + x);
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(recordId), true, false));
            }
        });
        final CommandData setYY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[1]);
            float y = Float.parseFloat(t.getArgs()[3]);
            Player player = (Player) t.getSender();
            if(isZaloha(player, recordId, false)) {
                setY(player, recordId, y);
                player.sendMessage(prefix.getZalohyPrefix(true, false)
                        + colors.getSecondaryColor() + "Souřadnice " + colors.getPrimaryColor() + "Y " + colors.getSecondaryColor() + "byla nastavena na " + colors.getPrimaryColor() + y);
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(recordId), true, false));
            }
        });
        final CommandData setZZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[1]);
            float z = Float.parseFloat(t.getArgs()[3]);
            Player player = (Player) t.getSender();
            if(isZaloha(player, recordId, false)) {
                setZ(player, recordId, z);
                player.sendMessage(prefix.getZalohyPrefix(true, false)
                        + colors.getSecondaryColor() + "Souřadnice " + colors.getPrimaryColor() + "Z " + colors.getSecondaryColor() + "byla nastavena na " + colors.getPrimaryColor() + z);
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(recordId), true, false));
            }
        });
        final CommandData setWorldWorld = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.WORLDS, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[1]);
            String world = t.getArgs()[3];
            Player player = (Player) t.getSender();
            if(isZaloha(player, recordId, false)) {
                if(checker.checkWorld(world)) {
                    setWorld(player, recordId, world);
                    player.sendMessage(prefix.getZalohyPrefix(true, false)
                            + colors.getSecondaryColor() + "Svět byl nastaven na " + colors.getPrimaryColor() + world);
                } else {
                    player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(world, true, false));
                }
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(recordId), true, false));
            }
        });
        final CommandData setNoteNote = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Poznámka >]", "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[1]);
            String note = strUt.getStringFromArgs(t.getArgs(), 3);
            Player player = (Player) t.getSender();
            if(isZaloha(player, recordId, false)) {
                setNote(player, recordId, note);
                player.sendMessage(prefix.getZalohyPrefix(true, false)
                        + colors.getSecondaryColor() + "Poznámka byl nastaven na " + colors.getPrimaryColor() + note);
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(recordId), true, false));
            }
        });
        final CommandData setNameName = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název stavby/staveb >]", "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[1]);
            String buildingName = strUt.getStringFromArgs(t.getArgs(), 3);
            Player player = (Player) t.getSender();
            if(isZaloha(player, recordId, false)) {
                setBuildingName(player, recordId, buildingName);
                player.sendMessage(prefix.getZalohyPrefix(true, false)
                        + colors.getSecondaryColor() + "Název byl nastaven na " + colors.getPrimaryColor() + buildingName);
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(String.valueOf(recordId), true, false));
            }
        });
        final CommandData adminNamePageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> loadAdminUserList(t.getArgs()[1], (Player) t.getSender(), Integer.parseInt(t.getArgs()[3])));

        // 5. stupeň
        final CommandData pridejWorld = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.WORLDS, "mutility.zalohy.create");

        // 6. stupeň
        final CommandData pridejName = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název stavby/staveb >]", "mutility.zalohy.create", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            float x = Float.parseFloat(t.getArgs()[1]);
            float y = Float.parseFloat(t.getArgs()[2]);
            float z = Float.parseFloat(t.getArgs()[3]);
            String world = t.getArgs()[4];
            String buildingName = strUt.getStringFromArgs(t.getArgs(), 5);
            int recordId = getMaxRecordId((Player)t.getSender()) + 1;
            if(checker.checkWorld(world)) {
                insertZaloha((Player)t.getSender(), recordId, buildingName, x, y, z, world);
                new JsonBuilder()
                        .addJsonSegment(prefix.getZalohyPrefix(true, true))
                        .text(": Záloha ")
                        .color(colors.getSecondaryColorHEX())
                        .text(buildingName)
                        .color(colors.getPrimaryColorHEX())
                        .text(" byla vytvořena!\nKliknutím ")
                        .color(colors.getSecondaryColorHEX())
                        .text("➥Zde")
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, new JsonBuilder(">> Klikni pro přidání ")
                                .color(colors.getSecondaryColorHEX())
                                .text("Poznámky")
                                .color(colors.getPrimaryColorHEX())
                                .text(" <<")
                                .color(colors.getSecondaryColorHEX())
                                .toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setNote ")
                        .color(colors.getPrimaryColorHEX())
                        .text(" můžete přidat poznámku pro moderátory.")
                        .color(colors.getSecondaryColorHEX())
                        .toPlayer((Player)t.getSender());
            } else {
                player.sendMessage(prefix.getZalohyPrefix(true, false) + errors.errWrongArgument(world, true, false));
            }
        });

        zalohy.setDescription("Systém pro správu záloh");

        help.setDescription("Nápověda k příkazu");
        help.setSyntax("/zalohy " + help.getSubcommand());

        admin.setDescription("Zobrazí seznam všech hráčů, kteří si zažádali o zálohu a jejich statistiky.\nSeznam staveb jednotlivého hráče lze specifikovat dalším parametrem");
        admin.setSyntax("/zalohy " + admin.getSubcommand() + "\n/zalohy " + admin.getSubcommand() + " [<Jméno hráče>]");

        pridej.setDescription("Vytvoření nové žádosti na přesun staveb na zadaných souřadnicích");
        pridej.setSyntax("/zalohy " + pridej.getSubcommand() + " [<X>] [<Y>] [<Z>] [<Název stavby/staveb>]");

        zobraz.setDescription("Zobrazí uživateli jeho žádosti na přesun staveb");
        zobraz.setSyntax("/zalohy " + zobraz.getSubcommand());

        zalohy.link(help);
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

        help.link(helpHelpPage);
        helpPage.link(helpPageID);
        pridej.link(pridejX);
        zobraz.link(zobrazPage);
        manage.link(manageID);
        delete.link(deleteID);
        delete.link(deleteConfirm);
        admin.link(adminPage);
        admin.link(adminName);
        tp.link(tpID);
        complete.link(completeID);
        reject.link(rejectID);
        returnZaloha.link(returnID);

        helpHelpPage.link(helpHelpPageID);
        pridejX.link(pridejY);
        zobrazPage.link(zobrazPageID);
        manageID.link(setX);
        manageID.link(setY);
        manageID.link(setZ);
        manageID.link(setWorld);
        manageID.link(setNote);
        manageID.link(setName);
        adminPage.link(adminPageID);
        adminName.link(adminNamePage);
        rejectID.link(rejectReason);
        deleteConfirm.link(deleteConfirmID);

        pridejY.link(pridejZ);
        setX.link(setXX);
        setY.link(setYY);
        setZ.link(setZZ);
        setWorld.link(setWorldWorld);
        setNote.link(setNoteNote);
        setName.link(setNameName);
        adminNamePage.link(adminNamePageID);

        pridejZ.link(pridejWorld);

        pridejWorld.link(pridejName);

        return zalohy;
    }

    private void deleteZaloha(Player player, int recordId) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            ResultSet rs;
            stm = db.getCon().prepareStatement("DELETE FROM " + tables.getZalohyTable() + " WHERE record_id = ? AND user_id = ?");
            stm.setInt(1, recordId);
            stm.setInt(2, playerManager.getUserId(player.getName()));
            stm.execute();
            stm = db.getCon().prepareStatement("UPDATE " + tables.getZalohyTable() + " SET record_id = record_id - 1 WHERE record_id > ? AND user_id = ? ");
            stm.setInt(1, recordId);
            stm.setInt(2, playerManager.getUserId(player.getName()));
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            deleteZaloha(player, recordId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadManageList(Player player, int recordId) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            manageList.clear();
            PreparedStatement stm = db.getCon().prepareStatement("SELECT building_name, posX, posY, posZ, world, note  FROM " + tables.getZalohyTable() + " WHERE user_id = ? AND record_id = ?");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            stm.setInt(2, recordId);
            ResultSet rs =  stm.executeQuery();
            String nameHover = new JsonBuilder(">> Klikni pro úpravu ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Názvu")
                    .color(colors.getPrimaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String posXHover = new JsonBuilder(">> Klikni pro úpravu souřadnice ")
                    .color(colors.getSecondaryColorHEX())
                    .text("X")
                    .color(colors.getPrimaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String posYHover = new JsonBuilder(">> Klikni pro úpravu souřadnice ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Y")
                    .color(colors.getPrimaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String posZHover = new JsonBuilder(">> Klikni pro úpravu souřadnice ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Z")
                    .color(colors.getPrimaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String worldHover = new JsonBuilder(">> Klikni pro úpravu ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Světa")
                    .color(colors.getPrimaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String noteHover = new JsonBuilder(">> Klikni pro úpravu ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Poznámky")
                    .color(colors.getPrimaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String buildingName;
            float posX;
            float posY;
            float posZ;
            String world;
            String note;
            while(rs.next()) {
                buildingName = rs.getString(1);
                posX = rs.getFloat(2);
                posY = rs.getFloat(3);
                posZ = rs.getFloat(4);
                world = rs.getString(5);
                note = rs.getString(6);
                manageList.add(new JsonBuilder("ID: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(recordId))
                        .color(colors.getPrimaryColorHEX())
                        .getJsonSegments());
                manageList.add(new JsonBuilder("Název: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(buildingName)
                        .color(colors.getPrimaryColorHEX())
                        .text(" [")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, nameHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setName ")
                        .text("•••")
                        .color(ChatColor.GOLD)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, nameHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setName ")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, nameHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setName ")
                        .getJsonSegments());
                manageList.add(new JsonBuilder("Souřadnice: ")
                        .color(colors.getSecondaryColorHEX())
                        .getJsonSegments());
                manageList.add(new JsonBuilder("   - X = ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(posX))
                        .color(colors.getPrimaryColorHEX())
                        .text(" [")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, posXHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setX ")
                        .text("•••")
                        .color(ChatColor.GOLD)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, posXHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setX ")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, posXHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setX ")
                        .getJsonSegments());
                manageList.add(new JsonBuilder("   - Y = ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(posY))
                        .color(colors.getPrimaryColorHEX())
                        .text(" [")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, posYHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setY ")
                        .text("•••")
                        .color(ChatColor.GOLD)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, posYHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setY ")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, posYHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setY ")
                        .getJsonSegments());
                manageList.add(new JsonBuilder("   - Z = ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(posZ))
                        .color(colors.getPrimaryColorHEX())
                        .text(" [")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, posZHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setZ ")
                        .text("•••")
                        .color(ChatColor.GOLD)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, posZHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setZ ")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, posZHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setZ ")
                        .getJsonSegments());
                manageList.add(new JsonBuilder("   - Svět = ")
                        .color(colors.getSecondaryColorHEX())
                        .text(world)
                        .color(colors.getPrimaryColorHEX())
                        .text(" [")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, worldHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setWorld ")
                        .text("•••")
                        .color(ChatColor.GOLD)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, worldHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setWorld ")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, worldHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setWorld ")
                        .getJsonSegments());
                manageList.add(new JsonBuilder("Poznámka: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(note)
                        .color(colors.getPrimaryColorHEX())
                        .text(" [")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, noteHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setNote ")
                        .text("•••")
                        .color(ChatColor.GOLD)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, noteHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setNote ")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, noteHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy manage " + recordId + " setNote ")
                        .getJsonSegments());
            }
            manageList.getList(1, null).toPlayer(player);
        } catch (CommunicationsException e) {
            db.openConnection();
            loadManageList(player, recordId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void loadShowList(Player player, int page) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            showList.clear();
            PreparedStatement stm = db.getCon().prepareStatement("SELECT * FROM " + tables.getZalohyTable() + " WHERE user_id = ?");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            ResultSet rs =  stm.executeQuery();
            String manageHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Úpravu")
                    .color(ChatColor.GOLD)
                    .text(" zálohy <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String deleteHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Smazání")
                    .color(ChatColor.DARK_RED)
                    .text(" zálohy <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String manageDisableHover = new JsonBuilder(">> ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Zálohu již ")
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
                    .text("Zálohu již ")
                    .color(colors.getDisableColorHEX())
                    .text("nelze")
                    .color(ChatColor.DARK_RED)
                    .text(" smazat")
                    .color(colors.getDisableColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            int recordId;
            String buildingName;
            String note;
            int rejected;
            String rejectedReason;
            int completed;
            int adminId;
            String world;
            float posX;
            float posY;
            float posZ;
            String createDate;
            String updateDate;
            while(rs.next()) {
                recordId = rs.getInt(3);
                buildingName = rs.getString(4);
                note = rs.getString(5);
                rejected = rs.getInt(6);
                rejectedReason = rs.getString(7);
                completed = rs.getInt(8);
                adminId = rs.getInt(9);
                world = rs.getString(10);
                posX = rs.getFloat(11);
                posY = rs.getFloat(12);
                posZ = rs.getFloat(13);
                createDate = rs.getString(14);
                updateDate = rs.getString(15);
                JsonBuilder jb = new JsonBuilder();
                JsonBuilder hoverInfo = getZalohaInfoHover(rejected, completed, posX, posY, posZ, world, adminId, rejectedReason, createDate, updateDate, note);
                jb.text("[")
                        .color(colors.getSecondaryColorHEX());
                if(completed == 0 && rejected == 0) {
                    jb.hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy manage " + recordId + " ")
                            .text("•••")
                            .color(ChatColor.GOLD)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy manage " + recordId + " ")
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy manage " + recordId + " ")
                            .text(" ")
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy delete " + recordId)
                            .text("✖")
                            .color(ChatColor.DARK_RED)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy delete " + recordId)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy delete " + recordId);
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
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                        .text(buildingName);
                if(completed == 0 && rejected == 0) {
                    jb.color(ChatColor.GOLD);
                } else if(completed == 1) {
                    jb.color(ChatColor.GREEN);
                } else {
                    jb.color(ChatColor.DARK_RED);
                }
                jb.hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true);
                showList.add(jb.getJsonSegments());
            }
            showList.getList(page, null).toPlayer(player);
        } catch (CommunicationsException e) {
            db.openConnection();
            loadShowList(player, page);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void loadAdminList(Player player, int page) {
        adminList.clear();
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            int statCompleted = 0;
            int statRejected = 0;
            int statTotal = 0;
            float statPercent = 0;
            PreparedStatement stm = db.getCon().prepareStatement("SELECT (SELECT count(completed) FROM "+ tables.getZalohyTable() + " WHERE completed = 1),(SELECT count(completed) FROM "+ tables.getZalohyTable() + " WHERE rejected = 1),(SELECT count(completed) FROM "+ tables.getZalohyTable() + ") FROM "+ tables.getZalohyTable() + " LIMIT 1");
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
            JsonBuilder statsHover = new JsonBuilder("Celkem: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.valueOf(statTotal))
                    .color(colors.getPrimaryColorHEX())
                    .text("\nPřesunuto: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.valueOf(statCompleted))
                    .color(ChatColor.GREEN)
                    .text("\nZamítnuto: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.valueOf(statRejected))
                    .color(ChatColor.DARK_RED)
                    .text("\nZbývá: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.valueOf((statTotal - (statCompleted + statRejected))))
                    .color(ChatColor.GOLD);
            JsonBuilder head = new JsonBuilder("Celkem přesunuto: ")
                    .color(colors.getSecondaryColorHEX())
                    .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, statsHover.toString(), true)
                    .text(String.format("%1.1f", statPercent));
            if(statPercent <= 33) {
                head.color(ChatColor.DARK_RED);
            } else if(statPercent <= 66) {
                head.color(ChatColor.GOLD);
            } else {
                head.color(ChatColor.GREEN);
            }
            head.hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, statsHover.toString(), true);
            head.text(" %")
                    .color(colors.getSecondaryColorHEX())
                    .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, statsHover.toString(), true);
            adminList.setHead(head);
            while(rs.next()) {
                username = rs.getString(1);
                rejected = rs.getInt(2);
                completed = rs.getInt(3);
                total = rs.getInt(4);
                done = rs.getInt(5);
                JsonBuilder jb = new JsonBuilder(" - ")
                        .color(colors.getSecondaryColorHEX())
                        .text("➥" + username);
                JsonBuilder userStatsHover = new JsonBuilder("Stav: ")
                        .color(colors.getSecondaryColorHEX());
                if(done == 0) {
                    jb.color(ChatColor.GOLD);
                    userStatsHover.text("Nekompletní")
                            .color(ChatColor.GOLD);
                } else {
                    jb.color(ChatColor.GREEN);
                    userStatsHover.text("Kompletní")
                            .color(ChatColor.GREEN);
                }
                userStatsHover.text("\nCelkem: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(total))
                        .color(colors.getPrimaryColorHEX())
                        .text("\nPřesunuto: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(completed))
                        .color(ChatColor.GREEN)
                        .text("\nZamítnuto: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(rejected))
                        .color(ChatColor.DARK_RED)
                        .text("\nZbývá: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(total - (completed + rejected)))
                        .color(ChatColor.GOLD);
                jb.hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, userStatsHover.toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy admin " + username);
                adminList.add(jb.getJsonSegments());
            }
            adminList.getList(page, null).toPlayer(player);
        } catch (CommunicationsException e) {
            db.openConnection();
            loadAdminList(player, page);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAdminUserList(String playerName, Player player, int page) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            adminUserList.clear();
            adminUserList.setCommand("/zalohy admin " + playerName);
            adminUserList.setTitleJson(prefix.getZalohyPrefix(true, true).replace("]", " - " + playerName + "]"));
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id, building_name, note, rejected, rejected_reason, completed, admin_id, world, posX, posY, posZ, create_date, update_date, (SUM(completed+rejected)) FROM " + tables.getZalohyTable() + " WHERE user_id = ? GROUP BY id ORDER BY (SUM(completed+rejected)), create_date DESC");
            stm.setInt(1, playerManager.getUserId(playerName));
            ResultSet rs =  stm.executeQuery();
            String completeHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Dokončení")
                    .color(ChatColor.GREEN)
                    .text(" zálohy <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String rejectHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Zamítnutí")
                    .color(ChatColor.DARK_RED)
                    .text(" zálohy <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String returnHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Vrácení")
                    .color(ChatColor.GOLD)
                    .text(" zálohy mezi nepřesunuté <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String teleportHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Teleportaci")
                    .color(colors.getPrimaryColorHEX())
                    .text(" na zálohu <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            int id;
            String buildingName;
            String note;
            int rejected;
            String rejectedReason;
            int completed;
            int adminId;
            String world;
            float posX;
            float posY;
            float posZ;
            String createDate;
            String updateDate;
            int done;
            while(rs.next()) {
                id = rs.getInt(1);
                buildingName = rs.getString(2);
                note = rs.getString(3);
                rejected = rs.getInt(4);
                rejectedReason = rs.getString(5);
                completed = rs.getInt(6);
                adminId = rs.getInt(7);
                world = rs.getString(8);
                posX = rs.getFloat(9);
                posY = rs.getFloat(10);
                posZ = rs.getFloat(11);
                createDate = rs.getString(12);
                updateDate = rs.getString(13);
                done = rs.getInt(14);
                JsonBuilder hoverInfo = getZalohaInfoHover(rejected, completed, posX, posY, posZ, world, adminId, rejectedReason, createDate, updateDate, note);
                JsonBuilder jb = new JsonBuilder();
                if(done == 0) {
                    jb.text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, completeHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy complete " + id)
                            .text("✔")
                            .color(ChatColor.GREEN)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, completeHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy complete " + id)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, completeHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy complete " + id)
                            .text(" ")
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, rejectHover, true)
                            .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy reject " + id + " ")
                            .text("✖")
                            .color(ChatColor.DARK_RED)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, rejectHover, true)
                            .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy reject " + id + " ")
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, rejectHover, true)
                            .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/zalohy reject " + id + " ");
                } else {
                    jb.text("     [")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, returnHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy return " + id)
                            .text("◀")
                            .color(ChatColor.GOLD)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, returnHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy return " + id)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, returnHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy return " + id);
                }
                jb.text(" [")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, teleportHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy tp " + id)
                        .text("☄")
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, teleportHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy tp " + id)
                        .text("] ")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, teleportHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/zalohy tp " + id)
                        .text(" - ")
                        .color(colors.getSecondaryColorHEX())
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                        .text(String.valueOf(id))
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                        .text("] ")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                        .text(buildingName);
                if(completed == 0 && rejected == 0) {
                    jb.color(ChatColor.GOLD);
                } else if(completed == 1) {
                    jb.color(ChatColor.GREEN);
                } else {
                    jb.color(ChatColor.DARK_RED);
                }
                jb.hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true);
                adminUserList.add(jb.getJsonSegments());
            }
            adminUserList.getList(page, null).toPlayer(player);
        } catch (CommunicationsException e) {
            db.openConnection();
            loadAdminUserList(playerName, player, page);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private JsonBuilder getZalohaInfoHover(int rejected, int completed, float posX, float posY, float posZ, String world, int adminId, String rejectedReason, String createDate, String updateDate, String note) {
        JsonBuilder hoverInfo = new JsonBuilder("X: ")
                .color(colors.getSecondaryColorHEX())
                .text(String.valueOf(posX))
                .color(colors.getPrimaryColorHEX())
                .text("\nY: ")
                .color(colors.getSecondaryColorHEX())
                .text(String.valueOf(posY))
                .color(colors.getPrimaryColorHEX())
                .text("\nZ: ")
                .color(colors.getSecondaryColorHEX())
                .text(String.valueOf(posZ))
                .color(colors.getPrimaryColorHEX())
                .text("\nSvět: ")
                .color(colors.getSecondaryColorHEX())
                .text(world)
                .color(colors.getPrimaryColorHEX())
                .text("\nVytvořeno: ")
                .color(colors.getSecondaryColorHEX())
                .text(createDate)
                .color(colors.getPrimaryColorHEX())
                .text("\nPoznámka: ")
                .color(colors.getSecondaryColorHEX())
                .text(note)
                .color(colors.getPrimaryColorHEX())
                .text("\nStav: ")
                .color(colors.getSecondaryColorHEX());
        if(rejected == 0 && completed == 0) {
            hoverInfo.text("Čeká na schválení")
                    .color(ChatColor.GOLD);
        } else if(completed == 1) {
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

    private boolean isZaloha(Player player, int id, boolean global) {
        int count = 0;
        PreparedStatement stm;
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            if(global) {
                stm = db.getCon().prepareStatement("SELECT count(id) FROM "+ tables.getZalohyTable() + " WHERE id = ?");
                stm.setInt(1, id);
            } else {
                stm = db.getCon().prepareStatement("SELECT count(record_id) FROM "+ tables.getZalohyTable() + " WHERE user_id= ? AND record_id = ?");
                stm.setInt(1, playerManager.getUserId(player.getName()));
                stm.setInt(2, id);
            }
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                count = rs.getInt(1);
            }
        } catch (CommunicationsException e)  {
            db.openConnection();
            return isZaloha(player, id, global);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (count != 0);
    }

    private boolean isCompleted(Player player, int recordId) {
        int sum = 0;
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT (completed+rejected) FROM " + tables.getZalohyTable() + " WHERE user_id = ? AND record_id = ?");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            stm.setInt(2, recordId);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                sum = rs.getInt(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            return isCompleted(player, recordId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (sum != 0);
    }

    private int getMaxRecordId(Player player) {
        int maxRecordId = 0;
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT COALESCE(MAX(record_id), 0) FROM "+ tables.getZalohyTable() + " WHERE user_id = ?");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                maxRecordId = rs.getInt(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            return getMaxRecordId(player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxRecordId;
    }

    private String getBuildingName(int id) {
        String buildingName = "";
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT building_name FROM "+ tables.getZalohyTable() + " WHERE id = ?");
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                buildingName = rs.getString(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            return getBuildingName(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buildingName;
    }

    private void insertZaloha(Player player, int recordId, String buildingName, float x, float y, float z, String world) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("INSERT INTO "+ tables.getZalohyTable() + " (user_id, record_id, building_name, note, rejected, completed, posX, posY, posZ, world, create_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            stm.setInt(2, recordId);
            stm.setString(3, buildingName);
            stm.setString(4, "-");
            stm.setInt(5, 0);
            stm.setInt(6, 0);
            stm.setDouble(7, x);
            stm.setDouble(8, y);
            stm.setDouble(9, z);
            stm.setString(10, world);
            stm.setString(11, strUt.getCurrentFormattedDate());
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            insertZaloha(player, recordId, buildingName, x, y, z, world);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void completeZaloha(Player player, int id) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET rejected = 0, completed = 1, rejected_reason = null, admin_id = ?, update_date = ? WHERE id= ? ");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            stm.setString(2, strUt.getCurrentFormattedDate());
            stm.setInt(3, id);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            completeZaloha(player, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void rejectZaloha(Player player, int id, String rejectedReason) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET rejected = 1, completed = 0, rejected_reason = ?, admin_id = ?, update_date = ? WHERE id= ? ");
            stm.setString(1, rejectedReason);
            stm.setInt(2, playerManager.getUserId(player.getName()));
            stm.setString(3, strUt.getCurrentFormattedDate());
            stm.setInt(4, id);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            rejectZaloha(player, id, rejectedReason);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void returnZaloha(int id) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET rejected = 0, completed = 0, rejected_reason = NULL, admin_id = NULL, update_date = NULL WHERE id= ? ");
            stm.setInt(1, id);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            returnZaloha(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setX(Player player, int recordId, float x) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET posX = ? WHERE user_id = ? AND record_id = ? ");
            stm.setFloat(1, x);
            stm.setInt(2, playerManager.getUserId(player.getName()));
            stm.setInt(3, recordId);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            setX(player, recordId, x);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setY(Player player, int recordId, float y) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET posY = ? WHERE user_id = ? AND record_id = ? ");
            stm.setFloat(1, y);
            stm.setInt(2, playerManager.getUserId(player.getName()));
            stm.setInt(3, recordId);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            setY(player, recordId, y);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setZ(Player player, int recordId, float z) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET posZ = ? WHERE user_id = ? AND record_id = ? ");
            stm.setFloat(1, z);
            stm.setInt(2, playerManager.getUserId(player.getName()));
            stm.setInt(3, recordId);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            setZ(player, recordId, z);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setWorld(Player player, int recordId, String world) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET world = ? WHERE user_id = ? AND record_id = ? ");
            stm.setString(1, world);
            stm.setInt(2, playerManager.getUserId(player.getName()));
            stm.setInt(3, recordId);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            setWorld(player, recordId, world);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setNote(Player player, int recordId, String note) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET note = ? WHERE user_id = ? AND record_id = ? ");
            stm.setString(1, note);
            stm.setInt(2, playerManager.getUserId(player.getName()));
            stm.setInt(3, recordId);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            setNote(player, recordId, note);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setBuildingName(Player player, int recordId, String buildingName) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("UPDATE "+ tables.getZalohyTable() + " SET building_name = ? WHERE user_id = ? AND record_id = ? ");
            stm.setString(1, buildingName);
            stm.setInt(2, playerManager.getUserId(player.getName()));
            stm.setInt(3, recordId);
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            setBuildingName(player, recordId, buildingName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void teleportPlayer(Player player, int id) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT building_name, posX, posY, posZ, world FROM "+ tables.getZalohyTable() + " WHERE id= ?");
            stm.setInt(1, id);
            ResultSet rs =  stm.executeQuery();
            float x = 0;
            float y = 0;
            float z = 0;
            String world = "";
            String name = "";
            if(rs.next()) {
                name = rs.getString(1);
                x = rs.getFloat(2);
                y = rs.getFloat(3);
                z = rs.getFloat(4);
                world = rs.getString(5);
            }
            Location destination = player.getLocation();
            destination.setX(x);
            destination.setY(y);
            destination.setZ(z);
            destination.setWorld(WorldCreator.name(world).createWorld());
            player.teleport(destination);
            player.sendMessage(prefix.getZalohyPrefix(true, false) + "Byl jsi teleportován k záloze " + colors.getPrimaryColor() + name + colors.getSecondaryColor() + "!");
        } catch (CommunicationsException e) {
            db.openConnection();
            teleportPlayer(player, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

