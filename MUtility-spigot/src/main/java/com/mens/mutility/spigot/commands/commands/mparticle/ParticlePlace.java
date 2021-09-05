package com.mens.mutility.spigot.commands.commands.mparticle;

import java.util.ArrayList;
import java.util.List;

public class ParticlePlace {
    private static final List<Integer> placeParticleList = new ArrayList<>();

    private final int id;

    public ParticlePlace(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static void registerPlace(int id){
        placeParticleList.add(id);
    }

    public static void unregisterPlace(int id) {
        placeParticleList.removeIf(pp -> pp == id);
    }

    public static boolean containsId(int id) {
        return placeParticleList.stream().anyMatch(pp -> pp == id);
    }
}
