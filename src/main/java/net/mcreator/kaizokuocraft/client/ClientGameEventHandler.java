package net.mcreator.kaizokuocraft.client;

import net.mcreator.kaizokuocraft.network.SkillUsePacket;
import net.mcreator.kaizokuocraft.player.SkillManager;

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

        /*
         * K = Combat
         */
        while (
                CombatKeyMappings.TOGGLE_COMBAT
                        .consumeClick()
        ) {

            CombatState.toggle();
        }

        /*
         * M = Menü
         */
        while (
                MenuKeyMappings.OPEN_MENU
                        .consumeClick()
        ) {

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

                minecraft.setScreen(
                        null
                );
            }
        }

        /*
         * Combat aktif değilse
         * skill inputlarını temizle.
         */
        if (
                minecraft.screen != null
                        || !CombatState.isActive()
        ) {

            resetKeyStates();

            return;
        }

        long window =
                minecraft.getWindow()
                        .getWindow();

        /*
         * ====================================
         * DASH
         * ====================================
         */
        boolean dashDown =
                GLFW.glfwGetKey(
                        window,
                        DASH_KEY
                )
                        == GLFW.GLFW_PRESS;

        if (
                dashDown
                        && !DASH_WAS_DOWN
        ) {

            long level =
                    ClientPlayerData.getLevel();

            if (
                    level
                            >= SkillManagerClient
                            .getDashUnlockLevel()
            ) {

                if (
                        !SkillCooldownClient
                                .isOnCooldown(
                                        "dash"
                                )
                ) {

                    double staminaCost =
                            SkillManager
                                    .getSkillStaminaCost(
                                            "dash"
                                    );

                    /*
                     * STAMINA YETERLİ Mİ?
                     */
                    if (
                            ClientStamina.getStamina()
                                    >= staminaCost
                    ) {

                        PacketDistributor
                                .sendToServer(
                                        new SkillUsePacket(
                                                "dash"
                                        )
                                );

                        SkillCooldownClient.start(
                                "dash",
                                SkillManager.getCooldown(
                                        "dash"
                                )
                        );
                    }
                }
            }
        }

        DASH_WAS_DOWN =
                dashDown;

        /*
         * ====================================
         * 9 SKILL
         * ====================================
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
                    )
                            == GLFW.GLFW_PRESS;

            if (
                    down
                            && !KEY_WAS_DOWN[slot]
            ) {

                String skillId =
                        SkillLoadout.getSkillId(
                                slot
                        );

                if (
                        skillId == null
                                || SkillCooldownClient
                                .isOnCooldown(
                                        skillId
                                )
                ) {

                    KEY_WAS_DOWN[slot] =
                            down;

                    continue;
                }

                SkillDefinition skill =
                        SkillRegistry.getSkill(
                                skillId
                        );

                if (
                        skill == null
                ) {

                    KEY_WAS_DOWN[slot] =
                            down;

                    continue;
                }

                /*
                 * Skill stamina maliyeti.
                 */
                double staminaCost =
                        SkillManager
                                .getSkillStaminaCost(
                                        skillId
                                );

                /*
                 * STAMINA YETMİYORSA:
                 *
                 * packet YOK
                 * cooldown YOK
                 */
                if (
                        ClientStamina.getStamina()
                                < staminaCost
                ) {

                    KEY_WAS_DOWN[slot] =
                            down;

                    continue;
                }

                /*
                 * Server'a gönder.
                 */
                PacketDistributor
                        .sendToServer(
                                new SkillUsePacket(
                                        skillId
                                )
                        );

                /*
                 * Client cooldown.
                 */
                SkillCooldownClient.start(
                        skillId,
                        skill.cooldownMillis()
                );
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