package net.mcreator.kaizokuocraft.client;

import java.util.HashMap;
import java.util.Map;

public final class SkillCooldownClient {

    private static final Map<String, Long> COOLDOWNS =
            new HashMap<>();

    private SkillCooldownClient() {
    }

    public static void start(
            String skillId,
            long cooldownMillis
    ) {

        if (
                skillId == null
                        || cooldownMillis <= 0
        ) {
            return;
        }

        COOLDOWNS.put(
                skillId,
                System.currentTimeMillis()
                        + cooldownMillis
        );
    }

    public static boolean isOnCooldown(
            String skillId
    ) {

        return getRemainingMillis(
                skillId
        ) > 0L;
    }

    public static long getRemainingMillis(
            String skillId
    ) {

        Long end =
                COOLDOWNS.get(
                        skillId
                );

        if (end == null) {
            return 0L;
        }

        long remaining =
                end - System.currentTimeMillis();

        if (remaining <= 0L) {

            COOLDOWNS.remove(
                    skillId
            );

            return 0L;
        }

        return remaining;
    }

    public static double getRemainingSeconds(
            String skillId
    ) {

        return getRemainingMillis(
                skillId
        ) / 1000.0D;
    }

    public static double getProgress(
            String skillId,
            long cooldownMillis
    ) {

        if (cooldownMillis <= 0L) {
            return 0.0D;
        }

        long remaining =
                getRemainingMillis(
                        skillId
                );

        return Math.max(
                0.0D,
                Math.min(
                        1.0D,
                        (double) remaining
                                / (double) cooldownMillis
                )
        );
    }
}