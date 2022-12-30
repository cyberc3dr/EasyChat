package ru.sliva.easychat.config.api;

import org.jetbrains.annotations.NotNull;
import ru.sliva.easychat.EasyChat;
import ru.sliva.easychat.config.PluginConfig;

public enum Parameters {

    changeDisplayName("change-display-name"),
    changeTabListName("change-tab-list-name"),
    changePlayerMessages("change-player-messages"),
    removePlayerMessages("remove-player-messages"),
    rangeMode("range-mode"),
    onlyPerWorld("only-per-world"),
    range("range");

    private final PluginConfig config;
    private final String key;

    Parameters(@NotNull String key) {
        this.config = EasyChat.getInstance().getConfig();
        this.key = "parameters." + key;
    }

    public boolean getBoolean() {
        return config.getBoolean(key);
    }

    public int getInt() {
        return config.getInt(key);
    }
}
