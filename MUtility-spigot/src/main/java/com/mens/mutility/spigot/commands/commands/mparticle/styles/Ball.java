package com.mens.mutility.spigot.commands.commands.mparticle.styles;

import com.mens.mutility.spigot.utils.Timer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class Ball {
    public void run(int recordId, Player player, String particle, int red, int green, int blue, int onPlace, Location location) {
        Timer timer = new Timer();
        double phi = 0.0D;
        timer.setOnRunning((sec, tt) -> {

        });
        /*try {
            Particle getParticle = Particles.getParticleFromName(particle);
            BlockData blockData = Particles.getBlockDataFromName(particle);
            int id = Methods.getId(id_user_record, onPlace);

            new BukkitRunnable() {
                double phi = 0.0D;
                @Override
                public void run() {

                    if(onPlace == 0) {
                        loc = player.getLocation();
                        if(!ParticlePlayer.getPlayer(player, id_user_record)) {
                            this.cancel();
                        }
                        if(Methods.isVanished(player)) {
                            this.cancel();
                            player.sendMessage(Prefix.prefix + Errors.inVanished);
                            stop.runQuietly(player, id_user_record, onPlace);
                        }
                    } else if(onPlace == 1) {
                        loc = location;
                        if(!ParticlePlace.getPlace(id)) {
                            this.cancel();
                        }
                    }
                    this.phi += 0.20943951023931953D;
                    for (double theta = 0.0D; theta <= 6.283185307179586D; theta += 0.06283185307179587D) {
                        double r = 1.75D;
                        double x = r * Math.cos(theta) * Math.sin(this.phi);
                        double y = r * Math.cos(this.phi) + r;
                        double z = r * Math.sin(theta) * Math.sin(this.phi);
                        loc.add(x, y, z);
                        if(getParticle.toString().equalsIgnoreCase("redstone")) {

                            if((red < 0) || (green < 0) || (blue < 0)){
                                Particle.DustOptions dusatOptions = new Particle.DustOptions(Color.fromRGB(rd.nextInt(255), rd.nextInt(255), rd.nextInt(255)),1);
                                loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, 0.0D, 0.0D, 0.0D, 1.0D, dusatOptions);
                            } else {
                                Particle.DustOptions dusatOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
                                loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, 0.0D, 0.0D, 0.0D, 1.0D, dusatOptions);
                            }

                        } else {
                            loc.getWorld().spawnParticle(getParticle, loc, 0, 0.0D, 0.0D, 0.0D, 1.0D, blockData);
                        }

                        loc.subtract(x, y, z);
                    }
                }
            }.runTaskTimer(Main.plugin, 0, 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }
}
