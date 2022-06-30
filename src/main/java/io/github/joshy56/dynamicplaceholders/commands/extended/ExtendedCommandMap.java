package io.github.joshy56.dynamicplaceholders.commands.extended;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 28/6/2022.
 */
public class ExtendedCommandMap implements CommandMap {
    private final Map<String, Command>

    @Override
    public void registerAll(@NotNull String fallbackPrefix, @NotNull List<Command> commands) {
        if(commands == null || commands.isEmpty())
            return;

        commands.forEach(command -> register(fallbackPrefix, command));
    }

    @Override
    public boolean register(@NotNull String alias, @NotNull String fallbackPrefix, @NotNull Command command) {

        return false;
    }

    @Override
    public boolean register(@NotNull String fallbackPrefix, @NotNull Command command) {
        return register(command.getName(), fallbackPrefix, command);
    }

    @Override
    public boolean dispatch(@NotNull CommandSender sender, @NotNull String commandLine) throws CommandException {
        return false;
    }

    @Override
    public void clearCommands() {

    }

    @Override
    public @Nullable Command getCommand(@NotNull String alias) {
        return null;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String commandLine) throws IllegalArgumentException {
        return null;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String commandLine, @Nullable Location location) throws IllegalArgumentException {
        return null;
    }

    @Override
    public @NotNull Map<String, Command> getKnownCommands() {
        return null;
    }
}
