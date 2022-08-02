package io.github.joshy56.dynamicplaceholders.hook;

import org.jetbrains.annotations.NotNull;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 14/6/2022.
 */
public class MySqlPlaceholder extends SerializablePlaceholder<Object> {
    private Connection connection;

    public MySqlPlaceholder(@NotNull Map<String, String> value) {
        super();
        try {
            DataSource ds = new MariaDbDataSource("urlOpcional");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Builder {

    }
}
