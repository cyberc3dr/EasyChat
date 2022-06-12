package ru.sliva.easychat;

import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import ru.sliva.easychat.config.EzChatConfig;
import ru.sliva.easychat.config.Format;
import ru.sliva.easychat.config.Parameters;
import ru.sliva.easychat.locale.HoverEvents;
import ru.sliva.easychat.locale.LocaleConfig;
import ru.sliva.easychat.platforms.LegacyPlatform;
import ru.sliva.easychat.platforms.PaperAdventurePlatform;
import ru.sliva.easychat.platforms.Platform;
import ru.sliva.easychat.text.TextUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class EasyChat extends JavaPlugin implements Runnable{

    private EzChatConfig config;
    private LocaleConfig localeConfig;
    private BukkitTask task;
    private LuckPerms luckperms;
    private Platform platform;

    private static EasyChat instance;

    @Override
    public void onEnable() {
        instance = this;

        config = new EzChatConfig(this);

        updateLocaleConfig();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckperms = provider.getProvider();
        }

        Platform platform;
        if(PaperLib.isPaper() && PaperLib.isVersion(16, 5)) {
            // Paper with adventure
            platform = new PaperAdventurePlatform();
        } else {
            // Generic CraftBukkit implementation
            platform = new LegacyPlatform();
        }
        Bukkit.getPluginManager().registerEvents(platform, this);
        platform.init(this);

        this.platform = platform;

        if(Parameters.changeTabListName.getBoolean() || Parameters.changeDisplayName.getBoolean()) {
            task = Bukkit.getScheduler().runTaskTimer(this, this, 0, 20);
        }

        Objects.requireNonNull(getCommand("ezchat")).setExecutor(platform);
    }

    public void updateLocaleConfig() {
        localeConfig = new LocaleConfig(this, "messages-" + Parameters.locale.getString() + ".yml");
    }

    public static EasyChat getInstance() {
        return instance;
    }

    @Contract(pure = true)
    @Override
    public @NotNull @Unmodifiable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(task.getTaskId());
        platform.stop();
    }

    public boolean outOfRange(@NotNull Location l, @NotNull Location ll) {
        if (l.equals(ll)) {
            return false;
        } else if (l.getWorld() != ll.getWorld()) {
            return true;
        }
        if(!Parameters.onlyPerWorld.getBoolean()) {
            return l.distanceSquared(ll) > Parameters.range.getInt();
        }
        return true;
    }

    public @NotNull Component addChannel(@NotNull Component format, boolean global) {
        Component channel = global ? Format.globalChat.getComponent() : Format.localChat.getComponent();
        return channel.append(format);
    }

    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message) {
        TextComponent textMessage = (TextComponent) message;
        textMessage = textMessage.hoverEvent(HoverEvent.showText(HoverEvents.copyMessage.getComponent()));
        textMessage = textMessage.clickEvent(ClickEvent.copyToClipboard(textMessage.content()));

        Component sendMessage = TextUtil.replaceLiteral(HoverEvents.tellMessage.getComponent(), "{player}", sourceDisplayName);
        TextComponent textDisplayName = (TextComponent) sourceDisplayName;
        textDisplayName = textDisplayName.hoverEvent(HoverEvent.showText(sendMessage));
        textDisplayName = textDisplayName.clickEvent(ClickEvent.suggestCommand("/tell " + source.getName() + " "));

        Component rendered = Format.format.getComponent();
        rendered = TextUtil.replaceLiteral(rendered, "{player}", textDisplayName);
        rendered = TextUtil.replaceLiteral(rendered, "{message}", textMessage);
        return rendered;
    }

    public LuckPerms getLuckPerms() {
        return luckperms;
    }

    public @NotNull EzChatConfig getPluginConfig() {
        return config;
    }

    public LocaleConfig getLocaleConfig() {
        return localeConfig;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(p -> platform.updatePlayerData(p));
    }
}
