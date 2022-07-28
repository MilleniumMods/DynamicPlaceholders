package io.github.joshy56.dynamicplaceholders.commands.specific;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.joshy56.dynamicplaceholders.DynamicPlaceholders;
import io.github.joshy56.dynamicplaceholders.commands.TranslatableCommand;
import io.github.joshy56.dynamicplaceholders.util.Storage;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 27/7/2022.
 */
public class Modify extends TranslatableCommand {
    private final DynamicPlaceholders plugin;

    public Modify(@NotNull Storage storage, @NotNull final DynamicPlaceholders plugin) {
        super("modify", "Modify an placeholder value", "<placeholder-id> <new-value>", new ArrayList<>(0), storage);
        this.plugin = Preconditions.checkNotNull(
                plugin,
                "Plugin instance chose is null..."
        );
        setPermission("dph.modify");
    }



    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
        if(super.execute(sender, args))
            return true;

        return false;
    }

    @Override
    protected boolean help(@NotNull CommandSender sender) {
        sender.sendMessage(
                ChatColor.translateAlternateColorCodes(
                        '&',
                        PlaceholderAPI.setPlaceholders(
                                (sender instanceof Player) ? (Player) sender : null,
                                Optional.ofNullable(getMessagesSection())
                                        .map(messages -> messages.getString("help"))
                                        .filter(message -> !Strings.isNullOrEmpty(message))
                                        .orElse("%dynamicplaceholders:environmental_prefix% &eModify the value of a placeholder")
                                        .replaceAll("%cmd_description%", getDescription())
                                        .replaceAll("%cmd_name%", getName())
                                        .replaceAll("%cmd_permission%", Optional.ofNullable(getPermission()).orElse("without-permission"))
                        )
                )
        );

        return true;
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull List<String> args, @Nullable Location location) {
        if(args.size() == 1)
            return Collections.singletonList("<placeholder-id>");

        if(args.size() == 2)
            return Collections.singletonList("<new_value>");

        return tabComplete(sender, location);
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @Nullable Location location) {
        return Collections.emptyList();
    }
}
