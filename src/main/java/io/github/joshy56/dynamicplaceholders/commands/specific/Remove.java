package io.github.joshy56.dynamicplaceholders.commands.specific;

import io.github.joshy56.dynamicplaceholders.commands.TranslatableCommand;
import io.github.joshy56.dynamicplaceholders.util.Storage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 26/7/2022.
 */
public class Remove extends TranslatableCommand {

    public Remove(@NotNull Storage storage) {
        super("remove", "Remove an custom placeholder", "<placeholder-id>", new ArrayList<>(0), storage);
    }

    @Override
    protected boolean help(@NotNull CommandSender sender) {
        return false;
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull List<String> args, @Nullable Location location) {
        return null;
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @Nullable Location location) {
        return null;
    }
}
