package com.mens.mutility.spigot.inventory;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.utils.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.Objects;

public class TeleportData {
    private int id;
    private int userId;
    private String inventory;
    private double fromX;
    private double fromY;
    private double fromZ;
    private String fromWorld;
    private String fromServer;
    private String gamemode;
    private int level;
    private float exp;
    private int foodLevel;
    private double health;
    private boolean allowFlight;
    private Collection<PotionEffect> activePotionEffects;

    private final PlayerManager playerManager;
    private final InventoryManager inventoryManager;

    public TeleportData() {
        playerManager = new PlayerManager();
        inventoryManager = new InventoryManager();
    }

    public TeleportData(MUtilitySpigot plugin, Player player, double x, double y, double z, String world) {
        playerManager = new PlayerManager();
        inventoryManager = new InventoryManager();
        userId = playerManager.getUserId(player.getName());
        inventory = inventoryManager.getInventory(player).toString();
        fromX = x;
        fromY = y;
        fromZ = z;
        fromWorld = world;
        fromServer = Objects.requireNonNull(plugin.getCurrentServer()).getName();
        gamemode = player.getGameMode().name();
        level = player.getLevel();
        exp = player.getExp();
        foodLevel = player.getFoodLevel();
        health = player.getHealth();
        allowFlight = player.getAllowFlight();
        activePotionEffects = player.getActivePotionEffects();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public double getFromX() {
        return fromX;
    }

    public double getFromY() {
        return fromY;
    }

    public double getFromZ() {
        return fromZ;
    }

    public String getFromWorld() {
        return fromWorld;
    }

    public String getFromServer() {
        return fromServer;
    }

    public String getGamemode() {
        return gamemode;
    }

    public void setGamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getExp() {
        return exp;
    }

    public void setExp(float exp) {
        this.exp = exp;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public boolean isAllowFlight() {
        return allowFlight;
    }

    public void setAllowFlight(boolean allowFlight) {
        this.allowFlight = allowFlight;
    }

    public Collection<PotionEffect> getActivePotionEffects() {
        return activePotionEffects;
    }

    public void setActivePotionEffects(Collection<PotionEffect> activePotionEffects) {
        this.activePotionEffects = activePotionEffects;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public String getEffectsToString() {
        StringBuilder effects = new StringBuilder();
        for(PotionEffect effect : activePotionEffects) {
            effects.append(effect.getType().getName()).append(":")
                    .append(effect.getDuration()).append(":")
                    .append(effect.getAmplifier()).append(":")
                    .append(effect.isAmbient()).append(":")
                    .append(effect.hasParticles()).append(":")
                    .append(effect.hasIcon()).append(";");
        }
        return activePotionEffects.size() > 0 ? effects.substring(0, effects.length() - 1) : null;
    }
}
