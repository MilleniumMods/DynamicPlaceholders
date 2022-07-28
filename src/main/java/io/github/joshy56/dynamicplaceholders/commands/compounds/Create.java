package io.github.joshy56.dynamicplaceholders.commands.compounds;

import io.github.joshy56.dynamicplaceholders.commands.CompoundCommand;
import io.github.joshy56.dynamicplaceholders.util.Storage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 27/7/2022.
 */
public class Create extends CompoundCommand {

    public Create(@NotNull Storage storage) {
        super("create", "CreateLocal a placeholder, choose the supplier of his value", "<type>", new ArrayList<>(0), storage);
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
        return super.execute(sender, args);
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @Nullable Location location) {
        return null;
    }
}
