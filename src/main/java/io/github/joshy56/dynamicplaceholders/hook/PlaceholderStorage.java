package io.github.joshy56.dynamicplaceholders.hook;

import org.bukkit.configuration.Configuration;
import org.jetbrains.annotations.NotNull;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 13/6/2022.
 */
public interface PlaceholderStorage extends Configuration {

    PlaceholderStorage migrateTo(@NotNull final Configuration backend);

}
