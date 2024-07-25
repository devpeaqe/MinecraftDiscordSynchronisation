package de.peaqe.minecraftDiscordSynchronisation.manager;

import de.peaqe.minecraftDiscordSynchronisation.objects.UserObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 24.07.2024 | 23:38 Uhr
 * *
 */

public class VerifyCodeManager {

    private final Map<UUID, String> codes;
    private final Map<String, UserObject> users;

    public VerifyCodeManager() {
        this.codes = new HashMap<>();
        this.users = new HashMap<>();
    }

    public void sendCode(UserObject userObject, String code) {
        this.codes.put(UUID.fromString(userObject.getUniqueId()), code);
        this.users.put(code, userObject);
    }

    public void revokeCode(UserObject userObject) {
        this.codes.remove(UUID.fromString(userObject.getUniqueId()));
        this.users.forEach((s, userObject1) -> {
            if (userObject.equals(userObject1)) this.users.remove(s);
        });
    }

    public String getCode(UUID uuid) {
        return this.codes.get(uuid);
    }

    public boolean codeSent(UUID uuid) {
        return this.codes.containsKey(uuid);
    }

    public UserObject getUserObject(String code) {
        return this.users.get(code);
    }

}
