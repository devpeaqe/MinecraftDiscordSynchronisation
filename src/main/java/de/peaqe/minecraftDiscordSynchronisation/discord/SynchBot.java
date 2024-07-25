package de.peaqe.minecraftDiscordSynchronisation.discord;

import de.peaqe.minecraftDiscordSynchronisation.MinecraftDiscordSynchronisation;
import de.peaqe.minecraftDiscordSynchronisation.api.DiscordBot;
import de.peaqe.minecraftDiscordSynchronisation.discord.commands.VerifyCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 24.07.2024 | 22:34 Uhr
 * *
 */

public class SynchBot extends DiscordBot {

    private final MinecraftDiscordSynchronisation minecraftDiscordSynchronisation;

    public SynchBot(MinecraftDiscordSynchronisation minecraftDiscordSynchronisation) {
        super(true, "» ");
        this.minecraftDiscordSynchronisation = minecraftDiscordSynchronisation;
        this.registerCommands();
    }

    @Override
    public String getToken() {
        return this.minecraftDiscordSynchronisation.getDiscordConfig().get("token");
    }

    @Override
    public Activity getActivity() {
        return Activity.listening("Kevins Gedanken");
    }

    @Override
    public OnlineStatus getOnlineStatus() {
        return OnlineStatus.DO_NOT_DISTURB;
    }

    @Override
    public boolean getAutoStart() {
        return false;
    }

    private void registerCommands() {
        this.registerCommand(new VerifyCommand(this.minecraftDiscordSynchronisation), "verify",
                "Verknüpfe dein Minecraft- mit deinem Discordaccount", new OptionData(
                        OptionType.STRING, "name", "Gebe deinen Minecraftnamen ein.", true
                ));
    }

    public JDA getJda() {
        return this.jda;
    }

}
