package de.peaqe.minecraftDiscordSynchronisation.minecraft.listener;

import de.peaqe.minecraftDiscordSynchronisation.MinecraftDiscordSynchronisation;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 24.07.2024 | 23:12 Uhr
 * *
 */

public class JoinListener implements Listener {

    private final MinecraftDiscordSynchronisation minecraftDiscordSynchronisation;

    public JoinListener(MinecraftDiscordSynchronisation minecraftDiscordSynchronisation) {
        this.minecraftDiscordSynchronisation = minecraftDiscordSynchronisation;
        this.minecraftDiscordSynchronisation.getServer().getPluginManager()
                .registerEvents(this, this.minecraftDiscordSynchronisation);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        var player = event.getPlayer();
        var userDatabase = this.minecraftDiscordSynchronisation.getUserDatabase();

        if (userDatabase.load(player.getUniqueId().toString()) == null) {

            // Spieler ist noch nicht mit Discord verknüpft
            player.sendMessage(this.minecraftDiscordSynchronisation.getPrefix() + "§cDu bist noch nicht verifiziert.");
            player.sendMessage(this.minecraftDiscordSynchronisation.getPrefix() +
                    "§cUm dich zu verifizieren gebe §e/verify §cauf Discord ein.");

        }

        event.joinMessage(Component.empty());
    }

}
