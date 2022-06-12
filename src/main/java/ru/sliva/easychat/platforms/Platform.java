package ru.sliva.easychat.platforms;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import ru.sliva.easychat.EasyChat;

public interface Platform extends Listener, CommandExecutor {

    void init(@NotNull EasyChat ezchat);

    EasyChat getEzChat();

    void stop();

    void updatePlayerData(Player player);
}
