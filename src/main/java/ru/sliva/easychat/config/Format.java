package ru.sliva.easychat.config;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.easychat.EasyChat;
import ru.sliva.easychat.text.TextUtil;

public enum Format {

    globalChat("global-chat"),
    localChat("local-chat"),
    format("format");

    private final ConfigurationNode node;

    Format(@NotNull String key) {
        this.node = EasyChat.getInstance().getPluginConfig().getFormat().node(key);
    }

    public @NotNull String getString() {
        return TextUtil.fromNullable(node.getString());
    }

    public @NotNull Component getComponent() {
        return TextUtil.ampersandSerializer.deserialize(getString());
    }
}
