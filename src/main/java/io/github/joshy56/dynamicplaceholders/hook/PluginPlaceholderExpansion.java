package io.github.joshy56.dynamicplaceholders.hook;

import com.google.common.base.Preconditions;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 10/6/2022.
 */
@SerializableAs("PluginPlaceholderExpansion")
public class PluginPlaceholderExpansion extends PlaceholderExpansion implements ConfigurationSerializable {
    private final NamespacedKey identifier;
    private final String version, author;
    private final Map<String, PlaceholderHook> placeholdersProcessors;

    public PluginPlaceholderExpansion(@NotNull Plugin namespace, @NotNull String key) {
        identifier = Preconditions.checkNotNull(
                NamespacedKey.fromString(
                        key,
                        namespace
                ),
                "Please chose a valid key for you '" + NamespacedKey.class.getSimpleName() + "'..."
        );
        author = Optional.of(namespace.getDescription().getAuthors())
                .filter(authors -> !authors.isEmpty())
                .map(authors -> authors.get(0))
                .orElse(namespace.getName());
        version = namespace.getDescription().getVersion();
        placeholdersProcessors = new HashMap<>();
    }

    @Override
    public @NotNull String getIdentifier() {
        return identifier.asString();
    }

    @Override
    public @NotNull String getAuthor() {
        return author;
    }

    @Override
    public @NotNull String getVersion() {
        return version;
    }

    @Override
    public @Nullable String getRequiredPlugin() {
        return identifier.namespace();
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
}
