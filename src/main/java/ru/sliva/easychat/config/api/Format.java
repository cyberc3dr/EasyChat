package ru.sliva.easychat.config.api;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import ru.sliva.easychat.EasyChat;
import ru.sliva.easychat.config.PluginConfig;
import ru.sliva.easychat.text.TextUtil;

public enum Format {

    globalChat("global-chat"),
    localChat("local-chat"),
    format("format");

    private final PluginConfig config;
    private final String key;

    Format(@NotNull String key) {
        this.config = EasyChat.getInstance().getConfig();
        this.key = "format." + key;
    }

    public @NotNull String getString() {
        return config.getString(key);
    }

    public @NotNull TextComponent getComponent() {
        return TextUtil.ampersandSerializer.deserialize(getString());
    }
}
