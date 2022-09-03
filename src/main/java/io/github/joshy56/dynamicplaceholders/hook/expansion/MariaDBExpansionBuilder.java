package io.github.joshy56.dynamicplaceholders.hook.expansion;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 24/8/2022.
 */
public class MariaDBExpansionBuilder {

    private final String identifier;
    private String pluginName;
    private String url;
    private String database;
    private String username;
    private String password;
    private String tableIdentifier;
    private String columnIdentifier;
    private int rowNumber;

    public static MariaDBExpansionBuilder of(@NotNull String identifier){
        return new MariaDBExpansionBuilder(identifier);
    }

    protected MariaDBExpansionBuilder(@NotNull String identifier){
        this.identifier = identifier;
    }

    public MariaDBExpansionBuilder withRequiredPlugin(@NotNull Plugin plugin){
        return withRequiredPlugin(plugin.getName());
    }

    public MariaDBExpansionBuilder withRequiredPlugin(@NotNull String pluginName){
        Optional.ofNullable(Bukkit.getPluginManager().getPlugin(pluginName))
                .ifPresent(plugin -> this.pluginName = plugin.getName());
        return this;
    }

    public MariaDBExpansionBuilder withUrl(@NotNull String url){
        this.url = url;
        return this;
    }

    public MariaDBExpansionBuilder withDatabase(@NotNull String database){
        this.database = database;
        return this;
    }

    public MariaDBExpansionBuilder withUsername(@NotNull String username){
        this.username = username;
        return this;
    }

    public MariaDBExpansionBuilder withPassword(@NotNull String password){
        this.password = password;
        return this;
    }

    public MariaDBExpansionBuilder withTableIdentifier(@NotNull String tableIdentifier){
        this.tableIdentifier = tableIdentifier;
        return this;
    }

    public MariaDBExpansionBuilder withColumnIdentifier(@NotNull String columnIdentifier){
        this.columnIdentifier = columnIdentifier;
        return this;
    }

    public MariaDBExpansionBuilder withRowNumber(int rowNumber){
        this.rowNumber = rowNumber;
        return this;
    }

    public MariaDbExpansion build(){
        return new MariaDbExpansion(
                Bukkit.getPluginManager().getPlugin(pluginName),
                identifier,
                url,
                database,
                username,
                password,
                tableIdentifier,
                columnIdentifier,
                rowNumber
        );
    }
}
