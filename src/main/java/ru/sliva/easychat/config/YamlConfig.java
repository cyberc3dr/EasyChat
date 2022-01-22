package ru.sliva.easychat.config;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class YamlConfig {

    private final Plugin plugin;
    private final File file;
    private final YamlConfigurationLoader loader;
    private ConfigurationNode root;

    public YamlConfig(@NotNull Plugin plugin, @NotNull String fileName) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), fileName);
        this.loader = YamlConfigurationLoader.builder().file(file).build();
        reloadConfig();
    }

    public ConfigurationNode getRoot() {
        return root;
    }

    public void reloadConfig() {
        try {
            if(!file.exists()) {
                if(!saveDefaultConfig()) {
                    this.root = loader.createNode();
                    saveConfig();
                }
                plugin.getLogger().info("Saved " + file.getName() + " for plugin " + plugin.getName());
            }

            this.root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            loader.save(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean saveDefaultConfig() {
        if(plugin.getResource(file.getName()) != null) {
            plugin.saveResource(file.getName(), true);
            return true;
        }
        return false;
    }
}
