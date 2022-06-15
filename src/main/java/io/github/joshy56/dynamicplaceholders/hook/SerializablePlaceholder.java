package io.github.joshy56.dynamicplaceholders.hook;

import com.google.common.base.Preconditions;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 14/6/2022.
 */
@SerializableAs("SerializablePlaceholder")
public class SerializablePlaceholder extends PlaceholderHook implements ConfigurationSerializable {
    private String value;

    public SerializablePlaceholder(@NotNull final String value) {
        setValue(value);
    }

    public @NotNull String getValue() {
        return value;
    }

    public SerializablePlaceholder setValue(@NotNull final String value) {
        this.value = Preconditions.checkNotNull(
                value,
                "Please, choose a not-null value..."
        );
        return this;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        /*return PlaceholderAPI.setPlaceholders(player, getValue());*/
        return getValue();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("value", getValue());
        return args;
    }
}
