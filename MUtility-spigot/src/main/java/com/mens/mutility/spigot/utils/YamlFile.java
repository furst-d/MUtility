package com.mens.mutility.spigot.utils;

import com.mens.mutility.spigot.MUtilitySpigot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class YamlFile {
    private final MUtilitySpigot plugin;
    private FileConfiguration conf;
    private final File file;

    public YamlFile(MUtilitySpigot plugin, String fileName) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder() + fileName);
        createFiles();
        loadData();
    }

    public File getFile() {
        return file;
    }

    private void createFiles() {
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(file.getName(), false);
        }
    }

    private void loadData() {
        try {
            conf = YamlConfiguration.loadConfiguration(file);
        } catch (IllegalArgumentException e) {
            conf = new YamlConfiguration();
        }
    }

    public void saveData() {
        try {
            getData().save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Chyba při ukládání do souboru " + file, e);
        }
    }

    public FileConfiguration getData() {
        if(conf == null) {
            loadData();
        }
        return conf;
    }
}
