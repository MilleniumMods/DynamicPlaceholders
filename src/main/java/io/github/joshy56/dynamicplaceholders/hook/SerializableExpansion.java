package io.github.joshy56.dynamicplaceholders.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.Nullable;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 4/8/2022.
 */
public abstract class SerializableExpansion extends PlaceholderExpansion implements ConfigurationSerializable {
    @Nullable
    public abstract String getRequiredPlugin();
}
