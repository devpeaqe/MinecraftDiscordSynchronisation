package de.peaqe.minecraftDiscordSynchronisation.api;

import com.google.gson.Gson;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 24.07.2024 | 11:44 Uhr
 * *
 */

public abstract class DiscordBot {

    protected JDA jda;
    protected Logger logger;
    protected boolean autoStart;
    protected String prefix;

    public abstract String getToken();
    public abstract Activity getActivity();
    public abstract OnlineStatus getOnlineStatus();
    public abstract boolean getAutoStart();

    public DiscordBot(boolean autoStart, String prefix) {
        this.autoStart = autoStart;
        this.logger = Logger.getGlobal();
        this.prefix = prefix;
        this.init();
    }

    private void init() {
        if (this.getToken() == null || this.getToken().isEmpty()) {
            this.logger.log(Level.SEVERE, "The BotToken cannot be null!");
            return;
        }

        var onlineStatus = this.getOnlineStatus();
        if (onlineStatus == null) onlineStatus = OnlineStatus.ONLINE;

        var activity = this.getActivity();
        if (activity == null) activity = Activity.watching("Yurei bot systems");

        this.logger.log(Level.INFO, "The bot %s is now ready to start".formatted(this.getClass().getSimpleName()));
        if (this.autoStart) this.start(activity, onlineStatus);
    }

    private void start(Activity activity, OnlineStatus onlineStatus) {
        this.jda = JDABuilder.createDefault(this.getToken())
                .setActivity(activity)
                .setStatus(onlineStatus)
                .build();

        this.logger.log(Level.INFO, "The bot %s is now online".formatted(this.getClass().getSimpleName()));
    }

    public void start() {
        this.start(this.getActivity(), this.getOnlineStatus());
    }

    public void stop() {
        this.jda.shutdownNow();
        this.logger.log(Level.INFO, "The bot %s is now offline".formatted(this.getClass().getSimpleName()));
    }

    @Deprecated
    public void update() {
        //this.jda.retrieveCommands().queue();

        //this.jda.shutdownNow();
        //this.init();
        //
        //if (!this.autoStart) this.start();
    }

    public void registerEvent(Object clazz) {
        this.jda.addEventListener(clazz);
    }

    public void registerCommand(Object clazz, String name, String description) {
        this.jda.addEventListener(clazz);
        this.jda.upsertCommand(name, description).queue(
                success -> this.logger.log(Level.INFO, "Command %s registered!".formatted(name)),
                error -> this.logger.log(Level.SEVERE, "Command %s cannot be registered!".formatted(name))
        );
    }

    public void registerCommand(Object clazz, String name, String description,
                                OptionData... optionData) {
        this.jda.addEventListener(clazz);
        if (optionData == null) this.registerCommand(clazz, name, description);
        else this.jda.upsertCommand(name, description).addOptions(optionData).queue(
                success -> this.logger.log(Level.INFO, "Command %s registered!".formatted(name)),
                error -> this.logger.log(Level.SEVERE, "Command %s cannot be registered!".formatted(name))
        );
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String serialize() {
        return new Gson().toJson(this);
    }

    public static DiscordBot deserialize(String json) {
        return new Gson().fromJson(json, DiscordBot.class);
    }

    public Logger getLogger() {
        return logger;
    }
}
