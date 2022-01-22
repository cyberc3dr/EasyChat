package ru.sliva.ezchat.platforms;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import ru.sliva.ezchat.EzChat;

public interface Platform extends Listener, Runnable, CommandExecutor {

    void init(@NotNull EzChat ezchat);

    EzChat getEzChat();

    void stop();
}
