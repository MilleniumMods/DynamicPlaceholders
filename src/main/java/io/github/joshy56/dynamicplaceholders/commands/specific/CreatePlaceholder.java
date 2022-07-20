package io.github.joshy56.dynamicplaceholders.commands.specific;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.joshy56.dynamicplaceholders.DynamicPlaceholders;
import io.github.joshy56.dynamicplaceholders.commands.TranslatableCommand;
import io.github.joshy56.dynamicplaceholders.hook.SerializablePlaceholder;
import io.github.joshy56.dynamicplaceholders.util.Storage;
import io.papermc.paper.text.PaperComponents;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 14/6/2022.
 */
public class CreatePlaceholder extends TranslatableCommand {
    private final DynamicPlaceholders plugin;

    public CreatePlaceholder(@NotNull final DynamicPlaceholders plugin, @NotNull final Storage storage) {
        super("create", "Crea un placeholder", "<nombre> <valor>", Lists.newArrayList("add", "new"), storage);
        this.plugin = plugin;
        setPermission("dph.create");
        getMessagesSection();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
        if(super.execute(sender, args))
            return true;

        ConfigurationSection messages = getMessagesSection();
        if (messages == null)
            return false;

        String message;
        if (args.size() < 1) {
            message = messages.getString("error.missed-placeholder-id");
            if (Strings.isNullOrEmpty(message)) {
                message = "%dynamicplaceholders:environmental_prefix% &cEspecifica que id debe tener este placeholder...";
                messages.set("error.missed-placeholder-id", message);
                getStorage().getStorage().set(messages.getName(), messages);
                saveStorage();
                getStoragePlugin().getSLF4JLogger().warn(String.format("Missing command message '%s' please configure it", messages.getCurrentPath() + ".error.missed-placeholder-id"));
            }

            sender.sendMessage(
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            PlaceholderAPI.setPlaceholders(null, message)
                    )
            );
            return false;
        }

        if (args.size() < 2) {
            message = messages.getString("error.missed-placeholder-value");
            if (Strings.isNullOrEmpty(message)) {
                message = "%dynamicplaceholders:environmental_prefix% &cEspecifica que valor debe tener este placeholder...";
                messages.set("error.missed-placeholder-value", message);
                getStorage().getStorage().set(messages.getName(), messages);
                saveStorage();
                getStoragePlugin().getSLF4JLogger().warn(String.format("Missing command message '%s' please configure it", messages.getCurrentPath() + ".error.missed-placeholder-value"));
            }

            sender.sendMessage(
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            PlaceholderAPI.setPlaceholders(null, message)
                    )
            );
            return false;
        }

        String identifier = args.get(0);
        String value = args.get(1);
        plugin.getPlaceholderStorage().addPlaceholder(
                plugin.getOwnExpansion().getIdentifier(),
                identifier,
                new SerializablePlaceholder(value)
        );

        message = messages.getString("success");
        if (Strings.isNullOrEmpty(message)) {
            message = "%dynamicplaceholders:environmental_prefix% &a¡Placeholder creado! ID: '&2" + identifier + "&a' Valor: '&2" + value + "&a'";
            messages.set("success", message);
            getStorage().getStorage().set(messages.getName(), messages);
            saveStorage();
            getStoragePlugin().getSLF4JLogger().warn(String.format("Missing command message '%s' please configure it", messages.getCurrentPath() + ".success"));
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
    protected boolean help(@NotNull CommandSender sender, @NotNull List<String> args) {
        return super.help(sender, args); //To do: Return help for the unique two args...
    }

    @Override
    protected boolean help(@NotNull CommandSender sender) {
        sender.sendMessage("aa");
        sender.sendMessage(
                Component.newline()
                        .append(
                                Component.text(
                                        ChatColor.translateAlternateColorCodes(
                                                '&',
                                                PlaceholderAPI.setPlaceholders(
                                                        ((sender instanceof Player) ? (Player) sender : null),
                                                        "%dynamicplaceholders:environmental_prefix% &6- &eComandos"
                                                )
                                        )
                                )
                        )
                        .append(Component.newline())
                        .append(Component.text(getName(), NamedTextColor.YELLOW))
                        .append(Component.text(" >> ", NamedTextColor.GOLD))
                        .append(Component.text(getDescription(), NamedTextColor.YELLOW))
        );
        return true;
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull List<String> args, @Nullable Location location) {
        return tabComplete(sender, location);
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @Nullable Location location) {
        return Lists.newArrayList("<placeholderID> <placeholderValue>");
    }

    @Override
    public @NotNull Command setAliases(@NotNull List<String> aliases) {
        return super.setAliases(aliases);
    }
}
