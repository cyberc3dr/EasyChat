package ru.sliva.easychat.platforms;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.sliva.easychat.EasyChat;
import ru.sliva.easychat.config.api.Commands;
import ru.sliva.easychat.config.api.Messages;
import ru.sliva.easychat.config.api.Parameters;
import ru.sliva.easychat.text.TextUtil;

import java.util.HashSet;
import java.util.Set;

public final class PaperAdventurePlatform implements Platform{

    private EasyChat easyChat;

    @Override
    public void init(@NotNull EasyChat easyChat) {
        this.easyChat = easyChat;
    }

    @Override
    public void stop() {}

    @Override
    public void updatePlayerData(Player player) {
        if(Parameters.changeTabListName.getBoolean()) {
            player.playerListName(getTabListName(player));
        }
        if(Parameters.changeDisplayName.getBoolean()) {
            player.displayName(getDisplayName(player));
        }
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player p = event.getPlayer();
        updatePlayerData(p);
        if(Parameters.changePlayerMessages.getBoolean()) {
            if(!Parameters.removePlayerMessages.getBoolean()) {
                Component join = TextUtil.replaceLiteral(
                        TextUtil.ampersandSerializer.deserialize(
                                PlaceholderAPI.setPlaceholders(p, Messages.join.getString())),
                        "{player}", p.displayName()
                );

                event.joinMessage(join);
            } else {
                event.joinMessage(null);
            }
        }
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        if(Parameters.changePlayerMessages.getBoolean()) {
            if(!Parameters.removePlayerMessages.getBoolean()) {
                Player p = event.getPlayer();

                Component quit = TextUtil.replaceLiteral(
                        TextUtil.ampersandSerializer.deserialize(
                                PlaceholderAPI.setPlaceholders(p, Messages.quit.getString())),
                        "{player}", p.displayName()
                );

                event.quitMessage(quit);
            } else {
                event.quitMessage(null);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(@NotNull AsyncChatEvent event) {
        Player p = event.getPlayer();

        Component message = TextUtil.removeSpaces(
                TextUtil.insertPlaceholders(p,
                        event.message()
                )
        );
        if(p.hasPermission("easychat.color")) {
            message = TextUtil.color(message);
        }

        if(Parameters.rangeMode.getBoolean()) {
            boolean global = false;

            if(TextUtil.startsWith(message, "!")) {
                message = TextUtil.replaceLiteralOnce(message, "!", Component.empty());
                global = true;
            } else {
                Set<Audience> viewers = event.viewers();
                for(Audience audience : new HashSet<>(viewers)) {
                    if(audience instanceof Player player) {
                        if(easyChat.outOfRange(p.getLocation(), player.getLocation())) {
                            viewers.remove(audience);
                        }
                    }
                }
            }
            event.message(message);

            if(message.equals(Component.empty())) {
                event.setCancelled(true);
            } else {
                event.renderer(constructChatRenderer(global));
            }
        } else {
            event.message(message);
            if(message.equals(Component.empty())) {
                event.setCancelled(true);
            } else {
                event.renderer(constructChatRenderer());
            }
        }
    }

    @Contract(value = "_ -> new", pure = true)
    public @NotNull ChatRenderer constructChatRenderer(boolean global) {
        return ChatRenderer.viewerUnaware((source, sourceDisplayName, message) ->
                easyChat.addChannel(easyChat.render(source, sourceDisplayName, message), global));
    }

    public @NotNull ChatRenderer constructChatRenderer() {
        return ChatRenderer.viewerUnaware((source, sourceDisplayName, message) ->
                easyChat.render(source, sourceDisplayName, message));
    }

    public @NotNull Component getDisplayName(@NotNull Player player) {
        LuckPerms luckPerms = easyChat.getLuckPerms();
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        CachedMetaData metaData = user.getCachedData().getMetaData();
        String prefix = metaData.getPrefix();
        if(prefix == null) {
            prefix = "";
        }
        return TextUtil.ampersandSerializer.deserialize(prefix + player.getName());
    }

    public @NotNull Component getTabListName(@NotNull Player player) {
        LuckPerms luckPerms = easyChat.getLuckPerms();
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        CachedMetaData metaData = user.getCachedData().getMetaData();
        String prefix = metaData.getPrefix();
        if(prefix == null) {
            prefix = "";
        }
        String suffix = metaData.getSuffix();
        if(suffix == null) {
            suffix = "";
        }
        return TextUtil.ampersandSerializer.deserialize(prefix + player.getName() + suffix);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(args.length < 1) {
            easyChat.getConfig().reloadConfig();
            sender.sendMessage(
                    TextUtil.ampersandSerializer.deserialize(
                            Commands.reload.getString()
                    )
            );
            return true;
        }
        return false;
    }
}
