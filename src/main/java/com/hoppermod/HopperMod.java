package com.hoppermod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("hoppermod")
public class HopperMod {

    public static final String MOD_ID = "hoppermod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Vanilla defaults: cooldown = 8 ticks, amount = 1 item
    public static int hopperSpeed = 8;
    public static int hopperAmount = 1;

    public HopperMod() {
        LOGGER.info("HopperMod 1.21.X loaded!");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class CommandRegistry {

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            event.getDispatcher().register(
                Commands.literal("hopperspeed")
                    .executes(ctx -> {
                        ctx.getSource().sendSuccess(
                            Component.literal(
                                "[HopperMod] Current hopper speed: " + hopperSpeed +
                                " tick(s) (Vanilla = 8, lower = faster)"
                            ),
                            false
                        );
                        return 1;
                    })
                    .then(Commands.argument("ticks", IntegerArgumentType.integer(0, 6400))
                        .executes(ctx -> {
                            int ticks = IntegerArgumentType.getInteger(ctx, "ticks");
                            hopperSpeed = ticks;
                            ctx.getSource().sendSuccess(
                                Component.literal(
                                    "[HopperMod] Hopper speed set to " + ticks +
                                    " tick(s) per transfer (Vanilla = 8)"
                                ),
                                false
                            );
                            return 1;
                        })
                    )
            );

            event.getDispatcher().register(
                Commands.literal("hopperamount")
                    .executes(ctx -> {
                        ctx.getSource().sendSuccess(
                            Component.literal(
                                "[HopperMod] Current hopper amount: " + hopperAmount +
                                " item(s) per transfer (Vanilla = 1)"
                            ),
                            false
                        );
                        return 1;
                    })
                    .then(Commands.argument("amount", IntegerArgumentType.integer(1, 64))
                        .executes(ctx -> {
                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                            hopperAmount = amount;
                            ctx.getSource().sendSuccess(
                                Component.literal(
                                    "[HopperMod] Hopper amount set to " + amount +
                                    " item(s) per transfer (Vanilla = 1)"
                                ),
                                false
                            );
                            return 1;
                        })
                    )
            );
        }
    }
}
