package net.mcreator.kaizokuocraft.client;

public final class CombatState {

    private static boolean active = false;

    private CombatState() {
    }

    public static boolean isActive() {
        return active;
    }

    public static void setActive(boolean value) {
        active = value;
    }

    public static void toggle() {
        active = !active;
    }
}