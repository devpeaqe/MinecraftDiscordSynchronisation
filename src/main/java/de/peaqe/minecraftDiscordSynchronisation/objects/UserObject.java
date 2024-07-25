package de.peaqe.minecraftDiscordSynchronisation.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 24.07.2024 | 22:40 Uhr
 * *
 */

@Data
@AllArgsConstructor
public class UserObject {

    private final String uniqueId;
    private String name;
    private String userId;

}
