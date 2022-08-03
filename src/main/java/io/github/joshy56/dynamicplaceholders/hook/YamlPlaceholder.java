package io.github.joshy56.dynamicplaceholders.hook;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 2/8/2022.
 */
@SerializableAs("YamlPlaceholder")
public class YamlPlaceholder extends SerializablePlaceholder {
     private Object value;

     public YamlPlaceholder(){
         //Logica principal de inicializacion
     }

     public YamlPlaceholder(@NotNull Object value){
         this();
         this.value = value;
     }

    @Override
    @Nullable
    public String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
        return getValueAsString();
    }

    @Override
    public void setValue(@Nullable Object value) {
        this.value = value;
    }

    @Override
    @Nullable
    public Object getValue() {
        return value;
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
         return Optional.ofNullable(getValue())
                 .map(value -> Collections.singletonMap("value", value))
                 .orElse(Collections.emptyMap());
    }

    @NotNull
    public static YamlPlaceholder deserialize(@NotNull final Map<String, Object> args) {
         return Optional.ofNullable(args.get("value"))
                 .map(YamlPlaceholder::new)
                 .orElse(new YamlPlaceholder());
    }
}
