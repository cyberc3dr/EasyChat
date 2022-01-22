package ru.sliva.ezchat.locale;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.ezchat.EzChat;
import ru.sliva.ezchat.config.YamlConfig;

public final class LocaleConfig extends YamlConfig {

    private final ConfigurationNode root;

    public LocaleConfig(@NotNull EzChat ezchat, @NotNull String fileName) {
        super(ezchat, fileName);
        this.root = getRoot();
    }

    public ConfigurationNode getHoverEvents() {
        return root.node("hover-events");
    }

    public ConfigurationNode getMessages() {
        return root.node("messages");
    }

    public ConfigurationNode getCommands() {
        return root.node("commands");
    }
}
