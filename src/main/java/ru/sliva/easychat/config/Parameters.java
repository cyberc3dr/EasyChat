package ru.sliva.easychat.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.easychat.EasyChat;
import ru.sliva.easychat.text.TextUtil;

public enum Parameters {

    changeDisplayName("change-display-name"),
    changeTabListName("change-tab-list-name"),
    changePlayerMessages("change-player-messages"),
    rangeMode("range-mode"),
    onlyPerWorld("only-per-world"),
    range("range"),
    locale("locale");

    private final ConfigurationNode node;

    Parameters(@NotNull String key) {
        this.node = EasyChat.getInstance().getPluginConfig().getParameters().node(key);
    }

    public boolean getBoolean() {
        return node.getBoolean();
    }

    public int getInt() {
        return node.getInt();
    }

    public @NotNull String getString() {
        return TextUtil.fromNullable(node.getString());
    }
}
