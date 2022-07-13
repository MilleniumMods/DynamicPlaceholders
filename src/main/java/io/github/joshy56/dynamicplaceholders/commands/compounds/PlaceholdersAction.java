package io.github.joshy56.dynamicplaceholders.commands.compounds;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.joshy56.dynamicplaceholders.commands.CompoundCommand;
import io.github.joshy56.dynamicplaceholders.util.Storage;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 2/7/2022.
 */
public class PlaceholdersAction extends CompoundCommand {

    public PlaceholdersAction(@NotNull Storage storage) {
        super("placeholders-action", "Elige una accion y ejecutala, juega con los placeholders", "<action>", Lists.newArrayList("action", "ph-action"), storage);
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender) {
        ConfigurationSection messages = getMessagesSection();
        if(messages == null)
            return false;

        String message = messages.getString("error.missed-action");
        if(Strings.isNullOrEmpty(message)) {
            message = "%dynamicplaceholders:environmental_prefix% &cEspecifica que accion queres ejecutar...";
            messages.set("error.missed-action", message);
            getStorage().getStorage().set(messages.getName(), messages);
            saveStorage();
            getStoragePlugin().getSLF4JLogger().warn(String.format("Missing command message '%s' please configure it", messages.getCurrentPath() + ".error.missed-action"));
        }

        sender.sendMessage(
                ChatColor.translateAlternateColorCodes(
                        '&',
                        PlaceholderAPI.setPlaceholders(null, message)
                )
        );
        return true;
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @Nullable Location location) {
        return new ArrayList<>(0);
    }

}
