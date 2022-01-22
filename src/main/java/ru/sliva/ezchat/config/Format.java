package ru.sliva.ezchat.config;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import ru.sliva.ezchat.EzChat;
import ru.sliva.ezchat.text.TextUtil;

import java.util.Collections;
import java.util.List;

public enum Format {

    globalChat("global-chat"),
    localChat("local-chat"),
    format("format"),
    patterns("patterns");

    private final ConfigurationNode node;

    Format(@NotNull String key) {
        this.node = EzChat.getInstance().getPluginConfig().getFormat().node(key);
    }

    public @NotNull List<String> getStringList() {
        List<String> list = null;
        try {
            list = node.getList(String.class);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        return list == null ? Collections.emptyList() : list;
    }

    public @NotNull String getString() {
        return TextUtil.fromNullable(node.getString());
    }

    public @NotNull Component getComponent() {
        return TextUtil.ampersandSerializer.deserialize(getString());
    }
}
