package ru.sliva.easychat.platforms;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import ru.sliva.easychat.EasyChat;
import ru.sliva.easychat.config.Format;
import ru.sliva.easychat.config.Parameters;
import ru.sliva.easychat.locale.Commands;
import ru.sliva.easychat.locale.Messages;
import ru.sliva.easychat.text.TextUtil;

import java.util.HashSet;
import java.util.Set;

public final class LegacyPlatform implements Platform {

    private EasyChat ezchat;
    private BukkitAudiences adventure;

    @Override
    public void init(@NotNull EasyChat ezchat) {
        this.ezchat = ezchat;
        this.adventure = BukkitAudiences.create(ezchat);
    }

    @Override
    public EasyChat getEzChat() {
        return ezchat;
    }

    @Override
    public void stop() {
        adventure.close();
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player p = event.getPlayer();
        updatePlayerData(p);
        if(Parameters.changePlayerMessages.getBoolean()) {
            event.setJoinMessage(null);
            Component join = TextUtil.replaceLiteral(Messages.join.getComponent(), "{player}", p.displayName());
            for(Player player : Bukkit.getOnlinePlayers()) {
                Audience audience = adventure.player(player);
                audience.sendMessage(join);
            }
            adventure.sender(Bukkit.getConsoleSender()).sendMessage(join);
        }
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        if(Parameters.changePlayerMessages.getBoolean()) {
            event.setQuitMessage(null);
            Player p = event.getPlayer();
            Component quit = TextUtil.replaceLiteral(Messages.quit.getComponent(), "{player}", p.displayName());
            for(Player player : Bukkit.getOnlinePlayers()) {
                Audience audience = adventure.player(player);
                audience.sendMessage(quit);
            }
            adventure.sender(Bukkit.getConsoleSender()).sendMessage(quit);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        Audience audience = adventure.player(p);

        Component message = TextUtil.paragraphSerializer.deserialize(event.getMessage());
        if(p.hasPermission("easychat.color")) {
            message = TextUtil.color(message);
        }
        for(@RegExp String pattern : Format.patterns.getStringList()) {
            message = TextUtil.replace(message, pattern, "");
        }
        message = TextUtil.removeSpaces(message);

        if(Parameters.rangeMode.getBoolean()) {
            boolean global = false;

            if(TextUtil.startsWith(message, "!")) {
                message = TextUtil.replaceLiteralOnce(message, "!", Component.empty());
                global = true;
            } else {
                Set<Player> viewers = event.getRecipients();
                for(Player player : new HashSet<>(viewers)) {
                    if(audience instanceof Player) {
                        if(ezchat.outOfRange(p.getLocation(), player.getLocation())) {
                            viewers.remove(audience);
                        }
                    }
                }
                if(viewers.size() == 1 && Bukkit.getOnlinePlayers().size() > 1) {
                    audience.sendMessage(Messages.nobodyHeard.getComponent());
                }
            }

            event.setCancelled(true);

            if(!TextUtil.isEmpty(message)) {
                Component format = ezchat.addChannel(ezchat.render(p, TextUtil.getDisplayName(p), message), global);
                for(Player player : Bukkit.getOnlinePlayers()) {
                    adventure.player(player).sendMessage(format);
                }
                adventure.sender(Bukkit.getConsoleSender()).sendMessage(format);
            }
        } else {
            event.setCancelled(true);

            if(!TextUtil.isEmpty(message)) {
                Component format = ezchat.render(p, TextUtil.getDisplayName(p), message);
                for(Player player : Bukkit.getOnlinePlayers()) {
                    adventure.player(player).sendMessage(format);
                }
                adventure.sender(Bukkit.getConsoleSender()).sendMessage(format);
            }
        }
    }

    public @NotNull String getDisplayName(@NotNull Player player) {
        LuckPerms luckPerms = ezchat.getLuckPerms();
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        CachedMetaData metaData = user.getCachedData().getMetaData();
        String prefix = metaData.getPrefix();
        if(prefix == null) {
            prefix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', prefix + player.getName());
    }

    public @NotNull String getTabListName(@NotNull Player player) {
        LuckPerms luckPerms = ezchat.getLuckPerms();
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
        return ChatColor.translateAlternateColorCodes('&', prefix + player.getName() + suffix);
    }

    @Override
    public void updatePlayerData(Player player) {
        if(Parameters.changeTabListName.getBoolean()) {
            player.setPlayerListName(getTabListName(player));
        }
        if(Parameters.changeDisplayName.getBoolean()) {
            player.setDisplayName(getDisplayName(player));
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length < 1) {
            ezchat.getPluginConfig().reloadConfig();
            ezchat.updateLocaleConfig();
            adventure.sender(sender).sendMessage(Commands.reload.getComponent());
            return true;
        }
        return false;
    }
}
