package de.peaqe.minecraftDiscordSynchronisation.api.provider;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.peaqe.minecraftDiscordSynchronisation.MinecraftDiscordSynchronisation;
import de.peaqe.minecraftDiscordSynchronisation.api.provider.cache.CachedMap;

import java.sql.SQLException;
import java.util.*;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 24.07.2024 | 14:42 Uhr
 * *
 */

@SuppressWarnings(value = {"unused", "SqlSourceToSinkFlow"})
public abstract class SimpleDatabase extends DatabaseProvider {

    private final MinecraftDiscordSynchronisation minecraftDiscordSynchronisation;
    private final Gson gson;
    private final CachedMap<String, String> redisCache;
    private final Set<String> databases;

    public SimpleDatabase(MinecraftDiscordSynchronisation minecraftDiscordSynchronisation) {
        super(minecraftDiscordSynchronisation);
        this.minecraftDiscordSynchronisation = minecraftDiscordSynchronisation;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.redisCache = new CachedMap<>(this.minecraftDiscordSynchronisation);
        this.databases = new HashSet<>();
    }

    public void createTable(String table) {

        var sql = "CREATE TABLE IF NOT EXISTS " + table +
                " (id VARCHAR(255) NOT NULL," +
                " json JSON NOT NULL," +
                " PRIMARY KEY (id))";
        this.connect();

        try (var statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    private void createDatabase(String database) {

        Preconditions.checkState(database.matches("[a-zA-Z0-9_]+"),
                "Database name can only contain letters, underscores and numbers.");

        if (this.databases.contains(database)) return;
        this.databases.add(database);

        var sql = "CREATE DATABASE IF NOT EXISTS " + database;
        this.connect();

        try (var statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public void setDatabase(String database) {
        var databaseName = "testplugin_" + database;
        var connection = this.minecraftDiscordSynchronisation.getDatabaseConnection();

        this.createDatabase(databaseName);

        this.minecraftDiscordSynchronisation.getDatabaseConnection().database(databaseName);
        this.setConnection(connection);
    }

    public void save(String table, String id, Object value, Class<?> clazz) {
        if (value == null) return;

        Preconditions.checkArgument(clazz.isInstance(value),
                "Value must be an instance of %s", clazz);

        // Cache
        if (this.redisCache.containsKey(id))
            if (this.redisCache.get(id, String.class) == null) this.redisCache.remove(id);
            else if (this.redisCache.get(id, String.class).equals(gson.toJson(value, clazz))) return;

        var sql = "INSERT INTO " + table + " (id, json) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE json = VALUES(json)";
        this.connect();

        try (var statement = this.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, gson.toJson(value, clazz));
            statement.executeUpdate();

            // Cache
            this.redisCache.put(id, gson.toJson(value, clazz));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public <T> T load(String database, String id, Class<T> clazz) {

        // Cache
        if (this.redisCache.containsKey(id))
            if (this.redisCache.get(id, String.class) != null) {
                if (this.redisCache.get(id, String.class).equalsIgnoreCase("null")) return null;
                return gson.fromJson(this.redisCache.get(id, String.class), clazz);
            } else return null;

        var sql = "SELECT json FROM " + database + " WHERE id = ?";
        this.connect();

        try (var statement = this.getConnection().prepareStatement(sql)) {

            statement.setString(1, id);
            var result = statement.executeQuery();

            if (result.next()) {
                var value = gson.fromJson(result.getString("json"), clazz);

                // Cache
                this.redisCache.put(id, gson.toJson(value, clazz));

                // Cache end
                return value;
            }

            this.redisCache.put(id, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
        return null;
    }

    public void remove(String table, String id) {

        if (this.redisCache.containsValue(id)) this.redisCache.remove(id);

        var sql = "DELETE FROM " + table + " WHERE id = ?";
        this.connect();

        try (var statement = this.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public List<?> loadAll(String table, Class<?> clazz) {

        var sql = "SELECT * FROM " + table;
        this.connect();

        try (var statement = this.getConnection().prepareStatement(sql)) {
            var result = statement.executeQuery();

            var map = new HashMap<String, Object>();
            var list = new ArrayList<Object>();

            while (result.next()) map.put(result.getString("id"),
                    gson.fromJson(result.getString("json"), clazz));

            map.forEach((s, o) -> {
                if (!clazz.isInstance(o)) return;
                list.add(o);
                this.redisCache.put(s, gson.toJson(o, clazz));
            });

            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }

    }

    public List<String> loadAllCached(String table, Class<?> clazz) {
        return this.redisCache.loadAllAsJson();
    }

}
