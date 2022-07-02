package io.github.joshy56.dynamicplaceholders;

import io.github.joshy56.dynamicplaceholders.commands.CreatePlaceholder;
import io.github.joshy56.dynamicplaceholders.commands.DynamicPlaceholdersCommand;
import io.github.joshy56.dynamicplaceholders.hook.PlaceholderStorage;
import io.github.joshy56.dynamicplaceholders.hook.PluginPlaceholderExpansion;
import io.github.joshy56.dynamicplaceholders.hook.SerializablePlaceholder;
import io.github.joshy56.dynamicplaceholders.util.Storage;
import me.clip.placeholderapi.libs.kyori.adventure.platform.bukkit.BukkitAudiences;
import me.clip.placeholderapi.libs.kyori.adventure.text.Component;
import me.clip.placeholderapi.libs.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 10/6/2022.
 */
public class DynamicPlaceholders extends JavaPlugin {
    private final PlaceholderStorage placeholderStorage;
    private Storage commands;
    private BukkitAudiences adventure;
    private final NamespacedKey expansionIdentifier;

    public DynamicPlaceholders() {
        this.placeholderStorage = new PlaceholderStorage(this, null);
        this.expansionIdentifier = NamespacedKey.fromString("environmental", this);
    }

    @Override
    public void onLoad() {
        ConfigurationSerialization.registerClass(PluginPlaceholderExpansion.class);
        ConfigurationSerialization.registerClass(SerializablePlaceholder.class);
        commands = new Storage(this, "commands.yml").reload();
    }

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);
        placeholderStorage.loadAll();
        if (getOwnExpansion() == null)
            placeholderStorage.add(
                    new PluginPlaceholderExpansion(this, expansionIdentifier.value())
            );
        adventure.console().sendMessage(
                Component.text(
                        "Loaded " + placeholderStorage.getExpansions().size() + " expansions",
                        NamedTextColor.GOLD
                )
        );
        DynamicPlaceholdersCommand mainCommand = new DynamicPlaceholdersCommand(getCommands());
        CreatePlaceholder createPlaceholder = new CreatePlaceholder(this, getCommands());
        mainCommand.register(getName(), createPlaceholder);

        Bukkit.getConsoleSender().sendMessage(
                new String[]{
                        "DynamicPlaceholders:Debug of:",
                        "Class '" + getClass().getSimpleName() + "' on",
                        "Method: 'onEnable' with 0 params",
                        "Local Variable: 'mainCommand' value of",
                        "Method: 'knownCommands' = " + mainCommand.getKnownCommands()
                }
        );

        Bukkit.getCommandMap().register(getName(), mainCommand);
    }

    @Override
    public void onDisable() {
        adventure.close();
        placeholderStorage.saveAll();
        adventure.console().sendMessage(
                Component.text(
                        "Saved " + placeholderStorage.getExpansions().size() + " expansions",
                        NamedTextColor.GOLD
                )
        );
    }

    public PlaceholderStorage getPlaceholderStorage() {
        return placeholderStorage;
    }

    public PluginPlaceholderExpansion getOwnExpansion() {
        return getPlaceholderStorage().getExpansions().get(expansionIdentifier.asString());
    }

    public Storage getCommands() {
        return commands;
    }
}
