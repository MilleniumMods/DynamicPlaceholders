package io.github.joshy56.dynamicplaceholders.hook;

import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 14/6/2022.
 */
@SerializableAs("MySqlPlaceholder")
public class MySqlPlaceholder extends SerializablePlaceholder {
    private MariaDbDataSource dataSource;
    private String identifier;
    private String column;
    private String table;
    private Object value;

    public MySqlPlaceholder(@NotNull Map<String, String> args) {
        if(!validConnectionArgs(args))
            throw new RuntimeException("Invalid arguments for remote connection and query");

        try {
            dataSource = new MariaDbDataSource(
                    (args.containsKey("url")) ? args.get("url") : "jdbc:mariadb:// " + args.get("ip") + "/" + args.get("database")
            );
            
            dataSource.setUser(args.get("user"));
            if(args.containsKey("password"))
                dataSource.setPassword(args.get("password"));

            table = args.get("table");
            column = args.get("column");
            identifier = args.get("identifier");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validConnectionArgs(@NotNull final Map<String, String> args) {
        if(!args.containsKey("table"))
            return false;
        if(!args.containsKey("column"))
            return false;
        if(!args.containsKey("identifier"))
            return false;
        if(!args.containsKey("user"))
            return false;

        return args.containsKey("url") || (args.containsKey("ip") && !args.containsKey("database"));
    }

    @Override
    @Nullable
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {

        return null;
    }

    @NotNull
    public static MySqlPlaceholder deserialize(@NotNull final Map<String, Object> args) {
        return new MySqlPlaceholder(Collections.emptyMap());
    }
}
