package com.mens.mutility.spigot.commands.commands.mstavba;

import com.mens.mutility.spigot.MUtilitySpigot;
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
import com.mens.mutility.spigot.messages.MessageChannel;
import com.mens.mutility.spigot.utils.MyStringUtils;
import com.mens.mutility.spigot.utils.PageList;
import com.mens.mutility.spigot.utils.PlayerManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Trida reprezentujici prikaz /mstavba
 */
public class MStavba extends CommandHelp {
    private final MUtilitySpigot plugin;
    private final Database db;
    private PageList helpList;
    private final PageList showList;
    private final PluginColors colors;
    private final Prefix prefix;
    private final DatabaseTables tables;
    private final PlayerManager playerManager;
    private final MyStringUtils strUt;
    private final MessageChannel messageChannel;

    public MStavba(MUtilitySpigot plugin) {
        this.plugin = plugin;
        db = plugin.getDb();
        prefix = new Prefix();
        helpList = new PageList(10, prefix.getStavbaPrefix(true, true).replace("]", " - nápověda]"), "/mstavba");
        showList = new PageList(10, prefix.getStavbaPrefix(true, true).replace("]", " - seznam]"), "/mstavba zobraz");
        colors = new PluginColors();
        tables = new DatabaseTables();
        playerManager = new PlayerManager();
        strUt = new MyStringUtils();
        messageChannel = new MessageChannel();
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public final CommandData create() {
        final CommandData stavba = new CommandData("mstavba", "M-Stavba", "mutility.stavba.help", CommandExecutors.PLAYER, t -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1, null).toPlayer((Player) t.getSender());
        });

        // 1. stupeň
        final CommandData help = new CommandData(ArgumentTypes.DEFAULT, "help", TabCompleterTypes.DEFAULT, "mutility.stavba.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1, null).toPlayer((Player) t.getSender());
        });
        final CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData zobraz = new CommandData(ArgumentTypes.DEFAULT, "zobraz", TabCompleterTypes.DEFAULT, "mutility.stavba.manage", CommandExecutors.PLAYER, (t) -> loadShowList((Player) t.getSender(), 1));
        final CommandData vytvor = new CommandData(ArgumentTypes.DEFAULT, "vytvor", TabCompleterTypes.DEFAULT, "mutility.stavba.create");
        final CommandData hlasuj = new CommandData(ArgumentTypes.DEFAULT, "hlasuj", TabCompleterTypes.DEFAULT, "mutility.stavba.vote", CommandExecutors.PLAYER, (t) -> {
            MStavbaVoteManager manager = new MStavbaVoteManager(plugin);
            if(manager.isActive()) {
                manager.createVoteLink((Player) t.getSender());
            } else {
                t.getSender().sendMessage(prefix.getStavbaPrefix(true, false) + "Stavba měsíce momentálně neprobíhá!");
            }
        });

        // 2. stupeň
        final CommandData helpHelpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.stavba.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1]), null).toPlayer((Player) t.getSender());
        });
        final CommandData zobrazPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData startDate = new CommandData(ArgumentTypes.DATE, TabCompleterTypes.DATE_NOW, "mutility.stavba.create");
        final CommandData accept = new CommandData(ArgumentTypes.DEFAULT, "accept", TabCompleterTypes.NONE);
        final CommandData reject = new CommandData(ArgumentTypes.DEFAULT, "reject", TabCompleterTypes.NONE);

        // 3. stupeň
        final CommandData helpHelpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.stavba.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[2]), null).toPlayer((Player) t.getSender());
        });
        final CommandData zobrazPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.stavba.manage", CommandExecutors.PLAYER, (t) -> loadShowList((Player) t.getSender(), Integer.parseInt(t.getArgs()[2])));
        final CommandData endDate = new CommandData(ArgumentTypes.DATE, TabCompleterTypes.DATE_PLUS_7, "mutility.stavba.create");
        final CommandData acceptID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.stavba.manage", CommandExecutors.PLAYER, (t) -> {
            int forumId = Integer.parseInt(t.getArgs()[2]);
            String[] buildingInfo = getBuildingInfo(forumId);
            int userId = Integer.parseInt(buildingInfo[0]);
            String title = buildingInfo[1];
            String content = buildingInfo[2];

            StringBuilder image = new StringBuilder();
            int firstIndex = content.indexOf("<img");
            int lastIndex = content.indexOf(">", firstIndex);
            for (int i = firstIndex; i <= lastIndex; i++) {
                image.append(content.charAt(i));
            }
            image = new StringBuilder(image.toString().replace("<img src=", ""));
            image = new StringBuilder(image.toString().replace(">", ""));
            image = new StringBuilder(image.toString().replaceAll("^\"|\"$", ""));

            int seasonId = getMaxSeason();
            int buildingId = getNewBuildingId(seasonId);
            insertBuilding(seasonId, buildingId, forumId, userId, title, image.toString());
            lockForumPost(forumId);
            insertAcceptComment(forumId, getBuildingDesc(seasonId));
            t.getSender().sendMessage(prefix.getStavbaPrefix(true, false) + "Stavba " + colors.getPrimaryColor() + title + colors.getSecondaryColor() + " byla přihlášena");
        });
        final CommandData rejectID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.stavba.manage", CommandExecutors.PLAYER, (t) -> {
            int forumId = Integer.parseInt(t.getArgs()[2]);
            lockForumPost(forumId);
            t.getSender().sendMessage(prefix.getStavbaPrefix(true, false) + "Stavba " + colors.getPrimaryColor() + getBuildingInfo(forumId)[1] + colors.getSecondaryColor() + " byla zamítnuta");
        });

        // 4. stupeň
        final CommandData popis = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Popis >]", "mutility.stavba.create", CommandExecutors.PLAYER, t -> {
            String startDateStr = t.getArgs()[1];
            String endDateStr = t.getArgs()[2];
            String desc = strUt.getStringFromArgs(t.getArgs(), 3);
            unsetSeasonActive();
            int seasonId = getMaxSeason() + 1;
            createCompetition(seasonId, playerManager.getUserId(t.getSender().getName()), startDateStr, endDateStr, desc);
            MStavbaVoteManager manager = new MStavbaVoteManager(plugin);
            manager.setActive(true);
            manager.setSeasonId(seasonId);
            messageChannel.sendToBungeeCord((Player)t.getSender(), "mens:start-mstavba", String.valueOf(seasonId));
            t.getSender().sendMessage(prefix.getStavbaPrefix(true, false) + "Stavba měsíce byla spuštěna");
        });

        stavba.setDescription("Systém pro Stavbu měsíce");

        help.setDescription("Nápověda k příkazu");
        help.setSyntax("/mstavba " + help.getSubcommand());

        vytvor.setDescription("Vytvoření nové ankety pro hlasování");
        vytvor.setSyntax("/mstavba " + vytvor.getSubcommand() + " [<Od>] [<Do>] [<Popis období>]");

        zobraz.setDescription("Zobrazí seznam přihlášených staveb a umožní jejich zařazení do soutěže");
        zobraz.setSyntax("/mstavba " + zobraz.getSubcommand());

        hlasuj.setDescription("Pokud je soutěž aktivní a hráč ještě nehlasoval, vygeneruje hlasovací odkaz pro hlasování");
        hlasuj.setSyntax("/mstavba " + hlasuj.getSubcommand());

        stavba.link(help);
        stavba.link(helpPage);
        stavba.link(zobraz);
        stavba.link(vytvor);
        stavba.link(hlasuj);

        help.link(helpHelpPage);
        helpPage.link(helpPageID);
        zobraz.link(zobrazPage);
        zobraz.link(accept);
        zobraz.link(reject);
        vytvor.link(startDate);

        helpHelpPage.link(helpHelpPageID);
        zobraz.link(zobrazPageID);
        accept.link(acceptID);
        reject.link(rejectID);
        startDate.link(endDate);

        endDate.link(popis);

        return stavba;
    }

    private void loadShowList(Player player, int page) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            showList.clear();
            PreparedStatement stm = db.getCon().prepareStatement("SELECT id, user_created, title FROM web_forum_posts WHERE parent_id = 37 AND active = 1;");
            ResultSet rs =  stm.executeQuery();
            String applicationHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Zobrazení")
                    .color(ChatColor.GOLD)
                    .text(" přihlášky <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String acceptHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Zaregistrování")
                    .color(ChatColor.GREEN)
                    .text(" stavby <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String rejectHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Zamítnutí")
                    .color(ChatColor.DARK_RED)
                    .text(" stavby <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            int forumId;
            int userId;
            String title;
            while(rs.next()) {
                forumId = rs.getInt(1);
                userId = rs.getInt(2);
                title = rs.getString(3).replace("\"", "'");
                    showList.add(new JsonBuilder()
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, acceptHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mstavba zobraz accept " + forumId)
                            .text("✔")
                            .color(ChatColor.GREEN)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, acceptHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mstavba zobraz accept " + forumId)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, acceptHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mstavba zobraz accept " + forumId)
                            .text(" ")
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, rejectHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mstavba zobraz reject " + forumId)
                            .text("✖")
                            .color(ChatColor.DARK_RED)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, rejectHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mstavba zobraz reject " + forumId)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, rejectHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mstavba zobraz reject " + forumId)
                            .text(" ")
                            .text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, applicationHover, true)
                            .clickEvent(JsonBuilder.ClickAction.OPEN_URL, "https://kostkuj.cz/forum/" + forumId)
                            .text(String.valueOf(forumId))
                            .color(ChatColor.GOLD)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, applicationHover, true)
                            .clickEvent(JsonBuilder.ClickAction.OPEN_URL, "https://kostkuj.cz/forum/" + forumId)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, applicationHover, true)
                            .clickEvent(JsonBuilder.ClickAction.OPEN_URL, "https://kostkuj.cz/forum/" + forumId)
                            .text(" - ")
                            .color(colors.getSecondaryColorHEX())
                            .text(playerManager.getUsername(userId))
                            .color(colors.getPrimaryColorHEX())
                            .text(" - ")
                            .color(colors.getSecondaryColorHEX())
                            .text(title)
                            .color(colors.getPrimaryColorHEX())
                            .getJsonSegments());
            }
            showList.getList(page, null).toPlayer(player);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private String[] getBuildingInfo(int id) {
        String[] info = new String[3];
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT user_created, title, content FROM web_forum_posts WHERE id = ?");
            stm.setInt(1, id);
            ResultSet rs =  stm.executeQuery();
            while(rs.next()) {
                info[0] = rs.getString(1);
                info[1] = rs.getString(2).replace("\"", "'");
                info[2] = rs.getString(3).replace("\"", "'");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return info;
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

    private int getNewBuildingId(int seasonId) {
        int buildingId = 0;
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT MAX(building_id) FROM "+ tables.getStavbaCompetitorsTable() + " WHERE season_id = ?");
            stm.setInt(1, seasonId);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                buildingId = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return buildingId + 1;
    }

    private void insertBuilding(int seasonId, int buildingId, int forumId, int userId, String title, String image) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("INSERT INTO "+ tables.getStavbaCompetitorsTable() + "(season_id, building_id, forum_id, user_id, building_name, image) VALUES (?, ?, ?, ?, ?, ?)");
            stm.setInt(1, seasonId);
            stm.setInt(2, buildingId);
            stm.setInt(3, forumId);
            stm.setInt(4, userId);
            stm.setString(5, title);
            stm.setString(6, image);
            stm.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void lockForumPost(int forumId) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("UPDATE web_forum_posts SET active = 0 WHERE id = ?");
            stm.setInt(1, forumId);
            stm.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void insertAcceptComment(int forumId, String months) {
        String message = "<p><h1 style=\"color:green;text-align:center\"><strong>Stavba byla zařazena do soutěže za " + months + "</strong></h1></p>";
        String date = strUt.getCurrentFormattedDate();
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("INSERT INTO web_forum_posts_messages (user_created, post_id, message, date_created, date_edited) VALUES (?, ?, ?, ?, ?)");
            stm.setInt(1, 11);
            stm.setInt(2, forumId);
            stm.setString(3, message);
            stm.setString(4, date);
            stm.setString(5, date);
            stm.execute();
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return desc;
    }

    private void createCompetition(int seasonId, int adminId, String startDate, String endDate, String description) {
        String hodiny = " 00:00:00";
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("INSERT INTO " + tables.getStavbaSeasonsTable() + " (season_id, admin_id, start_date, close_date, reward, web_new, active, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            stm.setInt(1, seasonId);
            stm.setInt(2, adminId);
            stm.setString(3, startDate + hodiny);
            stm.setString(4, endDate + hodiny);
            stm.setInt(5, 0);
            stm.setInt(6, 0);
            stm.setInt(7, 1);
            stm.setString(8, description);
            stm.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void unsetSeasonActive() {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("UPDATE " + tables.getStavbaSeasonsTable() + " SET active = 0");
            stm.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
