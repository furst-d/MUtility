package com.mens.mutility.spigot.commands.commands.minv;

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
import com.mens.mutility.spigot.inventory.InventoryPair;
import com.mens.mutility.spigot.utils.*;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import javafx.util.Pair;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MInv extends CommandHelp {
    private final MUtilitySpigot plugin;
    private final Prefix prefix;
    private PageList helpList;
    private final PageList loadList;
    private final PageList manageList;
    private final Database db;
    private final DatabaseTables tables;
    private final PlayerManager playerManager;
    private final PluginColors colors;
    private final MyStringUtils strUt;
    private final Errors errors;
    private final List<DeleteConfirmation> deleteConfirmationList;

    public MInv(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        helpList = new PageList(10, prefix.getInventoryPrefix(true, true).replace("]", " - nápověda]"), "/minv");
        loadList = new PageList(10, prefix.getInventoryPrefix(true, true).replace("]", " - seznam]"), "/minv nacti");
        manageList = new PageList(10, prefix.getInventoryPrefix(true, true).replace("]", " - úprava]"), "/minv spravuj");
        db = plugin.getDb();
        tables = new DatabaseTables(plugin);
        playerManager = new PlayerManager(plugin);
        colors = new PluginColors();
        strUt = new MyStringUtils();
        errors = new Errors();
        deleteConfirmationList = new ArrayList<>();
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public CommandData create() {
        CommandData minv = new CommandData("minv", prefix.getInventoryPrefix(true, false),"mutility.inventory.help", CommandExecutors.BOTH, t -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1).toPlayer((Player) t.getSender());
        });

        // 1. stupeň
        CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData uloz = new CommandData(ArgumentTypes.DEFAULT, "uloz", TabCompleterTypes.DEFAULT, "mutility.inventory.save", CommandExecutors.PLAYER, t -> {
            InventoryManager invManager = new InventoryManager();
            JsonObject inventory = invManager.getInventory((Player)t.getSender());
            saveInventory(((Player) t.getSender()), inventory, null, true);
            t.getSender().sendMessage(prefix.getEventPrefix(true, false) + "Inventář úspěšně uložen");
        });
        CommandData nacti = new CommandData(ArgumentTypes.DEFAULT, "nacti", TabCompleterTypes.DEFAULT, "mutility.inventory.load", CommandExecutors.PLAYER, t -> {
            loadLoadData();
            loadList.getList(1).toPlayer((Player) t.getSender());
        });
        CommandData delete = new CommandData(ArgumentTypes.DEFAULT, "delete", TabCompleterTypes.NONE, "mutility.inventory.delete");
        CommandData spravuj = new CommandData(ArgumentTypes.DEFAULT, "spravuj", TabCompleterTypes.DEFAULT, "mutility.inventory.manage", CommandExecutors.PLAYER, t ->  {
            loadManageListData();
            manageList.getList(1).toPlayer((Player) t.getSender());
        });
        CommandData show = new CommandData(ArgumentTypes.DEFAULT, "show", TabCompleterTypes.NONE, "mutility.inventory.show");

        // 2. stupeň
        CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.inventory.help", CommandExecutors.BOTH, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1])).toPlayer((Player) t.getSender());
        });
        CommandData nazev = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název inventáře >]", "mutility.inventory.save", CommandExecutors.PLAYER, t ->  {
            String inventoryName = strUt.getStringFromArgs(t.getArgs(), 1);
            InventoryManager invManager = new InventoryManager();
            JsonObject inventory = invManager.getInventory((Player)t.getSender());
            saveInventory(((Player) t.getSender()), inventory, inventoryName, false);
            t.getSender().sendMessage(prefix.getInventoryPrefix(true, false) + "Inventář " + colors.getPrimaryColor() + inventoryName + colors.getSecondaryColor() + " úspěšně uložen");
        });
        CommandData loadPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData loadID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.inventory.load", CommandExecutors.PLAYER, t -> {
            int id_user_record = Integer.parseInt(t.getArgs()[1]);
            if(isInventory((Player)t.getSender(), id_user_record)) {
                InventoryManager invManager = new InventoryManager();
                Pair<String, String> inventoryInfo = getInventory((Player) t.getSender(), id_user_record);
                invManager.loadInventory((Player) t.getSender(), invManager.toJsonObject(inventoryInfo.getValue()));
                if(inventoryInfo.getKey() == null) {
                    t.getSender().sendMessage(prefix.getInventoryPrefix(true, false) + "Inventář úspěšně načten");
                } else {
                    t.getSender().sendMessage(prefix.getInventoryPrefix(true, false) + "Inventář " + colors.getPrimaryColor() + inventoryInfo.getKey() + colors.getSecondaryColor() + " úspěšně načten");
                }
            } else {
                t.getSender().sendMessage(prefix.getInventoryPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1], true, false));
            }
        });
        CommandData deleteID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.inventory.delete", CommandExecutors.PLAYER, t -> {
            int id_user_record = Integer.parseInt(t.getArgs()[1]);
            if(isInventory((Player)t.getSender(), id_user_record)) {
                    DeleteConfirmation deleteConfirmation = new DeleteConfirmation(id_user_record, (Player) t.getSender(), "/minv delete confirm");
                    deleteConfirmation.setMessage(new JsonBuilder()
                            .addJsonSegment(prefix.getInventoryPrefix(true, true))
                            .text(": Opravdu si přejete odstranit inventář ")
                            .color(colors.getSecondaryColorHEX())
                            .text(getInventory((Player) t.getSender(), id_user_record).getKey())
                            .color(colors.getPrimaryColorHEX())
                            .text("?")
                            .color(colors.getSecondaryColorHEX()));
                    if(deleteConfirmationList.stream().noneMatch(x -> (x.getId() == id_user_record
                            && x.getPlayer().getName().equals(t.getSender().getName())
                            && !x.isFinished()))) {
                        deleteConfirmation.startTimer();
                        deleteConfirmationList.add(deleteConfirmation);
                    } else {
                        t.getSender().sendMessage(prefix.getInventoryPrefix(true, false)
                                + colors.getSecondaryColor() + "Žádost o potvrzení již byla vytvořena!");
                    }
            } else {
                t.getSender().sendMessage(prefix.getInventoryPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1],true, false));
            }
        });
        CommandData deleteConfirm = new CommandData(ArgumentTypes.DEFAULT, "confirm", TabCompleterTypes.NONE);
        CommandData manageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.inventory.manage");
        CommandData showId = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.inventory.show", CommandExecutors.PLAYER, t -> {
            int id_user_record = Integer.parseInt(t.getArgs()[1]);
            InventoryManager invManager = new InventoryManager();
            Pair<String, String> invData = getInventory((Player)t.getSender(), id_user_record);
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
        });
        CommandData managePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);

        // 3. stupeň
        CommandData loadPageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.inventory.load", CommandExecutors.PLAYER, t -> {
            loadLoadData();
            loadList.getList(Integer.parseInt(t.getArgs()[2])).toPlayer((Player) t.getSender());
        });
        CommandData setName = new CommandData(ArgumentTypes.DEFAULT, "setname", TabCompleterTypes.NONE);
        CommandData setInv = new CommandData(ArgumentTypes.DEFAULT, "setinv", TabCompleterTypes.NONE, "mutility.inventory.manage", CommandExecutors.PLAYER, t -> {
            int id_user_record = Integer.parseInt(t.getArgs()[1]);
            if(isInventory((Player)t.getSender(), id_user_record)) {
                InventoryManager invManager = new InventoryManager();
                Pair<String, String> inventoryInfo = getInventory((Player) t.getSender(), id_user_record);
                updateInventory((Player)t.getSender(), id_user_record, invManager.getInventory((Player)t.getSender()));
                t.getSender().sendMessage(prefix.getInventoryPrefix(true, false) + "Inventář " + colors.getPrimaryColor() + inventoryInfo.getKey() + colors.getSecondaryColor() + " úspěšně přepsán");
            } else {
                t.getSender().sendMessage(prefix.getInventoryPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1], true, false));
            }
        });
        CommandData managePageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.inventory.manage", CommandExecutors.PLAYER, t -> {
            loadManageListData();
            manageList.getList(Integer.parseInt(t.getArgs()[2])).toPlayer((Player) t.getSender());
        });
        CommandData deleteConfirmID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.inventory.delete", CommandExecutors.PLAYER, t -> {
            int id_user_record = Integer.parseInt(t.getArgs()[2]);
            if(isInventory((Player)t.getSender(), id_user_record)) {
                boolean valid = false;
                String inventoryName = getInventory((Player) t.getSender(), id_user_record).getKey();
                for (int i = deleteConfirmationList.size() - 1; i >= 0; i--) {
                    if(deleteConfirmationList.get(i).getId() == id_user_record
                            && deleteConfirmationList.get(i).getPlayer().getName().equals(t.getSender().getName())) {
                        if(!deleteConfirmationList.get(i).isFinished()) {
                            valid = true;
                            deleteConfirmationList.get(i).setFinished(true);
                            deleteInventory((Player)t.getSender(), id_user_record);
                            t.getSender().sendMessage(prefix.getInventoryPrefix(true, false) + "Inventář "
                                    + colors.getPrimaryColor() + inventoryName
                                    + colors.getSecondaryColor() + " byl smazán!");
                            break;
                        }
                    }
                }
                if(!valid) {
                    t.getSender().sendMessage(prefix.getInventoryPrefix(true, false)
                            + "Potvrzení o smazání inventáře není platné!");
                }
                deleteConfirmationList.removeIf(DeleteConfirmation::isFinished);
            } else {
                t.getSender().sendMessage(prefix.getInventoryPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1],true, false));
            }
        });

        // 4. stupeň
        CommandData setNameName = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název inventáře >]", "mutility.inventory.manage", CommandExecutors.PLAYER, t -> {
            int id_user_record = Integer.parseInt(t.getArgs()[1]);
            String inventoryName = strUt.getStringFromArgs(t.getArgs(), 3);
            if(isInventory((Player)t.getSender(), id_user_record)) {
                updateInventoryName((Player)t.getSender(), id_user_record, inventoryName);
                Pair<String, String> inventoryInfo = getInventory((Player) t.getSender(), id_user_record);
                t.getSender().sendMessage(prefix.getInventoryPrefix(true, false) + "Jméno inventáře úspěšně nastaveno na " + colors.getPrimaryColor() + inventoryInfo.getKey());
            } else {
                t.getSender().sendMessage(prefix.getInventoryPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1], true, false));
            }
        });
        minv.setDescription("Systém pro ukládání a obnovování inventářů");

        uloz.setDescription("Uložení inventáře\nInventář lze uložit buď rychlým uložením, kdy se přepíše poslední rychlé uložení, nebo uložením se zadáním jména inventáře, kdy bude inventář uložen natrvalo.");
        uloz.setSyntax("/minv " + uloz.getSubcommand() + "\n/minv " + uloz.getSubcommand() + " [<Název inventáře>]");

        nacti.setDescription("Zobrazení seznamu uložených inventářů pro obnovení");
        nacti.setSyntax("/minv " + nacti.getSubcommand());

        spravuj.setDescription("Správa vytvořených inventářů");
        spravuj.setSyntax("/minv " + spravuj.getSubcommand());

        minv.link(helpPage);
        minv.link(uloz);
        minv.link(nacti);
        minv.link(delete);
        minv.link(spravuj);
        minv.link(show);

        helpPage.link(helpPageID);
        uloz.link(nazev);
        nacti.link(loadPage);
        nacti.link(loadID);
        delete.link(deleteID);
        delete.link(deleteConfirm);
        spravuj.link(manageID);
        spravuj.link(managePage);
        show.link(showId);

        loadPage.link(loadPageID);
        manageID.link(setName);
        manageID.link(setInv);
        managePage.link(managePageID);
        deleteConfirm.link(deleteConfirmID);

        setName.link(setNameName);

        return minv;
    }

    private void loadLoadData() {
        try {
            loadList.clear();
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id_user_record, inventory_name FROM " + tables.getInventoryTable());
            ResultSet rs =  stm.executeQuery();
            String loadHover = new JsonBuilder(">> ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Načtení")
                    .color(ChatColor.GREEN)
                    .text(" inventáře")
                    .color(colors.getSecondaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            int id_user_record;
            String inventoryName;
            while(rs.next()) {
                id_user_record = rs.getInt(1);
                inventoryName = rs.getString(2);
                if(id_user_record != 0) {
                    loadList.add(new JsonBuilder()
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, loadHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv nacti " + id_user_record)
                            .text("✔")
                            .color(ChatColor.GREEN)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, loadHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv nacti " + id_user_record)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, loadHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv nacti " + id_user_record)
                            .text(" - ")
                            .color(colors.getSecondaryColorHEX())
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .text(String.valueOf(id_user_record))
                            .color(colors.getPrimaryColorHEX())
                            .text("] ")
                            .color(colors.getSecondaryColorHEX())
                            .text(inventoryName)
                            .color(colors.getPrimaryColorHEX())
                            .getJsonSegments());
                } else {
                    loadList.add(new JsonBuilder()
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, loadHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv nacti " + id_user_record)
                            .text("✔")
                            .color(ChatColor.GREEN)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, loadHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv nacti " + id_user_record)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, loadHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv nacti " + id_user_record)
                            .text(" - ")
                            .color(colors.getSecondaryColorHEX())
                            .text("Rychlý save")
                            .color(ChatColor.GREEN)
                            .getJsonSegments());
                }
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            loadLoadData();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void loadManageListData() {
        try {
            manageList.clear();
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id_user_record, inventory_name FROM " + tables.getInventoryTable());
            ResultSet rs =  stm.executeQuery();
            String overrideHover = new JsonBuilder(">> ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Přepsání")
                    .color(colors.getPrimaryColorHEX())
                    .text(" inventáře")
                    .color(colors.getSecondaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String renameHover = new JsonBuilder(">> ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Přejmenování")
                    .color(ChatColor.GOLD)
                    .text(" inventáře")
                    .color(colors.getSecondaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String deleteHover = new JsonBuilder(">> ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Smazání")
                    .color(ChatColor.DARK_RED)
                    .text(" inventáře")
                    .color(colors.getSecondaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String showHover = new JsonBuilder(">> ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Zobrazení")
                    .color(ChatColor.GREEN)
                    .text(" inventáře")
                    .color(colors.getSecondaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            int id_user_record;
            String inventoryName;
            while(rs.next()) {
                id_user_record = rs.getInt(1);
                inventoryName = rs.getString(2);
                if(id_user_record != 0) {
                    manageList.add(new JsonBuilder()
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, overrideHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv spravuj " + id_user_record + " setInv")
                            .text("✐")
                            .color(colors.getPrimaryColor())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, overrideHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv spravuj " + id_user_record + " setInv")
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, overrideHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv spravuj " + id_user_record + " setInv")
                            .text(" ")
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, renameHover, true)
                            .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/minv spravuj " + id_user_record + " setName ")
                            .text("✎")
                            .color(ChatColor.GOLD)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, renameHover, true)
                            .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/minv spravuj " + id_user_record + " setName ")
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, renameHover, true)
                            .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/minv spravuj " + id_user_record + " setName ")
                            .text(" ")
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv delete " + id_user_record)
                            .text("✖")
                            .color(ChatColor.DARK_RED)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv delete " + id_user_record)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv delete " + id_user_record)
                            .text(" ")
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, showHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv show " + id_user_record)
                            .text("□")
                            .color(ChatColor.GREEN)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, showHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv show " + id_user_record)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, showHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/minv show " + id_user_record)
                            .text(" - ")
                            .color(colors.getSecondaryColorHEX())
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .text(String.valueOf(id_user_record))
                            .color(colors.getPrimaryColorHEX())
                            .text("] ")
                            .color(colors.getSecondaryColorHEX())
                            .text(inventoryName)
                            .color(colors.getPrimaryColorHEX())
                            .getJsonSegments());
                }
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            loadManageListData();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void saveInventory(Player player, JsonObject inventory, String inventoryName, boolean quickSave) {
        try {
            PreparedStatement stm;
            ResultSet rs;
            if(quickSave) {
                if(getQuickSaveCount(player) == 0) {
                    stm = db.getCon().prepareStatement("INSERT INTO " + tables.getInventoryTable() + " (user_id, id_user_record, inventory) VALUE (?, ?, ?)");
                    stm.setInt(1, playerManager.getUserId(player.getName()));
                    stm.setInt(2, 0);
                    stm.setObject(3, inventory.toString());
                } else {
                    stm = db.getCon().prepareStatement("UPDATE " + tables.getInventoryTable() + " SET inventory = ? WHERE id_user_record = 0 AND user_id = ?");
                    stm.setObject(1, inventory.toString());
                    stm.setInt(2, playerManager.getUserId(player.getName()));
                }
            } else {
                stm = db.getCon().prepareStatement("INSERT INTO " + tables.getInventoryTable() + " (user_id, id_user_record, inventory_name, inventory) VALUE (?, ?, ?, ?)");
                stm.setInt(1, playerManager.getUserId(player.getName()));
                stm.setInt(2, (getSaveCount(player) + 1));
                stm.setString(3, inventoryName);
                stm.setObject(4, inventory.toString());
            }
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            saveInventory(player, inventory, inventoryName, quickSave);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void updateInventory(Player player, int id_user_record, JsonObject inventory) {
        try {
            PreparedStatement stm;
            ResultSet rs;
            stm = db.getCon().prepareStatement("UPDATE " + tables.getInventoryTable() + " SET inventory = ? WHERE id_user_record = ? AND user_id = ?");
            stm.setString(1, inventory.toString());
            stm.setInt(2, id_user_record);
            stm.setInt(3, playerManager.getUserId(player.getName()));
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            updateInventory(player, id_user_record, inventory);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getQuickSaveCount(Player player) {
        int count = -1;
        try {
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("SELECT COALESCE(COUNT(id_user_record), 0) FROM " + tables.getInventoryTable() + " WHERE user_id = ? AND id_user_record = 0");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                count = rs.getInt(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            getQuickSaveCount(player);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }

    private int getSaveCount(Player player) {
        int count = -1;
        try {
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("SELECT COALESCE(MAX(id_user_record), 0) FROM " + tables.getInventoryTable() + " WHERE user_id = ?");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                count = rs.getInt(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            getQuickSaveCount(player);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }

    private boolean isInventory(Player player, int id_user_record) {
        int count = 0;
        try {
            PreparedStatement stm = db.getCon().prepareStatement("SELECT count(id_user_record) FROM " + tables.getInventoryTable() + " WHERE user_id = ? AND id_user_record = ?");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            stm.setInt(2, id_user_record);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                count = rs.getInt(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            isInventory(player, id_user_record);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (count != 0);
    }

    private Pair<String, String> getInventory(Player player, int id_user_record) {
        String inventory = "";
        String inventory_name = null;
        try {
            PreparedStatement stm = db.getCon().prepareStatement("SELECT inventory, inventory_name FROM " + tables.getInventoryTable() + " WHERE user_id = ? AND id_user_record = ?");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            stm.setInt(2, id_user_record);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                inventory = rs.getString(1);
                inventory_name = rs.getString(2);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            getInventory(player, id_user_record);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Pair<>(inventory_name, inventory);
    }

    private void updateInventoryName(Player player, int id_user_record, String inventory_name) {
        try {
            PreparedStatement stm;
            ResultSet rs;
            stm = db.getCon().prepareStatement("UPDATE " + tables.getInventoryTable() + " SET inventory_name = ? WHERE id_user_record = ? AND user_id = ?");
            stm.setString(1, inventory_name);
            stm.setInt(2, id_user_record);
            stm.setInt(3, playerManager.getUserId(player.getName()));
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            updateInventoryName(player, id_user_record, inventory_name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteInventory(Player player, int id_user_record) {
        try {
            PreparedStatement stm;
            ResultSet rs;
            stm = db.getCon().prepareStatement("DELETE FROM " + tables.getInventoryTable() + " WHERE id_user_record = ? AND user_id = ?");
            stm.setInt(1, id_user_record);
            stm.setInt(2, playerManager.getUserId(player.getName()));
            stm.execute();
            stm = db.getCon().prepareStatement("UPDATE " + tables.getInventoryTable() + " SET id_user_record = id_user_record - 1 WHERE id_user_record > ? AND user_id = ? ");
            stm.setInt(1, id_user_record);
            stm.setInt(2, playerManager.getUserId(player.getName()));
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            deleteInventory(player, id_user_record);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
