package com.hoppermod.mixin;

import com.hoppermod.HopperMod;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HopperBlockEntity.class, priority = 900)
public abstract class HopperBlockEntityMixin {

    @Shadow
    private int cooldownTime;

    private static final ThreadLocal<Boolean> transferring = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Integer> prevCooldown = new ThreadLocal<>();

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

        // Set custom cooldown
        self.cooldownTime = HopperMod.hopperSpeed;

        int extra = HopperMod.hopperAmount - 1;
        if (extra <= 0) return;

        transferring.set(true);
        try {
            int moved = 0;
            while (moved < extra) {
                self.cooldownTime = 0; // force transfer
                HopperBlockEntity.pushItemsTick(level, pos, state, be);
                if (self.cooldownTime > 0) {
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
