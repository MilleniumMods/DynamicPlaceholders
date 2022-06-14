package io.github.joshy56.dynamicplaceholders.hook;

import com.google.common.base.Preconditions;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 10/6/2022.
 */
public class PluginPlaceholderExpansion extends PlaceholderExpansion {
    private final NamespacedKey identifier;
    private final String version, author;
    private final Map<String, Function<Optional<Player>, String>> placeholdersProcessors;

    public PluginPlaceholderExpansion(@NotNull Plugin namespace, @NotNull String key){
        identifier = Preconditions.checkNotNull(
                NamespacedKey.fromString(
                        key,
                        namespace
                ),
                "Pto"
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
        return getIdentifier();
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return placeholdersProcessors.keySet().stream().toList();
    }

    public Map<String, Function<Optional<Player>, String>> getPlaceholdersProcessors() {
        return placeholdersProcessors;
    }

    @Override
    public @Nullable String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
        return placeholdersProcessors.containsKey(params) ? placeholdersProcessors.get(params).apply(Optional.ofNullable(player)) : null;
    }
}
