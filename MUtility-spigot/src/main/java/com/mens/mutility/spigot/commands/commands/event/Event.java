package com.mens.mutility.spigot.commands.commands.event;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.chat.json.JsonBuilder;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandHelp;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.utils.PageList;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.mens.mutility.spigot.MUtilitySpigot.db;

public class Event extends CommandHelp {
    private final MUtilitySpigot plugin;
    private final Prefix prefix;
    private final PluginColors colors;
    private PageList helpList;
    private final PageList manageList;
    private final PageList manageIDList;

    public Event(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        colors = new PluginColors();
        helpList = new PageList(10, prefix.getEventPrefix(true, true).replace("]", " - nápověda]"), "/event");
        manageList = new PageList(10, prefix.getEventPrefix(true, true).replace("]", " - seznam]"), "/event spravuj");
        manageIDList = new PageList(20, prefix.getEventPrefix(true, true).replace("]", " - úprava]"), "/event spravuj");
    }

    private void loadManageListData() {
        try {
            manageList.clear();
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id, event_name, tpX, tpY, tpZ, necessaryItems, forbiddenItems, objective, note FROM " + prefix.getTablePrefix(plugin) +"events");
            ResultSet rs =  stm.executeQuery();
            String startHover = new JsonBuilder(">> ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Zapnutí")
                    .color(ChatColor.GREEN)
                    .text(" eventu")
                    .color(colors.getSecondaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String manageHover = new JsonBuilder(">> ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Úpravu")
                    .color(ChatColor.GOLD)
                    .text(" eventu")
                    .color(colors.getSecondaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String teleportHover = new JsonBuilder(">> ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Teleportaci")
                    .color(ChatColor.DARK_AQUA)
                    .text(" na event")
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
                    .text(" eventu")
                    .color(colors.getSecondaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            int id;
            String name;
            float tpX;
            float tpY;
            float tpZ;
            String necessaryItems;
            String forbiddenItems;
            String objective;
            String note;
            int index = 1;
            while(rs.next()) {
                id = rs.getInt(1);
                name = rs.getString(2);
                tpX = rs.getFloat(3);
                tpY = rs.getFloat(4);
                tpZ = rs.getFloat(5);
                necessaryItems = rs.getString(6);
                forbiddenItems = rs.getString(7);
                objective = rs.getString(8);
                note = rs.getString(9);
                String infoHover = new JsonBuilder("ID: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(id))
                        .color(colors.getPrimaryColorHEX())
                        .text("\nTeleport:")
                        .color(colors.getSecondaryColorHEX())
                        .text("\n   - X = ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(tpX))
                        .color(colors.getPrimaryColorHEX())
                        .text("\n   - Y = ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(tpY))
                        .color(colors.getPrimaryColorHEX())
                        .text("\n   - Z = ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(tpZ))
                        .color(colors.getPrimaryColorHEX())
                        .text("\nPotřebné věci: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(necessaryItems)
                        .color(colors.getPrimaryColorHEX())
                        .text("\nZakázané věci: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(forbiddenItems)
                        .color(colors.getPrimaryColorHEX())
                        .text("\nCíl eventu: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(objective)
                        .color(colors.getPrimaryColorHEX())
                        .text("\nPoznámky: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(note)
                        .color(colors.getPrimaryColorHEX())
                        .toString();
                manageList.add(new JsonBuilder()
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, startHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event message " + id)
                        .text("✔")
                        .color(ChatColor.GREEN)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, startHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event message " + id)
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, startHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event message " + id)
                        .text(" ")
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event spravuj " + id)
                        .text("•••")
                        .color(ChatColor.GOLD)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event spravuj " + id)
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event spravuj " + id)
                        .text(" ")
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, teleportHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event tp " + id)
                        .text("☄")
                        .color(ChatColor.DARK_AQUA)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, teleportHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event tp " + id)
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, teleportHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event tp " + id)
                        .text(" ")
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event delete " + id)
                        .text("✖")
                        .color(ChatColor.DARK_RED)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event delete " + id)
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event delete " + id)
                        .text(" - ")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, infoHover, true)
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, infoHover, true)
                        .text(String.valueOf(index))
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, infoHover, true)
                        .text("] ")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, infoHover, true)
                        .text(name)
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, infoHover, true)
                        .getJsonSegments());
                index++;
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            loadManageListData();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void loadManageIDListData(String idStr) {
        manageIDList.clear();
        try {
            int id = Integer.parseInt(idStr);
            PreparedStatement stm = db.getCon().prepareStatement("SELECT event_name, tpX, tpY, tpZ, necessaryItems, forbiddenItems, objective, note FROM "+ prefix.getTablePrefix(plugin) + "events WHERE id=?");
            stm.setInt(1, id);
            ResultSet rs =  stm.executeQuery();
            String manageTPHover = new JsonBuilder(">> Klikni pro úpravu ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Teleportu")
                    .color(colors.getPrimaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String manageNecItemsHover = new JsonBuilder(">> Klikni pro úpravu ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Potřebných věcí")
                    .color(colors.getPrimaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String manageForbItemsHover = new JsonBuilder(">> Klikni pro úpravu ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Zakázaných věcí")
                    .color(colors.getPrimaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String manageObjectiveHover = new JsonBuilder(">> Klikni pro úpravu ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Cíle eventu")
                    .color(colors.getPrimaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String manageNoteHover = new JsonBuilder(">> Klikni pro úpravu ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Poznámky")
                    .color(colors.getPrimaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String manageFakeMessHover = new JsonBuilder(">> Klikni pro odeslání ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Testovací zprávy")
                    .color(colors.getPrimaryColorHEX())
                    .text(".\nTuto zprávu uvidíš ")
                    .color(colors.getSecondaryColorHEX())
                    .text("pouze ty")
                    .color(colors.getPrimaryColorHEX())
                    .text(" <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();

            String name;
            float tpX;
            float tpY;
            float tpZ;
            String necessaryItems;
            String forbiddenItems;
            String objective;
            String note;
            while(rs.next()) {
                name = rs.getString(1);
                tpX = rs.getFloat(2);
                tpY = rs.getFloat(3);
                tpZ = rs.getFloat(4);
                necessaryItems = rs.getString(5);
                forbiddenItems = rs.getString(6);
                objective = rs.getString(7);
                note = rs.getString(8);
                manageIDList.add(new JsonBuilder("ID: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(idStr)
                        .color(colors.getPrimaryColorHEX())
                        .getJsonSegments());
                manageIDList.add(new JsonBuilder("Název: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(name)
                        .color(colors.getPrimaryColorHEX())
                        .getJsonSegments());
                manageIDList.add(new JsonBuilder("Teleport: ")
                        .color(colors.getSecondaryColorHEX())
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageTPHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setTP ")
                        .text("•••")
                        .color(ChatColor.GOLD)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageTPHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setTP ")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageTPHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setTP ")
                        .getJsonSegments());
                manageIDList.add(new JsonBuilder("   - X = ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(tpX))
                        .color(colors.getPrimaryColorHEX())
                        .getJsonSegments());
                manageIDList.add(new JsonBuilder("   - Y = ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(tpY))
                        .color(colors.getPrimaryColorHEX())
                        .getJsonSegments());
                manageIDList.add(new JsonBuilder("   - Z = ")
                        .color(colors.getSecondaryColorHEX())
                        .text(String.valueOf(tpZ))
                        .color(colors.getPrimaryColorHEX())
                        .getJsonSegments());
                manageIDList.add(new JsonBuilder("Potřebné věci: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(necessaryItems)
                        .color(colors.getPrimaryColorHEX())
                        .text(" ")
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageNecItemsHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setRequired ")
                        .text("•••")
                        .color(ChatColor.GOLD)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageNecItemsHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setRequired ")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageNecItemsHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setRequired ")
                        .getJsonSegments());
                manageIDList.add(new JsonBuilder("Zakázané věci: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(forbiddenItems)
                        .color(colors.getPrimaryColorHEX())
                        .text(" ")
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageForbItemsHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setForbidden ")
                        .text("•••")
                        .color(ChatColor.GOLD)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageForbItemsHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setForbidden ")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageForbItemsHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setForbidden ")
                        .getJsonSegments());
                manageIDList.add(new JsonBuilder("Cíl eventu: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(objective)
                        .color(colors.getPrimaryColorHEX())
                        .text(" ")
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageObjectiveHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setObjective ")
                        .text("•••")
                        .color(ChatColor.GOLD)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageObjectiveHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setObjective ")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageObjectiveHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setObjective ")
                        .getJsonSegments());
                manageIDList.add(new JsonBuilder("Poznámky: ")
                        .color(colors.getSecondaryColorHEX())
                        .text(note)
                        .color(colors.getPrimaryColorHEX())
                        .text(" ")
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageNoteHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setNote ")
                        .text("•••")
                        .color(ChatColor.GOLD)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageNoteHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setNote ")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageNoteHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/event spravuj " + id + " setNote ")
                        .getJsonSegments());
                manageIDList.add(new JsonBuilder("\n[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageFakeMessHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event fakemessage " + id)
                        .text("Odeslat testovací zprávu")
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageFakeMessHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event fakemessage " + id)
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, manageFakeMessHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event fakemessage " + id)
                        .getJsonSegments());
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            loadManageIDListData(idStr);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public CommandData create() {
            CommandData event = new CommandData("event", prefix.getEventPrefix(true, false), "mutility.eventy.help", CommandExecutors.BOTH, t -> {
                helpList = getCommandHelp(plugin, t.getSender(), helpList);
                helpList.getList(1).toPlayer((Player) t.getSender());
            });

            // 1. stupeň
            CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
            CommandData spust = new CommandData(ArgumentTypes.DEFAULT, "spust", TabCompleterTypes.DEFAULT, "mutility.eventy.otazky.create");
            CommandData vytvor = new CommandData(ArgumentTypes.DEFAULT, "vytvor", TabCompleterTypes.DEFAULT, "mutility.eventy.create");
            CommandData spravuj = new CommandData(ArgumentTypes.DEFAULT, "spravuj", TabCompleterTypes.DEFAULT, "mutility.eventy.manage", CommandExecutors.PLAYER, t -> {
                loadManageListData();
                manageList.getList(1).toPlayer((Player) t.getSender());
            });
            CommandData message = new CommandData(ArgumentTypes.DEFAULT, "message", TabCompleterTypes.NONE);
            CommandData fakeMessage = new CommandData(ArgumentTypes.DEFAULT, "fakemessage", TabCompleterTypes.NONE);
            CommandData tp = new CommandData(ArgumentTypes.DEFAULT, "tp", TabCompleterTypes.NONE);
            CommandData delete = new CommandData(ArgumentTypes.DEFAULT, "delete", TabCompleterTypes.NONE);

            // 2. stupeň
            CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.eventy.help", CommandExecutors.BOTH, (t) -> {
                helpList = getCommandHelp(plugin, t.getSender(), helpList);
                helpList.getList(Integer.parseInt(t.getArgs()[1])).toPlayer((Player) t.getSender());
            });
            CommandData otazky = new CommandData(ArgumentTypes.DEFAULT, "otazky", TabCompleterTypes.DEFAULT, "mutility.eventy.otazky.create");
            CommandData hledacka = new CommandData(ArgumentTypes.DEFAULT, "hledacka", TabCompleterTypes.DEFAULT, "mutility.eventy.hledacka.create");
            CommandData hrbitov = new CommandData(ArgumentTypes.DEFAULT, "hrbitov", TabCompleterTypes.DEFAULT, "mutility.eventy.hrbitov.create");
            CommandData nazev = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název >]", "mutility.eventy.create", CommandExecutors.PLAYER, t -> {
                //TODO
                System.out.println("Název");
            });
            CommandData manageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.manage", CommandExecutors.PLAYER, t -> {
                //TODO
                loadManageIDListData(t.getArgs()[1]);
                manageIDList.getList(1).toPlayer((Player) t.getSender());

            });
            CommandData page = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
            CommandData messageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.message", CommandExecutors.PLAYER, t -> {
                //TODO
                System.out.println("Message ID");
            });
            CommandData fakeMessageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.message", CommandExecutors.PLAYER, t -> {
                //TODO
                System.out.println("Fake Message ID");
            });
            CommandData tpID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.tp", CommandExecutors.PLAYER, t -> {
                //TODO
                System.out.println("TP ID");
            });
            CommandData deleteID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.delete", CommandExecutors.PLAYER, t -> {
                //TODO
                System.out.println("Delete ID");
            });

            // 3. stupeň
            CommandData ano = new CommandData(ArgumentTypes.DEFAULT, "ano", TabCompleterTypes.DEFAULT, "mutility.eventy.otazky.create");
            CommandData ne = new CommandData(ArgumentTypes.DEFAULT, "ne", TabCompleterTypes.DEFAULT, "mutility.eventy.otazky.create");
            CommandData resetEvent = new CommandData(ArgumentTypes.DEFAULT, "resetevent", TabCompleterTypes.DEFAULT, "mutility.eventy.otazky.reset", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Reset event");
            });
            CommandData resetKolo = new CommandData(ArgumentTypes.DEFAULT, "resetkolo", TabCompleterTypes.DEFAULT, "mutility.eventy.otazky.reset", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Reset kolo");
            });
            CommandData vote = new CommandData(ArgumentTypes.DEFAULT, "vote", TabCompleterTypes.NONE);
            CommandData startHledacka = new CommandData(ArgumentTypes.DEFAULT, "start", TabCompleterTypes.DEFAULT, "mutility.eventy.hledacka.create", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Start hledacka");
            });
            CommandData stopHledacka = new CommandData(ArgumentTypes.DEFAULT, "start", TabCompleterTypes.DEFAULT, "mutility.eventy.hledacka.create", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Stop hledacka");
            });
            CommandData setBlockHledacka = new CommandData(ArgumentTypes.DEFAULT, "block", TabCompleterTypes.DEFAULT, "mutility.eventy.hledacka.create");
            CommandData setFogHledacka = new CommandData(ArgumentTypes.DEFAULT, "mlha", TabCompleterTypes.DEFAULT, "mutility.eventy.hledacka.create");
            CommandData startHrbitov = new CommandData(ArgumentTypes.DEFAULT, "start", TabCompleterTypes.DEFAULT, "mutility.eventy.hrbitov.create", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Start hrbitov");
            });
            CommandData stopHrbitov = new CommandData(ArgumentTypes.DEFAULT, "stop", TabCompleterTypes.DEFAULT, "mutility.eventy.hrbitov.create", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Stop hrbitov");
            });
            CommandData setBlockHrbitov = new CommandData(ArgumentTypes.DEFAULT, "blok", TabCompleterTypes.DEFAULT, "mutility.eventy.hrbitov.create");
            CommandData setFogHrbitov = new CommandData(ArgumentTypes.DEFAULT, "mlha", TabCompleterTypes.DEFAULT, "mutility.eventy.hrbitov.create");
            CommandData setTP = new CommandData(ArgumentTypes.DEFAULT, "settp", TabCompleterTypes.NONE);
            CommandData setRequired = new CommandData(ArgumentTypes.DEFAULT, "setrequired", TabCompleterTypes.NONE);
            CommandData setForbidden = new CommandData(ArgumentTypes.DEFAULT, "setforbidden", TabCompleterTypes.NONE);
            CommandData setObjective = new CommandData(ArgumentTypes.DEFAULT, "setobjective", TabCompleterTypes.NONE);
            CommandData setNote = new CommandData(ArgumentTypes.DEFAULT, "setnote", TabCompleterTypes.NONE);
            CommandData pageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.manage", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Page ID");
                loadManageListData();
                manageList.getList(Integer.parseInt(t.getArgs()[2])).toPlayer((Player) t.getSender());
            });

            // 4. stupeň
            CommandData otazkaAno = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Otázka >]", "mutility.eventy.otazky.create", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Otazka ano");
            });
            CommandData otazkaNe = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Otázka >]", "mutility.eventy.otazky.create", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Otazka ne");
            });
            CommandData voteAno = new CommandData(ArgumentTypes.DEFAULT, "ano", TabCompleterTypes.NONE, "mutility.eventy.otazky.vote", CommandExecutors.PLAYER, t -> {
                //TODO
                System.out.println("Vote ano");
            });
            CommandData voteNe = new CommandData(ArgumentTypes.DEFAULT, "ne", TabCompleterTypes.NONE, "mutility.eventy.otazky.vote", CommandExecutors.PLAYER, t -> {
                //TODO
                System.out.println("Vote ne");
            });
            CommandData blockHledacka = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.BLOCKS, "mutility.eventy.hledacka.create", CommandExecutors.PLAYER, t -> {
                //TODO
                System.out.println("Hledacka block");
            });
            CommandData fogHledackaTrue = new CommandData(ArgumentTypes.DEFAULT,"ano", TabCompleterTypes.DEFAULT, "mutility.eventy.hledacka.create", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Hledacka fog true");
            });
            CommandData fogHledackaFalse = new CommandData(ArgumentTypes.DEFAULT,"ne", TabCompleterTypes.DEFAULT, "mutility.eventy.hledacka.create", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Hledacka fog false");
            });
            CommandData blockHrbitov = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.BLOCKS, "mutility.eventy.hrbitov.create", CommandExecutors.PLAYER, t -> {
                //TODO
                System.out.println("Hrbitov block");
            });
            CommandData fogHrbitovTrue = new CommandData(ArgumentTypes.DEFAULT,"ano", TabCompleterTypes.DEFAULT, "mutility.eventy.hrbitov.create", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Hrbitov fog true");
            });
            CommandData fogHrbitovFalse = new CommandData(ArgumentTypes.DEFAULT,"ne", TabCompleterTypes.DEFAULT, "mutility.eventy.hrbitov.create", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Hrbitov fog false");
            });
            CommandData setTPX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.eventy.manage");
            CommandData required = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Potřebné věci >]", "mutility.eventy.manage", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Potrebne veci");
            });
            CommandData forbidden = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Zakázané věci >]", "mutility.eventy.manage", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Zakazane veci");
            });
            CommandData objective = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Cíl eventu >]", "mutility.eventy.manage", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Zakazane veci");
            });
            CommandData note = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Poznámka >]", "mutility.eventy.manage", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("Zakazane veci");
            });

            // 5. stupeň
            CommandData setTPY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.eventy.manage");

            // 6. stupeň
            CommandData setTPZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.eventy.manage", CommandExecutors.BOTH, t -> {
                //TODO
                System.out.println("SetTP");
            });

            event.setDescription("Systém pro správu eventů");

            spust.setDescription("Obsluha naprogramovaných eventů");
            spust.setSyntax("/event " + spust.getSubcommand() + " [<Název eventu>] [<Další parametry>]");

            vytvor.setDescription("Vytváření nového eventu");
            vytvor.setSyntax("/event " + vytvor.getSubcommand() + " [<Název eventu>]");

            spravuj.setDescription("Seznam a správa jednotlivých eventů");
            spravuj.setSyntax("/event " + spravuj.getSubcommand());

            event.link(helpPage);
            event.link(spust);
            event.link(vytvor);
            event.link(spravuj);
            event.link(message);
            event.link(fakeMessage);
            event.link(tp);
            event.link(delete);

            helpPage.link(helpPageID);
            spust.link(otazky);
            spust.link(hledacka);
            spust.link(hrbitov);
            vytvor.link(nazev);
            spravuj.link(manageID);
            spravuj.link(page);
            message.link(messageID);
            fakeMessage.link(fakeMessageID);
            tp.link(tpID);
            delete.link(deleteID);

            otazky.link(ano);
            otazky.link(ne);
            otazky.link(resetEvent);
            otazky.link(resetKolo);
            otazky.link(vote);
            hledacka.link(startHledacka);
            hledacka.link(stopHledacka);
            hledacka.link(setBlockHledacka);
            hledacka.link(setFogHledacka);
            hrbitov.link(startHrbitov);
            hrbitov.link(stopHrbitov);
            hrbitov.link(setBlockHrbitov);
            hrbitov.link(setFogHrbitov);
            manageID.link(setTP);
            manageID.link(setRequired);
            manageID.link(setForbidden);
            manageID.link(setObjective);
            manageID.link(setNote);
            page.link(pageID);

            ano.link(otazkaAno);
            ne.link(otazkaNe);
            vote.link(voteAno);
            vote.link(voteNe);
            setBlockHledacka.link(blockHledacka);
            setFogHledacka.link(fogHledackaTrue);
            setFogHledacka.link(fogHledackaFalse);
            setBlockHrbitov.link(blockHrbitov);
            setFogHrbitov.link(fogHrbitovTrue);
            setFogHrbitov.link(fogHrbitovFalse);
            setTP.link(setTPX);
            setRequired.link(required);
            setForbidden.link(forbidden);
            setObjective.link(objective);
            setNote.link(note);

            setTPX.link(setTPY);

            setTPY.link(setTPZ);

            return event;
    }
}
