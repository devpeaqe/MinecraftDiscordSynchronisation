package de.peaqe.minecraftDiscordSynchronisation.database;

import de.peaqe.minecraftDiscordSynchronisation.MinecraftDiscordSynchronisation;
import de.peaqe.minecraftDiscordSynchronisation.api.provider.SimpleDatabase;
import de.peaqe.minecraftDiscordSynchronisation.objects.UserObject;

import javax.annotation.Nullable;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 24.07.2024 | 22:39 Uhr
 * *
 */

public class UserDatabase extends SimpleDatabase {

    private final String database, table;

    public UserDatabase(MinecraftDiscordSynchronisation minecraftDiscordSynchronisation) {
        super(minecraftDiscordSynchronisation);
        this.database = "synch";
        this.table = "users";
        this.setDatabase(this.database);
        this.createTable(this.table);
    }

    public void save(UserObject userObject) {
        this.setDatabase(this.database);
        this.save(this.table, userObject.getUniqueId(), userObject, UserObject.class);
    }

    @Nullable
    public UserObject load(String uniqueId) {
        this.setDatabase(this.database);
        return this.load(this.table, uniqueId, UserObject.class);
    }

}
