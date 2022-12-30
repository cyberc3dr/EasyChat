package ru.sliva.easychat.config;

import org.jetbrains.annotations.NotNull;
import ru.sliva.easychat.EasyChat;

public final class PluginConfig extends Config {

    public PluginConfig(@NotNull EasyChat plugin) {
        super(plugin, "config.yml");
    }
}
