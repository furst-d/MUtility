package com.mens.mutility.spigot.commands.commands.mparticle.styles;

import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.commands.mparticle.ParticleInfo;
import com.mens.mutility.spigot.commands.commands.mparticle.ParticlePlace;
import com.mens.mutility.spigot.commands.commands.mparticle.ParticlePlayer;
import com.mens.mutility.spigot.utils.Checker;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.TimerTask;

@SuppressWarnings("unused")
public class Wave {
    private Location loc;
    private final Checker checker;
    private final Prefix prefix;

    public Wave() {
        checker = new Checker();
        prefix = new Prefix();
    }

    public void run(ParticleInfo info) {
        Player player = info.getPlayer();

        new java.util.Timer().schedule(new TimerTask() {
            double t = 0.0D;
            final boolean isPlace = info.getLocation() != null;

            @Override
            public void run() {
                if(!isPlace) {
                    loc = player.getLocation();
                    if(!ParticlePlayer.containsPlayerAndRecord(player, info.getRecordID())) {
                        this.cancel();
                    }
                    if(checker.checkVanish(player)) {
                        this.cancel();
                        player.sendMessage(prefix.getMParticlePrefix(true, false) + "Particle nelze používat ve vanishi!");
                        ParticlePlayer.unregisterPlayer(player);
                    }
                } else {
                    loc = info.getLocation();
                    if(!ParticlePlace.containsId(info.getId())) {
                        this.cancel();
                    }
                }
                this.t += 0.3141592653589793D;
                for(double theta = 0.0D; theta <= 6.283185307179586D; theta += 0.09817477042468103D) {
                    double x = this.t * Math.cos(theta);
                    double y = Math.exp(-0.1D * this.t) * Math.sin(this.t) + 1.5D;
                    double z = this.t * Math.sin(theta);
                    loc.add(x, y, z);
                    Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.FIREWORKS_SPARK, loc, 0, 0.0D, 0.0D, 0.0D, 1.0D);
                    loc.subtract(x, y, z);
                    theta += 0.04908738521234052D;
                    x = this.t * Math.cos(theta);
                    y = Math.exp(-0.1D * this.t) * Math.sin(this.t) + 1.5D;
                    z = this.t * Math.sin(theta);
                    loc.add(x, y, z);
                    loc.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 0, 0.0D, 0.0D, 0.0D, 1.0D);
                    loc.subtract(x, y, z);
                    if(t > 80) {
                        t = 0;
                    }
                }
            }
        },0,50);
    }
}
