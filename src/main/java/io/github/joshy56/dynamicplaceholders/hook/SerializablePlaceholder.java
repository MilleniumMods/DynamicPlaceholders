package io.github.joshy56.dynamicplaceholders.hook;

import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 14/6/2022.
 */
@SerializableAs("SerializablePlaceholder")
public abstract class SerializablePlaceholder extends PlaceholderHook implements ConfigurationSerializable {

    public abstract Object getValue();

    public abstract void setValue(final Object value);

    public String getValueAsString(){
        return String.valueOf(getValue());
    }

}
