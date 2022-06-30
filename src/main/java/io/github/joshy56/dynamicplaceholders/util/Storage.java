package io.github.joshy56.dynamicplaceholders.util;

import com.google.common.collect.Streams;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Optional;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 19/6/2022.
 */
public class Storage {
    private final Plugin plugin;
    private FileConfiguration storage;
    private File backendStorage;
    private final String storageName;

    public Storage(@NotNull final Plugin plugin, @NotNull final String storageName) {
        this.plugin = plugin;
        this.storageName = storageName;
        backendStorage = new File(plugin.getDataFolder(), this.storageName);
    }

    public Storage reload(){
        storage = YamlConfiguration.loadConfiguration(backendStorage);

        // Try load default config if it exists
        Optional.ofNullable(plugin.getResource(storageName))
                .ifPresent(
                        inputStream -> {
                            try(BufferedReader buff = new BufferedReader(new InputStreamReader(inputStream))) {
                                YamlConfiguration defaultStorage = YamlConfiguration.loadConfiguration(buff);
                                storage.setDefaults(defaultStorage);
                            } catch (IOException e) {
                                plugin.getSLF4JLogger().error(
                                        "Couldn't load default storage to " + backendStorage,
                                        e
                                );
                            }
                        }
                );

        return this;
    }

    public FileConfiguration getStorage() {
        if(storage == null)
            reload();

        return storage;
    }

    public Storage save(){
        try {
            getStorage().save(backendStorage);
        } catch (IOException e) {
            plugin.getSLF4JLogger().error(
                    "Couldn't save storage to " + backendStorage,
                    e
            );
        }

        return this;
    }

    public Storage saveDefault(){
        if(!backendStorage.exists())
            plugin.saveResource(storageName, false);

        return this;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getStorageName() {
        return storageName;
    }
}
