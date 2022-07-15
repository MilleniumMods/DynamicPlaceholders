package io.github.joshy56.dynamicplaceholders.commands.specific;

import com.google.common.collect.Lists;
import io.github.joshy56.dynamicplaceholders.commands.TranslatableCommand;
import io.github.joshy56.dynamicplaceholders.util.Storage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 14/7/2022.
 */
public class Help extends TranslatableCommand {
    
    public Help(@NotNull Storage storage) {
        super("help", "Mira como usar los demas comandos", "[command]", new ArrayList<>(0), storage);
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
        return false;
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender) {
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
