package de.peaqe.minecraftDiscordSynchronisation.minecraft.commands;

import de.peaqe.minecraftDiscordSynchronisation.MinecraftDiscordSynchronisation;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 24.07.2024 | 23:18 Uhr
 * *
 */

public class VerifyCommand implements CommandExecutor, TabExecutor {

    private MinecraftDiscordSynchronisation minecraftDiscordSynchronisation;

    public VerifyCommand(MinecraftDiscordSynchronisation minecraftDiscordSynchronisation) {
        this.minecraftDiscordSynchronisation = minecraftDiscordSynchronisation;
        Objects.requireNonNull(this.minecraftDiscordSynchronisation.getServer()
                .getPluginCommand("verify")).setExecutor(this);
        Objects.requireNonNull(this.minecraftDiscordSynchronisation.getServer()
                .getPluginCommand("verify")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.minecraftDiscordSynchronisation.getPrefix() +
                    "§cDu musst ein Spieler sein um diesen Befehl ausführen zu können!");
            return true;
        }

        // /verify <code>
        if (args.length == 1) {

            var code = args[0];

            var verifyCodeManager = this.minecraftDiscordSynchronisation.getVerifyCodeManager();

            if (!verifyCodeManager.codeSent(player.getUniqueId())) {
                player.sendMessage(this.minecraftDiscordSynchronisation.getPrefix() +
                        "§cDir wurde kein §eVerifizierungscode §cgesendet!");
                return true;
            }

            var userDatabase = this.minecraftDiscordSynchronisation.getUserDatabase();
            if (userDatabase.load(player.getUniqueId().toString()) != null) {
                player.sendMessage(this.minecraftDiscordSynchronisation.getPrefix() +
                        "§cDu bist bereits mit einem §eDiscord Account §cverknüpft!");
                return true;
            }

            if (code.length() != 8) {
                player.sendMessage(this.minecraftDiscordSynchronisation.getPrefix() +
                        "§cDer von die angegebene §eCode §cist ungültig!");
                return true;
            }

            if (!code.equalsIgnoreCase(verifyCodeManager.getCode(player.getUniqueId()))) {
                player.sendMessage(this.minecraftDiscordSynchronisation.getPrefix() +
                        "§cDer §eVerifizierungscode §cist nicht korrekt!");
                return true;
            }

            player.sendMessage(this.minecraftDiscordSynchronisation.getPrefix() +
                    "Dein §eMinecraft Account §7wurde mit deinem §eDiscord Account §7verknüpft.");
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);

            var userObject = verifyCodeManager.getUserObject(code);
            this.minecraftDiscordSynchronisation.getUserDatabase().save(userObject);

            var user = this.minecraftDiscordSynchronisation.getSynchBot().getJda().getUserById(userObject.getUserId());
            if (user == null) return true;

            // Manche deaktivieren Direktnachrichten von Usern. Daher der Nullcheck ohne messaging
            user.openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessage("Du wurdest mit dem Minecraft Account %s verknüpft."
                                .formatted(player.getName())).queue();
            });

            return true;
        }

        player.sendMessage(this.minecraftDiscordSynchronisation.getPrefix() +
                "Bitte verwende: §7/§everify §8<§ecode§8>");

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                                @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}

