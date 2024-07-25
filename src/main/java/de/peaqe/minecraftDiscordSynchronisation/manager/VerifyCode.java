package de.peaqe.minecraftDiscordSynchronisation.manager;

import java.util.Random;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 24.07.2024 | 23:42 Uhr
 * *
 */

public class VerifyCode {

    // Die Codes werden täglich bei einem neustart gelöscht,
    // da diese nur in einer Map gespeichert werden.

    public static String generate() {
        var codeBuilder = new StringBuilder();

        // 8 Stellen
        for (int i = 0; i < 8; i++) {
            codeBuilder.append(new Random().nextInt(9) + 1);
        }

        return codeBuilder.toString().trim();
    }

}
