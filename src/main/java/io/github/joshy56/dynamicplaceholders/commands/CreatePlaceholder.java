package io.github.joshy56.dynamicplaceholders.commands;

import com.google.common.collect.Lists;
import io.github.joshy56.dynamicplaceholders.DynamicPlaceholders;
import io.github.joshy56.dynamicplaceholders.hook.SerializablePlaceholder;
import io.github.joshy56.dynamicplaceholders.util.Storage;
import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
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
        super("createph", "Crea un placeholder", "<nombre> <valor>", Lists.newArrayList("cph"), storage);
        this.plugin = plugin;
        setPermission("dph.main.createph");
        getMessagesSection();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
        if (!isRegistered())
            return false;

        if (args.size() < 1) {
            sender.sendMessage(
                    Optional.ofNullable(getMessagesSection())
                            .map(
                                    messages -> PaperComponents.legacySectionSerializer().deserialize(
                                            messages.getString("error.missed-placeholder-id", "Missing message '" + messages.getCurrentPath() + ".error.missed-placeholder-id'")
                                    )
                            )
                            .orElse(Component.text("Por favor, coloca el nombre del placeholder", NamedTextColor.RED))
            );
            return false;
        }

        if (args.size() < 2) {
            sender.sendMessage(
                    Component.text(
                            "Por favor coloca el valor del placeholder",
                            NamedTextColor.RED
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
        sender.sendMessage(
                Component.text(
                        "Placeholder '" + identifier + "' y valor '" + value + "' creado correctamente, usable como %" + plugin.getOwnExpansion().getIdentifier() + "_" + identifier + "%",
                        NamedTextColor.GREEN
                )
        );
        return true;
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender) {
        return true;
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
