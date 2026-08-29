package net.mcreator.kaizokuocraft.client;

public final class ClientStamina {

    private static double stamina =
            100.0D;

    private static double maxStamina =
            100.0D;

    private ClientStamina() {
    }

    public static double getStamina() {
        return stamina;
    }

    public static double getMaxStamina() {
        return maxStamina;
    }

    public static double getPercentage() {

        if (maxStamina <= 0.0D) {
            return 0.0D;
        }

        return Math.max(
                0.0D,
                Math.min(
                        1.0D,
                        stamina / maxStamina
                )
        );
    }

    public static void set(
            double newStamina,
            double newMaxStamina
    ) {

        maxStamina =
                Math.max(
                        1.0D,
                        newMaxStamina
                );

        stamina =
                Math.max(
                        0.0D,
                        Math.min(
                                maxStamina,
                                newStamina
                        )
                );
    }

    public static void reset() {

        stamina =
                100.0D;

        maxStamina =
                100.0D;
    }
}