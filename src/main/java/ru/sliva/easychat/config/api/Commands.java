package ru.sliva.easychat.config.api;

import org.jetbrains.annotations.NotNull;
import ru.sliva.easychat.EasyChat;
import ru.sliva.easychat.config.PluginConfig;

public enum Commands {

    reload("reload");

    private final PluginConfig config;
    private final String key;

    Commands(@NotNull String key) {
        this.config = EasyChat.getInstance().getConfig();
        this.key = "commands." + key;
    }

    public @NotNull String getString() {
        return config.getString(key);
    }
}
