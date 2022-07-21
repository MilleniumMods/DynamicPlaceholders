package io.github.joshy56.dynamicplaceholders.hook;

import com.google.common.base.Preconditions;
import io.github.joshy56.dynamicplaceholders.exceptions.InvalidPlaceholderExpansion;
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
public class SerializablePlaceholder<T> extends PlaceholderHook implements ConfigurationSerializable {
    private T value;

    public SerializablePlaceholder(@NotNull final T value) {
        setValue(value);
    }

    public @NotNull T getValue() {
        return value;
    }

    public SerializablePlaceholder<T> setValue(@NotNull final T value) {
        this.value = Preconditions.checkNotNull(
                value,
                "Please, choose a not-null value..."
        );
        return this;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        /*return PlaceholderAPI.setPlaceholders(player, getValue());*/
        return String.valueOf(value);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("value", getValue());
        return args;
    }

    public static SerializablePlaceholder<?> deserialize(@NotNull final Map<String, Object> args) throws InvalidPlaceholderExpansion {
        Object value;
        try{
            value = args.get("value");
        } catch (NullPointerException e) {
            throw new InvalidPlaceholderExpansion("value is not defined", e);
        } catch (ClassCastException e) {
            throw new InvalidPlaceholderExpansion("value is of wrong type", e);
        }
        return new SerializablePlaceholder<>(value);
    }
}
