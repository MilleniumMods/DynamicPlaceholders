package io.github.joshy56.dynamicplaceholders.hook;

import me.clip.placeholderapi.expansion.Taskable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/** Created by joshy23 (justJoshy23 - joshy56) on 4/8/2022. */
@SerializableAs("MariaDbExpansion")
public class MariaDbExpansion extends SerializableExpansion implements Taskable {
  private final @NotNull String pluginName;
  private final @NotNull String identifier;
  private final @NotNull String url;
  private final @NotNull String database;
  private final @NotNull String username;
  private final @NotNull String password;
  private final @NotNull String tableIdentifier;
  private final @NotNull String columnIdentifier;
  private final int rowNumber;

  private MariaDbDataSource dataSource;
  private BukkitTask fetcher;
  private Object value;

  public MariaDbExpansion(
      @NotNull final Plugin requiredPlugin,
      @NotNull String identifier,
      @NotNull String url,
      @NotNull String database,
      @NotNull String username,
      @NotNull String password,
      @NotNull String tableIdentifier,
      @NotNull String columnIdentifier,
      int rowNumber) {
    pluginName = requiredPlugin.getName();
    this.identifier = identifier;
    this.url = url;
    this.database = database;
    this.username = username;
    this.password = password;
    this.tableIdentifier = tableIdentifier;
    this.columnIdentifier = columnIdentifier;
    this.rowNumber = rowNumber;
  }

  @Override
  public @NotNull String getIdentifier() {
    return identifier;
  }

  @Override
  public @NotNull String getAuthor() {
    return Optional.ofNullable(getPluginInstance(getRequiredPlugin()))
        .map(plugin -> plugin.getDescription().getAuthors())
        .filter(authors -> !authors.isEmpty())
        .map(authors -> authors.get(0))
        .orElse("");
  }

  @Override
  public @NotNull String getVersion() {
    return Optional.ofNullable(getPluginInstance(getRequiredPlugin()))
        .map(plugin -> plugin.getDescription().getVersion())
        .orElse("");
  }

  @Override
  public @NotNull String getRequiredPlugin() {
    return pluginName;
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public @Nullable String onPlaceholderRequest(
      @Nullable final Player player, @NotNull String params) {
    return String.valueOf(value);
  }

  @Nullable
  public static Plugin getPluginInstance(@NotNull String pluginName) {
    return Bukkit.getPluginManager().getPlugin(pluginName);
  }

  @Override
  public @NotNull Map<String, Object> serialize() {
    LinkedHashMap<String, Object> args = new LinkedHashMap<>(3);
    LinkedHashMap<String, Object> remoteData = new LinkedHashMap<>(7);
    remoteData.put("url", url);
    remoteData.put("database", database);
    remoteData.put("username", username);
    remoteData.put("password", password);
    remoteData.put("table", tableIdentifier);
    remoteData.put("column", columnIdentifier);
    remoteData.put("row-number", rowNumber);

    args.put("identifier", getIdentifier());
    args.put("required-plugin", getRequiredPlugin());
    args.put("mysql-data", remoteData);
    return args;
  }

  public static MariaDbExpansion deserialize(@NotNull Map<String, Object> args) {
    if (!(args.containsKey("identifier")
        || args.containsKey("required-plugin")
        || args.containsKey("mysql-data")))
      throw new IllegalArgumentException(
          "Cannot deserialize an valid expansion from this args, insufficient arguments");
    if (!(args.get("mysql-data") instanceof Map))
      throw new IllegalArgumentException(
          "Cannot deserialize an valid expansion from this args, remote data not found");

    Map<String, Object> mysqlData = (Map<String, Object>) args.get("mysql-data");
    if (!(mysqlData.containsKey("url")
        || mysqlData.containsKey("database")
        || mysqlData.containsKey("username")
        || mysqlData.containsKey("table")
        || mysqlData.containsKey("column")
        || mysqlData.containsKey("row-number")))
      throw new IllegalArgumentException(
          "Cannot deserialize an valid expansion from this args, remote data is incomplete");

    try {
      String identifier = (String) args.get("identifier");
      String requiredPlugin = (String) args.get("required-plugin");
      Plugin plugin = getPluginInstance(requiredPlugin);
      if (plugin == null)
        throw new IllegalArgumentException(
            "Cannot deserialize an valid expansion from this args, required plugin does not exists");

      String url = (String) mysqlData.get("url");
      String database = (String) mysqlData.get("database");
      String username = (String) mysqlData.get("username");
      String password = (String) mysqlData.getOrDefault("password", "");
      String table = (String) mysqlData.get("table");
      String column = (String) mysqlData.get("column");
      int rowNumber = (int) mysqlData.get("row-number");

      return new MariaDbExpansion(
          plugin, identifier, url, database, username, password, table, column, rowNumber);
    } catch (ClassCastException e) {
      throw new IllegalArgumentException(
          "Cannot deserialize an valid expansion from this args, type of data not is the expected",
          e);
    }
  }

  @Override
  public void start() {
    if (!fetcher.isCancelled()) return;

    try {
      dataSource =
          new MariaDbDataSource(
              String.format(
                  "jdbc:mariadb://%s/%s?user=%s&password=%s", url, database, username, password));
      fetcher =
          Bukkit.getScheduler()
              .runTaskTimerAsynchronously(
                  getPluginInstance(getRequiredPlugin()),
                  () -> {
                    try (PreparedStatement preparedStatement =
                        dataSource.getConnection().prepareStatement("SELECT ? FROM ?;")) {
                      preparedStatement.setString(1, columnIdentifier);
                      preparedStatement.setString(2, tableIdentifier);
                      ResultSet resultSet = preparedStatement.executeQuery();
                      while (resultSet.next()) {
                        if (resultSet.getRow() == rowNumber) {
                          value = resultSet.getString(columnIdentifier);
                          break;
                        }
                      }
                    } catch (SQLException e) {
                      throw new RuntimeException(e);
                    }
                  },
                  1,
                  Math.max(10, new Random().nextInt(19)));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void stop() {
    if (fetcher != null && !fetcher.isCancelled()) fetcher.cancel();
  }
}
