package com.hoppermod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(HopperMod.MOD_ID)
public class HopperMod {

    public static final String MOD_ID = "hoppermod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Vanilla defaults
    public static int hopperSpeed = 8;
    public static int hopperAmount = 1;

    public HopperMod() {
        LOGGER.info("HopperMod initialized!");

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // /hopperspeed
        dispatcher.register(
            Commands.literal("hopperspeed")
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(
                        () -> Component.literal(
                            "§6[HopperMod] §eCurrent Hopper Speed: §f" + hopperSpeed +
                            " §eTick(s) (Vanilla = 8, lower = faster)"
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
                            () -> Component.literal(
                                "§6[HopperMod] §aHopper speed set to §f" + ticks +
                                " §aTick(s) per transfer. §7(Vanilla = 8)"
                            ),
                            false
                        );
                        return 1;
                    })
                )
        );

        // /hopperamount
        dispatcher.register(
            Commands.literal("hopperamount")
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(
                        () -> Component.literal(
                            "§6[HopperMod] §eCurrent Hopper Amount: §f" + hopperAmount +
                            " §eItem(s) per transfer (Vanilla = 1)"
                        ),
                        false
                    );
                    return 1;
                })
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 9999))
                    .executes(ctx -> {
                        int amount = IntegerArgumentType.getInteger(ctx, "amount");
                        hopperAmount = amount;

                        ctx.getSource().sendSuccess(
                            () -> Component.literal(
                                "§6[HopperMod] §aHopper amount set to §f" + amount +
                                " §aItem(s) per transfer. §7(Vanilla = 1)"
                            ),
                            false
                        );
                        return 1;
                    })
                )
        );
    }
}
