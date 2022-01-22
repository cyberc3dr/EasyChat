package ru.sliva.ezchat.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.ezchat.EzChat;

public final class EzChatConfig extends YamlConfig{

    private final ConfigurationNode root;

    public EzChatConfig(@NotNull EzChat ezchat) {
        super(ezchat, "config.yml");
        this.root = getRoot();
    }

    public ConfigurationNode getParameters() {
        return root.node("parameters");
    }

    public ConfigurationNode getFormat() {
        return root.node("format");
    }
}
