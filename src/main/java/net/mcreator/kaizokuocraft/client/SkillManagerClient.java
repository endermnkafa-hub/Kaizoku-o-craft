package net.mcreator.kaizokuocraft.client;

public final class SkillManagerClient {

    private static final long DASH_UNLOCK_LEVEL =
            10L;

    private SkillManagerClient() {
    }

    public static long getDashUnlockLevel() {
        return DASH_UNLOCK_LEVEL;
    }
}