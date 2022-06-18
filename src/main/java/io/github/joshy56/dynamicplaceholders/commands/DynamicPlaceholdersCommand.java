package io.github.joshy56.dynamicplaceholders.commands;

import io.github.joshy56.dynamicplaceholders.DynamicPlaceholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 17/6/2022.
 */
public class DynamicPlaceholdersCommand extends CompoundCommand {

    public DynamicPlaceholdersCommand() {
        this("dynamicplaceholders", "Comando principal de DynamicPlaceholders.", "/dph <subcommand> [args...]", List.of("dph", "dynph"));
    }

    protected DynamicPlaceholdersCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender) {
        sender.sendMessage(
                Component.text(
                                "Version: ", NamedTextColor.GREEN
                        )
                        .decorate(TextDecoration.BOLD)
                        .append(
                                Component.text(
                                        JavaPlugin.getPlugin(DynamicPlaceholders.class)
                                                .getDescription()
                                                .getVersion(),
                                        NamedTextColor.DARK_GREEN
                                )
                                        .decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)
                        )
        );
        return true;
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @Nullable Location location) {
        return List.of();
    }
}