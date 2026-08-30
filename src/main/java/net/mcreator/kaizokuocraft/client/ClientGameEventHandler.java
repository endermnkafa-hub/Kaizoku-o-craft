package net.mcreator.kaizokuocraft.client;

import net.mcreator.kaizokuocraft.network.SkillUsePacket;

import net.minecraft.client.Minecraft;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import org.lwjgl.glfw.GLFW;

public final class ClientGameEventHandler {

    private static final int[] SKILL_KEYS = {
            GLFW.GLFW_KEY_Z,
            GLFW.GLFW_KEY_X,
            GLFW.GLFW_KEY_C,
            GLFW.GLFW_KEY_V,
            GLFW.GLFW_KEY_B,
            GLFW.GLFW_KEY_N,
            GLFW.GLFW_KEY_1,
            GLFW.GLFW_KEY_2,
            GLFW.GLFW_KEY_3
    };

    private static final int DASH_KEY =
            GLFW.GLFW_KEY_R;

    private static final boolean[] KEY_WAS_DOWN =
            new boolean[9];

    private static boolean DASH_WAS_DOWN =
            false;

    private ClientGameEventHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(
            ClientTickEvent.Post event
    ) {

        Minecraft minecraft =
                Minecraft.getInstance();

        if (
                minecraft.player == null
        ) {
            return;
        }

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

            if (
                    minecraft.screen == null
            ) {
                if (!ClientPlayerData.isCharacterCreated()) {
                    minecraft.setScreen(
                            new CharacterCreationScreen()
                    );
                } else {
                    minecraft.setScreen(
                            new KaizokuMenuScreen()
                    );
                }

            } else if (
                    minecraft.screen instanceof KaizokuMenuScreen
                            || minecraft.screen instanceof CharacterCreationScreen
            ) {

                minecraft.setScreen(
                        null
                );
            }
        }

        /*
         * Menü açıksa veya karakter oluşturulmadıysa combat tuşlarını kullanma (Vanilla).
         */
        if (
                minecraft.screen != null
                        || !CombatState.isActive()
                        || !ClientPlayerData.isCharacterCreated()
        ) {

            resetKeyStates();
            return;
        }

        long window =
                minecraft.getWindow()
                        .getWindow();

        /*
         * R = Dash
         */
        boolean dashDown =
                GLFW.glfwGetKey(
                        window,
                        DASH_KEY
                ) == GLFW.GLFW_PRESS;

        if (
                dashDown
                        && !DASH_WAS_DOWN
        ) {

            long level =
                    ClientPlayerData.getLevel();

            if (
                    level
                            >= SkillManagerClient.getDashUnlockLevel()
            ) {

                if (
                        !SkillCooldownClient.isOnCooldown(
                                "dash"
                        )
                ) {

                    PacketDistributor.sendToServer(
                            new SkillUsePacket(
                                    "dash"
                            )
                    );

                    SkillCooldownClient.start(
                            "dash",
                            1000L
                    );
                }
            }
        }

        DASH_WAS_DOWN =
                dashDown;

        /*
         * Normal 9 skill slotu
         */
        for (
                int slot = 0;
                slot < 9;
                slot++
        ) {

            boolean down =
                    GLFW.glfwGetKey(
                            window,
                            SKILL_KEYS[slot]
                    ) == GLFW.GLFW_PRESS;

            /*
             * Sadece tuşa yeni basıldığında çalıştır.
             */
            if (
                    down
                            && !KEY_WAS_DOWN[slot]
            ) {

                String skillId =
                        SkillLoadout.getSkillId(
                                slot
                        );

                if (
                        skillId != null
                                && !SkillCooldownClient.isOnCooldown(
                                skillId
                        )
                ) {

                    SkillDefinition skill =
                            SkillRegistry.getSkill(
                                    skillId
                            );

                    if (
                            skill != null
                    ) {

                        PacketDistributor.sendToServer(
                                new SkillUsePacket(
                                        skillId
                                )
                        );

                        SkillCooldownClient.start(
                                skillId,
                                skill.cooldownMillis()
                        );
                    }
                }
            }

            KEY_WAS_DOWN[slot] =
                    down;
        }
    }

    private static void resetKeyStates() {

        for (
                int i = 0;
                i < KEY_WAS_DOWN.length;
                i++
        ) {

            KEY_WAS_DOWN[i] =
                    false;
        }

        DASH_WAS_DOWN =
                false;
    }
}