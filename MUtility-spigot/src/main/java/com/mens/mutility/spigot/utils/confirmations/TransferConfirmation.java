package com.mens.mutility.spigot.utils.confirmations;

import org.bukkit.entity.Player;

public class TransferConfirmation extends Confirmation {
    private final String targetName;
    private final int count;

    public TransferConfirmation(int id, Player player, String command, String targetName, int count) {
        super(id, player, command);
        this.targetName = targetName;
        this.count = count;
    }

    public String getTargetName() {
        return targetName;
    }

    public int getCount() {
        return count;
    }
}
