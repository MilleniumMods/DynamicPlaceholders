package io.github.joshy56.dynamicplaceholders.commands.specific;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.joshy56.dynamicplaceholders.DynamicPlaceholders;
import io.github.joshy56.dynamicplaceholders.commands.TranslatableCommand;
import io.github.joshy56.dynamicplaceholders.hook.PluginPlaceholderExpansion;
import io.github.joshy56.dynamicplaceholders.util.Storage;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 26/7/2022.
 */
public class Remove extends TranslatableCommand {
    private final DynamicPlaceholders plugin;

    public Remove(@NotNull Storage storage, @NotNull final DynamicPlaceholders plugin) {
        super("remove", "Remove an custom placeholder", "<placeholder-id>", new ArrayList<>(0), storage);
        this.plugin = Preconditions.checkNotNull(
                plugin, "Plugin instance chose is null..."
        );
        setPermission("dph.remove");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
        if (super.execute(sender, args))
            return true;

        String placeholderID = args.get(0);
        if (Strings.isNullOrEmpty(placeholderID)) {
            sender.sendMessage(
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            PlaceholderAPI.setPlaceholders(
                                    (sender instanceof Player) ? (Player) sender : null,
                                    Optional.ofNullable(getMessagesSection())
                                            .map(messages -> messages.getString("error.missed_id"))
                                            .filter(message -> !Strings.isNullOrEmpty(message))
                                            .orElse("%dynamicplaceholders:environmental_prefix% &cYou missed the placeholder id")
                                            .replaceAll("%cmd_description%", getDescription())
                                            .replaceAll("%cmd_name%", getName())
                                            .replaceAll("%cmd_permission%", Optional.ofNullable(getPermission()).orElse("without-permission"))
                            )
                    )
            );

            return false;
        }

        PluginPlaceholderExpansion expansion = plugin.getPlaceholderStorage().getExpansions().get(placeholderID);
        if (expansion == null) {
            sender.sendMessage(
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            PlaceholderAPI.setPlaceholders(
                                    (sender instanceof Player) ? (Player) sender : null,
                                    Optional.ofNullable(getMessagesSection())
                                            .map(messages -> messages.getString("error.placeholder_not_found"))
                                            .filter(message -> !Strings.isNullOrEmpty(message))
                                            .orElse("%dynamicplaceholders:environmental_prefix% &cPlaceholder doesn't exists")
                                            .replaceAll("%cmd_description%", getDescription())
                                            .replaceAll("%cmd_name%", getName())
                                            .replaceAll("%cmd_permission%", Optional.ofNullable(getPermission()).orElse("without-permission"))
                            )
                    )
            );

            return false;
        }

        if (expansion.isRegistered())
            expansion.unregister();

        if (!plugin.getPlaceholderStorage().getExpansions().remove(placeholderID, expansion)) {
            sender.sendMessage(
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            PlaceholderAPI.setPlaceholders(
                                    (sender instanceof Player) ? (Player) sender : null,
                                    Optional.ofNullable(getMessagesSection())
                                            .map(messages -> messages.getString("error.placeholder_already_removed"))
                                            .filter(message -> !Strings.isNullOrEmpty(message))
                                            .orElse("%dynamicplaceholders:environmental_prefix% &cThis placeholder is already removed (Possible concurrent operation error)")
                                            .replaceAll("%cmd_description%", getDescription())
                                            .replaceAll("%cmd_name%", getName())
                                            .replaceAll("%cmd_permission%", Optional.ofNullable(getPermission()).orElse("without-permission"))
                            )
                    )
            );

            return false;
        }

        sender.sendMessage(
                ChatColor.translateAlternateColorCodes(
                        '&',
                        PlaceholderAPI.setPlaceholders(
                                (sender instanceof Player) ? (Player) sender : null,
                                Optional.ofNullable(getMessagesSection())
                                        .map(messages -> messages.getString("success"))
                                        .filter(message -> !Strings.isNullOrEmpty(message))
                                        .orElse("%dynamicplaceholders:environmental_prefix% &aPlaceholder successfully removed")
                                        .replaceAll("%cmd_description%", getDescription())
                                        .replaceAll("%cmd_name%", getName())
                                        .replaceAll("%cmd_permission%", Optional.ofNullable(getPermission()).orElse("without-permission"))
                        )
                )
        );

        return true;
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender) {
        if (getMessagesSection() == null)
            return true;

        sender.sendMessage(
                ChatColor.translateAlternateColorCodes(
                        '&',
                        PlaceholderAPI.setPlaceholders(
                                (sender instanceof Player) ? (Player) sender : null,
                                Optional.ofNullable(getMessagesSection())
                                        .map(messages -> messages.getString("error.missed_id"))
                                        .filter(message -> !Strings.isNullOrEmpty(message))
                                        .orElse("%dynamicplaceholders:environmental_prefix% &cYou missed the placeholder id")
                                        .replaceAll("%cmd_description%", getDescription())
                                        .replaceAll("%cmd_name%", getName())
                                        .replaceAll("%cmd_permission%", Optional.ofNullable(getPermission()).orElse("without-permission"))
                        )
                )
        );

        return true;
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
                                        .orElse("%dynamicplaceholders:environmental_prefix% &eRemove a placeholder from the storage")
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
        return (args.size() == 1) ? Lists.newArrayList("<placeholder-ID>") : tabComplete(sender, location);
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @Nullable Location location) {
        return new ArrayList<>(0);
    }
}
