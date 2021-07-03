package com.mens.mutility.spigot.commands.commands.event;

public class EventData {
    private int id;
    private String name;
    private float tpX;
    private float tpY;
    private float tpZ;
    private String world;
    private String server;
    private String objective;
    private String note;

    public EventData(int id, String name, float tpX, float tpY, float tpZ, String world, String server, String objective, String note) {
        this.id = id;
        this.name = name;
        this.tpX = tpX;
        this.tpY = tpY;
        this.tpZ = tpZ;
        this.world = world;
        this.server = server;
        this.objective = objective;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getTpX() {
        return tpX;
    }

    public float getTpY() {
        return tpY;
    }

    public float getTpZ() {
        return tpZ;
    }

    public String getWorld() {
        return world;
    }

    public String getServer() {
        return server;
    }

    public String getObjective() {
        return objective;
    }

    public String getNote() {
        return note;
    }
}
