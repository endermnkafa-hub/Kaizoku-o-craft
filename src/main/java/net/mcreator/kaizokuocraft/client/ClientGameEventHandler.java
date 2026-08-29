package net.mcreator.kaizokuocraft.client;

import net.mcreator.kaizokuocraft.network.SkillUsePacket;

import net.minecraft.client.Minecraft;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ClientGameEventHandler {

    private ClientGameEventHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(
            ClientTickEvent.Post event
    ) {

        while (
                CombatKeyMappings.TOGGLE_COMBAT
                        .consumeClick()
        ) {

            CombatState.toggle();
        }

        while (
                MenuKeyMappings.OPEN_MENU
                        .consumeClick()
        ) {

            Minecraft minecraft =
                    Minecraft.getInstance();

            if (
                    minecraft.player == null
            ) {
                return;
            }

            if (
                    minecraft.screen == null
            ) {

                minecraft.setScreen(
                        new KaizokuMenuScreen()
                );

            } else if (
                    minecraft.screen
                            instanceof KaizokuMenuScreen
            ) {

                minecraft.setScreen(null);
            }
        }

        /*
         * Combat Mode aktif değilse
         * skill tuşlarını kullanma.
         */
        if (
                !CombatState.isActive()
        ) {
            return;
        }

        for (
                int slot = 0;
                slot < SkillLoadout.getSlotCount();
                slot++
        ) {

            /*
             * Tuş sistemini şimdilik keyboard
             * state üzerinden kontrol ediyoruz.
             */
            if (
                    isSkillKeyPressed(
                            slot
                    )
            ) {

                String skillId =
                        SkillLoadout.getSkillId(
                                slot
                        );

                if (
                        skillId != null
                ) {

                    PacketDistributor.sendToServer(
                            new SkillUsePacket(
                                    skillId
                            )
                    );
                }
            }
        }
    }

    private static boolean isSkillKeyPressed(
            int slot
    ) {

        String key =
                SkillLoadout.getSkillKey(
                        slot
                );

        Minecraft minecraft =
                Minecraft.getInstance();

        long window =
                minecraft.getWindow()
                        .getWindow();

        int keyCode =
                switch (key) {

                    case "Z" ->
                            org.lwjgl.glfw.GLFW.GLFW_KEY_Z;

                    case "X" ->
                            org.lwjgl.glfw.GLFW.GLFW_KEY_X;

                    case "C" ->
                            org.lwjgl.glfw.GLFW.GLFW_KEY_C;

                    case "V" ->
                            org.lwjgl.glfw.GLFW.GLFW_KEY_V;

                    case "B" ->
                            org.lwjgl.glfw.GLFW.GLFW_KEY_B;

                    case "N" ->
                            org.lwjgl.glfw.GLFW.GLFW_KEY_N;

                    case "1" ->
                            org.lwjgl.glfw.GLFW.GLFW_KEY_1;

                    case "2" ->
                            org.lwjgl.glfw.GLFW.GLFW_KEY_2;

                    case "3" ->
                            org.lwjgl.glfw.GLFW.GLFW_KEY_3;

                    default ->
                            -1;
                };

        if (keyCode == -1) {
            return false;
        }

        return org.lwjgl.glfw.GLFW.glfwGetKey(
                window,
                keyCode
        ) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
    }
}