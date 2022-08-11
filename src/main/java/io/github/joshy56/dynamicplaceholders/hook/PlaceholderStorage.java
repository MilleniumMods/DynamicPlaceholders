package io.github.joshy56.dynamicplaceholders.hook;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.github.joshy56.dynamicplaceholders.DynamicPlaceholders;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
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
    private final Multimap<Plugin, SerializableExpansion> expansionMultimap;
    private final DynamicPlaceholders plugin;

    public PlaceholderStorage(@NotNull final DynamicPlaceholders plugin, @Nullable final Map<String, PluginPlaceholderExpansion> defaults) {
        this.plugin = Preconditions.checkNotNull(
                plugin,
                "Please, choose a not-null plugin instance..."
        );
        expansions = new HashMap<>();
        if(defaults != null)
            expansions.putAll(defaults);
        expansionMultimap = HashMultimap.create();
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

        upsertExpansion(expansion.getIdentifier(), expansion);

        return this;
    }

    public PlaceholderStorage addPlaceholder(@NotNull final String expansionIdentifier, @NotNull final String placeholderIdentifier, @NotNull final SerializablePlaceholder placeholder){
        Preconditions.checkArgument(
                !Strings.isNullOrEmpty(expansionIdentifier),
                "Please, choose a not-null or not-empty expansion identifier..."
        );
        Preconditions.checkArgument(
                !Strings.isNullOrEmpty(placeholderIdentifier),
                "Please, choose a not-null or not-empty placeholder identifier..."
        );
        Preconditions.checkNotNull(
                placeholder,
                "Please, choose a not-null placeholder..."
        );
        PluginPlaceholderExpansion expansion = getExpansions().get(expansionIdentifier);
        if(expansion == null)
            expansion = new PluginPlaceholderExpansion(plugin, "environmental");
        expansion.getPlaceholdersProcessors().put(placeholderIdentifier, placeholder);

        upsertExpansion(expansionIdentifier, expansion);

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

        upsertExpansion(expansionIdentifier, null);

        return this;
    }

    protected void upsertExpansion(@NotNull String expansionIdentifier, @Nullable PluginPlaceholderExpansion expansion){
        Preconditions.checkArgument(
                !Strings.isNullOrEmpty(expansionIdentifier),
                "Please, choose a not-null or not-empty expansion identifier..."
        );
        if(expansion != null)
            Preconditions.checkArgument(
                    expansionIdentifier.equalsIgnoreCase(expansion.getIdentifier()),
                    "Expansion identifier don't matches with identifier of expansion..."
            );

        Configuration config = plugin.getConfig();
        ConfigurationSection expansionsSection = config.getConfigurationSection("expansions");
        if(expansionsSection == null)
            expansionsSection = config.createSection("expansions");
        expansionsSection.set(expansionIdentifier, expansion);
        config.set("expansions", expansionsSection);
        plugin.saveConfig();
    }

    PlaceholderStorage migrateTo(@NotNull final Configuration backend) {
        return this;
    }

}
