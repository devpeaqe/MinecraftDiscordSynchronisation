package de.peaqe.minecraftDiscordSynchronisation.api.provider;

import de.peaqe.minecraftDiscordSynchronisation.MinecraftDiscordSynchronisation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 24.07.2024 | 16:18 Uhr
 * *
 */

public abstract class DatabaseProvider {

    private final MinecraftDiscordSynchronisation minecraftDiscordSynchronisation;

    private Connection connection;
    private DatabaseConnection databaseConnection;

    public DatabaseProvider(MinecraftDiscordSynchronisation minecraftDiscordSynchronisation) {
        this.minecraftDiscordSynchronisation = minecraftDiscordSynchronisation;
        this.databaseConnection = this.minecraftDiscordSynchronisation.getDatabaseConnection();
    }

    public void connect() {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.databaseConnection.hostname() + ":" +
                            this.databaseConnection.port() + "/" + this.databaseConnection.database(),
                    this.databaseConnection.username(),
                    this.databaseConnection.password()
            );

            this.minecraftDiscordSynchronisation.getLogger().log(Level.INFO, "Database connected.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            this.connection.close();
            this.minecraftDiscordSynchronisation.getLogger().log(Level.INFO, "Database disconnected.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setConnection(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    protected Connection getConnection() {
        return this.connection;
    }

}
