package io.github.joshy56.dynamicplaceholders.hook;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;

import java.sql.ConnectionBuilder;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 13/6/2022.
 */
public class MySqlConfiguration extends MemoryConfiguration implements PlaceholderStorage{

    public MySqlConfiguration() {

    }

    @Override
    public PlaceholderStorage migrateTo(@NotNull Configuration backend) {
        return null;
    }
}
