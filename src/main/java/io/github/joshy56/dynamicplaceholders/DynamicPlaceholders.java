package io.github.joshy56.dynamicplaceholders;

import io.github.joshy56.dynamicplaceholders.commands.CreatePlaceholder;
import io.github.joshy56.dynamicplaceholders.hook.PlaceholderStorage;
import io.github.joshy56.dynamicplaceholders.hook.PluginPlaceholderExpansion;
import io.github.joshy56.dynamicplaceholders.hook.SerializablePlaceholder;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.cloud.CloudExpansion;
import me.clip.placeholderapi.expansion.manager.CloudExpansionManager;
import me.clip.placeholderapi.libs.kyori.adventure.platform.bukkit.BukkitAudiences;
import me.clip.placeholderapi.libs.kyori.adventure.text.Component;
import me.clip.placeholderapi.libs.kyori.adventure.text.format.NamedTextColor;
import me.clip.placeholderapi.libs.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 10/6/2022.
 */
public class DynamicPlaceholders extends JavaPlugin {
    private final PlaceholderStorage placeholderStorage;
    private BukkitAudiences adventure;
    private final NamespacedKey expansionIdentifier;

    public DynamicPlaceholders() {
        this.placeholderStorage = new PlaceholderStorage(this, null);
        this.expansionIdentifier = NamespacedKey.fromString("environmental", this);
    }

    @Override
    public void onLoad() {
        ConfigurationSerialization.registerClass(PluginPlaceholderExpansion.class);
        ConfigurationSerialization.registerClass(SerializablePlaceholder.class);
    }

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);
        placeholderStorage.loadAll();
        if(getOwnExpansion() == null)
            placeholderStorage.add(
                    new PluginPlaceholderExpansion(this, expansionIdentifier.value())
            );
        adventure.console().sendMessage(
                Component.text(
                        "Loaded " + placeholderStorage.getExpansions().size() + " expansions",
                        NamedTextColor.GOLD
                )
        );
        Bukkit.getCommandMap().register("createph", new CreatePlaceholder(this));
    }

    @Override
    public void onDisable() {
        adventure.close();
        placeholderStorage.saveAll();
        adventure.console().sendMessage(
                Component.text(
                        "Saved " + placeholderStorage.getExpansions().size() + " expansions",
                        NamedTextColor.GOLD
                )
        );
    }

    public PlaceholderStorage getPlaceholderStorage() {
        return placeholderStorage;
    }

    public PluginPlaceholderExpansion getOwnExpansion(){
        return getPlaceholderStorage().getExpansions().get(expansionIdentifier.asString());
    }
}
