package de.peaqe.minecraftDiscordSynchronisation.discord.commands;

import de.peaqe.minecraftDiscordSynchronisation.MinecraftDiscordSynchronisation;
import de.peaqe.minecraftDiscordSynchronisation.manager.VerifyCode;
import de.peaqe.minecraftDiscordSynchronisation.objects.UserObject;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 24.07.2024 | 22:38 Uhr
 * *
 */

public class VerifyCommand extends ListenerAdapter {

    private final MinecraftDiscordSynchronisation minecraftDiscordSynchronisation;

    public VerifyCommand(MinecraftDiscordSynchronisation minecraftDiscordSynchronisation) {
        this.minecraftDiscordSynchronisation = minecraftDiscordSynchronisation;
    }

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        // Usage /verify <minecraft_name>
        var nameOption = event.getOption("name");
        if (nameOption == null) {
            event.reply("Du einen Namen angeben um dich zu verifizieren.").setEphemeral(true).queue();
            return;
        }

        var minecraftName = nameOption.getAsString();
        var player = this.minecraftDiscordSynchronisation.getServer().getPlayer(minecraftName);

        if (player == null) {
            event.reply("Der Spieler %s ist derzeit nicht online!".formatted(minecraftName))
                    .setEphemeral(true).queue();
            return;
        }

        var userObject = this.minecraftDiscordSynchronisation.getUserDatabase().load(player.getUniqueId().toString());
        if (userObject != null) {
            event.reply("Der Spieler %s ist bereits mit einem Discord Account verknüpft!".formatted(player.getName()))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        var verifyCodeManager = this.minecraftDiscordSynchronisation.getVerifyCodeManager();
        if (verifyCodeManager.codeSent(player.getUniqueId())) {
            event.reply("Dem Spieler %s wurde bereits ein Verifizierungscode gesendet.".formatted(player.getName()))
                    .setEphemeral(true).queue();
            return;
        }

        userObject = new UserObject(player.getUniqueId().toString(), player.getName(), event.getUser().getId());

        var verifyCode = VerifyCode.generate();
        event.reply("Dein Verifizierungscode lautet **`%s`**".formatted(verifyCode)).setEphemeral(true).queue();

        verifyCodeManager.sendCode(userObject, verifyCode);

    }

}
