package com.mens.mutility.spigot.commands.commands.mparticle.enums;

import java.util.Arrays;

public enum CustomStyles {
    USI_A_OCAS("usi_a_ocas"),
    KRIDLA("kridla"),
    KRIDLA_DEMON("kridla_demon"),
    KRIDLA_ANDEL("kridla_andel"),
    KRIDLA_VELKA("kridla_velka"),
    KOSTKUJ("kostkuj"),
    KORUNA("koruna");

    private final String name;

    CustomStyles(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static CustomStyles getCustomStyleEnumByName(String name) {
        return Arrays.stream(CustomStyles.values()).filter(s -> s.getName().equals(name)).findFirst().orElse(null);
    }
}
