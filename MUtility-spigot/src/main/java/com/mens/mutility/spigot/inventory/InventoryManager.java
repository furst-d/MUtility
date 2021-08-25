package com.mens.mutility.spigot.inventory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager {
    public void loadInventory(Player player, JsonObject inventoryJson) {
        JsonArray itemsJson = inventoryJson.getAsJsonArray("items");

        String helmet = inventoryJson.get("helmet").getAsJsonObject().get("ntb").getAsString();
        String chestplate = inventoryJson.get("chestplate").getAsJsonObject().get("ntb").getAsString();
        String leggings = inventoryJson.get("leggings").getAsJsonObject().get("ntb").getAsString();
        String boots = inventoryJson.get("boots").getAsJsonObject().get("ntb").getAsString();
        String offHand = inventoryJson.get("offHand").getAsJsonObject().get("ntb").getAsString();

        try {
            player.getInventory().setHelmet(CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(MojangsonParser.parse(helmet))));
            player.getInventory().setChestplate(CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(MojangsonParser.parse(chestplate))));
            player.getInventory().setLeggings(CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(MojangsonParser.parse(leggings))));
            player.getInventory().setBoots(CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(MojangsonParser.parse(boots))));
            player.getInventory().setItemInOffHand(CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(MojangsonParser.parse(offHand))));

            for (int i = 0; i < itemsJson.size(); i++) {
                player.getInventory().setItem(i, CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(MojangsonParser.parse(itemsJson.get(i).getAsJsonObject().get("ntb").getAsString()))));
            }
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

    public JsonObject getInventory(Player player) {
        List<ItemStack> inventory = new ArrayList<>();
        JsonObject inventoryJson = new JsonObject();
        JsonArray itemsJson = new JsonArray();
        for (int i = 0; i < 36; i++) {
            inventory.add(player.getInventory().getItem(i));
        }
        inventory.forEach(item -> {
            NBTTagCompound compound = CraftItemStack.asNMSCopy(item).save(new NBTTagCompound());
            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("ntb", compound.toString());
            itemsJson.add(itemJson);
        });

        NBTTagCompound compoundHelmet = CraftItemStack.asNMSCopy(player.getInventory().getHelmet()).save(new NBTTagCompound());
        NBTTagCompound compoundChestplate = CraftItemStack.asNMSCopy(player.getInventory().getChestplate()).save(new NBTTagCompound());
        NBTTagCompound compoundLeggings = CraftItemStack.asNMSCopy(player.getInventory().getLeggings()).save(new NBTTagCompound());
        NBTTagCompound compoundBoots = CraftItemStack.asNMSCopy(player.getInventory().getBoots()).save(new NBTTagCompound());
        NBTTagCompound compoundOffHand = CraftItemStack.asNMSCopy(player.getInventory().getItemInOffHand()).save(new NBTTagCompound());

        JsonObject helmetJson = new JsonObject();
        JsonObject chestplateJson = new JsonObject();
        JsonObject leggingsJson = new JsonObject();
        JsonObject bootsJson = new JsonObject();
        JsonObject offHandJson = new JsonObject();

        helmetJson.addProperty("ntb", compoundHelmet.toString());
        chestplateJson.addProperty("ntb", compoundChestplate.toString());
        leggingsJson.addProperty("ntb", compoundLeggings.toString());
        bootsJson.addProperty("ntb", compoundBoots.toString());
        offHandJson.addProperty("ntb", compoundOffHand.toString());

        inventoryJson.add("items", itemsJson);
        inventoryJson.add("helmet", helmetJson);
        inventoryJson.add("chestplate", chestplateJson);
        inventoryJson.add("leggings", leggingsJson);
        inventoryJson.add("boots", bootsJson);
        inventoryJson.add("offHand", offHandJson);

        return inventoryJson;
    }

    public InventoryPair getInventoryAsItemStack(JsonObject inventoryJson) {
        List<ItemStack> items = new ArrayList<>();
        List<ItemStack> armor = new ArrayList<>();
        JsonArray itemsJson = inventoryJson.getAsJsonArray("items");

        String helmet = inventoryJson.get("helmet").getAsJsonObject().get("ntb").getAsString();
        String chestplate = inventoryJson.get("chestplate").getAsJsonObject().get("ntb").getAsString();
        String leggings = inventoryJson.get("leggings").getAsJsonObject().get("ntb").getAsString();
        String boots = inventoryJson.get("boots").getAsJsonObject().get("ntb").getAsString();
        String offHand = inventoryJson.get("offHand").getAsJsonObject().get("ntb").getAsString();

        try {
            for (int i = 0; i < itemsJson.size(); i++) {
                items.add(CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(MojangsonParser.parse(itemsJson.get(i).getAsJsonObject().get("ntb").getAsString()))));
            }

            armor.add(CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(MojangsonParser.parse(helmet))));
            armor.add(CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(MojangsonParser.parse(chestplate))));
            armor.add(CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(MojangsonParser.parse(leggings))));
            armor.add(CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(MojangsonParser.parse(boots))));
            armor.add(CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(MojangsonParser.parse(offHand))));

        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return new InventoryPair(items, armor);
    }

    public JsonObject toJsonObject(String json) {
        return new JsonParser().parse(json).getAsJsonObject();
    }
}
