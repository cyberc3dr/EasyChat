package ru.sliva.easychat.locale;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.easychat.EasyChat;
import ru.sliva.easychat.text.TextUtil;

public enum Commands {

    reload("reload");

    private final ConfigurationNode node;

    Commands(@NotNull String key) {
        this.node = EasyChat.getInstance().getLocaleConfig().getCommands().node(key);
    }

    public @NotNull String getString() {
        return TextUtil.fromNullable(node.getString());
    }

    public @NotNull Component getComponent() {
        return TextUtil.ampersandSerializer.deserialize(getString());
    }
}
