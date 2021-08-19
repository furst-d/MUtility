package com.mens.mutility.spigot.commands.commands.cc;

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
import com.mens.mutility.spigot.utils.PlayerManager;
import com.mens.mutility.spigot.utils.confirmations.Confirmation;
import com.mens.mutility.spigot.utils.CraftCoinManager;
import com.mens.mutility.spigot.utils.PageList;
import com.mens.mutility.spigot.utils.confirmations.TransferConfirmation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CC extends CommandHelp {
    private final MUtilitySpigot plugin;
    private PageList helpList;
    private final Prefix prefix;
    private final PluginColors colors;
    private final List<TransferConfirmation> transferConfirmationList;
    private final CraftCoinManager ccManager;
    private final Errors errors;

    public CC(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        helpList = new PageList(10, prefix.getCCPrefix(true, true).replace("]", " - nápověda]"), "/cc");
        colors = new PluginColors();
        transferConfirmationList = new ArrayList<>();
        ccManager = new CraftCoinManager();
        errors = new Errors();
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public final CommandData create() {
        final CommandData cc = new CommandData("cc", "Craft-Coin", "mutility.cc.status", CommandExecutors.PLAYER, t -> t.getSender().sendMessage(prefix.getCCPrefix(true, false) + "Momentálně vlastníš " + colors.getPrimaryColor() + ccManager.getCC(t.getSender().getName()) + colors.getSecondaryColor() + " Craft-Coinů"));

        // 1. stupeň
        final CommandData help = new CommandData(ArgumentTypes.DEFAULT, "help", TabCompleterTypes.DEFAULT, "mutility.cc.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(1, null).toPlayer((Player) t.getSender());
        });
        final CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData transfer = new CommandData(ArgumentTypes.DEFAULT, "posli", TabCompleterTypes.DEFAULT, "mutility.cc.transfer");

        // 2. stupeň
        final CommandData helpHelpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        final CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.cc.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[1]), null).toPlayer((Player) t.getSender());
        });
        final CommandData transferName = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.GLOBAL_ONLINE_PLAYERS, "mutility.cc.transfer");
        final CommandData transferConfirm = new CommandData(ArgumentTypes.DEFAULT, "confirm", TabCompleterTypes.NONE);

        // 3. stupeň
        final CommandData helpHelpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.cc.help", CommandExecutors.PLAYER, (t) -> {
            helpList = getCommandHelp(plugin, t.getSender(), helpList);
            helpList.getList(Integer.parseInt(t.getArgs()[2]), null).toPlayer((Player) t.getSender());
        });
        final CommandData transferNameCount = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.CUSTOM, "[< Počet CC >]", "mutility.cc.transfer", CommandExecutors.PLAYER, (t) -> {
            int MIN_NUMBER = 200;
            String targetName = t.getArgs()[1];
            int count = Integer.parseInt(t.getArgs()[2]);
            if(!t.getSender().getName().equalsIgnoreCase(targetName)) {
                if(count >= MIN_NUMBER) {
                    if(count <= ccManager.getCC(t.getSender().getName())) {
                        int id = (int)(Math.random() * 10000);
                        TransferConfirmation transferConfirmation = new TransferConfirmation(id, (Player) t.getSender(), "/cc posli confirm", targetName, count);
                        transferConfirmation.setMessage(new JsonBuilder()
                                .addJsonSegment(prefix.getCCPrefix(true, true))
                                .text(": Opravdu si přejete poslat hráči ")
                                .color(colors.getSecondaryColorHEX())
                                .text(targetName + " " + count)
                                .color(colors.getPrimaryColorHEX())
                                .text(" Craft-Coinů? Tato akce je nevratná!")
                                .color(colors.getSecondaryColorHEX()));
                        if(transferConfirmationList.stream().noneMatch(x -> (x.getId() == id
                                && x.getPlayer().getName().equals(t.getSender().getName())
                                && !x.isFinished()))) {
                            transferConfirmation.startTimer();
                            transferConfirmationList.add(transferConfirmation);
                        } else {
                            t.getSender().sendMessage(prefix.getCCPrefix(true, false)
                                    + colors.getSecondaryColor() + "Žádost o potvrzení již byla vytvořena!");
                        }
                    } else {
                        t.getSender().sendMessage(prefix.getCCPrefix(true, false) + "Nedostatek Craft-Coinů");
                    }
                } else {
                    t.getSender().sendMessage(prefix.getCCPrefix(true, false) + "Minimální povolená částka pro odeslání je " + colors.getPrimaryColor() + MIN_NUMBER + colors.getSecondaryColor() + " Craft-Coinů");
                }
            } else {
                t.getSender().sendMessage(prefix.getCCPrefix(true, false) + "Nelze poslat Craft-Coiny sám sobě!");
            }
        });
        final CommandData transferConfirmId = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.cc.transfer", CommandExecutors.PLAYER, (t) -> {
            int id = Integer.parseInt(t.getArgs()[2]);
            boolean valid = false;
            for (int i = transferConfirmationList.size() - 1; i >= 0; i--) {
                if(transferConfirmationList.get(i).getId() == id
                        && transferConfirmationList.get(i).getPlayer().getName().equals(t.getSender().getName())) {
                    if(!transferConfirmationList.get(i).isFinished()) {
                        valid = true;
                        transferConfirmationList.get(i).setFinished(true);
                        int count = transferConfirmationList.get(i).getCount();
                        String targetName = transferConfirmationList.get(i).getTargetName();
                        int targetId = new PlayerManager().getUserId(targetName);
                        if(targetId != 0) {
                            ccManager.removeCC(count, t.getSender().getName(), 14);
                            ccManager.addCC(count, targetName, 14);
                            t.getSender().sendMessage(prefix.getCCPrefix(true, false) + colors.getPrimaryColor() + count + colors.getSecondaryColor() + " Craft-Coinů bylo odesláno hráči " + colors.getPrimaryColor() + targetName + colors.getSecondaryColor() + "!");
                            Player target = Bukkit.getPlayer(targetName);
                            if(target != null) {
                                target.sendMessage(prefix.getCCPrefix(true, false) + "Obdržel jsi " + colors.getPrimaryColor() + count + colors.getSecondaryColor() + " Craft-Coinů od hráče " + colors.getPrimaryColor() + t.getSender().getName() + colors.getSecondaryColor() + "!");
                            }
                        } else {
                            t.getSender().sendMessage(prefix.getCCPrefix(true, false) + errors.errWrongArgumentPlayer(targetName,true, false));
                        }
                        break;
                    }
                }
            }
            if(!valid) {
                t.getSender().sendMessage(prefix.getNavrhyPrefix(true, false)
                        + "Potvrzení o smazání návrhu není platné!");
            }
            transferConfirmationList.removeIf(Confirmation::isFinished);
        });

        cc.setDescription("Systém pro správu Craft-Coinů");

        help.setDescription("Nápověda k příkazu");
        help.setSyntax("/cc " + help.getSubcommand());

        transfer.setDescription("Posílání Craft-Coinů jiným hráčům");
        transfer.setSyntax("/cc " + transfer.getSubcommand() + " [<Jméno hráče>] [<Počet CC>]");

        cc.link(help);
        cc.link(helpPage);
        cc.link(transfer);

        help.link(helpHelpPage);
        helpPage.link(helpPageID);
        transfer.link(transferConfirm);
        transfer.link(transferName);

        helpHelpPage.link(helpHelpPageID);
        transferName.link(transferNameCount);
        transferConfirm.link(transferConfirmId);

        return cc;
    }
}
