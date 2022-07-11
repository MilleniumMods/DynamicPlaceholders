package io.github.joshy56.dynamicplaceholders.commands;

import io.github.joshy56.dynamicplaceholders.util.Storage;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 19/6/2022.
 */
public abstract class TranslatableCommand extends ExtendedCommand {
    protected Storage storage;

    protected TranslatableCommand(@NotNull String name, @NotNull final Storage storage) {
        super(name);
        if(storage.getStorage().getName().equalsIgnoreCase(getName()))
            this.storage = storage;
        else {
            this.storage = storage;
            ConfigurationSection section = this.storage.getStorage().getConfigurationSection(getName());
            if(section == null)
                section = this.storage.getStorage().createSection(getName());
            this.storage.getStorage().set(getName(), section);
        }
        storage.save();
    }

    protected TranslatableCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases, @NotNull final Storage storage) {
        super(name, description, usageMessage, aliases);
        if(storage.getStorage().getName().equalsIgnoreCase(getName()))
            this.storage = storage;
        else {
            this.storage = storage;
            ConfigurationSection section = this.storage.getStorage().getConfigurationSection(getName());
            if(section == null)
                section = this.storage.getStorage().createSection(getName());
            this.storage.getStorage().set(getName(), section);
        }
        storage.save();
    }

    @Nullable
    public ConfigurationSection getMessagesSection() {
        return isRegistered() ? storage.getStorage().getConfigurationSection(getName()) : null;
    }

    public void reloadStorage(){
        storage.reload();
    }

}
