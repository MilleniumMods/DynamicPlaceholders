package io.github.joshy56.dynamicplaceholders.commands.compounds;

import com.google.common.collect.Lists;
import io.github.joshy56.dynamicplaceholders.commands.CompoundCommand;
import io.github.joshy56.dynamicplaceholders.util.Storage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 2/7/2022.
 */
public class PlaceholdersAction extends CompoundCommand {

    protected PlaceholdersAction(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases, @NotNull Storage storage) {
        super("placeholders-action", description, "<action>", Lists.newArrayList("action", "ph-action"), storage);
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender) {
        sender.sendMessage(
                getMessagesSection().getString("error.not-action", "Porfa, escribi que accion deseas realizar")
        );
        return true;
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @Nullable Location location) {
        return new ArrayList<>(0);
    }

}
