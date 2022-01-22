package ru.sliva.ezchat.locale;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.ezchat.EzChat;
import ru.sliva.ezchat.text.TextUtil;

public enum Messages {

    join("join"),
    quit("quit"),
    nobodyHeard("nobody-heard");

    private final ConfigurationNode node;

    Messages(@NotNull String key) {
        this.node = EzChat.getInstance().getLocaleConfig().getMessages().node(key);
    }

    public @NotNull String getString() {
        return TextUtil.fromNullable(node.getString());
    }

    public @NotNull Component getComponent() {
        return TextUtil.ampersandSerializer.deserialize(getString());
    }
}
