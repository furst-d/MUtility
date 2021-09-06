package com.mens.mutility.spigot.commands.commands.mparticle.enums;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;

import java.util.Arrays;

public enum Particles {
    ASH(Particle.ASH, "ash", null),
    BARRIER(Particle.BARRIER, "barrier", null),
    BUBBLE_COLUMN_UP(Particle.BUBBLE_COLUMN_UP, "bubble_column_up", null),
    BUBBLE_POP(Particle.BUBBLE_POP, "bubble_pop", null),
    CAMPFIRE_COSY_SMOKE(Particle.CAMPFIRE_COSY_SMOKE, "campfire_cosy_smoke", null),
    CAMPFIRE_SIGNAL_SMOKE(Particle.CAMPFIRE_SIGNAL_SMOKE, "campfire_signal_smoke", null),
    CLOUD(Particle.CLOUD, "cloud", null),
    CRIMSON_SPORE(Particle.CRIMSON_SPORE, "crimson_spore", null),
    CRIT(Particle.CRIT, "crit", null),
    CRIT_MAGIC(Particle.CRIT_MAGIC, "crit_magic", null),
    CURRENT_DOWN(Particle.CURRENT_DOWN, "current_down", null),
    DAMAGE_INDICATOR(Particle.DAMAGE_INDICATOR, "damage_indicator", null),
    DOLPHIN(Particle.DOLPHIN, "doplhin", null),
    DRAGON_BREATH(Particle.DRAGON_BREATH, "dragon_breath", null),
    DRIP_LAVA(Particle.DRIP_LAVA, "drip_lava", null),
    DRIP_WATER(Particle.DRIP_WATER, "drip_water", null),
    DRIPPING_DRIPSTONE_LAVA(Particle.DRIPPING_DRIPSTONE_LAVA, "dripping_dripstone_lava", null),
    DRIPPING_DRIPSTONE_WATER(Particle.DRIPPING_DRIPSTONE_WATER, "dripping_dripstone_water", null),
    DRIPPING_HONEY(Particle.DRIPPING_HONEY, "dripping_honey", null),
    DRIPPING_OBSIDIAN_TEAR(Particle.DRIPPING_OBSIDIAN_TEAR, "dripping_obsidian_tear", null),
    ELECTRIC_SPARK(Particle.ELECTRIC_SPARK, "electric_spark", null),
    ENCHANTMENT_TABLE(Particle.ENCHANTMENT_TABLE, "enchantment_table", null),
    END_ROD(Particle.END_ROD, "end_rod", null),
    EXPLOSION_HUGE(Particle.EXPLOSION_HUGE, "explosion_huge", null),
    EXPLOSION_LARGE(Particle.EXPLOSION_LARGE, "explosion_large", null),
    EXPLOSION_NORMAL(Particle.EXPLOSION_NORMAL, "explosion_normal", null),
    FIREWORKS_SPARK(Particle.FIREWORKS_SPARK, "firework_spark", null),
    FALLING_DRIPSTONE_LAVA(Particle.FALLING_DRIPSTONE_LAVA, "falling_dripstone_lava", null),
    FALLING_DRIPSTONE_WATER(Particle.FALLING_DRIPSTONE_WATER, "falling_dripstone_water", null),
    FALLING_HONEY(Particle.FALLING_HONEY, "falling_honey", null),
    FALLING_NECTAR(Particle.FALLING_NECTAR, "falling_nectar", null),
    FALLING_OBSIDIAN_TEAR(Particle.FALLING_OBSIDIAN_TEAR, "falling_obsidian_tear", null),
    FALLING_SPORE_BLOSSOM(Particle.FALLING_SPORE_BLOSSOM, "falling_spore_blossom", null),
    FLAME(Particle.FLAME, "flame", null),
    GLOW(Particle.GLOW, "glow", null),
    GLOW_SQUID_INK(Particle.GLOW_SQUID_INK, "glow_squid_ink", null),
    GRAVEL(Particle.FALLING_DUST, "gravel", Material.GRAVEL.createBlockData()),
    HEART(Particle.HEART, "heart", null),
    LANDING_HONEY(Particle.LANDING_HONEY, "landing_honey", null),
    LANDING_OBSIDIAN_TEAR(Particle.LANDING_OBSIDIAN_TEAR, "landing_obsidian_tear", null),
    LAVA(Particle.LAVA, "lava", null),
    LIGHT(Particle.LIGHT, "light", null),
    MOB_APPEARANCE(Particle.MOB_APPEARANCE, "mob_appearance", null),
    NAUTILUS(Particle.NAUTILUS, "nautilus", null),
    NOTE(Particle.NOTE, "note", null),
    PORTAL(Particle.PORTAL, "portal", null),
    REDSTONE(Particle.REDSTONE, "redstone", null),
    SAND(Particle.FALLING_DUST, "sand", Material.SAND.createBlockData()),
    SCRAPE(Particle.SCRAPE, "scrape", null),
    SLIME(Particle.SLIME, "slime", null),
    SMOKE_LARGE(Particle.SMOKE_LARGE, "smoke_large", null),
    SMOKE_NORMAL(Particle.SMOKE_NORMAL, "smoke_normal", null),
    SNEEZE(Particle.SNEEZE, "sneeze", null),
    SNOW(Particle.FALLING_DUST, "snow", Material.SNOW.createBlockData()),
    SNOW_SHOVEL(Particle.SNOW_SHOVEL, "snow_shovel", null),
    SNOWBALL(Particle.SNOWBALL, "snowball", null),
    SNOWFLAKE(Particle.SNOWFLAKE, "snowflake", null),
    SOUL(Particle.SOUL, "soul", null),
    SOUL_FIRE_FLAME(Particle.SOUL_FIRE_FLAME, "soul_fire_flame", null),
    SPELL(Particle.SPELL, "spell", null),
    SPELL_INSTANT(Particle.SPELL_INSTANT, "spell_instant", null),
    SPELL_MOB(Particle.SPELL_MOB, "spell_mob", null),
    SPELL_MOB_AMBIENT(Particle.SPELL_MOB_AMBIENT, "spell_mob_ambient", null),
    SPELL_WITCH(Particle.SPELL_WITCH, "spell_witch", null),
    SPIT(Particle.SPIT, "spit", null),
    SPORE_BLOSSOM_AIR(Particle.SPORE_BLOSSOM_AIR, "spore_blossom_air", null),
    SQUID_INK(Particle.SQUID_INK, "squid_ink", null),
    SUSPENDED(Particle.SUSPENDED, "suspended", null),
    SUSPENDED_DEPTH(Particle.SUSPENDED_DEPTH, "suspended_depth", null),
    SWEEP_ATTACK(Particle.SWEEP_ATTACK, "sweep_attack", null),
    TOTEM(Particle.TOTEM, "totem", null),
    TOWN_AURA(Particle.TOWN_AURA, "town_aura", null),
    VILLAGER_ANGRY(Particle.VILLAGER_ANGRY, "villager_angry", null),
    VILLAGER_HAPPY(Particle.VILLAGER_HAPPY, "villager_happy", null),
    WARPED_SPORE(Particle.WARPED_SPORE, "warped_spore", null),
    WATER_BUBBLE(Particle.WATER_BUBBLE, "water_bubble", null),
    WATER_DROP(Particle.WATER_DROP, "water_drop", null),
    WATER_SPLASH(Particle.WATER_SPLASH, "water_splash", null),
    WATER_WAKE(Particle.WATER_WAKE, "water_wake", null),
    WAX_OFF(Particle.WAX_OFF, "wax_off", null),
    WAX_ON(Particle.WAX_ON, "wax_on", null),
    WHITE_ASH(Particle.WHITE_ASH, "white_ash", null);

    private final Particle particle;
    private final String name;
    private final BlockData data;

    Particles(Particle particle, String name, BlockData data) {
        this.particle = particle;
        this.name = name;
        this.data = data;
    }

    public Particle getParticle() {
        return particle;
    }

    public String getName() {
        return this.name;
    }

    public BlockData getData() {
        return data;
    }

    public static Particles getParticleEnumByName(String name) {
        return Arrays.stream(Particles.values()).filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }
}
