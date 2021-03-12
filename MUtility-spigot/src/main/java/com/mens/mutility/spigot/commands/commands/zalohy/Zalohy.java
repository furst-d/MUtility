package com.mens.mutility.spigot.commands.commands.zalohy;

import com.mens.mutility.spigot.chat.PluginColors;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;

public class Zalohy {

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public static CommandData create() {
        PluginColors colors = new PluginColors();
        CommandData zalohy = new CommandData("zalohy", colors.getSecondaryColor() + "["
                + colors.getPrimaryColor() + "Zálohy"
                + colors.getSecondaryColor() + "] "
                + colors.getSecondaryColor(),"mutility.zalohy.help", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Zalohy");
        });

        // 1. stupeň
        CommandData pridej = new CommandData(ArgumentTypes.DEFAULT, "pridej", TabCompleterTypes.DEFAULT, "mutility.zalohy.create");
        CommandData zobraz = new CommandData(ArgumentTypes.DEFAULT, "zobraz", TabCompleterTypes.DEFAULT, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Zobraz");
        });
        CommandData manage = new CommandData(ArgumentTypes.DEFAULT, "manage", TabCompleterTypes.NONE);
        CommandData delete = new CommandData(ArgumentTypes.DEFAULT, "delete", TabCompleterTypes.NONE);
        CommandData admin = new CommandData(ArgumentTypes.DEFAULT, "admin", TabCompleterTypes.DEFAULT, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Admin");
        });
        CommandData tp = new CommandData(ArgumentTypes.DEFAULT, "tp", TabCompleterTypes.NONE);
        CommandData complete = new CommandData(ArgumentTypes.DEFAULT, "complete", TabCompleterTypes.NONE);
        CommandData reject = new CommandData(ArgumentTypes.DEFAULT, "reject", TabCompleterTypes.NONE);
        CommandData returnZaloha = new CommandData(ArgumentTypes.DEFAULT, "return", TabCompleterTypes.NONE);

        // 2. stupeň
        CommandData pridejX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.zalohy.create");
        CommandData zobrazPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData manageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Manage ID");
        });
        CommandData deleteID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.delete", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Delete ID");
        });
        CommandData adminPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData adminName = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.ONLINE_PLAYERS, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Admin name");
        });
        CommandData tpID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("tp ID");
        });
        CommandData completeID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Complete ID");
        });
        CommandData rejectID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE);
        CommandData returnID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Return ID");
        });

        // 3. stupeň
        CommandData pridejY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.zalohy.create");
        CommandData zobrazPageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.manage");
        CommandData setX = new CommandData(ArgumentTypes.DEFAULT, "setx", TabCompleterTypes.NONE);
        CommandData setY = new CommandData(ArgumentTypes.DEFAULT, "sety", TabCompleterTypes.NONE);
        CommandData setZ = new CommandData(ArgumentTypes.DEFAULT, "setz", TabCompleterTypes.NONE);
        CommandData setNote = new CommandData(ArgumentTypes.DEFAULT, "setnote", TabCompleterTypes.NONE);
        CommandData setName = new CommandData(ArgumentTypes.DEFAULT, "setname", TabCompleterTypes.NONE);
        CommandData adminPageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Admin page ID");
        });
        CommandData adminNamePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData rejectReason = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Důvod zamítnutí >]", "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Reject reason");
        });

        // 4. stupeň
        CommandData pridejZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.zalohy.create");
        CommandData setXX = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSX, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("SetXX");
        });
        CommandData setYY = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSY, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("SetYY");
        });
        CommandData setZZ = new CommandData(ArgumentTypes.FLOAT, TabCompleterTypes.POSZ, "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("SetZZ");
        });
        CommandData setNoteNote = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Poznámka >]", "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("SetNoteNote");
        });
        CommandData setNameName = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název stavby/staveb >]", "mutility.zalohy.manage", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("SetNameName");
        });
        CommandData adminNamePageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.zalohy.admin", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("AdminNamePageID");
        });

        // 5. stupeň
        CommandData pridejName = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název stavby/staveb >]", "mutility.zalohy.create", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("PridejName");
        });

        zalohy.link(pridej);
        zalohy.link(zobraz);
        zalohy.link(manage);
        zalohy.link(delete);
        zalohy.link(admin);
        zalohy.link(tp);
        zalohy.link(complete);
        zalohy.link(reject);
        zalohy.link(returnZaloha);

        pridej.link(pridejX);
        zobraz.link(zobrazPage);
        manage.link(manageID);
        delete.link(deleteID);
        admin.link(adminPage);
        admin.link(adminName);
        tp.link(tpID);
        complete.link(completeID);
        reject.link(rejectID);
        returnZaloha.link(returnID);

        pridejX.link(pridejY);
        zobrazPage.link(zobrazPageID);
        manageID.link(setX);
        manageID.link(setY);
        manageID.link(setZ);
        manageID.link(setNote);
        manageID.link(setName);
        adminPage.link(adminPageID);
        adminName.link(adminNamePage);
        rejectID.link(rejectReason);

        pridejY.link(pridejZ);
        setX.link(setXX);
        setY.link(setYY);
        setZ.link(setZZ);
        setNote.link(setNoteNote);
        setName.link(setNameName);
        adminNamePage.link(adminNamePageID);

        pridejZ.link(pridejName);

        return zalohy;
    }
}
