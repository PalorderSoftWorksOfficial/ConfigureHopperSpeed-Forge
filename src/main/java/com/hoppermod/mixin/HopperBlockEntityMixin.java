package com.hoppermod.mixin;

import com.hoppermod.HopperMod;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HopperBlockEntity.class, priority = 900)
public abstract class HopperBlockEntityMixin {

    @Shadow
    private int cooldownTime; // name differs in Mojang mappings

    private static final ThreadLocal<Boolean> transferring =
            ThreadLocal.withInitial(() -> false);

    private static final ThreadLocal<Integer> prevCooldown =
            new ThreadLocal<>();

    @Inject(method = "pushItemsTick", at = @At("HEAD"))
    private static void onHead(Level level, BlockPos pos, BlockState state,
                               HopperBlockEntity be, CallbackInfo ci) {

        if (transferring.get()) return;

        prevCooldown.set(((HopperBlockEntityMixin)(Object) be).cooldownTime);
    }

    @Inject(method = "pushItemsTick", at = @At("RETURN"))
    private static void onReturn(Level level, BlockPos pos, BlockState state,
                                 HopperBlockEntity be, CallbackInfo ci) {

        if (transferring.get()) return;

        Integer prev = prevCooldown.get();
        if (prev == null) return;

        HopperBlockEntityMixin self = (HopperBlockEntityMixin)(Object) be;
        int curr = self.cooldownTime;

        if (prev > 1 || curr <= 1) return;

        self.cooldownTime = HopperMod.hopperSpeed;

        int extra = HopperMod.hopperAmount - 1;
        if (extra <= 0) return;

        transferring.set(true);

        try {
            int moved = 0;

            while (moved < extra) {

                int before = self.cooldownTime;
                self.cooldownTime = 0;

                HopperBlockEntity.pushItemsTick(level, pos, state, be);

                int after = self.cooldownTime;

                if (after > 0) {
                    moved++;
                    self.cooldownTime = HopperMod.hopperSpeed;
                } else {
                    self.cooldownTime = HopperMod.hopperSpeed;
                    break;
                }
            }

        } finally {
            transferring.set(false);
        }

        self.cooldownTime = HopperMod.hopperSpeed;
    }
}
