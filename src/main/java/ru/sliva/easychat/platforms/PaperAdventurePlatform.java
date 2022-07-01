package ru.sliva.easychat.platforms;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
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
import ru.sliva.easychat.config.Parameters;
import ru.sliva.easychat.locale.Commands;
import ru.sliva.easychat.locale.Messages;
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
    public EasyChat getEzChat() {
        return easyChat;
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
            Component join = TextUtil.replaceLiteral(Messages.join.getComponent(), "{player}", p.displayName());
            event.joinMessage(join);
        }
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        if(Parameters.changePlayerMessages.getBoolean()) {
            Player p = event.getPlayer();
            Component quit = TextUtil.replaceLiteral(Messages.quit.getComponent(), "{player}", p.displayName());
            event.quitMessage(quit);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(@NotNull AsyncChatEvent event) {
        Player p = event.getPlayer();

        Component message = event.message();
        if(p.hasPermission("easychat.color")) {
            message = TextUtil.color(message);
        }
        message = TextUtil.removeSpaces(message);

        if(Parameters.rangeMode.getBoolean()) {
            boolean global = false;

            if(TextUtil.startsWith(message, "!")) {
                message = TextUtil.replaceLiteralOnce(message, "!", Component.empty());
                global = true;
            } else {
                Set<Audience> viewers = event.viewers();
                for(Audience audience : new HashSet<>(viewers)) {
                    if(audience instanceof Player) {
                        Player player = (Player) audience;
                        if(easyChat.outOfRange(p.getLocation(), player.getLocation())) {
                            viewers.remove(audience);
                        }
                    }
                }
                if(viewers.size() == 2 && Bukkit.getOnlinePlayers().size() > 1) {
                    p.sendMessage(Messages.nobodyHeard.getComponent());
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length < 1) {
            easyChat.getPluginConfig().reloadConfig();
            easyChat.updateLocaleConfig();
            sender.sendMessage(Commands.reload.getComponent());
            return true;
        }
        return false;
    }
}
