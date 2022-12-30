package ru.sliva.easychat.config;

import com.google.common.base.Charsets;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Config extends YamlConfiguration {

    private final File file;
    private final Plugin plugin;

    public Config(@NotNull Plugin plugin, @NotNull String filename) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), filename);
        initDefaults();
        reloadConfig();
    }

    @SuppressWarnings("all")
    public final void reloadConfig() {
        try {
            if (!file.exists()) {
                if (!saveDefaultConfig()) {
                    if (file.createNewFile()) {
                        setDefaults();
                        saveConfig();
                    }
                }
            }

            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void initDefaults() {
        final InputStream defConfigStream = plugin.getResource(file.getName());

        if (defConfigStream != null) {
            setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        }
    }

    public void setDefaults() {
    }

    public final void saveConfig() {
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final boolean saveDefaultConfig() {
        if (plugin.getResource(file.getName()) != null) {
            plugin.saveResource(file.getName(), true);
            return true;
        }
        return false;
    }

    @Override
    public final @NotNull String getString(@NotNull String path, @Nullable String def) {
        String result = super.getString(path, def);
        if (result == null) {
            result = path;
        }
        return result.replace("/n", "\n");
    }

    @Override
    public final @NotNull String getString(@NotNull String path) {
        String result = super.getString(path);
        if (result == null) {
            result = path;
        }
        return result.replace("/n", "\n");
    }
}