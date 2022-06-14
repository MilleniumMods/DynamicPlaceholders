package io.github.joshy56.dynamicplaceholders;

import io.github.joshy56.dynamicplaceholders.hook.PluginPlaceholderExpansion;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

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
                new PlaceholderHook() {
                    @Override
                    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
                        return Optional.ofNullable(player)
                                .map(
                                        p -> {
                                            p.sendMessage("Se chequearon tus disparos");
                                            return String.valueOf((Math.random() * 2) + 1);
                                        }
                                ).orElse("None");
                    }
                }
        );
    }
}
