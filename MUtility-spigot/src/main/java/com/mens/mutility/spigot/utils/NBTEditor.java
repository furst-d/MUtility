package com.mens.mutility.spigot.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

@SuppressWarnings("unused")
public class NBTEditor {
    public String getNBT(Entity e) {
        net.minecraft.world.entity.Entity nms = ((CraftEntity) e).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        nms.d(nbt);
        return nbt.toString();
    }

    public void addNBT(Entity e, String value) {
        net.minecraft.world.entity.Entity nms = ((CraftEntity) e).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        nms.d(nbt);
        try {
            NBTTagCompound nbtv = MojangsonParser.parse(value);
            nbt.a(nbtv);
            nms.d(nbt);
        } catch (CommandSyntaxException ignored) {
        }
    }

    public void setNBT(Entity e, String value) {
        net.minecraft.world.entity.Entity nms = ((CraftEntity) e).getHandle();
        try {
            NBTTagCompound nbtv = MojangsonParser.parse(value);
            nms.load(nbtv);
        } catch (CommandSyntaxException ignored) {
        }
    }
}
