package com.mens.mutility.spigot.commands.commands.mparticle.styles;

import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.commands.mparticle.ParticleInfo;
import com.mens.mutility.spigot.commands.commands.mparticle.ParticlePlace;
import com.mens.mutility.spigot.commands.commands.mparticle.ParticlePlayer;
import com.mens.mutility.spigot.commands.commands.mparticle.RGB;
import com.mens.mutility.spigot.utils.Checker;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Random;
import java.util.TimerTask;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

@SuppressWarnings("unused")
public class Ring {
    private Location loc;
    private final Random rd;
    private final Checker checker;
    private final Prefix prefix;

    public Ring() {
        rd = new Random();
        checker = new Checker();
        prefix = new Prefix();
    }

    public void run(ParticleInfo info) {
        Player player = info.getPlayer();
        Particle particle = info.getParticle().getParticle();
        BlockData data = info.getParticle().getData();
        RGB color = info.getColor();

        new java.util.Timer().schedule(new TimerTask() {
            double t = 0.0D;
            double r = 0.75D;
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
                this.t += 0.2D;
                double x = this.r * StrictMath.sin(this.t);
                double y = 2.0D;
                double z = this.r * Math.cos(this.t);
                loc.add(x, y, z);
                if(info.getParticle().getName().equalsIgnoreCase("redstone")) {
                    Particle.DustOptions dustOptions;
                    if((color.getRed() < 0)) {
                        dustOptions = new Particle.DustOptions(Color.fromRGB(rd.nextInt(255), rd.nextInt(255), rd.nextInt(255)), 1);
                    } else {
                        dustOptions = new Particle.DustOptions(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()), 1);
                    }
                    Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.REDSTONE, loc, 0, 0.0D, 0.0D, 0.0D, 1.0D, dustOptions);
                } else {
                    Objects.requireNonNull(loc.getWorld()).spawnParticle(particle, loc, 0, 0.0D, 0.0D, 0.0D, 1.0D, data);
                }
                loc.subtract(x, y, z);
                if (this.t > 12.566370614359172D) {
                    this.t = 0.0D;
                }
            }
        },0,50);
    }
}
