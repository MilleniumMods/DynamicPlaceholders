package io.github.joshy56.dynamicplaceholders.commands;

import io.github.joshy56.dynamicplaceholders.DynamicPlaceholders;
import io.github.joshy56.dynamicplaceholders.hook.SerializablePlaceholder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 14/6/2022.
 */
public class CreatePlaceholder extends Command {
    private final DynamicPlaceholders plugin;

    public CreatePlaceholder(@NotNull final DynamicPlaceholders plugin) {
        super("createph");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!label.equalsIgnoreCase(getLabel()))
            return false;
        if(args.length < 1) {
            sender.sendMessage(
                    Component.text(
                            "Por favor coloca el nombre del placeholder",
                            NamedTextColor.RED
                    )
            );
            return false;
        }

        if(args.length < 2) {
            sender.sendMessage(
                    Component.text(
                            "Por favor coloca el valor del placeholder",
                            NamedTextColor.RED
                    )
            );
            return false;
        }

        String identifier = args[0];
        String value = String.join(
                " ",
                Arrays.copyOfRange(args, 1, args.length)
        );
        /*SerializablePlaceholder placeholder = new SerializablePlaceholder(value);
        plugin.getOwnExpansion().getPlaceholdersProcessors().put(
                identifier,
                placeholder
        );*/
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
}
