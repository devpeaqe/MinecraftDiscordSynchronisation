package de.peaqe.minecraftDiscordSynchronisation.minecraft.commands;

import de.peaqe.minecraftDiscordSynchronisation.MinecraftDiscordSynchronisation;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
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
import java.util.logging.Level;

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

            // Send success message
            var jda = this.minecraftDiscordSynchronisation.getSynchBot().getJda();
            var textChannelId = this.minecraftDiscordSynchronisation.getDiscordConfig().get("channelId");

            var guild = jda.getGuildById(this.minecraftDiscordSynchronisation.getDiscordConfig().get("guildId"));
            if (guild == null) {
                this.minecraftDiscordSynchronisation.getLogger().log(Level.SEVERE,
                        "Failed to get guild from configured guildId!");
                return true;
            }

            var textChannel = guild.getTextChannelById(textChannelId);
            if (textChannel == null) {
                this.minecraftDiscordSynchronisation.getLogger().log(Level.SEVERE,
                        "Failed to send success message to the configured textchannel!");
                return true;
            }

            var user = jda.getUserById(userObject.getUserId());
            if (user == null) {
                this.minecraftDiscordSynchronisation.getLogger().log(Level.SEVERE,
                        "Failed to send success message because we couldn't get a user from the memberId!");
                return true;
            }

            // Extra Methode, da ich den generellen Ablauf nicht unterbrechen möchte.
            // Falls das Fehler wirft, wird trotzdem die Successnachricht gesendet.
            this.modifyNickname(guild, user, player);

            textChannel.sendMessage(this.minecraftDiscordSynchronisation.getSynchBot().getPrefix() +
                    "Du wurdest mit dem Account %s verknüpft %s".formatted(player.getName(), user.getName())).queue();

            this.minecraftDiscordSynchronisation.getServer().getScheduler().runTaskLaterAsynchronously(
                    this.minecraftDiscordSynchronisation, () -> {

                        var lastMessageId = textChannel.getLatestMessageId();
                        textChannel.deleteMessageById(lastMessageId).queue();

                    }, 20 * 20 // 20 Seconds
            );

            return true;
        }

        player.sendMessage(this.minecraftDiscordSynchronisation.getPrefix() +
                "Bitte verwende: §7/§everify §7<§ecode§7>");

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                                @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }

    private void modifyNickname(Guild guild, User user, Player player) {
        var guildMember = guild.getMemberById(user.getId());

        if (guildMember == null) {
            this.minecraftDiscordSynchronisation.getLogger().log(Level.SEVERE,
                    "Failed to modify nickname because user couldn't found!");
            return;
        }

        guild.modifyNickname(guildMember, player.getName()).queue();
    }

}

