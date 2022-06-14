package io.github.joshy56.dynamicplaceholders;

import io.github.joshy56.dynamicplaceholders.hook.PluginPlaceholderExpansion;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 10/6/2022.
 */
public class DynamicPlaceholders extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginPlaceholderExpansion expansion = new PluginPlaceholderExpansion(this, "principalKey");
        expansion.register();
        expansion.getPlaceholdersProcessors().put(
                "shoots",
                optionalPlayer -> optionalPlayer
                        .filter(Objects::nonNull)
                        .map(
                                player -> {
                                    player.sendMessage("Se chequearon tus disparos");
                                    return String.valueOf((Math.random() * 2) + 1);
                                }
                        ).orElse("None")
        );
    }
}
