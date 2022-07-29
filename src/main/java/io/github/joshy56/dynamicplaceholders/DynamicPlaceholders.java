package io.github.joshy56.dynamicplaceholders;

import io.github.joshy56.dynamicplaceholders.commands.compounds.Create;
import io.github.joshy56.dynamicplaceholders.commands.compounds.PlaceholdersAction;
import io.github.joshy56.dynamicplaceholders.commands.specific.CreateLocal;
import io.github.joshy56.dynamicplaceholders.commands.compounds.DynamicPlaceholdersCommand;
import io.github.joshy56.dynamicplaceholders.commands.specific.CreateRemote;
import io.github.joshy56.dynamicplaceholders.commands.specific.Reload;
import io.github.joshy56.dynamicplaceholders.commands.specific.Remove;
import io.github.joshy56.dynamicplaceholders.hook.PlaceholderStorage;
import io.github.joshy56.dynamicplaceholders.hook.PluginPlaceholderExpansion;
import io.github.joshy56.dynamicplaceholders.hook.SerializablePlaceholder;
import io.github.joshy56.dynamicplaceholders.util.Storage;
import me.clip.placeholderapi.libs.kyori.adventure.platform.bukkit.BukkitAudiences;
import me.clip.placeholderapi.libs.kyori.adventure.text.Component;
import me.clip.placeholderapi.libs.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

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

        setCommands();
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

    public Storage getCommandsMessages() {
        return commands;
    }

    private void setCommands() {
        DynamicPlaceholdersCommand mainCommand = new DynamicPlaceholdersCommand(getCommandsMessages());
        PlaceholdersAction actionCommand = new PlaceholdersAction(getCommandsMessages());
        Create createCommand = new Create(getCommandsMessages());
        createCommand.register(getName(), new CreateLocal(this, getCommandsMessages()));
        createCommand.register(getName(), new CreateRemote(getCommandsMessages()));

        actionCommand.register(getName(), createCommand);
        actionCommand.register(getName(), new Remove(getCommandsMessages(), this));

        mainCommand.register(getName(), actionCommand);
        mainCommand.register(getName(), new Reload(getCommandsMessages(), this));
        Bukkit.getCommandMap().register(
                getName(),
                mainCommand
        );
    }
}
