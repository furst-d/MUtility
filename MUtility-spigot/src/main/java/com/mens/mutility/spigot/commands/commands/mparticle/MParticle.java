package com.mens.mutility.spigot.commands.commands.mparticle;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Errors;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.chat.json.JsonBuilder;
import com.mens.mutility.spigot.commands.commands.mparticle.enums.Colors;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandHelp;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.database.Database;
import com.mens.mutility.spigot.database.DatabaseTables;
import com.mens.mutility.spigot.messages.MessageChannel;
import com.mens.mutility.spigot.utils.Checker;
import com.mens.mutility.spigot.utils.MyStringUtils;
import com.mens.mutility.spigot.utils.PageList;
import com.mens.mutility.spigot.utils.PlayerManager;
import com.mens.mutility.spigot.utils.confirmations.Confirmation;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MParticle extends CommandHelp {
    private final MUtilitySpigot plugin;
    private PageList helpList;
    private final Database db;
    private final DatabaseTables tables;
    private final MessageChannel messageChannel;
    private final Prefix prefix;
    private final PluginColors colors;
    private final Checker checker;
    private final Errors errors;
    private final PlayerManager playerManager;
    private final MyStringUtils strUt;
    private final PageList particleList;
    private final List<Confirmation> playerDeleteConfirmationList;
    private final List<Confirmation> placeDeleteConfirmationList;

    public MParticle(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        db = plugin.getDb();
        tables = new DatabaseTables();
        helpList = new PageList(10, prefix.getMParticlePrefix(true, true).replace("]", " - nápověda]"), "/mparticle");
        messageChannel = new MessageChannel();
        colors = new PluginColors();
        checker = new Checker();
        errors = new Errors();
        playerManager = new PlayerManager();
        strUt = new MyStringUtils();
        particleList = new PageList(10, prefix.getMParticlePrefix(true, true).replace("]", " - seznam]"), null);
        particleList.setEmptyMessage(" Nemáš vytvořené žádné particly!\n Pro vytvoření particlu použij /mparticle vytvor [<Parametry>]");
        playerDeleteConfirmationList = new ArrayList<>();
        placeDeleteConfirmationList = new ArrayList<>();
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public final CommandData create() {
        final CommandData mparticle = new CommandData("mparticle", "M-Particle", "mutility.mparticle.help", CommandExecutors.BOTH, t -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1, null).toPlayer((Player) t.getSender());
        });

        // 1. stupeň
        final CommandData help = new CommandData(ArgumentTypes.DEFAULT, "help", TabCompleterTypes.DEFAULT, "mutility.mparticle.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1, null).toPlayer((Player) t.getSender());
        });
        final CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData create = new CommandData(ArgumentTypes.DEFAULT, "vytvor", TabCompleterTypes.DEFAULT, "mutility.mparticle.create");
        final CommandData delete = new CommandData(ArgumentTypes.DEFAULT, "delete", TabCompleterTypes.NONE);
        final CommandData show = new CommandData(ArgumentTypes.DEFAULT, "zobraz", TabCompleterTypes.DEFAULT, "mutility.mparticle.list", CommandExecutors.PLAYER, t -> loadMParticleList((Player) t.getSender(), 1, false));
        final CommandData manage = new CommandData(ArgumentTypes.DEFAULT, "manage", TabCompleterTypes.NONE);
        final CommandData start = new CommandData(ArgumentTypes.DEFAULT, "start", TabCompleterTypes.DEFAULT, "mutility.mparticle.start", CommandExecutors.PLAYER, t -> {
            System.out.println("Start");
            //TODO
        });
        final CommandData stop = new CommandData(ArgumentTypes.DEFAULT, "stop", TabCompleterTypes.DEFAULT, "mutility.mparticle.stop", CommandExecutors.PLAYER, t -> {
            System.out.println("Stop");
            //TODO
        });

        // 2. stupeň
        final CommandData helpHelpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.mparticle.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1]), null).toPlayer((Player) t.getSender());
        });
        final CommandData createOnPlayer = new CommandData(ArgumentTypes.DEFAULT, "hrac", TabCompleterTypes.DEFAULT, "mutility.mparticle.create");
        final CommandData createOnPlace = new CommandData(ArgumentTypes.DEFAULT, "pozice", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place");
        final CommandData startOnPlayer = new CommandData(ArgumentTypes.DEFAULT, "player", TabCompleterTypes.NONE);
        final CommandData startOnPlace = new CommandData(ArgumentTypes.DEFAULT, "place", TabCompleterTypes.NONE);
        final CommandData stopOnPlayer = new CommandData(ArgumentTypes.DEFAULT, "player", TabCompleterTypes.NONE);
        final CommandData stopOnPlace = new CommandData(ArgumentTypes.DEFAULT, "place", TabCompleterTypes.NONE);
        final CommandData deleteOnPlayer = new CommandData(ArgumentTypes.DEFAULT, "player", TabCompleterTypes.NONE);
        final CommandData deleteOnPlace = new CommandData(ArgumentTypes.DEFAULT, "place", TabCompleterTypes.NONE);
        final CommandData manageOnPlayer = new CommandData(ArgumentTypes.DEFAULT, "player", TabCompleterTypes.NONE);
        final CommandData manageOnPlace = new CommandData(ArgumentTypes.DEFAULT, "place", TabCompleterTypes.NONE);
        final CommandData showPlayer = new CommandData(ArgumentTypes.DEFAULT, "hrac", TabCompleterTypes.DEFAULT, "mutility.mparticle.list", CommandExecutors.PLAYER, t -> loadMParticleList((Player) t.getSender(), 1, false));
        final CommandData showPlace = new CommandData(ArgumentTypes.DEFAULT, "pozice", TabCompleterTypes.DEFAULT, "mutility.mparticle.list.place", CommandExecutors.PLAYER, t -> loadMParticleList((Player) t.getSender(), 1, true));

        // 3. stupeň
        final CommandData helpHelpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.mparticle.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[2]), null).toPlayer((Player) t.getSender());
        });
        final CommandData createOnPlayerCustom = new CommandData(ArgumentTypes.DEFAULT, "vlastni", TabCompleterTypes.DEFAULT, "mutility.mparticle.create");
        final CommandData createOnPlayerStyle = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLE_STYLES, "mutility.mparticle.create");
        final CommandData createOnPlaceCustom = new CommandData(ArgumentTypes.DEFAULT, "vlastni", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyle = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLE_STYLES, "mutility.mparticle.create.place");
        final CommandData startOnPlayerID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.mparticle.start", CommandExecutors.PLAYER, (t) -> {
            System.out.println("Start player id");
            //TODO
        });
        final CommandData startOnPlaceID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.mparticle.start.place", CommandExecutors.PLAYER, (t) -> {
            System.out.println("Start place id");
            //TODO
        });
        final CommandData stopOnPlayerID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.mparticle.stop", CommandExecutors.PLAYER, (t) -> {
            System.out.println("Stop player id");
            //TODO
        });
        final CommandData stopOnPlaceID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.mparticle.stop.place", CommandExecutors.PLAYER, (t) -> {
            System.out.println("Stop place id");
            //TODO
        });
        final CommandData deleteOnPlayerID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.mparticle.delete", CommandExecutors.PLAYER, (t) -> {
            int recordId = Integer.parseInt(t.getArgs()[2]);
            if(isRecordId((Player)t.getSender(), recordId, false)) {
                Confirmation deleteConfirmation = new Confirmation(recordId, (Player) t.getSender(), "/mparticle delete player confirm");
                deleteConfirmation.setMessage(new JsonBuilder()
                        .addJsonSegment(prefix.getMParticlePrefix(true, true))
                        .text(": Opravdu si přejete odstranit tento particle?")
                        .color(colors.getSecondaryColorHEX()));
                if(playerDeleteConfirmationList.stream().noneMatch(x -> (x.getId() == recordId
                        && x.getPlayer().getName().equals(t.getSender().getName())
                        && !x.isFinished()))) {
                    deleteConfirmation.startTimer();
                    playerDeleteConfirmationList.add(deleteConfirmation);
                } else {
                    t.getSender().sendMessage(prefix.getMParticlePrefix(true, false)
                            + colors.getSecondaryColor() + "Žádost o potvrzení již byla vytvořena!");
                }
            } else {
                t.getSender().sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(t.getArgs()[2],true, false));
            }
        });
        final CommandData deleteOnPlaceID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.mparticle.delete.place", CommandExecutors.PLAYER, (t) -> {
            int recordId = Integer.parseInt(t.getArgs()[2]);
            if(isRecordId((Player)t.getSender(), recordId, true)) {
                Confirmation deleteConfirmation = new Confirmation(recordId, (Player) t.getSender(), "/mparticle delete place confirm");
                deleteConfirmation.setMessage(new JsonBuilder()
                        .addJsonSegment(prefix.getMParticlePrefix(true, true))
                        .text(": Opravdu si přejete odstranit tento particle?")
                        .color(colors.getSecondaryColorHEX()));
                if(placeDeleteConfirmationList.stream().noneMatch(x -> (x.getId() == recordId
                        && x.getPlayer().getName().equals(t.getSender().getName())
                        && !x.isFinished()))) {
                    deleteConfirmation.startTimer();
                    placeDeleteConfirmationList.add(deleteConfirmation);
                } else {
                    t.getSender().sendMessage(prefix.getMParticlePrefix(true, false)
                            + colors.getSecondaryColor() + "Žádost o potvrzení již byla vytvořena!");
                }
            } else {
                t.getSender().sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(t.getArgs()[2],true, false));
            }
        });
        final CommandData manageOnPlayerID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE);
        final CommandData manageOnPlaceID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE);
        final CommandData showPlayerPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData showPlacePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData deleteOnPlayerConfirm = new CommandData(ArgumentTypes.DEFAULT, "confirm", TabCompleterTypes.NONE);
        final CommandData deleteOnPlaceConfirm = new CommandData(ArgumentTypes.DEFAULT, "confirm", TabCompleterTypes.NONE);

        // 4. stupeň
        final CommandData createOnPlayerStyleRedstone = new CommandData(ArgumentTypes.DEFAULT, "redstone", TabCompleterTypes.DEFAULT, "mutility.mparticle.create");
        final CommandData createOnPlayerStyleParticle = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLES, "mutility.mparticle.create", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            String style = t.getArgs()[2];
            String particle = t.getArgs()[3];
            createParticle(player, getMaxPlayerRecordId(player) + 1, style, null, particle, 0, -2, -2, -2, 0, -1, -1, -1, null, null, -1, -1);
        });
        final CommandData createOnPlayerCustomStyle = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.CUSTOM_STYLES, "mutility.mparticle.create");
        final CommandData createOnPlaceStyleRedstone = new CommandData(ArgumentTypes.DEFAULT, "redstone", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyleParticle = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLES, "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyle = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.CUSTOM_STYLES, "mutility.mparticle.create.place");
        final CommandData manageOnPlayerIDRename = new CommandData(ArgumentTypes.DEFAULT, "rename",  TabCompleterTypes.NONE);
        final CommandData manageOnPlaceIDRename = new CommandData(ArgumentTypes.DEFAULT,  "rename", TabCompleterTypes.NONE);
        final CommandData showPlayerPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.mparticle.list", CommandExecutors.PLAYER, (t) -> loadMParticleList((Player) t.getSender(), Integer.parseInt(t.getArgs()[3]), false));
        final CommandData showPlacePageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.mparticle.list.place", CommandExecutors.PLAYER, (t) -> loadMParticleList((Player) t.getSender(), Integer.parseInt(t.getArgs()[3]), true));
        final CommandData deleteOnPlayerConfirmID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.mparticle.delete", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[3]);
            if(isRecordId((Player)t.getSender(), recordId, false)) {
                boolean valid = false;
                for (int i = playerDeleteConfirmationList.size() - 1; i >= 0; i--) {
                    if(playerDeleteConfirmationList.get(i).getId() == recordId
                            && playerDeleteConfirmationList.get(i).getPlayer().getName().equals(t.getSender().getName())) {
                        if(!playerDeleteConfirmationList.get(i).isFinished()) {
                            valid = true;
                            playerDeleteConfirmationList.get(i).setFinished(true);
                            deleteParticle((Player)t.getSender(), recordId, false);
                            t.getSender().sendMessage(prefix.getMParticlePrefix(true, false) + "Particle byl smazán!");
                            break;
                        }
                    }
                }
                if(!valid) {
                    t.getSender().sendMessage(prefix.getMParticlePrefix(true, false)
                            + "Potvrzení o smazání návrhu není platné!");
                }
                playerDeleteConfirmationList.removeIf(Confirmation::isFinished);
            } else {
                t.getSender().sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(t.getArgs()[3],true, false));
            }
        });
        final CommandData deleteOnPlaceConfirmID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.mparticle.delete.place", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[3]);
            if(isRecordId((Player)t.getSender(), recordId, true)) {
                boolean valid = false;
                for (int i = placeDeleteConfirmationList.size() - 1; i >= 0; i--) {
                    if(placeDeleteConfirmationList.get(i).getId() == recordId
                            && placeDeleteConfirmationList.get(i).getPlayer().getName().equals(t.getSender().getName())) {
                        if(!placeDeleteConfirmationList.get(i).isFinished()) {
                            valid = true;
                            placeDeleteConfirmationList.get(i).setFinished(true);
                            deleteParticle((Player)t.getSender(), recordId, true);
                            t.getSender().sendMessage(prefix.getMParticlePrefix(true, false) + "Particle byl smazán!");
                            break;
                        }
                    }
                }
                if(!valid) {
                    t.getSender().sendMessage(prefix.getMParticlePrefix(true, false)
                            + "Potvrzení o smazání návrhu není platné!");
                }
                playerDeleteConfirmationList.removeIf(Confirmation::isFinished);
            } else {
                t.getSender().sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(t.getArgs()[3],true, false));
            }
        });

        // 5. stupeň
        final CommandData createOnPlayerStyleRedstoneColor = new CommandData(ArgumentTypes.DEFAULT, "color", TabCompleterTypes.DEFAULT, "mutility.mparticle.create");
        final CommandData createOnPlayerStyleRedstoneRGB = new CommandData(ArgumentTypes.DEFAULT, "rgb", TabCompleterTypes.DEFAULT, "mutility.mparticle.create");
        final CommandData createOnPlayerCustomStyleRedstone = new CommandData(ArgumentTypes.DEFAULT, "redstone", TabCompleterTypes.DEFAULT, "mutility.mparticle.create");
        final CommandData createOnPlayerCustomStyleParticle = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLES, "mutility.mparticle.create", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            String style = t.getArgs()[2];
            String customStyle = t.getArgs()[3];
            String particle = t.getArgs()[4];
            createParticle(player, getMaxPlayerRecordId(player) + 1, style, customStyle, particle, 0, -2, -2, -2, 0, -1, -1, -1, null, null, -1, -1);
        });
        final CommandData createOnPlaceStyleRedstoneColor = new CommandData(ArgumentTypes.DEFAULT, "color", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyleRedstoneRGB = new CommandData(ArgumentTypes.DEFAULT, "rgb", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstone = new CommandData(ArgumentTypes.DEFAULT, "redstone", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleParticle = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLES, "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyleParticleHere = new CommandData(ArgumentTypes.DEFAULT, "zde", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            Location loc = player.getLocation();
            String style = t.getArgs()[2];
            String particle = t.getArgs()[3];
            createParticle(player, getMaxGlobalRecordId() + 1, style, null, particle, 0, -2, -2, -2, 1, (float)(loc.getBlockX() + 0.5), (int)(loc.getY()), (float)(loc.getBlockZ() + 0.5), Objects.requireNonNull(loc.getWorld()).getName(), Objects.requireNonNull(plugin.getCurrentServer()).getName(), loc.getPitch(), loc.getYaw());
        });
        final CommandData createOnPlaceStyleParticleX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.mparticle.create.place");
        final CommandData manageOnPlayerIDRenameName = new CommandData(ArgumentTypes.STRINGINF,  TabCompleterTypes.CUSTOM, "[< Nové jméno >]", "mutility.mparticle.manage", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[2]);
            String name = strUt.getStringFromArgs(t.getArgs(), 4);
            if(isRecordId((Player)t.getSender(), recordId, false)) {
                renameParticle((Player) t.getSender(), recordId, false, name);
            } else {
                t.getSender().sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(t.getArgs()[2],true, false));
            }
        });
        final CommandData manageOnPlaceIDRenameName = new CommandData(ArgumentTypes.STRINGINF,  TabCompleterTypes.CUSTOM, "[< Nové jméno >]", "mutility.mparticle.manage.place", CommandExecutors.PLAYER, t -> {
            int recordId = Integer.parseInt(t.getArgs()[2]);
            String name = strUt.getStringFromArgs(t.getArgs(), 4);
            if(isRecordId((Player)t.getSender(), recordId, true)) {
                renameParticle((Player) t.getSender(), recordId, true, name);
            } else {
                t.getSender().sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(t.getArgs()[2],true, false));
            }
        });

        // 6. stupeň
        final CommandData createOnPlayerStyleRedstoneColorFinal = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLE_COLORS, "mutility.mparticle.create", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            String style = t.getArgs()[2];
            String particle = t.getArgs()[3];
            String color = t.getArgs()[5];
            if(checker.checkParticleColor(color)) {
                RGB rgb = Colors.getRGBByName(color);
                createParticle(player, getMaxPlayerRecordId(player) + 1, style, null, particle, 1, rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 0, -1, -1, -1, null, null, -1, -1);
            } else {
                t.getSender().sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(color, true, false));
            }
        });
        final CommandData createOnPlayerStyleRedstoneColorR = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Červená (0-255)>]", "mutility.mparticle.create");
        final CommandData createOnPlayerCustomStyleRedstoneColor = new CommandData(ArgumentTypes.DEFAULT, "color", TabCompleterTypes.DEFAULT, "mutility.mparticle.create");
        final CommandData createOnPlayerCustomStyleRedstoneRGB = new CommandData(ArgumentTypes.DEFAULT, "rgb", TabCompleterTypes.DEFAULT, "mutility.mparticle.create");
        final CommandData createOnPlaceStyleRedstoneColorFinal = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLE_COLORS, "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyleRedstoneColorR = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Červená (0-255)>]", "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstoneColor = new CommandData(ArgumentTypes.DEFAULT, "color", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstoneRGB = new CommandData(ArgumentTypes.DEFAULT, "rgb", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyleParticleY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleParticleHere = new CommandData(ArgumentTypes.DEFAULT, "zde", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            Location loc = player.getLocation();
            String style = t.getArgs()[2];
            String customStyle = t.getArgs()[3];
            String particle = t.getArgs()[4];
            createParticle(player, getMaxGlobalRecordId() + 1, style, customStyle, particle, 0, -2, -2, -2, 1, (float)(loc.getBlockX() + 0.5), (int)(loc.getY()), (float)(loc.getBlockZ() + 0.5), Objects.requireNonNull(loc.getWorld()).getName(), Objects.requireNonNull(plugin.getCurrentServer()).getName(), loc.getPitch(), loc.getYaw());
        });
        final CommandData createOnPlaceCustomStyleParticleX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.mparticle.create.place");

        // 7. stupeň
        final CommandData createOnPlayerStyleRedstoneColorRG = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Zelená (0-255)>]", "mutility.mparticle.create");
        final CommandData createOnPlayerCustomStyleRedstoneColorR = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Červená (0-255)>]", "mutility.mparticle.create");
        final CommandData createOnPlayerCustomStyleRedstoneColorFinal = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLE_COLORS, "mutility.mparticle.create", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            String style = t.getArgs()[2];
            String customStyle = t.getArgs()[3];
            String particle = t.getArgs()[4];
            String color = t.getArgs()[6];
            if(checker.checkParticleColor(color)) {
                RGB rgb = Colors.getRGBByName(color);
                createParticle(player, getMaxPlayerRecordId(player) + 1, style, customStyle, particle, 1, rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 0, -1, -1, -1, null, null, -1, -1);
            } else {
                t.getSender().sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(color, true, false));
            }

        });
        final CommandData createOnPlaceStyleRedstoneColorRG = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Zelená (0-255)>]", "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstoneColorR = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Červená (0-255)>]", "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstoneColorFinal = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLE_COLORS, "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyleParticleZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            Location loc = player.getLocation();
            String style = t.getArgs()[2];
            String particle = t.getArgs()[3];
            float x = Float.parseFloat(t.getArgs()[4]);
            float y = Float.parseFloat(t.getArgs()[5]);
            float z = Float.parseFloat(t.getArgs()[6]);
            createParticle(player, getMaxGlobalRecordId() + 1, style, null, particle, 0, -2, -2, -2, 1, x, y, z, Objects.requireNonNull(loc.getWorld()).getName(), Objects.requireNonNull(plugin.getCurrentServer()).getName(), loc.getPitch(), loc.getYaw());
        });
        final CommandData createOnPlaceCustomStyleParticleY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyleRedstoneColorFinalHere = new CommandData(ArgumentTypes.DEFAULT, "zde", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            Location loc = player.getLocation();
            String style = t.getArgs()[2];
            String particle = t.getArgs()[3];
            String color = t.getArgs()[5];
            if(checker.checkParticleColor(color)) {
                RGB rgb = Colors.getRGBByName(color);
                createParticle(player, getMaxGlobalRecordId() + 1, style, null, particle, 1, rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 1, (float)(loc.getBlockX() + 0.5), (int)(loc.getY()), (float)(loc.getBlockZ() + 0.5), Objects.requireNonNull(loc.getWorld()).getName(), Objects.requireNonNull(plugin.getCurrentServer()).getName(), loc.getPitch(), loc.getYaw());
            } else {
                t.getSender().sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(color, true, false));
            }
        });
        final CommandData createOnPlaceStyleRedstoneColorFinalX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstoneColorFinalHere = new CommandData(ArgumentTypes.DEFAULT, "zde", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            Location loc = player.getLocation();
            String style = t.getArgs()[2];
            String customStyle = t.getArgs()[3];
            String particle = t.getArgs()[4];
            String color = t.getArgs()[6];
            if(checker.checkParticleColor(color)) {
                RGB rgb = Colors.getRGBByName(color);
                createParticle(player, getMaxGlobalRecordId() + 1, style, customStyle, particle, 1, rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 1, (float)(loc.getBlockX() + 0.5), (int)(loc.getY()), (float)(loc.getBlockZ() + 0.5), Objects.requireNonNull(loc.getWorld()).getName(), Objects.requireNonNull(plugin.getCurrentServer()).getName(), loc.getPitch(), loc.getYaw());
            } else {
                t.getSender().sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(color, true, false));
            }
        });
        final CommandData createOnPlaceCustomStyleRedstoneColorFinalX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.mparticle.create.place");

        // 8. stupeň
        final CommandData createOnPlayerStyleRedstoneColorRGB = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Modrá (0-255)>]", "mutility.mparticle.create", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            String style = t.getArgs()[2];
            String particle = t.getArgs()[3];
            int red = Integer.parseInt(t.getArgs()[5]);
            int green = Integer.parseInt(t.getArgs()[6]);
            int blue = Integer.parseInt(t.getArgs()[7]);
            createParticle(player, getMaxPlayerRecordId(player) + 1, style, null, particle, 1, red, green, blue, 0, -1, -1, -1, null, null, -1, -1);
        });
        final CommandData createOnPlayerCustomStyleRedstoneColorRG = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Zelená (0-255)>]", "mutility.mparticle.create");
        final CommandData createOnPlaceStyleRedstoneColorRGB = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Modrá (0-255)>]", "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstoneColorRG = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Zelená (0-255)>]", "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleParticleZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            Location loc = player.getLocation();
            String style = t.getArgs()[2];
            String customStyle = t.getArgs()[3];
            String particle = t.getArgs()[4];
            float x = Float.parseFloat(t.getArgs()[5]);
            float y = Float.parseFloat(t.getArgs()[6]);
            float z = Float.parseFloat(t.getArgs()[7]);
            createParticle(player, getMaxGlobalRecordId() + 1, style, customStyle, particle, 0, -2, -2, -2, 1, x, y, z, Objects.requireNonNull(loc.getWorld()).getName(), Objects.requireNonNull(plugin.getCurrentServer()).getName(), loc.getPitch(), loc.getYaw());
        });
        final CommandData createOnPlaceStyleRedstoneColorFinalY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstoneColorFinalY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.mparticle.create.place");

        // 9. stupeň
        final CommandData createOnPlayerCustomStyleRedstoneColorRGB = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Modrá (0-255)>]", "mutility.mparticle.create", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            String style = t.getArgs()[2];
            String customStyle = t.getArgs()[3];
            String particle = t.getArgs()[4];
            int red = Integer.parseInt(t.getArgs()[6]);
            int green = Integer.parseInt(t.getArgs()[7]);
            int blue = Integer.parseInt(t.getArgs()[8]);
            createParticle(player, getMaxPlayerRecordId(player) + 1, style, customStyle, particle, 1, red, green, blue, 0, -1, -1, -1, null, null, -1, -1);
        });
        final CommandData createOnPlaceCustomStyleRedstoneColorRGB = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Modrá (0-255)>]", "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyleRedstoneColorFinalZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            Location loc = player.getLocation();
            String style = t.getArgs()[2];
            String particle = t.getArgs()[3];
            String color = t.getArgs()[5];
            float x = Float.parseFloat(t.getArgs()[6]);
            float y = Float.parseFloat(t.getArgs()[7]);
            float z = Float.parseFloat(t.getArgs()[8]);
            if(checker.checkParticleColor(color)) {
                RGB rgb = Colors.getRGBByName(color);
                createParticle(player, getMaxGlobalRecordId() + 1, style, null, particle, 1, rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 1, x, y, z, Objects.requireNonNull(loc.getWorld()).getName(), Objects.requireNonNull(plugin.getCurrentServer()).getName(), loc.getPitch(), loc.getYaw());
            } else {
                t.getSender().sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(color, true, false));
            }
        });
        final CommandData createOnPlaceCustomStyleRedstoneColorFinalZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            Location loc = player.getLocation();
            String style = t.getArgs()[2];
            String customStyle = t.getArgs()[3];
            String particle = t.getArgs()[4];
            String color = t.getArgs()[6];
            float x = Float.parseFloat(t.getArgs()[7]);
            float y = Float.parseFloat(t.getArgs()[8]);
            float z = Float.parseFloat(t.getArgs()[9]);
            if(checker.checkParticleColor(color)) {
                RGB rgb = Colors.getRGBByName(color);
                createParticle(player, getMaxGlobalRecordId() + 1, style, customStyle, particle, 1, rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 1, x, y, z, Objects.requireNonNull(loc.getWorld()).getName(), Objects.requireNonNull(plugin.getCurrentServer()).getName(), loc.getPitch(), loc.getYaw());
            } else {
                t.getSender().sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(color, true, false));
            }
        });
        final CommandData createOnPlaceStyleRedstoneColorRGBHere = new CommandData(ArgumentTypes.DEFAULT, "zde", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            Location loc = player.getLocation();
            String style = t.getArgs()[2];
            String particle = t.getArgs()[3];
            int red = Integer.parseInt(t.getArgs()[5]);
            int green = Integer.parseInt(t.getArgs()[6]);
            int blue = Integer.parseInt(t.getArgs()[7]);
            createParticle(player, getMaxGlobalRecordId() + 1, style, null, particle, 1, red, green, blue, 1, (float)(loc.getBlockX() + 0.5), (int)(loc.getY()), (float)(loc.getBlockZ() + 0.5), Objects.requireNonNull(loc.getWorld()).getName(), Objects.requireNonNull(plugin.getCurrentServer()).getName(), loc.getPitch(), loc.getYaw());
        });
        final CommandData createOnPlaceStyleRedstoneColorRGBX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.mparticle.create.place");

        // 10. stupeň
        final CommandData createOnPlaceStyleRedstoneColorRGBY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstoneColorRGBHere = new CommandData(ArgumentTypes.DEFAULT, "zde", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            Location loc = player.getLocation();
            String style = t.getArgs()[2];
            String customStyle = t.getArgs()[3];
            String particle = t.getArgs()[4];
            int red = Integer.parseInt(t.getArgs()[6]);
            int green = Integer.parseInt(t.getArgs()[7]);
            int blue = Integer.parseInt(t.getArgs()[8]);
            createParticle(player, getMaxGlobalRecordId() + 1, style, customStyle, particle, 1, red, green, blue, 1, (float)(loc.getBlockX() + 0.5), (int)(loc.getY()), (float)(loc.getBlockZ() + 0.5), Objects.requireNonNull(loc.getWorld()).getName(), Objects.requireNonNull(plugin.getCurrentServer()).getName(), loc.getPitch(), loc.getYaw());
        });
        final CommandData createOnPlaceCustomStyleRedstoneColorRGBX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.mparticle.create.place");

        // 11. stupeň
        final CommandData createOnPlaceStyleRedstoneColorRGBZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            Location loc = player.getLocation();
            String style = t.getArgs()[2];
            String particle = t.getArgs()[3];
            int red = Integer.parseInt(t.getArgs()[5]);
            int green = Integer.parseInt(t.getArgs()[6]);
            int blue = Integer.parseInt(t.getArgs()[7]);
            float x = Float.parseFloat(t.getArgs()[8]);
            float y = Float.parseFloat(t.getArgs()[9]);
            float z = Float.parseFloat(t.getArgs()[10]);
            createParticle(player, getMaxGlobalRecordId() + 1, style, null, particle, 1, red, green, blue, 1, x, y, z, Objects.requireNonNull(loc.getWorld()).getName(), Objects.requireNonNull(plugin.getCurrentServer()).getName(), loc.getPitch(), loc.getYaw());
        });
        final CommandData createOnPlaceCustomStyleRedstoneColorRGBY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.mparticle.create.place");

        // 12. stupeň
        final CommandData createOnPlaceCustomStyleRedstoneColorRGBZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            Player player = (Player) t.getSender();
            Location loc = player.getLocation();
            String style = t.getArgs()[2];
            String customStyle = t.getArgs()[3];
            String particle = t.getArgs()[4];
            int red = Integer.parseInt(t.getArgs()[6]);
            int green = Integer.parseInt(t.getArgs()[7]);
            int blue = Integer.parseInt(t.getArgs()[8]);
            float x = Float.parseFloat(t.getArgs()[9]);
            float y = Float.parseFloat(t.getArgs()[10]);
            float z = Float.parseFloat(t.getArgs()[11]);
            createParticle(player, getMaxGlobalRecordId() + 1, style, customStyle, particle, 1, red, green, blue, 1, x, y, z, Objects.requireNonNull(loc.getWorld()).getName(), Objects.requireNonNull(plugin.getCurrentServer()).getName(), loc.getPitch(), loc.getYaw());
        });

        mparticle.setDescription("Systém pro správu particlů");

        help.setDescription("Nápověda k příkazu");
        help.setSyntax("/mparticle " + help.getSubcommand());

        create.setDescription("Vytvoření particlu");
        create.setSyntax("/mparticle " + create.getSubcommand() + " hrac [<Styl>] [<Particle>]\n" +
                "/mparticle " + create.getSubcommand() + " hrac [<Styl>] redstone barva [<Barva>]\n" +
                "/mparticle " + create.getSubcommand() + " hrac [<Styl>] redstone rgb [<Červená>] [<Zelená>] [<Modrá>]\n" +
                "/mparticle " + create.getSubcommand() + " hrac vlastni [<Vlastní Styl>] [<Particle>]\n" +
                "/mparticle " + create.getSubcommand() + " hrac vlastni [<Vlastní Styl>] redstone barva [<Barva>]\n" +
                "/mparticle " + create.getSubcommand() + " hrac vlastni [<Vlastní Styl>] redstone rgb [<Červená>] [<Zelená>] [<Modrá>]\n" +
                "/mparticle " + create.getSubcommand() + " pozice [<Styl>] [<Particle>] zde\n" +
                "/mparticle " + create.getSubcommand() + " pozice [<Styl>] [<Particle>] [<X>] [<Y>] [<Z>]\n" +
                "/mparticle " + create.getSubcommand() + " pozice [<Styl>] redstone barva [<Barva>] zde\n" +
                "/mparticle " + create.getSubcommand() + " pozice [<Styl>] redstone barva [<Barva>] [<X>] [<Y>] [<Z>]\n" +
                "/mparticle " + create.getSubcommand() + " pozice [<Styl>] redstone rgb [<Červená>] [<Zelená>] [<Modrá>] zde\n" +
                "/mparticle " + create.getSubcommand() + " pozice [<Styl>] redstone rgb [<Červená>] [<Zelená>] [<Modrá>] [<X>] [<Y>] [<Z>]\n" +
                "/mparticle " + create.getSubcommand() + " pozice vlastni [<Vlastní Styl>] [<Particle>] zde\n" +
                "/mparticle " + create.getSubcommand() + " pozice vlastni [<Vlastní Styl>] [<Particle>] [<X>] [<Y>] [<Z>]\n" +
                "/mparticle " + create.getSubcommand() + " pozice vlastni [<Vlastní Styl>] redstone barva [<Barva>] zde\n" +
                "/mparticle " + create.getSubcommand() + " pozice vlastni [<Vlastní Styl>] redstone barva [<Barva>] [<X>] [<Y>] [<Z>]\n" +
                "/mparticle " + create.getSubcommand() + " pozice vlastni [<Vlastní Styl>] redstone rgb [<Červená>] [<Zelená>] [<Modrá>] zde\n" +
                "/mparticle " + create.getSubcommand() + " pozice vlastni [<Vlastní Styl>] redstone rgb [<Červená>] [<Zelená>] [<Modrá>] [<X>] [<Y>] [<Z>]");

        show.setDescription("Seznam vytvořených particlů u hráče nebo globálních");
        show.setSyntax("/mparticle " + show.getSubcommand() + "\n/mparticle " + show.getSubcommand() + " hrac\n/mparticle " + show.getSubcommand() + " pozice");

        start.setDescription("Zapnutí momentálně vybraného particlu");
        start.setSyntax("/mparticle " + start.getSubcommand());

        stop.setDescription("Vypnutí momentálně vybraného particlu");
        stop.setSyntax("/mparticle " + stop.getSubcommand());

        mparticle.link(help);
        mparticle.link(helpPage);
        mparticle.link(create);
        mparticle.link(delete);
        mparticle.link(show);
        mparticle.link(manage);
        mparticle.link(start);
        mparticle.link(stop);

        help.link(helpHelpPage);
        helpPage.link(helpPageID);
        create.link(createOnPlayer);
        create.link(createOnPlace);
        start.link(startOnPlayer);
        start.link(startOnPlace);
        stop.link(stopOnPlayer);
        stop.link(stopOnPlace);
        delete.link(deleteOnPlayer);
        delete.link(deleteOnPlace);
        manage.link(manageOnPlayer);
        manage.link(manageOnPlace);
        show.link(showPlayer);
        show.link(showPlace);
        show.link(showPlayerPage);

        helpHelpPage.link(helpHelpPageID);
        createOnPlayer.link(createOnPlayerCustom);
        createOnPlayer.link(createOnPlayerStyle);
        createOnPlace.link(createOnPlaceCustom);
        createOnPlace.link(createOnPlaceStyle);
        startOnPlayer.link(startOnPlayerID);
        startOnPlace.link(startOnPlaceID);
        stopOnPlayer.link(stopOnPlayerID);
        stopOnPlace.link(stopOnPlaceID);
        deleteOnPlayer.link(deleteOnPlayerID);
        deleteOnPlace.link(deleteOnPlaceID);
        manageOnPlayer.link(manageOnPlayerID);
        manageOnPlace.link(manageOnPlaceID);
        showPlayer.link(showPlayerPage);
        showPlace.link(showPlacePage);
        deleteOnPlayer.link(deleteOnPlayerConfirm);
        deleteOnPlace.link(deleteOnPlaceConfirm);

        createOnPlayerStyle.link(createOnPlayerStyleRedstone);
        createOnPlayerStyle.link(createOnPlayerStyleParticle);
        createOnPlayerCustom.link(createOnPlayerCustomStyle);
        createOnPlaceStyle.link(createOnPlaceStyleRedstone);
        createOnPlaceStyle.link(createOnPlaceStyleParticle);
        createOnPlaceCustom.link(createOnPlaceCustomStyle);
        manageOnPlayerID.link(manageOnPlayerIDRename);
        manageOnPlaceID.link(manageOnPlaceIDRename);
        showPlayerPage.link(showPlayerPageID);
        showPlacePage.link(showPlacePageID);
        deleteOnPlayerConfirm.link(deleteOnPlayerConfirmID);
        deleteOnPlaceConfirm.link(deleteOnPlaceConfirmID);

        createOnPlayerStyleRedstone.link(createOnPlayerStyleRedstoneColor);
        createOnPlayerStyleRedstone.link(createOnPlayerStyleRedstoneRGB);
        createOnPlayerCustomStyle.link(createOnPlayerCustomStyleRedstone);
        createOnPlayerCustomStyle.link(createOnPlayerCustomStyleParticle);
        createOnPlaceStyleRedstone.link(createOnPlaceStyleRedstoneColor);
        createOnPlaceStyleRedstone.link(createOnPlaceStyleRedstoneRGB);
        createOnPlaceCustomStyle.link(createOnPlaceCustomStyleRedstone);
        createOnPlaceCustomStyle.link(createOnPlaceCustomStyleParticle);
        createOnPlaceStyleParticle.link(createOnPlaceStyleParticleHere);
        createOnPlaceStyleParticle.link(createOnPlaceStyleParticleX);
        manageOnPlayerIDRename.link(manageOnPlayerIDRenameName);
        manageOnPlaceIDRename.link(manageOnPlaceIDRenameName);

        createOnPlayerStyleRedstoneColor.link(createOnPlayerStyleRedstoneColorFinal);
        createOnPlayerStyleRedstoneRGB.link(createOnPlayerStyleRedstoneColorR);
        createOnPlayerCustomStyleRedstone.link(createOnPlayerCustomStyleRedstoneColor);
        createOnPlayerCustomStyleRedstone.link(createOnPlayerCustomStyleRedstoneRGB);
        createOnPlaceStyleRedstoneColor.link(createOnPlaceStyleRedstoneColorFinal);
        createOnPlaceStyleRedstoneRGB.link(createOnPlaceStyleRedstoneColorR);
        createOnPlaceCustomStyleRedstone.link(createOnPlaceCustomStyleRedstoneColor);
        createOnPlaceCustomStyleRedstone.link(createOnPlaceCustomStyleRedstoneRGB);
        createOnPlaceStyleParticleX.link(createOnPlaceStyleParticleY);
        createOnPlaceCustomStyleParticle.link(createOnPlaceCustomStyleParticleHere);
        createOnPlaceCustomStyleParticle.link(createOnPlaceCustomStyleParticleX);

        createOnPlayerStyleRedstoneColorR.link(createOnPlayerStyleRedstoneColorRG);
        createOnPlayerCustomStyleRedstoneColor.link(createOnPlayerCustomStyleRedstoneColorFinal);
        createOnPlayerCustomStyleRedstoneRGB.link(createOnPlayerCustomStyleRedstoneColorR);
        createOnPlaceStyleRedstoneColorR.link(createOnPlaceStyleRedstoneColorRG);
        createOnPlaceCustomStyleRedstoneColor.link(createOnPlaceCustomStyleRedstoneColorFinal);
        createOnPlaceCustomStyleRedstoneRGB.link(createOnPlaceCustomStyleRedstoneColorR);
        createOnPlaceStyleParticleY.link(createOnPlaceStyleParticleZ);
        createOnPlaceCustomStyleParticleX.link(createOnPlaceCustomStyleParticleY);
        createOnPlaceStyleRedstoneColorFinal.link(createOnPlaceStyleRedstoneColorFinalHere);
        createOnPlaceStyleRedstoneColorFinal.link(createOnPlaceStyleRedstoneColorFinalX);

        createOnPlayerStyleRedstoneColorRG.link(createOnPlayerStyleRedstoneColorRGB);
        createOnPlayerCustomStyleRedstoneColorR.link(createOnPlayerCustomStyleRedstoneColorRG);
        createOnPlaceStyleRedstoneColorRG.link(createOnPlaceStyleRedstoneColorRGB);
        createOnPlaceCustomStyleRedstoneColorR.link(createOnPlaceCustomStyleRedstoneColorRG);
        createOnPlaceCustomStyleParticleY.link(createOnPlaceCustomStyleParticleZ);
        createOnPlaceStyleRedstoneColorFinalX.link(createOnPlaceStyleRedstoneColorFinalY);
        createOnPlaceCustomStyleRedstoneColorFinal.link(createOnPlaceCustomStyleRedstoneColorFinalHere);
        createOnPlaceCustomStyleRedstoneColorFinal.link(createOnPlaceCustomStyleRedstoneColorFinalX);

        createOnPlayerCustomStyleRedstoneColorRG.link(createOnPlayerCustomStyleRedstoneColorRGB);
        createOnPlaceCustomStyleRedstoneColorRG.link(createOnPlaceCustomStyleRedstoneColorRGB);
        createOnPlaceStyleRedstoneColorFinalY.link(createOnPlaceStyleRedstoneColorFinalZ);
        createOnPlaceCustomStyleRedstoneColorFinalX.link(createOnPlaceCustomStyleRedstoneColorFinalY);
        createOnPlaceStyleRedstoneColorRGB.link(createOnPlaceStyleRedstoneColorRGBHere);
        createOnPlaceStyleRedstoneColorRGB.link(createOnPlaceStyleRedstoneColorRGBX);

        createOnPlaceStyleRedstoneColorRGBX.link(createOnPlaceStyleRedstoneColorRGBY);
        createOnPlaceCustomStyleRedstoneColorRGB.link(createOnPlaceCustomStyleRedstoneColorRGBHere);
        createOnPlaceCustomStyleRedstoneColorRGB.link(createOnPlaceCustomStyleRedstoneColorRGBX);

        createOnPlaceStyleRedstoneColorRGBY.link(createOnPlaceStyleRedstoneColorRGBZ);
        createOnPlaceCustomStyleRedstoneColorRGBX.link(createOnPlaceCustomStyleRedstoneColorRGBY);

        createOnPlaceCustomStyleRedstoneColorFinalY.link(createOnPlaceCustomStyleRedstoneColorFinalZ);
        createOnPlaceCustomStyleRedstoneColorRGBY.link(createOnPlaceCustomStyleRedstoneColorRGBZ);

        return mparticle;
    }

    private void renameParticle(Player player, int recordId, boolean global, String name) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            if(global) {
                stm = db.getCon().prepareStatement("UPDATE " + tables.getMParticleTable() + " SET particle_name = ? WHERE record_id = ? AND place = 1");
                stm.setString(1, name);
                stm.setInt(2, recordId);
            } else {
                stm = db.getCon().prepareStatement("UPDATE " + tables.getMParticleTable() + " SET particle_name = ? WHERE record_id = ? AND user_id = ? AND place = 0");
                stm.setString(1, name);
                stm.setInt(2, recordId);
                stm.setInt(3, playerManager.getUserId(player.getName()));
            }
            stm.execute();
            player.sendMessage(prefix.getMParticlePrefix(true, false) + "Particle " + colors.getPrimaryColor() + recordId + colors.getSecondaryColor() + " byl přejmenován na " + colors.getPrimaryColor() + name + colors.getSecondaryColor() + "!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteParticle(Player player, int recordId, boolean global) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            if(global) {
                stm = db.getCon().prepareStatement("DELETE FROM " + tables.getMParticleTable() + " WHERE record_id = ? AND place = 1");
                stm.setInt(1, recordId);
                stm.execute();
                stm = db.getCon().prepareStatement("UPDATE " + tables.getMParticleTable() + " SET record_id = record_id - 1 WHERE record_id > ? AND place = 1");
                stm.setInt(1, recordId);
            } else {
                stm = db.getCon().prepareStatement("DELETE FROM " + tables.getMParticleTable() + " WHERE record_id = ? AND user_id = ? AND place = 0");
                stm.setInt(1, recordId);
                stm.setInt(2, playerManager.getUserId(player.getName()));
                stm.execute();
                stm = db.getCon().prepareStatement("UPDATE " + tables.getMParticleTable() + " SET record_id = record_id - 1 WHERE record_id > ? AND user_id = ? AND place = 0");
                stm.setInt(1, recordId);
                stm.setInt(2, playerManager.getUserId(player.getName()));
            }
            stm.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isRecordId(Player player, int recordId, boolean global) {
        return recordId <= getMaxRecordId(player, global);
    }

    private int getMaxRecordId(Player player, boolean global) {
        int maxRecordId = 0;
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            if(global) {
                stm = db.getCon().prepareStatement("SELECT COALESCE(MAX(record_id), 0) FROM "+ tables.getMParticleTable() + " WHERE place = 1");
            } else {
                stm = db.getCon().prepareStatement("SELECT COALESCE(MAX(record_id), 0) FROM "+ tables.getMParticleTable() + " WHERE user_id = ? AND place = 0");
                stm.setInt(1, playerManager.getUserId(player.getName()));
            }
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                maxRecordId = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return maxRecordId;
    }

    private void createParticle(Player player, int recordId, String style, String customStyle, String particle, int color, int red, int green, int blue, int place, float posX, float posY, float posZ, String world, String server, double pitch, double yaw) {
        if(style != null) {
            if(!checker.checkParticleStyle(style)) {
                player.sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(style, true, false));
                return;
            }
        }
        if(customStyle != null) {
            if(!checker.checkParticleCustomStyle(customStyle)) {
                player.sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(customStyle, true, false));
                return;
            }
        }
        if(particle != null) {
            if(!checker.checkParticle(particle)) {
                player.sendMessage(prefix.getMParticlePrefix(true, false) + errors.errWrongArgument(particle, true, false));
                return;
            }
        }
        if(red > 255) {
            player.sendMessage(prefix.getMParticlePrefix(true, false) + "Červená musí být v rozsahu 0-255!");
            return;
        }
        if(green > 255) {
            player.sendMessage(prefix.getMParticlePrefix(true, false) + "Zelená musí být v rozsahu 0-255!");
            return;
        }
        if(blue > 255) {
            player.sendMessage(prefix.getMParticlePrefix(true, false) + "Modrá musí být v rozsahu 0-255!");
            return;
        }
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("INSERT INTO " + tables.getMParticleTable() + " (user_id, record_id, style, custom_style, particle, selected, color, red, green, blue, place, posX, posY, posZ, world, server, pitch, yaw) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            stm.setInt(2, recordId);
            stm.setString(3, style);
            stm.setString(4, customStyle);
            stm.setString(5, particle);
            stm.setInt(6, 0);
            stm.setInt(7, color);
            if(red == -2) {
                stm.setNull(8, 4);
            } else {
                stm.setInt(8, red);
            }
            if(green == -2) {
                stm.setNull(9, 4);
            } else {
                stm.setInt(9, green);
            }
            if(blue == -2) {
                stm.setNull(10, 4);
            } else {
                stm.setInt(10, blue);
            }
            stm.setInt(11, place);
            if(posX == -1) {
                stm.setNull(12, 8);
            } else {
                stm.setDouble(12, posX);
            }
            if(posY == -1) {
                stm.setNull(13, 8);
            } else {
                stm.setDouble(13, posY);
            }
            if(posZ == -1) {
                stm.setNull(14, 8);
            } else {
                stm.setDouble(14, posZ);
            }
            stm.setString(15, world);
            stm.setString(16, server);
            if(pitch == -1) {
                stm.setNull(17, 8);
            } else {
                stm.setDouble(17, pitch);
            }
            if(yaw == -1) {
                stm.setNull(18, 8);
            } else {
                stm.setDouble(18, yaw);
            }
            stm.execute();
            player.sendMessage(prefix.getMParticlePrefix(true, false) + "Particle byl úspěšně vytvořen!");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private int getMaxPlayerRecordId(Player player) {
        int recordId = 0;
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT COALESCE(MAX(record_id), 0) FROM "+ tables.getMParticleTable() + " WHERE user_id = ? AND place = 0");
            stm.setInt(1, playerManager.getUserId(player.getName()));
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                recordId = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return recordId;
    }

    private int getMaxGlobalRecordId() {
        int recordId = 0;
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm = db.getCon().prepareStatement("SELECT COALESCE(MAX(record_id), 0) FROM "+ tables.getMParticleTable() + " WHERE place = 1");
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                recordId = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return recordId;
    }

    private void loadMParticleList(Player player, int page, boolean global) {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            particleList.clear();
            PreparedStatement stm;
            String mode;
            if(global) {
                particleList.setCommand("/mparticle zobraz pozice");
                mode = "place";
                stm = db.getCon().prepareStatement("SELECT * FROM " + tables.getMParticleTable() + " WHERE place = 1");
            } else {
                particleList.setCommand("/mparticle zobraz hrac");
                mode = "player";
                stm = db.getCon().prepareStatement("SELECT * FROM " + tables.getMParticleTable() + " WHERE user_id = ? AND place = 0");
                stm.setInt(1, playerManager.getUserId(player.getName()));
            }
            ResultSet rs =  stm.executeQuery();
            String startHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Zapnutí")
                    .color(ChatColor.GREEN)
                    .text(" particlu <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String stopHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Vypnutí")
                    .color(ChatColor.RED)
                    .text(" particlu <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String renameHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Přejmenování")
                    .color(colors.getPrimaryColorHEX())
                    .text(" particlu <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            String deleteHover = new JsonBuilder(">> Klikni pro ")
                    .color(colors.getSecondaryColorHEX())
                    .text("Smazání")
                    .color(ChatColor.DARK_RED)
                    .text(" particlu <<")
                    .color(colors.getSecondaryColorHEX())
                    .toString();
            int userId;
            int recordId;
            String style;
            String customStyle;
            String particle;
            String particleName;
            int selected;
            int color;
            int red;
            int green;
            int blue;
            int place;
            double posX;
            double posY;
            double posZ;
            String world;
            String server;
            double pitch;
            double yaw;
            while(rs.next()) {
                userId = rs.getInt(2);
                recordId = rs.getInt(3);
                style = rs.getString(4);
                customStyle = rs.getString(5);
                particle = rs.getString(6);
                particleName = rs.getString(7);
                selected = rs.getInt(8);
                color = rs.getInt(9);
                red = rs.getInt(10);
                green = rs.getInt(11);
                blue = rs.getInt(12);
                place = rs.getInt(13);
                posX = rs.getDouble(14);
                posY = rs.getDouble(15);
                posZ = rs.getDouble(16);
                world = rs.getString(17);
                server = rs.getString(18);
                pitch = rs.getDouble(19);
                yaw = rs.getDouble(20);
                JsonBuilder jb = new JsonBuilder();
                JsonBuilder hoverInfo = getMParticleInfoHover(userId, style, customStyle, particle, selected, color, red, green, blue, place, posX, posY, posZ, world, server, pitch, yaw);
                if(isRunning()) {
                    jb.text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, stopHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mparticle stop " + mode + " " + recordId)
                            .text("○")
                            .color(ChatColor.RED)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, stopHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mparticle stop " + mode + " " + recordId)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, stopHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mparticle stop " + mode + " " + recordId);
                } else {
                    jb.text("[")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, startHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mparticle start " + mode + " " + recordId)
                            .text("✔")
                            .color(ChatColor.GREEN)
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, startHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mparticle start " + mode + " " + recordId)
                            .text("]")
                            .color(colors.getSecondaryColorHEX())
                            .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, startHover, true)
                            .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mparticle start " + mode + " " + recordId);
                }
                jb.text(" ")
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mparticle delete " + mode + " " + recordId)
                        .text("✖")
                        .color(ChatColor.DARK_RED)
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mparticle delete " + mode + " " + recordId)
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, deleteHover, true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, "/mparticle delete " + mode + " " + recordId)
                        .text(" [")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, renameHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/mparticle manage " + mode + " " + recordId + " rename ")
                        .text("✎")
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, renameHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/mparticle manage " + mode + " " + recordId + " rename ")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, renameHover, true)
                        .clickEvent(JsonBuilder.ClickAction.SUGGEST_COMMAND, "/mparticle manage " + mode + " " + recordId + " rename ")
                        .text(" - ")
                        .color(colors.getSecondaryColorHEX())
                        .text("[")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                        .text(String.valueOf(recordId));
                if(selected == 1) {
                    jb.color(ChatColor.GREEN);
                } else {
                    jb.color(colors.getPrimaryColorHEX());
                }
                jb.hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true)
                        .text("] ")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true);
                if(particleName != null) {
                    jb.text(particleName);
                    if(selected == 1) {
                        jb.color(ChatColor.GREEN);
                    } else {
                        jb.color(colors.getPrimaryColorHEX());
                    }
                    jb.hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true);
                }
                jb.hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, hoverInfo.toString(), true);
                particleList.add(jb.getJsonSegments());
            }
            particleList.getList(page, null).toPlayer(player);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private JsonBuilder getMParticleInfoHover(int userId, String style, String customStyle, String particle, int selected, int color, int red, int green, int blue, int place, double posX, double posY, double posZ, String world, String server, double pitch, double yaw) {
        JsonBuilder jb = new JsonBuilder("Status: ")
                .color(colors.getSecondaryColorHEX());
        if(isRunning()) {
            jb.text("Zapnuto")
                    .color(ChatColor.GREEN);
        } else {
            jb.text("Vypnuto")
                    .color(ChatColor.DARK_RED);
        }
        jb.text("\nStyl: ")
                .color(colors.getSecondaryColorHEX())
                .text(style)
                .color(colors.getPrimaryColorHEX());
        if(customStyle != null) {
            jb.text("\nVlastní styl: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(customStyle)
                    .color(colors.getPrimaryColorHEX());
        }
        jb.text("\nParticle: ")
                .color(colors.getSecondaryColorHEX())
                .text(particle)
                .color(colors.getPrimaryColorHEX());
        if(color == 1) {
            jb.text("\nBarva: ")
                    .color(colors.getSecondaryColorHEX())
                    .text("\n - Červená: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.valueOf(red)).color(ChatColor.RED)
                    .text("\n - Zelená: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.valueOf(green)).color(ChatColor.GREEN)
                    .text("\n - Modrá: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.valueOf(blue)).color(ChatColor.BLUE);
        }
        if(place == 1) {
            jb.text("\nVytvořil/a: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(playerManager.getUsername(userId))
                    .color(colors.getPrimaryColorHEX())
                    .text("\nPozice: ")
                    .color(colors.getSecondaryColorHEX())
                    .text("\n - X: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.format("%.1f", posX))
                    .color(colors.getPrimaryColorHEX())
                    .text("\n - Y: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.format("%.1f", posY))
                    .color(colors.getPrimaryColorHEX())
                    .text("\n - Z: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.format("%.1f", posZ))
                    .color(colors.getPrimaryColorHEX())
                    .text("\n - Svět: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.valueOf(world))
                    .color(colors.getPrimaryColorHEX())
                    .text("\n - Server: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.valueOf(server))
                    .color(colors.getPrimaryColorHEX())
                    .text("\nPitch: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.format("%.3f", pitch))
                    .color(colors.getPrimaryColorHEX())
                    .text("\nYaw: ")
                    .color(colors.getSecondaryColorHEX())
                    .text(String.format("%.3f", yaw))
                    .color(colors.getPrimaryColorHEX());
        } else {
                jb.text("\nVybráno: ")
                    .color(colors.getSecondaryColorHEX());
            if(selected == 1) {
                jb.text("Ano")
                        .color(ChatColor.GREEN);
            } else {
                jb.text("Ne")
                        .color(ChatColor.DARK_RED);
            }
        }
        return jb;
    }

    private boolean isRunning() {
        //TODO kontrola, jestli běží
        return false;
    }
}
