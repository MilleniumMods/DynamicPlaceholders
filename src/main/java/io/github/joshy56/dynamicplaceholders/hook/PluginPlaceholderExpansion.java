package io.github.joshy56.dynamicplaceholders.hook;

import com.google.common.base.Preconditions;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 10/6/2022.
 */
public class PluginPlaceholderExpansion extends PlaceholderExpansion {
    private final NamespacedKey identifier;
    private final int version;
    private final String author;

    public PluginPlaceholderExpansion(@NotNull Plugin namespace, @NotNull String key, @NotNull String value){
        identifier = Preconditions.checkNotNull(
                NamespacedKey.fromString(
                        key,
                        namespace
                ),
                "Pto"
        );
        
    }

    @Override
    public @NotNull String getIdentifier() {
        return null;
    }

    @Override
    public @NotNull String getAuthor() {
        return null;
    }

    @Override
    public @NotNull String getVersion() {
        return null;
    }

    @Override
    public @Nullable String getRequiredPlugin() {
        return super.getRequiredPlugin();
    }
}
