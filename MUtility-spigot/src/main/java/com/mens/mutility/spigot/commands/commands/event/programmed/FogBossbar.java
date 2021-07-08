package com.mens.mutility.spigot.commands.commands.event.programmed;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.utils.Checker;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class FogBossbar {
    private final BossBar bar;
    private final Checker checker;
    private boolean enabled;

    public FogBossbar(MUtilitySpigot plugin) {
        bar = Bukkit.createBossBar("", BarColor.PURPLE, BarStyle.SOLID);
        checker = new Checker(plugin);
        enabled = true;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void set(String event) {
        if(enabled) {
            bar.setProgress(0);
            bar.addFlag(BarFlag.CREATE_FOG);
            Bukkit.getOnlinePlayers().forEach(player -> {
                if(checker.chechEventLocation(player.getLocation(), event)) {
                    if(bar.getPlayers().stream().noneMatch(barPlayer -> player.getName().equals(barPlayer.getName()))) {
                        bar.addPlayer(player);
                    }
                } else {
                    if(bar.getPlayers().stream().anyMatch(barPlayer -> player.getName().equals(barPlayer.getName()))) {
                        bar.removePlayer(player);
                    }
                }
            });
        } else {
            bar.removeAll();
        }
    }
}
