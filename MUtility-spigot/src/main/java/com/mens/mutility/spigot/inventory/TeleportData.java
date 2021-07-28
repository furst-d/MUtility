package com.mens.mutility.spigot.inventory;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.utils.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class TeleportData {
    private int id;
    private int userId;
    private String inventory;
    private double fromX;
    private double fromY;
    private double fromZ;
    private String fromWorld;
    private String fromServer;
    private double toX;
    private double toY;
    private double toZ;
    private String toWorld;
    private String toServer;
    private String gamemode;
    private int level;
    private float exp;
    private int foodLevel;
    private double health;
    private boolean allowFlight;
    private Collection<PotionEffect> activePotionEffects;

    private final PlayerManager playerManager;
    private final InventoryManager inventoryManager;

    public TeleportData(MUtilitySpigot plugin) {
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
        fromServer = plugin.getCurrentServer();
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

    public void setUserId(int userId) {
        this.userId = userId;
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

    public void setFromX(double fromX) {
        this.fromX = fromX;
    }

    public double getFromY() {
        return fromY;
    }

    public void setFromY(double fromY) {
        this.fromY = fromY;
    }

    public double getFromZ() {
        return fromZ;
    }

    public void setFromZ(double fromZ) {
        this.fromZ = fromZ;
    }

    public String getFromWorld() {
        return fromWorld;
    }

    public void setFromWorld(String fromWorld) {
        this.fromWorld = fromWorld;
    }

    public String getFromServer() {
        return fromServer;
    }

    public void setFromServer(String fromServer) {
        this.fromServer = fromServer;
    }

    public double getToX() {
        return toX;
    }

    public void setToX(double toX) {
        this.toX = toX;
    }

    public double getToY() {
        return toY;
    }

    public void setToY(double toY) {
        this.toY = toY;
    }

    public double getToZ() {
        return toZ;
    }

    public void setToZ(double toZ) {
        this.toZ = toZ;
    }

    public String getToWorld() {
        return toWorld;
    }

    public void setToWorld(String toWorld) {
        this.toWorld = toWorld;
    }

    public String getToServer() {
        return toServer;
    }

    public void setToServer(String toServer) {
        this.toServer = toServer;
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

    public InventoryManager getInventoryManager() {
        return inventoryManager;
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
