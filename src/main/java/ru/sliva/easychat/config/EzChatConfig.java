package ru.sliva.easychat.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.easychat.EasyChat;

public final class EzChatConfig extends YamlConfig{

    private final ConfigurationNode root;

    public EzChatConfig(@NotNull EasyChat ezchat) {
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
