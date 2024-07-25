package de.peaqe.minecraftDiscordSynchronisation.config;

import de.peaqe.minecraftDiscordSynchronisation.MinecraftDiscordSynchronisation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 25.07.2024 | 11:37 Uhr
 * *
 */

public class DiscordConfig {

    private final MinecraftDiscordSynchronisation minecraftDiscordSynchronisation;
    private final File file;
    private final FileConfiguration fileConfiguration;

    public DiscordConfig(MinecraftDiscordSynchronisation minecraftDiscordSynchronisation) {
        this.minecraftDiscordSynchronisation = minecraftDiscordSynchronisation;
        this.file = new File(this.minecraftDiscordSynchronisation.getDataFolder().getAbsolutePath(), "discord.yml");
        this.fileConfiguration = this.createConfig();
    }

    private FileConfiguration createConfig() {
        var configuration = (FileConfiguration) null;

        if (!file.exists()) {
            var directoryCreated = this.minecraftDiscordSynchronisation.getDataFolder().mkdirs();
            if (directoryCreated) this.minecraftDiscordSynchronisation.getLogger()
                    .log(Level.INFO, "Config Ordner wurde erstellt.");

            try {
                var fileCreated = this.file.createNewFile();
                if (fileCreated) this.minecraftDiscordSynchronisation.getLogger()
                        .log(Level.INFO, "Config %s wurde erstellt.".formatted(this.file.getName()));

                configuration = YamlConfiguration.loadConfiguration(this.file);

                configuration.set("Discord.channelId", "insert here");
                configuration.set("Discord.guildId", "insert here");
                configuration.set("Discord.token", "insert here");

                configuration.save(this.file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return configuration;
    }

    public String get(String key) {
        return this.fileConfiguration.getString("Discord." + key);
    }

}
