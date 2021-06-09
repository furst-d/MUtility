package com.mens.mutility.spigot.portal;

import com.mens.mutility.spigot.MUtilitySpigot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PortalRequestChecker {
    public void checkRequests() {
        if(!Bukkit.getServer().getOnlinePlayers().isEmpty()) {
            for(Player player: Bukkit.getServer().getOnlinePlayers()) {
                if(!MUtilitySpigot.portalQueue.isEmpty()) {
                    for (PortalManager pm : MUtilitySpigot.portalQueue) {
                        if(player.getName().equals(pm.getPlayer().getName())) {
                            player.teleport(pm.getPortalLocation());
                            MUtilitySpigot.portalQueue.remove(pm);
                            break;
                        }
                    }
                }
            }
        }
    }
}
