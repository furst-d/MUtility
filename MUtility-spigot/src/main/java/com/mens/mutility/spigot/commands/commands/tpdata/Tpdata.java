package com.mens.mutility.spigot.commands.commands.tpdata;

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
import com.mens.mutility.spigot.gui.GUIManager;
import com.mens.mutility.spigot.inventory.InventoryManager;
import com.mens.mutility.spigot.inventory.InventoryPair;
import com.mens.mutility.spigot.inventory.TeleportData;
import com.mens.mutility.spigot.inventory.TeleportDataManager;
import com.mens.mutility.spigot.utils.Confirmation;
import com.mens.mutility.spigot.utils.PageList;
import com.mens.mutility.spigot.utils.PlayerManager;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import javafx.util.Pair;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tpdata extends CommandHelp {
    private final MUtilitySpigot plugin;
    private PageList helpList;
    private final PageList showList;
    private final PageList showNameList;
    private final Prefix prefix;
    private final PluginColors colors;
    private final Database db;
    private final DatabaseTables tables;
    private final PlayerManager playerManager;
    private final Errors errors;
    private final List<Confirmation> invConfirmationList;
    private final List<Confirmation> allConfirmationList;

    public Tpdata(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        helpList = new PageList(10, prefix.getTpDataPrefix(true, true).replace("]", " - nápověda]"), "/tpdata");
        showList = new PageList(10, prefix.getTpDataPrefix(true, true).replace("]", " - seznam]"), "/tpdata zobraz");
        showNameList = new PageList(10, null, null);
        colors = new PluginColors();
        db = plugin.getDb();
        tables = new DatabaseTables();
        playerManager = new PlayerManager();
        errors = new Errors();
        invConfirmationList = new ArrayList<>();
        allConfirmationList = new ArrayList<>();
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public final CommandData create() {
        final CommandData tpData = new CommandData("tpdata", "TP-Data", "mutility.tpdata.help", CommandExecutors.PLAYER, t -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1).toPlayer((Player) t.getSender());
        });

        // 1. stupeň
        final CommandData help = new CommandData(ArgumentTypes.DEFAULT, "help", TabCompleterTypes.DEFAULT, "mutility.tpdata.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1).toPlayer((Player) t.getSender());
        });
        final CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData show = new CommandData(ArgumentTypes.DEFAULT, "zobraz", TabCompleterTypes.DEFAULT, "mutility.tpdata.show", CommandExecutors.PLAYER, t -> loadShowList((Player)t.getSender(), 1));
        final CommandData rb = new CommandData(ArgumentTypes.DEFAULT, "rb", TabCompleterTypes.NONE);

        // 2. stupeň
        final CommandData helpHelpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.tpdata.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1])).toPlayer((Player) t.getSender());
        });
        final CommandData showName = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.ONLINE_PLAYERS, "mutility.tpdata.show", CommandExecutors.PLAYER, t -> loadShowNameList(t.getArgs()[1], (Player)t.getSender(), 1));
        final CommandData rbInv = new CommandData(ArgumentTypes.DEFAULT, "inv", TabCompleterTypes.NONE);
        final CommandData rbAll = new CommandData(ArgumentTypes.DEFAULT, "all", TabCompleterTypes.NONE);
        final CommandData rbPreview = new CommandData(ArgumentTypes.DEFAULT, "preview", TabCompleterTypes.NONE);
        final CommandData showPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);

        // 3. stupeň
        final CommandData helpHelpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.tpdata.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[2])).toPlayer((Player) t.getSender());
        });
        final CommandData rbInvId = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.tpdata.rb.inv", CommandExecutors.PLAYER, (t) -> {
            int id = Integer.parseInt(t.getArgs()[2]);
            if(isData(id)) {
                Confirmation invConfirmation = new Confirmation(id, (Player) t.getSender(), "/tpdata rb inv confirm");
                invConfirmation.setMessage(new JsonBuilder()
                        .addJsonSegment(prefix.getTpDataPrefix(true, true))
                        .text(": Opravdu si přejete ")
                        .color(colors.getSecondaryColorHEX())
                        .text("Rollbacknout ")
                        .color(colors.getPrimaryColorHEX())
                        .text("tento inventář?\nPři rollbacku dojde k přepsání současného inventáře hráče.")
                        .color(colors.getSecondaryColorHEX()));
                if(invConfirmationList.stream().noneMatch(x -> (x.getId() == id
                        && x.getPlayer().getName().equals(t.getSender().getName())
                        && !x.isFinished()))) {
                    invConfirmation.startTimer();
                    invConfirmationList.add(invConfirmation);
                } else {
                    t.getSender().sendMessage(prefix.getTpDataPrefix(true, false)
                            + colors.getSecondaryColor() + "Žádost o potvrzení již byla vytvořena!");
                }
            } else {
                t.getSender().sendMessage(prefix.getTpDataPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1],true, false));
            }
        });
        final CommandData rbAllId = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.tpdata.rb.all", CommandExecutors.PLAYER, (t) -> {
            int id = Integer.parseInt(t.getArgs()[2]);
            if(isData(id)) {
                Confirmation allConfirmation = new Confirmation(id, (Player) t.getSender(), "/tpdata rb all confirm");
                allConfirmation.setMessage(new JsonBuilder()
                        .addJsonSegment(prefix.getTpDataPrefix(true, true))
                        .text(": Opravdu si přejete ")
                        .color(colors.getSecondaryColorHEX())
                        .text("Rollbacknout ")
                        .color(colors.getPrimaryColorHEX())
                        .text("veškerá data?\nPři rollbacku dojde k přepsání současného hráčova inventáře, gamemodu, levelu, hungeru, zdraví, fly a efektů.")
                        .color(colors.getSecondaryColorHEX()));
                if(allConfirmationList.stream().noneMatch(x -> (x.getId() == id
                        && x.getPlayer().getName().equals(t.getSender().getName())
                        && !x.isFinished()))) {
                    allConfirmation.startTimer();
                    allConfirmationList.add(allConfirmation);
                } else {
                    t.getSender().sendMessage(prefix.getTpDataPrefix(true, false)
                            + colors.getSecondaryColor() + "Žádost o potvrzení již byla vytvořena!");
                }
            } else {
                t.getSender().sendMessage(prefix.getTpDataPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1],true, false));
            }
        });
        final CommandData rbPreviewId = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.tpdata.rb.all", CommandExecutors.PLAYER, (t) -> {
            int id = Integer.parseInt(t.getArgs()[2]);
            if(isData(id)) {
                InventoryManager invManager = new InventoryManager();
                Pair<String, String> invData = getInventory((Player)t.getSender(), id);
                InventoryPair invPair = invManager.getInventoryAsItemStack(invManager.toJsonObject(invData.getValue()));
                GUIManager guiManager = new GUIManager(plugin, 45, colors.getPrimaryColor()  + "§l" + invData.getKey());

                for (int i = 0; i < invPair.getItems().size(); i++) {
                    ItemStack item = invPair.getItems().get(i);
                    ItemMeta meta = item.getItemMeta();
                    if(item.getType() != Material.AIR) {
                        List<String> lore = new ArrayList<>();
                        lore.add(colors.getPrimaryColor() + "Ilustrační item");
                        assert meta != null;
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }
                    guiManager.addItem(item, i);
                }

                for (int i = 0; i < invPair.getArmor().size(); i++) {
                    ItemStack item = invPair.getArmor().get(i);
                    ItemMeta meta = item.getItemMeta();
                    if(item.getType() != Material.AIR) {
                        List<String> lore = new ArrayList<>();
                        lore.add(colors.getPrimaryColor() + "Ilustrační item");
                        assert meta != null;
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }
                    guiManager.addItem(item, i + 36);
                }

                guiManager.openGUI((Player)t.getSender());
            } else {
                t.getSender().sendMessage(prefix.getTpDataPrefix(true, false) + errors.errWrongArgument(t.getArgs()[2], true, false));
            }
        });
        final CommandData rbInvConfirm = new CommandData(ArgumentTypes.DEFAULT, "confirm", TabCompleterTypes.NONE);
        final CommandData rbAllConfirm = new CommandData(ArgumentTypes.DEFAULT, "confirm", TabCompleterTypes.NONE);
        final CommandData showNamePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData showPageId = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.tpdata.show", CommandExecutors.PLAYER, (t) -> loadShowList((Player)t.getSender(), Integer.parseInt(t.getArgs()[2])));

        // 4. stupeň
        final CommandData rbInvConfirmId = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.tpdata.rb.inv", CommandExecutors.PLAYER, (t) -> {
            int id = Integer.parseInt(t.getArgs()[3]);
            if(isData(id)) {
                boolean valid = false;
                for (int i = invConfirmationList.size() - 1; i >= 0; i--) {
                    if(invConfirmationList.get(i).getId() == id
                            && invConfirmationList.get(i).getPlayer().getName().equals(t.getSender().getName())) {
                        if(!invConfirmationList.get(i).isFinished()) {
                            valid = true;
                            invConfirmationList.get(i).setFinished(true);
                            TeleportDataManager teleportDataManager = new TeleportDataManager();
                            TeleportData data = teleportDataManager.loadDataById(id);
                            Player player = Bukkit.getPlayer(playerManager.getUsername(data.getUserId()));
                            if(player != null) {
                                InventoryManager inventoryManager = new InventoryManager();
                                inventoryManager.loadInventory(player, inventoryManager.toJsonObject(data.getInventory()));
                                t.getSender().sendMessage(prefix.getTpDataPrefix(true, false) + "Inventář byl rollbacknut!");
                            } else {
                                t.getSender().sendMessage(prefix.getTpDataPrefix(true, false) + "Hráč není online nebo se nachází na jiném serveru");
                            }
                            break;
                        }
                    }
                }
                if(!valid) {
                    t.getSender().sendMessage(prefix.getTpDataPrefix(true, false)
                            + "Potvrzení o rollback inventáře není platné!");
                }
                invConfirmationList.removeIf(Confirmation::isFinished);
            } else {
                t.getSender().sendMessage(prefix.getTpDataPrefix(true, false) + errors.errWrongArgument(t.getArgs()[3],true, false));
            }
        });
        final CommandData rbAllConfirmId = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.tpdata.rb.inv", CommandExecutors.PLAYER, (t) -> {
            int id = Integer.parseInt(t.getArgs()[3]);
            if(isData(id)) {
                boolean valid = false;
                for (int i = allConfirmationList.size() - 1; i >= 0; i--) {
                    if(allConfirmationList.get(i).getId() == id
                            && allConfirmationList.get(i).getPlayer().getName().equals(t.getSender().getName())) {
                        if(!allConfirmationList.get(i).isFinished()) {
                            valid = true;
                            allConfirmationList.get(i).setFinished(true);
                            TeleportDataManager teleportDataManager = new TeleportDataManager();
                            TeleportData data = teleportDataManager.loadDataById(id);
                            Player player = Bukkit.getPlayer(playerManager.getUsername(data.getUserId()));
                            if(player != null) {
                                teleportDataManager.applyData(player, data, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), Objects.requireNonNull(player.getLocation().getWorld()).getName());
                                t.getSender().sendMessage(prefix.getTpDataPrefix(true, false) + "Data byla rollbacknuta!");
                            } else {
                                t.getSender().sendMessage(prefix.getTpDataPrefix(true, false) + "Hráč není online nebo se nachází na jiném serveru");
                            }
                            break;
                        }
                    }
                }
                if(!valid) {
                    t.getSender().sendMessage(prefix.getTpDataPrefix(true, false)
                            + "Potvrzení o rollback dat není platné!");
                }
                allConfirmationList.removeIf(Confirmation::isFinished);
            } else {
                t.getSender().sendMessage(prefix.getTpDataPrefix(true, false) + errors.errWrongArgument(t.getArgs()[3],true, false));
            }
        });
        final CommandData showNamePageId = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.tpdata.show", CommandExecutors.PLAYER, (t) -> loadShowNameList(t.getArgs()[1], (Player)t.getSender(), Integer.parseInt(t.getArgs()[3])));

        tpData.setDescription("Systém teleportačních dat pro správu v případě nastání chyby při teleportaci");

        help.setDescription("Nápověda k příkazu");
        help.setSyntax("/tpdata " + help.getSubcommand());

        show.setDescription("Zobrazení seznamu teleportací za posledních 30 dní.\nSeznam lze filtrovat dle hráče přidáním jména hráče za příkaz");
        show.setSyntax("/tpdata " + show.getSubcommand() + "\n/tpdata " + show.getSubcommand() + " [<Jméno hráče>]");

        tpData.link(help);
        tpData.link(helpPage);
        tpData.link(show);
        tpData.link(rb);

        help.link(helpHelpPage);
        helpPage.link(helpPageID);
        show.link(showPage);
        show.link(showName);
        rb.link(rbInv);
        rb.link(rbAll);
        rb.link(rbPreview);

        helpHelpPage.link(helpHelpPageID);
        rbInv.link(rbInvId);
        rbAll.link(rbAllId);
        rbInv.link(rbInvConfirm);
        rbAll.link(rbAllConfirm);
        rbPreview.link(rbPreviewId);
        showPage.link(showPageId);
        showName.link(showNamePage);

        rbInvConfirm.link(rbInvConfirmId);
        rbAllConfirm.link(rbAllConfirmId);
        showNamePage.link(showNamePageId);

        return tpData;
    }

    private void loadShowList(Player player, int page) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            showList.clear();
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id, user_id, fromX, fromY, fromZ, fromWorld, fromServer, toX, toY, toZ, toWorld, toServer, gamemode, exp, level, hunger, health, fly, effects, created_date, completed FROM " + tables.getTeleportDataTable() + " ORDER BY created_date DESC");
            ResultSet rs =  stm.executeQuery();
            loadFormattedList(rs, showList);
            showList.getList(page).toPlayer(player);
        } catch (CommunicationsException e) {
            db.openConnection();
            loadShowList(player, page);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void loadShowNameList(String playerName, Player player, int page) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            showNameList.clear();
            showNameList.setCommand("/tpdata zobraz " + playerName);
            showNameList.setTitleJson(prefix.getTpDataPrefix(true, true).replace("]", " - " + playerName + "]"));
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id, user_id, fromX, fromY, fromZ, fromWorld, fromServer, toX, toY, toZ, toWorld, toServer, gamemode, exp, level, hunger, health, fly, effects, created_date, completed FROM " + tables.getTeleportDataTable() + " WHERE user_id = ? ORDER BY created_date DESC");
            stm.setInt(1, playerManager.getUserId(playerName));
            ResultSet rs =  stm.executeQuery();
            loadFormattedList(rs, showNameList);
            showNameList.getList(page).toPlayer(player);
        } catch (CommunicationsException e) {
            db.openConnection();
            loadShowNameList(playerName, player, page);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void loadFormattedList(ResultSet rs, PageList list) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            String showHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Zobrazení inventáře")
                    .color(ChatColor.GREEN)
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String inventoryRbHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Rollback inventáře")
                    .color(ChatColor.GOLD)
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String allRbHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Rollback všech dat")
                    .color(colors.getPrimaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            int id;
            int userID;
            String inventory;
            double fromX;
            double fromY;
            double fromZ;
            String fromWorld;
            String fromServer;
            double toX;
            double toY;
            double toZ;
            String toWorld;
            String toServer;
            String gamemode;
            float exp;
            int level;
            int hunger;
            double health;
            int fly;
            String effects;
            String created_date;
            int completed;
            while(rs.next()) {
                id = rs.getInt(1);
                userID = rs.getInt(2);
                fromX = rs.getDouble(3);
                fromY = rs.getDouble(4);
                fromZ = rs.getDouble(5);
                fromWorld = rs.getString(6);
                fromServer = rs.getString(7);
                toX = rs.getDouble(8);
                toY = rs.getDouble(9);
                toZ = rs.getDouble(10);
                toWorld = rs.getString(11);
                toServer = rs.getString(12);
                gamemode = rs.getString(13);
                exp = rs.getFloat(14);
                level = rs.getInt(15);
                hunger = rs.getInt(16);
                health = rs.getDouble(17);
                fly = rs.getInt(18);
                effects = rs.getString(19);
                created_date = rs.getString(20);
                completed = rs.getInt(21);
                JsonBuilder hoverInfo = new JsonBuilder();
                hoverInfo.text("Stav: ")
                        .color(colors.getSecondaryColorHEX());
                if(completed == 0) {
                    hoverInfo.text("Nedokončeno")
                            .color(ChatColor.DARK_RED);
                } else {
                    hoverInfo.text("Dokončeno")
                            .color(ChatColor.GREEN);
                }
                hoverInfo.text("\nPřesun Z: ")
                        .color(colors.getSecondaryColorHEX())
                        .text("\n - X: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.format("%.2f", fromX))
                        .color(colors.getPrimaryColorHEX())
                        .text(", Y: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.format("%.2f", fromY))
                        .color(colors.getPrimaryColorHEX())
                        .text(", Z: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.format("%.2f", fromZ))
                        .color(colors.getPrimaryColorHEX())
                        .text("\n - Svět: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(fromWorld)
                        .color(colors.getPrimaryColorHEX())
                        .text("\n - Server: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(fromServer)
                        .color(colors.getPrimaryColorHEX());
                if(completed == 1) {
                    hoverInfo.text("\nPřesun Do: ")
                            .color(colors.getSecondaryColorHEX())
                            .text("\n - X: ")
                            .color(colors.getSecondaryColorHEX())
                            .text(String.format("%.2f", toX))
                            .color(colors.getPrimaryColorHEX())
                            .text(", Y: ")
                            .color(colors.getSecondaryColorHEX())
                            .text(String.format("%.2f", toY))
                            .color(colors.getPrimaryColorHEX())
                            .text(", Z: ")
                            .color(colors.getSecondaryColorHEX())
                            .text(String.format("%.2f", toZ))
                            .color(colors.getPrimaryColorHEX())
                            .text("\n - Svět: ")
                            .color(colors.getSecondaryColorHEX())
                            .text(toWorld)
                            .color(colors.getPrimaryColorHEX())
                            .text("\n - Server: ")
                            .color(colors.getSecondaryColorHEX())
                            .text(toServer)
                            .color(colors.getPrimaryColorHEX());
                }
                hoverInfo.text("\nGamemode: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(gamemode)
                        .color(colors.getPrimaryColorHEX())
                        .text("\nLevel: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.format("%.2f", (level + exp)))
                        .color(colors.getPrimaryColorHEX())
                        .text("\nHlad: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(hunger))
                        .color(colors.getPrimaryColorHEX())
                        .text("\nZdraví: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.format("%.2f", health))
                        .color(colors.getPrimaryColorHEX())
                        .text("\nFly: ")
                        .color(colors.getSecondaryColorHEX());
                if(fly == 0) {
                    hoverInfo.text("Ne")
                            .color(ChatColor.DARK_RED);
                } else {
                    hoverInfo.text("Ano")
                            .color(ChatColor.GREEN);
                }
                if(effects != null) {
                    hoverInfo.text("\nEfekty: ")
                            .color(colors.getSecondaryColorHEX());
                    String[] effectsStr = effects.split(";");
                    for(String effectStr : effectsStr) {
                        String[] attributes = effectStr.split(":");
                        hoverInfo.text("\n " + attributes[0])
                                .color(colors.getPrimaryColorHEX())
                                .text("\n - Délka: ")
                                .color(colors.getSecondaryColorHEX())
                                .text(String.format("%.2f", (Double.parseDouble(attributes[1])) / 20)+ " sekund")
                                .color(colors.getPrimaryColorHEX())
                                .text("\n - Stupeň: ")
                                .color(colors.getSecondaryColorHEX())
                                .text(attributes[2])
                                .color(colors.getPrimaryColorHEX());
                    }
                }
                JsonBuilder jb = new JsonBuilder();
                jb.text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, showHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/tpdata rb preview " + id)
                        .text("□")
                        .color(ChatColor.GREEN)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, showHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/tpdata rb preview " + id)
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, showHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/tpdata rb preview " + id)
                        .text(" [")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, inventoryRbHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/tpdata rb inv " + id + " ")
                        .text("◀")
                        .color(ChatColor.GOLD)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, inventoryRbHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/tpdata rb inv " + id + " ")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, inventoryRbHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/tpdata rb inv " + id + " ")
                        .text(" [")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, allRbHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/tpdata rb all " + id + " ")
                        .text("◀◀")
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, allRbHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/tpdata rb all " + id + " ")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, allRbHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/tpdata rb all " + id + " ")
                        .text(" - ")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                        .text(String.valueOf(id))
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                        .text("] - ")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                        .text(playerManager.getUsername(userID));
                if(completed == 0) {
                    jb.color(ChatColor.DARK_RED)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                            .text(" - ")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                            .text(created_date)
                            .color(ChatColor.DARK_RED)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true);
                } else {
                    jb.color(ChatColor.GREEN)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                            .text(" - ")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                            .text(created_date)
                            .color(ChatColor.GREEN)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true);
                }
                list.add(jb.getJsonSegments());
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            loadFormattedList(rs, list);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private Pair<String, String> getInventory(Player player, int id) {
        String inventory = "";
        String inventory_name = null;
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT inventory, user_id, created_date FROM " + tables.getTeleportDataTable() + " WHERE id = ?");
            stm.setInt(1, id);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                inventory = rs.getString(1);
                inventory_name = playerManager.getUsername(rs.getInt(2)) + " - " + id;
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            return getInventory(player, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Pair<>(inventory_name, inventory);
    }

    private boolean isData(int id) {
        int count = 0;
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT count(id) FROM " + tables.getTeleportDataTable() + " WHERE id = ?");
            stm.setInt(1, id);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                count = rs.getInt(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            return isData(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (count != 0);
    }
}
