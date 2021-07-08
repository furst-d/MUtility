package com.mens.mutility.spigot.commands.commands.event.programmed;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.utils.Timer;

public class EventFinder {
    private final FogBossbar bossbar;
    private final Timer timer;
    private boolean active;

    public EventFinder(MUtilitySpigot plugin, String event) {
        bossbar = new FogBossbar(plugin);
        timer = new Timer();
        timer.setOnRunning((sec, tt) -> {
            if(active) {
                if(sec % 15 == 0) {
                    bossbar.set(event);
                }
            } else {
                tt.cancel();
            }
        });
    }

    public FogBossbar getBossbar() {
        return bossbar;
    }

    public void start(int timeInSec) {
        active = true;
        timer.startTimer(timeInSec);
    }

    public void stop() {
        active = false;
    }
}
