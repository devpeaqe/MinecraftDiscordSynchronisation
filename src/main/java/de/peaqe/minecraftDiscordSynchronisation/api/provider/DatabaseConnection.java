package de.peaqe.minecraftDiscordSynchronisation.api.provider;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 24.07.2024 | 13:34 Uhr
 * *
 */

public record DatabaseConnection(String hostname, String username, String password, String database, int port) {

    private static String customDatabase;

    public DatabaseConnection {
        customDatabase = database;
    }

    public void database(String database) {
        customDatabase = database;
    }

    public String database() {
        if (customDatabase != null && !customDatabase.isEmpty()) return customDatabase;
        else return database;
    }
}
