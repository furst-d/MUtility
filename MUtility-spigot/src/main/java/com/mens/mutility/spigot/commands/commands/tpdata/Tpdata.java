package com.mens.mutility.spigot.commands.commands.tpdata;

import com.google.gson.JsonObject;
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
import com.mens.mutility.spigot.inventory.InventoryNamePair;
import com.mens.mutility.spigot.inventory.InventoryPair;
import com.mens.mutility.spigot.inventory.TeleportData;
import com.mens.mutility.spigot.utils.ServerInfo;
import com.mens.mutility.spigot.utils.confirmations.Confirmation;
import com.mens.mutility.spigot.utils.MyStringUtils;
import com.mens.mutility.spigot.utils.PageList;
import com.mens.mutility.spigot.utils.PlayerManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Tpdata extends CommandHelp {
    private final MUtilitySpigot plugin;
    private PageList helpList;
    private static PageList showList;
    private final Prefix prefix;
    private final PluginColors colors;
    private final MyStringUtils strUt;
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
        colors = new PluginColors();
        strUt = new MyStringUtils();
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
        showList = new PageList(10, prefix.getTpDataPrefix(true, true).replace("]", " - seznam]"), "/tpdata zobraz");
        loadShowList();
        final CommandData tpData = new CommandData("tpdata", "TP-Data", "mutility.tpdata.help", CommandExecutors.PLAYER, t -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1, null).toPlayer((Player) t.getSender());
        });

        // 1. stupeň
        final CommandData help = new CommandData(ArgumentTypes.DEFAULT, "help", TabCompleterTypes.DEFAULT, "mutility.tpdata.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1, null).toPlayer((Player) t.getSender());
        });
        final CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData show = new CommandData(ArgumentTypes.DEFAULT, "zobraz", TabCompleterTypes.DEFAULT, "mutility.tpdata.show", CommandExecutors.PLAYER, t -> {
            updateShowList();
            showList.getList(1, null).toPlayer((Player)t.getSender());
        });
        final CommandData rb = new CommandData(ArgumentTypes.DEFAULT, "rb", TabCompleterTypes.NONE);

        // 2. stupeň
        final CommandData helpHelpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.tpdata.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1]), null).toPlayer((Player) t.getSender());
        });
        final CommandData showName = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.GLOBAL_ONLINE_PLAYERS, "mutility.tpdata.show", CommandExecutors.PLAYER, t -> loadShowNameList(t.getArgs()[1], (Player)t.getSender(), 1));
        final CommandData rbInv = new CommandData(ArgumentTypes.DEFAULT, "inv", TabCompleterTypes.NONE);
        final CommandData rbAll = new CommandData(ArgumentTypes.DEFAULT, "all", TabCompleterTypes.NONE);
        final CommandData rbPreview = new CommandData(ArgumentTypes.DEFAULT, "preview", TabCompleterTypes.NONE);
        final CommandData showPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);

        // 3. stupeň
        final CommandData helpHelpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.tpdata.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[2]), null).toPlayer((Player) t.getSender());
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
                InventoryNamePair invData = getInventory(id);
                InventoryPair invPair = invManager.getInventoryAsItemStack(invManager.toJsonObject(invData.getInventory()));
                GUIManager guiManager = new GUIManager(plugin, 45, colors.getPrimaryColor()  + "§l" + invData.getName());

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
        final CommandData showPageId = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.tpdata.show", CommandExecutors.PLAYER, (t) -> showList.getList(Integer.parseInt(t.getArgs()[2]), null).toPlayer((Player)t.getSender()));

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
                            TeleportData data = loadDataById(id);
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
                            TeleportData data = loadDataById(id);
                            Player player = Bukkit.getPlayer(playerManager.getUsername(data.getUserId()));
                            if(player != null) {
                                applyData(player, data, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), Objects.requireNonNull(player.getLocation().getWorld()).getName());
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

    private void loadShowList() {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            showList.clear();
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id, user_id, fromX, fromY, fromZ, fromWorld, fromServer, toX, toY, toZ, toWorld, toServer, gamemode, exp, level, hunger, health, fly, effects, created_date, completed FROM " + tables.getTeleportDataTable() + " ORDER BY created_date DESC");
            ResultSet rs =  stm.executeQuery();
            loadFormattedList(rs, showList, false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void updateShowList() {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id, user_id, fromX, fromY, fromZ, fromWorld, fromServer, toX, toY, toZ, toWorld, toServer, gamemode, exp, level, hunger, health, fly, effects, created_date, completed FROM " + tables.getTeleportDataTable() + " WHERE created_date > SYSDATE() - INTERVAL 1 DAY ");
            ResultSet rs =  stm.executeQuery();
            loadFormattedList(rs, showList,true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void loadShowNameList(String playerName, Player player, int page) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PageList showNameList = new PageList(showList);
            showNameList.setCommand("/tpdata zobraz " + playerName);
            showNameList.setTitleJson(prefix.getTpDataPrefix(true, true).replace("]", " - " + playerName + "]"));
            showNameList.setRows(showList.getRows().stream().filter(x -> x.contains(playerName)).collect(Collectors.toList()));
            showList.getList(page, showNameList).toPlayer(player);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void loadFormattedList(ResultSet rs, PageList list, boolean update) {
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
                if(update) {
                    if(!list.getRows().contains(jb.getJsonSegments())) {
                        list.add(0, jb.getJsonSegments());
                    }
                } else {
                    list.add(jb.getJsonSegments());
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private InventoryNamePair getInventory(int id) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new InventoryNamePair(inventory_name, inventory);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (count != 0);
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteOldData(int days) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("DELETE FROM " + tables.getTeleportDataTable() + " WHERE created_date < NOW() - INTERVAL ? DAY");
            stm.setInt(1, days);
            stm.execute();
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
            Collection<PotionEffect> effects;
            if(rs.next()) {
                effects = new ArrayList<>();
                TeleportData data = new TeleportData();
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void applyData(Player player, TeleportData data, double x, double y, double z, String world) {
        InventoryManager manager = new InventoryManager();
        int inventoryId = data.getId();
        JsonObject inventory = manager.toJsonObject(data.getInventory());
        ServerInfo server = plugin.getCurrentServer();
        if(server == null) {
            updateData(inventoryId, x, y, z, world, null);
        } else {
            updateData(inventoryId, x, y, z, world, server.getName());
        }
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
