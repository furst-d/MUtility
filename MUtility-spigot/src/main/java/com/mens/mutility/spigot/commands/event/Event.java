package com.mens.mutility.spigot.commands.event;

import com.mens.mutility.spigot.commands.system.CommandData;
import com.mens.mutility.spigot.commands.system.enums.ArgumentTypes;
import com.mens.mutility.spigot.commands.system.enums.CommandExecutors;
import com.mens.mutility.spigot.commands.system.enums.TabCompleterTypes;

public class Event {
    /**
     * Metoda slouzici k definovani a sestaveni prikazu a jeho parametru v ramci vlastniho prikazovaho systemu
     */
    public static CommandData create() {
        CommandData event = new CommandData("event");

        // 1. stupeň
        CommandData run = new CommandData(ArgumentTypes.DEFAULT, "run", TabCompleterTypes.DEFAULT, "mutility.eventy.otazky.create");
        CommandData create = new CommandData(ArgumentTypes.DEFAULT, "create", TabCompleterTypes.DEFAULT, "mutility.eventy.create");
        CommandData manage = new CommandData(ArgumentTypes.DEFAULT, "manage", TabCompleterTypes.DEFAULT, "mutility.eventy.manage", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Manage");
        });
        CommandData message = new CommandData(ArgumentTypes.DEFAULT, "message", TabCompleterTypes.NONE);
        CommandData fakeMessage = new CommandData(ArgumentTypes.DEFAULT, "fakemessage", TabCompleterTypes.NONE);
        CommandData tp = new CommandData(ArgumentTypes.DEFAULT, "tp", TabCompleterTypes.NONE);
        CommandData delete = new CommandData(ArgumentTypes.DEFAULT, "delete", TabCompleterTypes.NONE);

        // 2. stupeň
        CommandData otazky = new CommandData(ArgumentTypes.DEFAULT, "otazky", TabCompleterTypes.DEFAULT, "mutility.eventy.otazky.create");
        CommandData hledacka = new CommandData(ArgumentTypes.DEFAULT, "hledacka", TabCompleterTypes.DEFAULT, "mutility.eventy.hledacka.create");
        CommandData hrbitov = new CommandData(ArgumentTypes.DEFAULT, "hrbitov", TabCompleterTypes.DEFAULT, "mutility.eventy.hrbitov.create");
        CommandData nazev = new CommandData(ArgumentTypes.STRINGINF, TabCompleterTypes.CUSTOM, "[< Název >]", "mutility.eventy.create", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Název");
        });
        CommandData manageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.manage", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Manage ID");
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
        CommandData setBlockHledacka = new CommandData(ArgumentTypes.DEFAULT, "setblock", TabCompleterTypes.DEFAULT, "mutility.eventy.hledacka.create");
        CommandData setFogHledacka = new CommandData(ArgumentTypes.DEFAULT, "setfog", TabCompleterTypes.DEFAULT, "mutility.eventy.hledacka.create");
        CommandData startHrbitov = new CommandData(ArgumentTypes.DEFAULT, "start", TabCompleterTypes.DEFAULT, "mutility.eventy.hrbitov.create", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Start hrbitov");
        });
        CommandData stopHrbitov = new CommandData(ArgumentTypes.DEFAULT, "stop", TabCompleterTypes.DEFAULT, "mutility.eventy.hrbitov.create", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Stop hrbitov");
        });
        CommandData setBlockHrbitov = new CommandData(ArgumentTypes.DEFAULT, "setblock", TabCompleterTypes.DEFAULT, "mutility.eventy.hrbitov.create");
        CommandData setFogHrbitov = new CommandData(ArgumentTypes.DEFAULT, "setfog", TabCompleterTypes.DEFAULT, "mutility.eventy.hrbitov.create");
        CommandData setTP = new CommandData(ArgumentTypes.DEFAULT, "settp", TabCompleterTypes.NONE);
        CommandData setRequired = new CommandData(ArgumentTypes.DEFAULT, "setrequired", TabCompleterTypes.NONE);
        CommandData setForbidden = new CommandData(ArgumentTypes.DEFAULT, "setforbidden", TabCompleterTypes.NONE);
        CommandData setObjective = new CommandData(ArgumentTypes.DEFAULT, "setobjective", TabCompleterTypes.NONE);
        CommandData setNote = new CommandData(ArgumentTypes.DEFAULT, "setnote", TabCompleterTypes.NONE);
        CommandData pageID = new CommandData(ArgumentTypes.INTEGER, TabCompleterTypes.NONE, "mutility.eventy.manage", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Page ID");
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
        CommandData fogHledackaTrue = new CommandData(ArgumentTypes.DEFAULT,"true", TabCompleterTypes.DEFAULT, "mutility.eventy.hledacka.create", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Hledacka fog true");
        });
        CommandData fogHledackaFalse = new CommandData(ArgumentTypes.DEFAULT,"false", TabCompleterTypes.DEFAULT, "mutility.eventy.hledacka.create", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Hledacka fog false");
        });
        CommandData blockHrbitov = new CommandData(ArgumentTypes.STRING, TabCompleterTypes.BLOCKS, "mutility.eventy.hrbitov.create", CommandExecutors.PLAYER, t -> {
            //TODO
            System.out.println("Hrbitov block");
        });
        CommandData fogHrbitovTrue = new CommandData(ArgumentTypes.DEFAULT,"true", TabCompleterTypes.DEFAULT, "mutility.eventy.hrbitov.create", CommandExecutors.BOTH, t -> {
            //TODO
            System.out.println("Hrbitov fog true");
        });
        CommandData fogHrbitovFalse = new CommandData(ArgumentTypes.DEFAULT,"false", TabCompleterTypes.DEFAULT, "mutility.eventy.hrbitov.create", CommandExecutors.BOTH, t -> {
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

        event.link(run);
        event.link(create);
        event.link(manage);
        event.link(message);
        event.link(fakeMessage);
        event.link(tp);
        event.link(delete);

        run.link(otazky);
        run.link(hledacka);
        run.link(hrbitov);
        create.link(nazev);
        manage.link(manageID);
        manage.link(page);
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
