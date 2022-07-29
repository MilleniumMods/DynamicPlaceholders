package io.github.joshy56.dynamicplaceholders.hook;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 14/6/2022.
 */
public class MySqlPlaceholder extends SerializablePlaceholder {
    private Connection connection;

    public MySqlPlaceholder(@NotNull String value) {
        super(value);
    }

    public static class Builder {}
}
