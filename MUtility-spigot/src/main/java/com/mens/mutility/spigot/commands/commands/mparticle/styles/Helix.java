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
public class Helix {
    private Location loc;
    private final Random rd;
    private final Checker checker;
    private final Prefix prefix;

    public Helix() {
        rd = new Random();
        checker = new Checker();
        prefix = new Prefix();
    }

    public void run(ParticleInfo info) {
        Player player = info.getPlayer();
        Particle particle = info.getParticle().getParticle();
        BlockData blockData = info.getParticle().getBlockData();
        RGB color = info.getColor();

        new java.util.Timer().schedule(new TimerTask() {
            double phi = 0.0D;
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
                phi += Math.PI / 16;
                double x; double y; double z;
                for(double t = 0; t <= 2*Math.PI; t += Math.PI / 16) {
                    for(double i = 0; i <= 1; i += 1) {
                        x = 0.15 * (2*Math.PI - t) * cos (t + phi + i * Math.PI);
                        y = 0.5 * t;
                        z = 0.15 * (2*Math.PI - t) * sin (t + phi + i * Math.PI);
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
                            Objects.requireNonNull(loc.getWorld()).spawnParticle(particle, loc, 0, 0.0D, 0.0D, 0.0D, 1.0D, blockData);
                        }
                        loc.subtract(x, y, z);
                    }
                }
            }
        },0,250);
    }
}
