package io.github.joshy56.dynamicplaceholders.hook;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import io.github.joshy56.dynamicplaceholders.DynamicPlaceholders;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 13/6/2022.
 */
public class PlaceholderStorage {
    private final Map<String, PluginPlaceholderExpansion> expansions;
    private final DynamicPlaceholders plugin;

    public PlaceholderStorage(@NotNull final DynamicPlaceholders plugin, @Nullable final Map<String, PluginPlaceholderExpansion> defaults) {
        this.plugin = Preconditions.checkNotNull(
                plugin,
                "Please, choose a not-null plugin instance..."
        );
        expansions = new HashMap<>();
        if(defaults != null)
            expansions.putAll(defaults);
    }

    public PlaceholderStorage reload(){
        expansions.forEach(
                (identifier, expansion) -> expansion.unregister()
        );
        expansions.clear();
        return loadAll();
    }

    public PlaceholderStorage loadAll(){
        Configuration config = plugin.getConfig();
        Optional.ofNullable(config.getConfigurationSection("expansions"))
                .ifPresent(
                        expansionsSection -> expansionsSection.getKeys(false).forEach(
                                identifier -> {
                                    PluginPlaceholderExpansion expansion = expansionsSection.getSerializable(identifier, PluginPlaceholderExpansion.class);
                                    if(expansion != null && expansion.register())
                                        this.expansions.put(expansion.getIdentifier(), expansion);
                                }
                        )
                );
        return this;
    }

    public PlaceholderStorage saveAll(){
        Configuration config = plugin.getConfig();
        config.createSection("expansions", expansions);
        plugin.saveConfig();
        return this;
    }

    public Map<String, PluginPlaceholderExpansion> getExpansions() {
        return expansions;
    }

    public PlaceholderStorage add(@NotNull final PluginPlaceholderExpansion expansion){
        Preconditions.checkNotNull(
                expansion,
                "Please, choose a not-null expansion..."
        );
        Optional.ofNullable(
                expansions.put(expansion.getIdentifier(), expansion)
        ).ifPresent(PlaceholderExpansion::unregister);
        expansion.register();
        return this;
    }

    public PlaceholderStorage remove(@NotNull final String expansionIdentifier){
        Preconditions.checkArgument(
                !Strings.isNullOrEmpty(expansionIdentifier),
                "Please, choose a not-null or not-empty identifier..."
        );
        Optional.ofNullable(expansions.get(expansionIdentifier))
                .ifPresent(
                        expansion -> {
                            expansion.unregister();
                            expansions.remove(expansionIdentifier);
                        }
                );
        return this;
    }

    PlaceholderStorage migrateTo(@NotNull final Configuration backend) {
        return this;
    }

}
