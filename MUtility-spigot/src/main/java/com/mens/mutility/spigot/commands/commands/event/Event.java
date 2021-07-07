package com.mens.mutility.spigot.commands.commands.event;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Errors;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.chat.json.JsonBuilder;
import com.mens.mutility.spigot.commands.commands.event.programmed.questions.Questions;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandHelp;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.database.Database;
import com.mens.mutility.spigot.messages.MessageChannel;
import com.mens.mutility.spigot.utils.DeleteConfirmation;
import com.mens.mutility.spigot.utils.MyStringUtils;
import com.mens.mutility.spigot.utils.PageList;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Event extends CommandHelp {
    private final MUtilitySpigot plugin;
    private final Prefix prefix;
    private final Errors errors;
    private final MyStringUtils strUt;
    private final PluginColors colors;
    private PageList helpList;
    private final PageList manageList;
    private final PageList manageIDList;
    private final MessageChannel messageChannel;
    private final Database db;
    private final List<DeleteConfirmation> deleteConfirmationList;
    private final Questions questions;

    public Event(MUtilitySpigot plugin) {
        this.plugin = plugin;
        db = plugin.getDb();
        prefix = new Prefix();
        errors = new Errors();
        strUt = new MyStringUtils();
        colors = new PluginColors();
        helpList = new PageList(10, prefix.getEventPrefix(true, true).replace("]", " - nápověda]"), "/event");
        manageList = new PageList(10, prefix.getEventPrefix(true, true).replace("]", " - seznam]"), "/event spravuj");
        manageIDList = new PageList(20, prefix.getEventPrefix(true, true).replace("]", " - úprava]"), "/event spravuj");
        messageChannel = new MessageChannel(plugin);
        deleteConfirmationList = new ArrayList<>();
        questions = new Questions(plugin);
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
            String name = strUt.getStringFromArgs(t.getArgs(), 1);
            createEvent(name, (Player) t.getSender());
        });
        CommandData manageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.manage", CommandExecutors.PLAYER, t -> {
            loadManageIDListData(t.getArgs()[1]);
            manageIDList.getList(1).toPlayer((Player) t.getSender());

        });
        CommandData page = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData messageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.message", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            if(isEvent(id)) {
                messageChannel.broadcastJson(getEventMessageData(id).getList(1).toString());
            } else {
                t.getSender().sendMessage(prefix.getEventPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1],true, false));
            }
        });
        CommandData fakeMessageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.message", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            if(isEvent(id)) {
                getEventMessageData(id).getList(1).toPlayer((Player) t.getSender());
            } else {
                t.getSender().sendMessage(prefix.getEventPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1],true, false));
            }
        });
        CommandData tpID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.tp", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            if(isEvent(id)) {
                EventData data = getEventData(id);
                if(data != null) {
                    messageChannel.sendTeleportRequest((Player)t.getSender(), data.getTpX(), data.getTpY(), data.getTpZ(), data.getWorld(), data.getServer());
                    t.getSender().sendMessage(prefix.getEventPrefix(true, false) + "Teleportuji na event " + colors.getPrimaryColor() + data.getName());
                }
            } else {
                t.getSender().sendMessage(prefix.getEventPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1],true, false));
            }
        });
        CommandData deleteID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.delete", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[1]);
            if(isEvent(id)) {
                EventData data = getEventData(id);
                if(data != null) {
                    DeleteConfirmation deleteConfirmation = new DeleteConfirmation(id, (Player) t.getSender(), "/event delete confirm");
                    deleteConfirmation.setMessage(new JsonBuilder()
                            .addJsonSegment(prefix.getEventPrefix(true, true))
                            .text(": Opravdu si přejete odstranit event ")
                            .color(colors.getSecondaryColorHEX())
                            .text(data.getName())
                            .color(colors.getPrimaryColorHEX())
                            .text("?")
                            .color(colors.getSecondaryColorHEX()));
                    if(deleteConfirmationList.stream().noneMatch(x -> (x.getId() == id
                            && x.getPlayer().getName().equals(t.getSender().getName())
                            && !x.isFinished()))) {
                        deleteConfirmation.startTimer();
                        deleteConfirmationList.add(deleteConfirmation);
                    } else {
                        t.getSender().sendMessage(prefix.getEventPrefix(true, false)
                                + colors.getSecondaryColor() + "Žádost o potvrzení již byla vytvořena!");
                    }

                }
            } else {
                t.getSender().sendMessage(prefix.getEventPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1],true, false));
            }
        });
        CommandData deleteConfirm = new CommandData(ArgumentTypes.DEFAULT, "confirm", TabCompleterTypes.NONE);

        // 3. stupeň
        CommandData ano = new CommandData(ArgumentTypes.DEFAULT, "ano", TabCompleterTypes.DEFAULT, "mutility.eventy.otazky.create");
        CommandData ne = new CommandData(ArgumentTypes.DEFAULT, "ne", TabCompleterTypes.DEFAULT, "mutility.eventy.otazky.create");
        CommandData resetEvent = new CommandData(ArgumentTypes.DEFAULT, "resetevent", TabCompleterTypes.DEFAULT, "mutility.eventy.otazky.reset", CommandExecutors.BOTH, t -> {
            questions.resetEvent();
            t.getSender().sendMessage(prefix.getEventPrefix(true, false)
                    + colors.getSecondaryColor() + "Event byl zresetován!");
        });
        CommandData resetKolo = new CommandData(ArgumentTypes.DEFAULT, "resetkolo", TabCompleterTypes.DEFAULT, "mutility.eventy.otazky.reset", CommandExecutors.BOTH, t -> {
            questions.resetKolo();
            t.getSender().sendMessage(prefix.getEventPrefix(true, false)
                    + colors.getSecondaryColor() + "Kolo bylo zresetováno!");
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
            loadManageListData();
            manageList.getList(Integer.parseInt(t.getArgs()[2])).toPlayer((Player) t.getSender());
        });
        CommandData deleteConfirmID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.delete", CommandExecutors.PLAYER, t -> {
            int id = Integer.parseInt(t.getArgs()[2]);
            if(isEvent(id)) {
                EventData data = getEventData(id);
                if(data != null) {
                    boolean valid = false;
                    for (int i = deleteConfirmationList.size() - 1; i >= 0; i--) {
                        if(deleteConfirmationList.get(i).getId() == id
                                && deleteConfirmationList.get(i).getPlayer().getName().equals(t.getSender().getName())) {
                            if(!deleteConfirmationList.get(i).isFinished()) {
                                valid = true;
                                deleteConfirmationList.get(i).setFinished(true);
                                deleteEvent(id);
                                t.getSender().sendMessage(prefix.getEventPrefix(true, false) + "Event "
                                        + colors.getPrimaryColor() + data.getName()
                                        + colors.getSecondaryColor() + " byl smazán!");
                                break;
                            }
                        }
                    }
                    if(!valid) {
                        t.getSender().sendMessage(prefix.getEventPrefix(true, false)
                                + "Potvrzení o smazání eventu není platné!");
                    }
                    deleteConfirmationList.removeIf(DeleteConfirmation::isFinished);
                }
            } else {
                t.getSender().sendMessage(prefix.getEventPrefix(true, false) + errors.errWrongArgument(t.getArgs()[1],true, false));
            }
        });

        // 4. stupeň
        CommandData otazkaAno = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Otázka >]", "mutility.eventy.otazky.create", CommandExecutors.BOTH, t -> questions.run(strUt.getStringFromArgs(t.getArgs(), 3), t.getArgs()[2]));
        CommandData otazkaNe = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Otázka >]", "mutility.eventy.otazky.create", CommandExecutors.BOTH, t -> questions.run(strUt.getStringFromArgs(t.getArgs(), 3), t.getArgs()[2]));
        CommandData voteAno = new CommandData(ArgumentTypes.DEFAULT, "ano", TabCompleterTypes.NONE, "mutility.eventy.otazky.vote", CommandExecutors.PLAYER, t -> {
            if(questions.isRegistered((Player)t.getSender())) {
                questions.vote((Player)t.getSender(), t.getArgs()[3]);
            } else {
                t.getSender().sendMessage(prefix.getEventPrefix(true, false) + "Již jsi hlasoval!");
            }
        });
        CommandData voteNe = new CommandData(ArgumentTypes.DEFAULT, "ne", TabCompleterTypes.NONE, "mutility.eventy.otazky.vote", CommandExecutors.PLAYER, t -> {
            if(questions.isRegistered((Player)t.getSender())) {
                questions.vote((Player)t.getSender(), t.getArgs()[3]);
            } else {
                t.getSender().sendMessage(prefix.getEventPrefix(true, false) + "Již jsi hlasoval!");
            }
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

        CommandData objective = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Cíl eventu >]", "mutility.eventy.manage", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Objective");
        });
        CommandData note = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Poznámka >]", "mutility.eventy.manage", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Note");
        });

        // 5. stupeň
        CommandData setTPY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.eventy.manage");

        // 6. stupeň
        CommandData setTPZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.eventy.manage");

        // 7. stupeň
        CommandData setWorld = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.WORLDS, "mutility.eventy.manage");

        // 8. stupeň
        CommandData setServer = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.SERVERS, "mutility.eventy.manage", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("SetServer");
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
        delete.link(deleteConfirm);

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
        deleteConfirm.link(deleteConfirmID);

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
        setObjective.link(objective);
        setNote.link(note);

        setTPX.link(setTPY);

        setTPY.link(setTPZ);

        setTPZ.link(setWorld);

        setWorld.link(setServer);

        return event;
    }

    private void loadManageListData() {
        try {
            manageList.clear();
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id, event_name, tpX, tpY, tpZ, world, server, objective, note FROM " + prefix.getTablePrefix(plugin) +"events");
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
            String world;
            String server;
            String objective;
            String note;
            int index = 1;
            while(rs.next()) {
                id = rs.getInt(1);
                name = rs.getString(2);
                tpX = rs.getFloat(3);
                tpY = rs.getFloat(4);
                tpZ = rs.getFloat(5);
                world = rs.getString(6);
                server = rs.getString(7);
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
                        .text("\n   - Svět = ")
                        .color(colors.getSecondaryColorHEX())
                        .text(world)
                        .color(colors.getPrimaryColorHEX())
                        .text("\n   - Server = ")
                        .color(colors.getSecondaryColorHEX())
                        .text(server)
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
            PreparedStatement stm = db.getCon().prepareStatement("SELECT event_name, tpX, tpY, tpZ, world, server, objective, note FROM "+ prefix.getTablePrefix(plugin) + "events WHERE id = ?");
            stm.setInt(1, id);
            ResultSet rs =  stm.executeQuery();
            String manageTPHover = new JsonBuilder(">> Klikni pro úpravu ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Teleportu")
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
            String world;
            String server;
            String objective;
            String note;
            while(rs.next()) {
                name = rs.getString(1);
                tpX = rs.getFloat(2);
                tpY = rs.getFloat(3);
                tpZ = rs.getFloat(4);
                world = rs.getString(5);
                server = rs.getString(6);
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
                manageIDList.add(new JsonBuilder("   - Svět = ")
                        .color(colors.getSecondaryColorHEX())
                        .text(world)
                        .color(colors.getPrimaryColorHEX())
                        .getJsonSegments());
                manageIDList.add(new JsonBuilder("   - Server = ")
                        .color(colors.getSecondaryColorHEX())
                        .text(server)
                        .color(colors.getPrimaryColorHEX())
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

    private void createEvent(String eventName, Player player) {
        try {
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("INSERT INTO " + prefix.getTablePrefix(plugin) + "events (event_name, tpX, tpY, tpZ, world, server, objective, note) VALUE (?, ?, ?, ?, ?, ?, ?, ?)");
            stm.setString(1, eventName);
            stm.setInt(2, 0);
            stm.setInt(3, 100);
            stm.setInt(4, 0);
            stm.setString(5, Objects.requireNonNull(player.getLocation().getWorld()).getName());
            stm.setString(6, plugin.getCurrentServer());
            stm.setString(7, "-");
            stm.setString(8, "-");
            stm.execute();
            new JsonBuilder()
                    .addJsonSegment(prefix.getEventPrefix(true, true))
                    .text(" Event ")
                    .color(colors.getSecondaryColorHEX())
                    .text(eventName)
                    .color(colors.getPrimaryColorHEX())
                    .text(" byl vytvořen. \nKlikni ")
                    .color(colors.getSecondaryColorHEX())
                    .text("➥Zde")
                    .color(colors.getPrimaryColorHEX())
                    .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT,
                            new JsonBuilder(">> Klikni pro ")
                                    .color(colors.getSecondaryColorHEX())
                                    .text("Úpravu")
                                    .color(colors.getPrimaryColorHEX()).text(" eventu <<")
                                    .color(colors.getSecondaryColorHEX())
                                    .toString(), true)
                    .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event spravuj " + getEventIdFromName(eventName))
                    .text(" pro jeho úpravu.")
                    .color(colors.getSecondaryColorHEX())
                    .toPlayer(player);
        } catch (CommunicationsException e) {
            db.openConnection();
            createEvent(eventName, player);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private int getEventIdFromName(String name) {
        int id = 0;
        try {
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id FROM " + prefix.getTablePrefix(plugin) + "events WHERE event_name = ?");
            stm.setString(1, name);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                id = rs.getInt(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            getEventIdFromName(name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    private boolean isEvent(int id) {
        int count = 0;
        try {
            PreparedStatement stm = db.getCon().prepareStatement("SELECT count(id) FROM " + prefix.getTablePrefix(plugin) + "events WHERE id= ?");
            stm.setInt(1, id);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                count = rs.getInt(1);
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            isEvent(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (count != 0);
    }

    private PageList getEventMessageData(int id) {
        PageList list = new PageList(10, prefix.getEventPrefix(true, true), null);
        try {
            PreparedStatement stm = db.getCon().prepareStatement("SELECT event_name, objective, note FROM " + prefix.getTablePrefix(plugin) + "events WHERE id= ?");
            stm.setInt(1, id);
            ResultSet rs =  stm.executeQuery();
            String name = "";
            String objective = "";
            String note = "";
            if(rs.next()) {
                name = rs.getString(1);
                objective = rs.getString(2);
                note = rs.getString(3);
            }
            list.add(
                    new JsonBuilder("Právě se koná event: ")
                            .color(colors.getSecondaryColorHEX())
                            .text(name)
                            .color(colors.getPrimaryColorHEX())
                            .text("\n Teleport: ")
                            .color(colors.getSecondaryColorHEX())
                            .text("➥Zde")
                            .color(colors.getPrimaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT,
                                    new JsonBuilder(">> Klikni pro ")
                                            .color(colors.getSecondaryColorHEX())
                                            .text("Teleportaci")
                                            .color(colors.getPrimaryColorHEX()).text(" na event <<")
                                            .color(colors.getSecondaryColorHEX())
                                            .toString(), true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/event tp " + id)
                            .text("\n Cíl eventu: ")
                            .color(colors.getSecondaryColorHEX())
                            .text(objective)
                            .color(colors.getPrimaryColorHEX())
                            .text("\n Poznámky: ")
                            .color(colors.getSecondaryColorHEX())
                            .text(note)
                            .color(colors.getPrimaryColorHEX())
                            .getJsonSegments()
            );
        } catch (CommunicationsException e) {
            db.openConnection();
            getEventMessageData(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    private EventData getEventData(int id) {
        try {
            EventData data;
            PreparedStatement stm = db.getCon().prepareStatement("SELECT event_name, tpX, tpY, tpZ, world, server, objective, note FROM " + prefix.getTablePrefix(plugin) + "events WHERE id= ?");
            stm.setInt(1, id);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                String name = rs.getString(1);
                float x = rs.getFloat(2);
                float y = rs.getFloat(3);
                float z = rs.getFloat(4);
                String world = rs.getString(5);
                String server = rs.getString(6);
                String objective = rs.getString(7);
                String note = rs.getString(8);
                data = new EventData(id, name, x, y, z, world, server, objective, note);
                return data;
            }
        } catch (CommunicationsException e) {
            db.openConnection();
            getEventData(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private void deleteEvent(int id) {
        try {
            PreparedStatement stm = db.getCon().prepareStatement("DELETE FROM " + prefix.getTablePrefix(plugin) + "events WHERE id=" + id + "");
            stm.execute();
        } catch (CommunicationsException e) {
            db.openConnection();
            deleteEvent(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
