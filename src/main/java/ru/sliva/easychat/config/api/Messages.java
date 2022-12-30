package ru.sliva.easychat.config.api;

import org.jetbrains.annotations.NotNull;
import ru.sliva.easychat.EasyChat;
import ru.sliva.easychat.config.PluginConfig;

public enum Messages {

    join("join"),
    quit("quit");

    private final PluginConfig config;
    private final String key;

    Messages(@NotNull String key) {
        this.config = EasyChat.getInstance().getConfig();
        this.key = "messages." + key;
    }

    public @NotNull String getString() {
        return config.getString(key);
    }

}
