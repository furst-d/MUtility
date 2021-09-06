package com.mens.mutility.spigot.commands.commands.mparticle.styles;

import com.mens.mutility.spigot.chat.Prefix;
import com.mens.mutility.spigot.commands.commands.mparticle.ParticleInfo;
import com.mens.mutility.spigot.commands.commands.mparticle.ParticlePlace;
import com.mens.mutility.spigot.commands.commands.mparticle.ParticlePlayer;
import com.mens.mutility.spigot.commands.commands.mparticle.RGB;
import com.mens.mutility.spigot.utils.Checker;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;
import java.util.TimerTask;

@SuppressWarnings("unused")
public class Custom {
    private Location loc;
    private final Random rd;
    private final Checker checker;
    private final Prefix prefix;
    
    private static final boolean x = true;
    private static final boolean o = false;
    private static double heightTemp;
    private static double spaceTemp;
    private static boolean[][] shapePom;

    public Custom() {
        rd = new Random();
        checker = new Checker();
        prefix = new Prefix();
    }

    public void run(ParticleInfo info) {
        boolean[][] shape = shapePom;
        double height = heightTemp;
        double space = spaceTemp;
        long speed = 0;

        Player player = info.getPlayer();
        Particle particle = info.getParticle().getParticle();
        BlockData data = info.getParticle().getData();
        RGB color = info.getColor();
        String customType = info.getCustomStyle().getName();

        if(customType.equalsIgnoreCase("usi_a_ocas")) {
            shape = ears_and_tail;
            space = 0.15;
            height = 2.20;
            speed = 300;
        } else if(customType.equalsIgnoreCase("kridla")) {
            shape = simple_wing;
            space = 0.20;
            height = 2.8;
            speed = 300;
        } else if(customType.equalsIgnoreCase("kridla_demon")) {
            shape = demon_wing;
            space = 0.20;
            height = 2.8;
            speed = 300;
        } else if(customType.equalsIgnoreCase("kridla_andel")) {
            shape = angel_wing;
            space = 0.20;
            height = 5;
            speed = 300;
        } else if(customType.equalsIgnoreCase("kridla_velka")) {
            shape = wing_v_1_15;
            space = 0.20;
            height = 2.8;
            speed = 300;
        } else if(customType.equalsIgnoreCase("kostkuj")) {
            shape = kostkuj;
            space = 0.15;
            height = 7;
            speed = 300;
        } else if(customType.equalsIgnoreCase("koruna")) {
            shape = crown;
            space = 0.19;
            height = 4.0;
            speed = 300;
        }

        boolean[][] finalShape = shape;
        double finalHeight = height;
        double finalSpace = space;

        new java.util.Timer().schedule(new TimerTask() {
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
                drawParticles(loc, finalHeight, finalSpace, particle, color, finalShape, data);
            }
        }, 0, speed);
    }

    public void drawParticles(Location loc, double height, double space, Particle getParticle, RGB color, boolean[][] shape, BlockData data) {
        double defX = loc.getX() - (space * (shape[0].length-1) / 2);
        double x = defX;
        double y = loc.clone().getY() + height;
        Random random = new Random();
        double fire = -((loc.getYaw() + 180) / 60);
        fire += (loc.getYaw() < -180 ? 3.25 : 2.985);
        for (boolean[] booleans : shape) {
            for (boolean aBoolean : booleans) {
                if (aBoolean) {
                    Location target = loc.clone();
                    target.setX(x);
                    target.setY(y);

                    Vector v = target.toVector().subtract(loc.toVector());
                    Vector v2 = getBackVector(loc);
                    v = rotateAroundAxisY(v, fire);
                    v2.setY(0).multiply(-0.5);

                    loc.add(v);
                    loc.add(v2);
                    if (getParticle.toString().equalsIgnoreCase("redstone")) {
                        Particle.DustOptions dusatOptions;
                        if ((color.getRed() < 0)) {
                            dusatOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(rd.nextInt(255), rd.nextInt(255), rd.nextInt(255)), 1);
                        } else {
                            dusatOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()), 1);
                        }
                        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.REDSTONE, loc, 0, 0.0D, 0.0D, 0.0D, 1.0D, dusatOptions);
                    } else {
                        Objects.requireNonNull(loc.getWorld()).spawnParticle(getParticle, loc, 0, 0.0D, 0.0D, 0.0D, 1.0D, data);
                    }
                    loc.subtract(v2);
                    loc.subtract(v);
                }
                x += space;
            }
            y -= space;
            x = defX;
        }
    }

    private final boolean[][] ears_and_tail = {
            {o, o, o, o, o, o, x, o, o, o, x, o, o, o, o, o, o},
            {o, o, o, o, o, o, x, o, o, o, x, o, o, o, o, o, o},
            {o, o, o, o, o, x, o, x, o, x, o, x, o, o, o, o, o},
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
            {x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
            {o, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
            {o, x, x, o, o, o, o, x, x, o, o, o, o, o, o, o, o},
            {o, o, x, x, o, o, o, x, x, o, o, o, o, o, o, o, o},
            {o, o, x, x, x, o, x, x, o, o, o, o, o, o, o, o, o},
            {o, o, o, x, x, x, x, o, o, o, o, o, o, o, o, o, o},
            {o, o, o, o, x, x, o, o, o, o, o, o, o, o, o, o, o},
    };

    private final boolean[][] simple_wing = {
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
            {o, o, o, x, o, o, o, o, o, o, o, o, o, x, o, o, o},
            {o, o, x, x, o, o, o, o, o, o, o, o, o, x, x, o, o},
            {o, x, x, x, x, o, o, o, o, o, o, o, x, x, x, x, o},
            {o, x, x, x, x, o, o, o, o, o, o, o, x, x, x, x, o},
            {o, o, x, x, x, x, o, o, o, o, o, x, x, x, x, o, o},
            {o, o, o, x, x, x, x, o, o, o, x, x, x, x, o, o, o},
            {o, o, o, o, x, x, x, x, x, x, x, x, x, o, o, o, o},
            {o, o, o, o, o, x, x, x, x, x, x, x, o, o, o, o, o},
            {o, o, o, o, o, o, x, x, x, x, x, o, o, o, o, o, o},
            {o, o, o, o, o, x, x, o, o, o, x, x, o, o, o, o, o},
            {o, o, o, o, x, x, x, o, o, o, x, x, x, o, o, o, o},
            {o, o, o, x, x, x, o, o, o, o, o, x, x, x, o, o, o},
            {o, o, o, o, x, o, o, o, o, o, o, o, x, o, o, o, o},
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
    };

    private final boolean[][] demon_wing = {
            {o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, x, o, o, o, o, o, o, o, o, o, x, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, x, x, x, x, x, x, x, x, x, x, x, o, x, x, x, x, x, x, x, x, x, x, x, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, x, x, x, x, x, x, x, x, x, x, x, x, x, x, o, o, o, x, x, x, x, x, x, x, x, x, x, x, x, x, x, o, o, o, o, o,},
            {o, o, o, o, x, x, x, x, x, x, x, x, x, x, x, x, x, x, o, o, o, o, o, x, x, x, x, x, x, x, x, x, x, x, x, x, x, o, o, o, o,},
            {o, o, o, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, o, o, o, o, o, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, o, o, o,},
            {o, o, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, o, o, o, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, o, o,},
            {o, x, o, o, o, x, x, x, x, x, x, x, x, x, x, x, x, x, x, o, o, o, x, x, x, x, x, x, x, x, x, x, x, x, x, x, o, o, o, x, o,},
            {x, o, o, o, o, x, o, o, o, x, x, x, o, o, o, o, x, x, x, x, x, x, x, x, x, o, o, o, o, x, x, x, o, o, o, x, o, o, o, o, x,},
            {o, o, o, o, x, o, o, o, o, o, x, o, o, o, o, o, o, x, x, x ,x, x, x, x, o, o, o, o, o, o, x, o, o, o, o, o, x, o, o, o, o,},
            {o, o, o, x, o, o, o, o, o, o, x, o, o, o, o, o, o, o, x, x, x, x, x, o, o, o, o, o, o, o, x, o, o, o, o, o, o, x, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, o, x, o, o, o, o, o, o, o, x, x, x, o, o, o, o, o, o, o, x, o, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, o, o, x, o, o, o, o, o, o, x, x, x, o, o, o, o, o, o, x, o, o, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o,},
    };

    private final boolean[][] angel_wing = {
            {x, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o,   o,   o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, x,},
            {o, o, x, x, x, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o,   o,   o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, x, x, x, o, o,},
            {o, o, o, o, x, x, x, x, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o,   o,   o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, x, x, x, x, o, o, o, o,},
            {o, o, o, o, o, o, o, x, x, x, x, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o,   o,   o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, x, x, x, x, o, o, o, o, o, o, o,},
            {o, o, x, x, x, x, o, o, o, o, x, x, x, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o,   o,   o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, x, x, x, o, o, o, o, x, x, x, x, o, o,},
            {o, o, o, o, x, x, x, x, o, o, x, x, x, x, x, x, x, x, o, o, o, o, o, o, o, o, o, o, o,   o,   o, o, o, o, o, o, o, o, o, o, o, x, x, x, x, x, x, x, x, o, o, x, x, x, x, o, o, o, o,},
            {o, o, o, o, o, o, x, x, x, x, x, x, o, o, o, x, x, x, x, x, o, o, o, o, o, o, o, o, o,   o,   o, o, o, o, o, o, o, o, o, x, x, x, x, x, o, o, o, x, x, x, x, x, x, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, x, x, x, o, o, o, o, o, x, x, x, x, x, x, x, o, o, o, o, o, o, o,   o,   o, o, o, o, o, o, o, x, x, x, x, x, x, x, o, o, o, o, o, x, x, x, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, o, o, o, x, x, x, x, x, o, o, o, o, o,   o,   o, o, o, o, o, x, x, x, x, x, o, o, o, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, x, x, o, x, x, o, x, x, x, x, o, o, o, o, x, x, x, x, x, x, x, o, o, o,   o,   o, o, o, x, x, x, x, x, x, x, o, o, o, o, x, x, x, x, o, x, x, o, x, x, o, o, o, o, o,},
            {o, o, o, o, o, o, x, x, x, x, x, x, x, x, o, o, o, x, x, x, o, o, o, x, x, x, x, o, o,   o,   o, o, x, x, x, x, o, o, o, x, x, x, o, o, o, x, x, x, x, x, x, x, x, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, x, x, o, o, o, o, x, x, x, o, o, o, o, x, x, x, x, x, o,   o,   o, x, x, x, x, x, o, o, o, o, x, x, x, o, o, o, o, x, x, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, x, x, o, o, o, x, x, x, x, o, x, x, o,   o,   o, x, x, o, x, x, x, x, o, o, o, x, x, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, x, x, o, x, x, x, x, x, x, x, o, o, x, x, x, o, o, o, x, x, o,   o,   o, x, x, o, o, o, x, x, x, o, o, x, x, x, x, x, x, x, o, x, x, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, x, x, x, x, x, x, x, o, o, o, o, o, x, x, x, x, o, o, x, x, o, o,   o,   o, o, x, x, o, o, x, x, x, x, o, o, o, o, o, x, x, x, x, x, x, x, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, x, x, o, x, o, o, o, o, o, x, x, x, o, o, o, x, x, o, o, o,   o,   o, o, o, x, x, o, o, o, x, x, x, o, o, o, o, o, x, o, x, x, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, x, x, x, o, o, o, x, x, o, o, o, o,   o,   o, o, o, o, x, x, o, o, o, x, x, x, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, x, x, x, x, x, o, o, x, x, x, x, o, o, o,   o,   o, o, o, x, x, x, x, o, o, x, x, x, x, x, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, o, x, x, x, o, x, x, o, o, o, o, x, x, x, x, x, o, o, o,   o,   o, o, o, x, x, x, x, x, o, o, o, o, x, x, o, x, x, x, o, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, o, o, x, o, o, o, o, o, o, o, x, x, x, o, x, x, x, x, o,   o,   o, x, x, x, x, o, x, x, x, o, o, o, o, o, o, o, x, o, o, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, x, x, o, o, o, x, x, x, x,   x,   x, x, x, x, o, o, o, x, x, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, x, x, o, o, o, o, o, o, x, x, x,   x,   x, x, x, o, o, o, o, o, o, x, x, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o,   o,   o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o,},
    };

    private final boolean[][] wing_v_1_15 = {
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, o,   o, o, o, o,   o, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o,},
            {x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, o, o,   o, o, o, o,   o, o, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x,},
            {o, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, o, o,   o, o, o, o,   o, o, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, o,},
            {o, o, x, x, x, x, x, x, x, o, o, o, o, o, o, o, x, x, o, o, o,   o, o, o, o,   o, o, o, x, x, o, o, o, o, o, o, o, x, x, x, x, x, x, x, o, o,},
            {o, o, x, x, x, x, x, x, x, x, x, x, x, x, x, o, o, x, x, o, o,   o, o, o, o,   o, o, x, x, o, o, x, x, x, x, x, x, x, x, x, x, x, x, x, o, o,},
            {o, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, o, x, x, o,   o, o, o, o,   o, x, x, o, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, o,},
            {o, o, o, o, o, o, o, x, x, x, x, x, x, x, x, x, x, x, o, x, x,   o, o, o, o,   x, x, o, x, x, x, x, x, x, x, x, x, x, x, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, x, x, x, x, x, x, x, x, x, x, x, x, x, o, x,   x, o, o, x,   x, o, x, x, x, x, x, x, x, x, x, x, x, x, x, o, o, o, o, o, o,},
            {o, o, o, o, o, x, o, o, o, o, x, x, x, o, x, x, x, x, x, x, x,   x, o, o, x,   x, x, x, x, x, x, x, o, x, x, x, o, o, o, o, x, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, x, o, o, o, o, o, o, o, x, x, x, x,   x, o, o, x,   x, x, x, x, o, o, o, o, o, o, o, x, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, o, o, x, x, x, x, x, x, x, x, x,   x, o, o, x,   x, x, x, x, x, x, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, o, x, x, x, x, x, x, x, o, x, x,   o, o, o, o,   x, x, o, x, x, x, x, x, x, x, o, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, o, o, x, x, x, x, x, x, x, o, x, x, o,   o, o, o, o,   o, x, x, o, x, x, x, x, x, x, x, o, o, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, o, o, x, x, x, x, x, x, x, x, o, x, x, o, o,   o, o, o, o,   o, o, x, x, o, x, x, x, x, x, x, x, x, o, o, o, o, o, o, o, o,},
            {o, o, o, o, o, o, x, x, x, o, o, x, o, o, o, o, x, x, o, o, o,   o, o, o, o,   o, o, o, x, x, o, o, o, o, x, o, o, x, x, x, o, o, o, o, o, o,},
            {o, o, o, o, o, x, o, o, o, o, x, o, o, o, o, x, x, o, o, o, o,   o, o, o, o,   o, o, o, o, x, x, o, o, o, o, x, o ,o, o, o, x, o, o, o, o, o,},

    };

    private final boolean[][] kostkuj = {
            {x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,},
            {x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x,},
            {x, o, x, x, o, o, o, o, x, x, o, o, o, o, x, x, x, x, x, x, o, o, o, o, o, o, x, x, x, x, x, x, o, o, o, o, x, x, x, x, x, x, x, x, x, x, o, o, x, x, o, o, o, o, x, x, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, x, x, x, x, x, x, o, x,},
            {x, o, x, x, o, o, o, o, x, x, o, o, o, x, x, x, x, x, x, x, x, o, o, o, o, x, x, x, x, x, x, x, x, o, o, o, x, x, x, x, x, x, x, x, x, x, o, o, x, x, o, o, o, o, x, x, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, x, x, x, x, x, x, o, x,},
            {x, o, x, x, o, o, o, x, x, o, o, o, x, x, x, o, o, o, o, x, x, x, o, o, x, x, x, o, o, o, o, x, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, x, x, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, x, x, o, x,},
            {x, o, x, x, o, o, o, x, x, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, x, x, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, x, x, o, x,},
            {x, o, x, x, o, o, x, x, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, x, x, o, x,},
            {x, o, x, x, o, o, x, x, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, x, x, o, x,},
            {x, o, x, x, o, x, x, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, x, x, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, x, x, o, x,},
            {x, o, x, x, o, x, x, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, x, x, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, x, x, o, x,},
            {x, o, x, x, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, x, x, o, x,},
            {x, o, x, x, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, x, x, x, x, o, o, o, o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, x, x, o, x,},
            {x, o, x, x, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, x, x, o, o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, x, x, o, x,},
            {x, o, x, x, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, x, x, x, o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, x, x, o, x,},
            {x, o, x, x, o, x, x, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, x, x, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, x, x, o, x,},
            {x, o, x, x, o, x, x, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, x, x, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, o, o, o, o, o, x, x, o, x,},
            {x, o, x, x, o, o, x, x, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, o, o, o, o, x, x, o, x,},
            {x, o, x, x, o, o, x, x, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, o, o, o, o, x, x, o, x,},
            {x, o, x, x, o, o, o, x, x, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, x, x, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, x, x, o, o, o, o, x, x, o, x,},
            {x, o, x, x, o, o, o, x, x, o, o, o, x, x, x, o, o, o, o, x, x, x, o, o, x, x, x, o, o, o, o, x, x, x, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, x, x, o, o, o, x, x, x, o, o, o, o, x, x, x, o, o, x, x, x, o, o, x, x, x, o, x,},
            {x, o, x, x, o, o, o, o, x, x, o, o, o, x, x, x, x, x, x, x, x, o, o, o, o, x, x, x, x, x, x, x, x, o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, x, x, o, o, o, x, x, x, x, x, x, x, x, o, o, o, o, x, x, x, x, x, x, o, o, x,},
            {x, o, x, x, o, o, o, o, x, x, o, o, o, o, x, x, x, x, x, x, o, o, o, o, o, o, x, x, x, x, x, x, o, o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o, o, x, x, o, o, o, o, x, x, x, x, x, x, o, o, o, o, o, o, x, x, x, x, o, o, o, x,},
            {x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x,},
            {x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,},
    };


    public static boolean[][] crown = {
            {o, o, o, o, o, o, o, o, o, o, o},
            {o, o, o, o, o, x, o, o, o, o, o},
            {o, x, o, o, o, x, o, o, o, x, o},
            {o, x, x, o, x, o, x, o, x, x, o},
            {o, x, x, o, x, o, x, o, x, x, o},
            {o, x, o, x, o, o, o, x, o, x, o},
            {o, x, o, x, o, o, o, x, o, x, o},
            {o, x, o, o, o, o, o, o, o, x, o},
            {o, x, x, x, x, x, x, x, x, x, o},
            {o, o, o, o, o, o, o, o, o, o, o},
    };

    public static Vector rotateAroundAxisY(Vector v, double fire) {
        double x, z, cos, sin;
        cos = Math.cos(fire);
        sin = Math.sin(fire);
        x = v.getX() * cos + v.getZ() * sin;
        z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public static Vector getBackVector(Location loc) {
        final float newZ = (float) (loc.getZ() + (1 * Math.sin(Math.toRadians(loc.getYaw() + 90))));
        final float newX = (float) (loc.getX() + (1 * Math.cos(Math.toRadians(loc.getYaw() + 90))));
        return new Vector(newX - loc.getX(), 0, newZ - loc.getZ());
    }
}
