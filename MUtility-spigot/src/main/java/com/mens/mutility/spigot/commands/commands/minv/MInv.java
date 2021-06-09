package com.mens.mutility.spigot.commands.commands.minv;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;
import com.mens.mutility.spigot.utils.PageList2;

public class MInv {
    private MUtilitySpigot plugin;

    public MInv(MUtilitySpigot plugin) {
        this.plugin = plugin;
    }

    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public CommandData create() {
        Prefix prefix = new Prefix();

        PageList2 helpList = new PageList2(10, prefix.getInventoryPrefix(true, false), "/minv");

        CommandData minv = new CommandData("minv", prefix.getInventoryPrefix(true, false),"mutility.inventory.help", CommandExecutors.BOTH, t -> {
            //TODO
            t.getSender().spigot().sendMessage(helpList.getList(1).create());
        });

        // 1. stupeň
        CommandData helpPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData uloz = new CommandData(ArgumentTypes.DEFAULT, "uloz", TabCompleterTypes.DEFAULT, "mutility.inventory.save", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Uloz");
        });
        CommandData nacti = new CommandData(ArgumentTypes.DEFAULT, "nacti", TabCompleterTypes.DEFAULT, "mutility.inventory.load", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Nacti");
        });
        CommandData smaz = new CommandData(ArgumentTypes.DEFAULT, "smaz", TabCompleterTypes.DEFAULT, "mutility.inventory.delete");
        CommandData spravuj = new CommandData(ArgumentTypes.DEFAULT, "spravuj", TabCompleterTypes.DEFAULT, "mutility.inventory.manage", CommandExecutors.PLAYER, t ->  {
            //TODO
            System.out.println("Spravuj");
        });

        // 2. stupeň
        CommandData helpPageID = new CommandData(ArgumentTypes.POSITIVE_INTEGER,  TabCompleterTypes.NONE, "mutility.inventory.help", CommandExecutors.BOTH, (t) -> {
            t.getSender().spigot().sendMessage(helpList.getList(Integer.parseInt(t.getArgs()[1])).create());
        });
        CommandData nazev = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název inventáře >]", "mutility.inventory.save", CommandExecutors.PLAYER, t ->  {
            //TODO
            System.out.println("Název");
        });
        CommandData loadPage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);
        CommandData loadID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.inventory.load", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Load ID");
        });
        CommandData deleteID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.inventory.delete", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Delete ID");
        });
        CommandData manageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.inventory.delete", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Manage ID");
        });
        CommandData managePage = new CommandData(ArgumentTypes.DEFAULT, "page", TabCompleterTypes.NONE);

        // 3. stupeň
        CommandData loadPageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.inventory.load", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Load page ID");
        });
        CommandData setName = new CommandData(ArgumentTypes.DEFAULT, "setname", TabCompleterTypes.NONE);
        CommandData setInv = new CommandData(ArgumentTypes.DEFAULT, "setinv", TabCompleterTypes.NONE);
        CommandData managePageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.inventory.manage", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Manage page ID");
        });

        // 4. stupeň
        CommandData setNameName = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název inventáře >]", "mutility.inventory.manage", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Manage page ID");
        });

        minv.link(helpPage);
        minv.link(uloz);
        minv.link(nacti);
        minv.link(smaz);
        minv.link(spravuj);

        helpPage.link(helpPageID);
        uloz.link(nazev);
        nacti.link(loadPage);
        nacti.link(loadID);
        smaz.link(deleteID);
        spravuj.link(manageID);
        spravuj.link(managePage);

        loadPage.link(loadPageID);
        manageID.link(setName);
        manageID.link(setInv);
        managePage.link(managePageID);

        setName.link(setNameName);

        return minv;
    }
}
