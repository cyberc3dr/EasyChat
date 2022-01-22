package ru.sliva.easychat.locale;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.easychat.EasyChat;
import ru.sliva.easychat.config.YamlConfig;

public final class LocaleConfig extends YamlConfig {

    private final ConfigurationNode root;

    public LocaleConfig(@NotNull EasyChat ezchat, @NotNull String fileName) {
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
