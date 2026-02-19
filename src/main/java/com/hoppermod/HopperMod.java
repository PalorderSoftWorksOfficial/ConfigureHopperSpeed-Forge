package com.hoppermod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HopperMod implements ModInitializer {

    public static final String MOD_ID = "hoppermod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Vanilla defaults: cooldown = 8 ticks, amount = 1 item
    public static int hopperSpeed = 8;
    public static int hopperAmount = 1;

    @Override
    public void onInitialize() {
        LOGGER.info("HopperMod initialized!");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // /hopperspeed [ticks]
            dispatcher.register(
                CommandManager.literal("hopperspeed")
                    .executes(ctx -> {
                        ctx.getSource().sendFeedback(
                            () -> Text.literal(
                                "§6[HopperMod] §eAktuelle Hopper-Geschwindigkeit: §f" + hopperSpeed +
                                " §eTick(s) (Vanilla = 8, niedriger = schneller)"
                            ),
                            false
                        );
                        return 1;
                    })
                    .then(CommandManager.argument("ticks", IntegerArgumentType.integer(1, 1200))
                        .executes(ctx -> {
                            int ticks = IntegerArgumentType.getInteger(ctx, "ticks");
                            hopperSpeed = ticks;
                            ctx.getSource().sendFeedback(
                                () -> Text.literal(
                                    "§6[HopperMod] §aHopper-Geschwindigkeit gesetzt auf §f" + ticks +
                                    " §aTick(s) pro Transfer. §7(Vanilla = 8)"
                                ),
                                false
                            );
                            return 1;
                        })
                    )
            );

            // /hopperamount [items]
            dispatcher.register(
                CommandManager.literal("hopperamount")
                    .executes(ctx -> {
                        ctx.getSource().sendFeedback(
                            () -> Text.literal(
                                "§6[HopperMod] §eAktuelle Hopper-Item-Menge: §f" + hopperAmount +
                                " §eItem(s) pro Transfer (Vanilla = 1)"
                            ),
                            false
                        );
                        return 1;
                    })
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer(1, 64))
                        .executes(ctx -> {
                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                            hopperAmount = amount;
                            ctx.getSource().sendFeedback(
                                () -> Text.literal(
                                    "§6[HopperMod] §aHopper-Item-Menge gesetzt auf §f" + amount +
                                    " §aItem(s) pro Transfer. §7(Vanilla = 1)"
                                ),
                                false
                            );
                            return 1;
                        })
                    )
            );
        });
    }
}
