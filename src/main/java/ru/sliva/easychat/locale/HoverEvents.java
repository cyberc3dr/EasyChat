package ru.sliva.easychat.locale;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.easychat.EasyChat;
import ru.sliva.easychat.text.TextUtil;

public enum HoverEvents {

    tellMessage("tell-message"),
    copyMessage("copy-message");

    private final ConfigurationNode node;

    HoverEvents(@NotNull String key) {
        this.node = EasyChat.getInstance().getLocaleConfig().getHoverEvents().node(key);
    }

    public @NotNull String getString() {
        return TextUtil.fromNullable(node.getString());
    }

    public @NotNull Component getComponent() {
        return TextUtil.ampersandSerializer.deserialize(getString());
    }
}
