package com.mens.mutility.spigot.commands.commands.mparticle;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.CommandHelp;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.messages.MessageChannel;
import com.mens.mutility.spigot.utils.PageList;
import org.bukkit.entity.Player;

public class MParticle extends CommandHelp {
    private final MUtilitySpigot plugin;
    private PageList helpList;
    private final MessageChannel messageChannel;
    private final Prefix prefix;
    private final PluginColors colors;

    public MParticle(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        helpList = new PageList(10, prefix.getMParticlePrefix(true, true).replace("]", " - nápověda]"), "/mparticle");
        messageChannel = new MessageChannel();
        colors = new PluginColors();
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
        final CommandData show = new CommandData(ArgumentTypes.DEFAULT, "zobraz", TabCompleterTypes.DEFAULT, "mutility.mparticle.list", CommandExecutors.PLAYER, t -> {
            System.out.println("Zobraz");
            //TODO
        });
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
        final CommandData showPlayer = new CommandData(ArgumentTypes.DEFAULT, "hrac", TabCompleterTypes.DEFAULT, "mutility.mparticle.list", CommandExecutors.PLAYER, t -> {
            System.out.println("Zobraz player");
            //TODO
        });
        final CommandData showPlace = new CommandData(ArgumentTypes.DEFAULT, "pozice", TabCompleterTypes.DEFAULT, "mutility.mparticle.list.place", CommandExecutors.PLAYER, t -> {
            System.out.println("Zobraz place");
            //TODO
        });

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
        final CommandData deleteOnPlayerID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.mparticle.stop", CommandExecutors.PLAYER, (t) -> {
            System.out.println("Delete player id");
            //TODO
        });
        final CommandData deleteOnPlaceID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.mparticle.stop.place", CommandExecutors.PLAYER, (t) -> {
            System.out.println("Delete place id");
            //TODO
        });
        final CommandData manageOnPlayerID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE);
        final CommandData manageOnPlaceID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE);
        final CommandData showPlayerPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData showPlacePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);

        // 4. stupeň
        final CommandData createOnPlayerStyleRedstone = new CommandData(ArgumentTypes.DEFAULT, "redstone", TabCompleterTypes.DEFAULT, "mutility.mparticle.create");
        final CommandData createOnPlayerStyleParticle = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLES, "mutility.mparticle.create", CommandExecutors.PLAYER, t -> {
            System.out.println("on player particle");
            //TODO
        });
        final CommandData createOnPlayerCustomStyle = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.CUSTOM_STYLES, "mutility.mparticle.create");
        final CommandData createOnPlaceStyleRedstone = new CommandData(ArgumentTypes.DEFAULT, "redstone", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyleParticle = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLES, "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyle = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.CUSTOM_STYLES, "mutility.mparticle.create.place");
        final CommandData manageOnPlayerIDRename = new CommandData(ArgumentTypes.DEFAULT, "rename",  TabCompleterTypes.NONE);
        final CommandData manageOnPlaceIDRename = new CommandData(ArgumentTypes.DEFAULT,  "rename", TabCompleterTypes.NONE);
        final CommandData showPlayerPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.mparticle.list", CommandExecutors.PLAYER, (t) -> {
            System.out.println("Show player ID page id");
            //TODO
        });
        final CommandData showPlacePageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.mparticle.list.place", CommandExecutors.PLAYER, (t) -> {
            System.out.println("Show place ID page id");
            //TODO
        });

        // 5. stupeň
        final CommandData createOnPlayerStyleRedstoneColor = new CommandData(ArgumentTypes.DEFAULT, "color", TabCompleterTypes.DEFAULT, "mutility.mparticle.create");
        final CommandData createOnPlayerStyleRedstoneRGB = new CommandData(ArgumentTypes.DEFAULT, "rgb", TabCompleterTypes.DEFAULT, "mutility.mparticle.create");
        final CommandData createOnPlayerCustomStyleRedstone = new CommandData(ArgumentTypes.DEFAULT, "redstone", TabCompleterTypes.DEFAULT, "mutility.mparticle.create");
        final CommandData createOnPlayerCustomStyleParticle = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLES, "mutility.mparticle.create", CommandExecutors.PLAYER, t -> {
            System.out.println("on player custom particle");
            //TODO
        });
        final CommandData createOnPlaceStyleRedstoneColor = new CommandData(ArgumentTypes.DEFAULT, "color", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyleRedstoneRGB = new CommandData(ArgumentTypes.DEFAULT, "rgb", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstone = new CommandData(ArgumentTypes.DEFAULT, "redstone", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleParticle = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLES, "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyleParticleHere = new CommandData(ArgumentTypes.DEFAULT, "zde", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            System.out.println("on place particle here");
            //TODO
        });
        final CommandData createOnPlaceStyleParticleX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.mparticle.create.place");
        final CommandData manageOnPlayerIDRenameName = new CommandData(ArgumentTypes.STRINGINF,  TabCompleterTypes.NONE, "mutility.mparticle.manage", CommandExecutors.PLAYER, t -> {
            System.out.println("Manage player rename name");
            //TODO
        });
        final CommandData manageOnPlaceIDRenameName = new CommandData(ArgumentTypes.STRINGINF,  TabCompleterTypes.NONE, "mutility.mparticle.manage.place", CommandExecutors.PLAYER, t -> {
            System.out.println("Manage place rename name");
            //TODO
        });

        // 6. stupeň
        final CommandData createOnPlayerStyleRedstoneColorFinal = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLE_COLORS, "mutility.mparticle.create", CommandExecutors.PLAYER, t -> {
            System.out.println("on player redstone color");
            //TODO
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
            System.out.println("on place custom particle here");
            //TODO
        });
        final CommandData createOnPlaceCustomStyleParticleX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.mparticle.create.place");

        // 7. stupeň
        final CommandData createOnPlayerStyleRedstoneColorRG = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Zelená (0-255)>]", "mutility.mparticle.create");
        final CommandData createOnPlayerCustomStyleRedstoneColorR = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Červená (0-255)>]", "mutility.mparticle.create");
        final CommandData createOnPlayerCustomStyleRedstoneColorFinal = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLE_COLORS, "mutility.mparticle.create", CommandExecutors.PLAYER, t -> {
            System.out.println("on player custom redstone color");
            //TODO
        });
        final CommandData createOnPlaceStyleRedstoneColorRG = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Zelená (0-255)>]", "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstoneColorR = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Červená (0-255)>]", "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstoneColorFinal = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.PARTICLE_COLORS, "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyleParticleZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            System.out.println("on place particle z");
            //TODO
        });
        final CommandData createOnPlaceCustomStyleParticleY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyleRedstoneColorFinalHere = new CommandData(ArgumentTypes.DEFAULT, "zde", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            System.out.println("on place recstone color here");
            //TODO
        });
        final CommandData createOnPlaceStyleRedstoneColorFinalX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstoneColorFinalHere = new CommandData(ArgumentTypes.DEFAULT, "zde", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            System.out.println("on place custom redstone color here");
            //TODO
        });
        final CommandData createOnPlaceCustomStyleRedstoneColorFinalX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.mparticle.create.place");

        // 8. stupeň
        final CommandData createOnPlayerStyleRedstoneColorRGB = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Modrá (0-255)>]", "mutility.mparticle.create", CommandExecutors.PLAYER, t -> {
            System.out.println("on player redstone rgb");
            //TODO
        });
        final CommandData createOnPlayerCustomStyleRedstoneColorRG = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Zelená (0-255)>]", "mutility.mparticle.create");
        final CommandData createOnPlaceStyleRedstoneColorRGB = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Modrá (0-255)>]", "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstoneColorRG = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Zelená (0-255)>]", "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleParticleZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            System.out.println("on place custom particle z");
            //TODO
        });
        final CommandData createOnPlaceStyleRedstoneColorFinalY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstoneColorFinalY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.mparticle.create.place");

        // 9. stupeň
        final CommandData createOnPlayerCustomStyleRedstoneColorRGB = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Modrá (0-255)>]", "mutility.mparticle.create", CommandExecutors.PLAYER, t -> {
            System.out.println("on player custom redstone rgb");
            //TODO
        });
        final CommandData createOnPlaceCustomStyleRedstoneColorRGB = new CommandData(ArgumentTypes.POSITIVE_INTEGER, TabCompleterTypes.CUSTOM, "[<Modrá (0-255)>]", "mutility.mparticle.create.place");
        final CommandData createOnPlaceStyleRedstoneColorFinalZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            System.out.println("on place recstone color z");
            //TODO
        });
        final CommandData createOnPlaceCustomStyleRedstoneColorFinalZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            System.out.println("on place custom redstone color z");
            //TODO
        });
        final CommandData createOnPlaceStyleRedstoneColorRGBHere = new CommandData(ArgumentTypes.DEFAULT, "zde", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            System.out.println("on place redstone rgb here");
            //TODO
        });
        final CommandData createOnPlaceStyleRedstoneColorRGBX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.mparticle.create.place");

        // 10. stupeň
        final CommandData createOnPlaceStyleRedstoneColorRGBY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.mparticle.create.place");
        final CommandData createOnPlaceCustomStyleRedstoneColorRGBHere = new CommandData(ArgumentTypes.DEFAULT, "zde", TabCompleterTypes.DEFAULT, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            System.out.println("on place custom redstone rgb here");
            //TODO
        });
        final CommandData createOnPlaceCustomStyleRedstoneColorRGBX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.mparticle.create.place");

        // 11. stupeň
        final CommandData createOnPlaceStyleRedstoneColorRGBZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            System.out.println("on place recstone rgb z");
            //TODO
        });
        final CommandData createOnPlaceCustomStyleRedstoneColorRGBY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.mparticle.create.place");

        // 12. stupeň
        final CommandData createOnPlaceCustomStyleRedstoneColorRGBZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.mparticle.create.place", CommandExecutors.PLAYER, t -> {
            System.out.println("on place custom redstone rgb z");
            //TODO
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
}
