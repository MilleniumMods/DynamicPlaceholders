package io.github.joshy56.dynamicplaceholders.hook;

import com.google.common.base.Preconditions;
import io.github.joshy56.dynamicplaceholders.exceptions.InvalidPlaceholderExpansion;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 10/6/2022.
 */
@SerializableAs("PluginPlaceholderExpansion")
public class PluginPlaceholderExpansion extends PlaceholderExpansion implements ConfigurationSerializable {
    private final NamespacedKey identifier;
    private final PluginDescriptionFile description;
    private final Map<String, PlaceholderHook> placeholdersProcessors;

    public PluginPlaceholderExpansion(@NotNull Plugin namespace, @NotNull String key) {
        identifier = Preconditions.checkNotNull(
                NamespacedKey.fromString(
                        key,
                        namespace
                ),
                "Please chose a valid key for you '" + NamespacedKey.class.getSimpleName() + "'..."
        );
        description = namespace.getDescription();
        placeholdersProcessors = new HashMap<>();
    }

    @Override
    public @NotNull String getIdentifier() {
        return identifier.asString();
    }

    @Override
    public @NotNull String getAuthor() {
        return description.getAuthors().isEmpty() ? identifier.namespace() : description.getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return description.getVersion();
    }

    @Override
    public @Nullable String getRequiredPlugin() {
        return description.getName();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return new ArrayList<>(placeholdersProcessors.keySet());
    }

    public Map<String, PlaceholderHook> getPlaceholdersProcessors() {
        return placeholdersProcessors;
    }

    @Override
    public @Nullable String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
        return Optional.ofNullable(placeholdersProcessors.get(params))
                .map(
                        placeholder -> {
                            String[] args = params.split("_");
                            if (args.length > 0)
                                return placeholder.onPlaceholderRequest(
                                        player,
                                        String.join(
                                                "_",
                                                Arrays.copyOfRange(args, 1, args.length)
                                        )
                                );
                            return null;
                        }
                )
                .orElse(null);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("plugin", getRequiredPlugin());
        args.put("identifier", getIdentifier());
        args.put("placeholders", getPlaceholdersProcessors());
        return args;
    }

    public static PluginPlaceholderExpansion deserialize(@NotNull final Map<String, Object> args) throws InvalidPlaceholderExpansion {
        Plugin plugin;
        String identifier;
        Map<String, SerializablePlaceholder> placeholderMap = new HashMap<>();
        try{
            plugin = Bukkit.getPluginManager().getPlugin(
                    args.get("plugin").toString()
            );
            if(plugin == null)
                throw new InvalidPlaceholderExpansion("plugin is not loaded");
        } catch (NullPointerException e) {
            throw new InvalidPlaceholderExpansion("plugin is not defined", e);
        } catch (ClassCastException e) {
            throw new InvalidPlaceholderExpansion("plugin is of wrong type", e);
        }

        try{
            identifier = args.get("identifier").toString();
        } catch (NullPointerException e) {
            throw new InvalidPlaceholderExpansion("identifier is not defined", e);
        } catch (ClassCastException e) {
            throw new InvalidPlaceholderExpansion("identifier is of wrong type", e);
        }

        if(args.get("placeholders") != null) {
            try{
                placeholderMap = (Map<String, SerializablePlaceholder>) args.get("placeholders");
            } catch (ClassCastException e){
                throw new InvalidPlaceholderExpansion("placeholders are of wrong type");
            }
        }
        PluginPlaceholderExpansion expansion = new PluginPlaceholderExpansion(plugin, identifier);
        expansion.getPlaceholdersProcessors().putAll(placeholderMap);
        return expansion;
    }
}
