package de.peaqe.minecraftDiscordSynchronisation;

import de.peaqe.minecraftDiscordSynchronisation.api.provider.DatabaseConnection;
import de.peaqe.minecraftDiscordSynchronisation.api.provider.config.DatabaseConfig;
import de.peaqe.minecraftDiscordSynchronisation.database.UserDatabase;
import de.peaqe.minecraftDiscordSynchronisation.discord.SynchBot;
import de.peaqe.minecraftDiscordSynchronisation.manager.VerifyCodeManager;
import de.peaqe.minecraftDiscordSynchronisation.minecraft.commands.VerifyCommand;
import de.peaqe.minecraftDiscordSynchronisation.minecraft.listener.JoinListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class MinecraftDiscordSynchronisation extends JavaPlugin {

    private DatabaseConnection databaseConnection;
    private UserDatabase userDatabase;
    private String prefix;
    private SynchBot synchBot;
    private VerifyCodeManager verifyCodeManager;

    @Override
    public void onEnable() {
        this.registerServices();
    }

    private void registerServices() {

        this.initializeDatabase();
        this.registerBots();

        this.prefix = "§6§lTwerion §7§l» §7";
        this.verifyCodeManager = new VerifyCodeManager();

        this.registerListener();
        this.registerCommands();

    }

    private void initializeDatabase() {
        var databaseConfig = new DatabaseConfig(this);
        this.databaseConnection = new DatabaseConnection(
                databaseConfig.get("hostname"),
                databaseConfig.get("username"),
                databaseConfig.get("password"),
                databaseConfig.get("database"),
                databaseConfig.getInt("port")
        );
        this.userDatabase = new UserDatabase(this);
    }

    private void registerBots() {
        this.synchBot = new SynchBot(this);
    }

    private void registerListener() {
        new JoinListener(this);
    }

    private void registerCommands() {
        new VerifyCommand(this);
    }

}
