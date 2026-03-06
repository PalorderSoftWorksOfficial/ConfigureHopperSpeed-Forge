package com.hoppermod.mixin;

import com.hoppermod.HopperMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

    private static final ThreadLocal<Boolean> transferring = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Integer> prevCooldown = new ThreadLocal<>();

    @Inject(method = "lambda$pushItemsTick$0", at = @At("HEAD"))
    private static void onLambdaHead(Level world, HopperBlockEntity be, CallbackInfoReturnable<Boolean> cir) {
        if (transferring.get()) return;
        prevCooldown.set(((HopperBlockEntityAccessor) be).getCooldownTime());
    }

    @Inject(method = "lambda$pushItemsTick$0", at = @At("RETURN"))
    private static void onLambdaReturn(Level world, HopperBlockEntity be, CallbackInfoReturnable<Boolean> cir) {
        if (transferring.get()) return;

        Integer prev = prevCooldown.get();
        if (prev == null) return;

        int curr = ((HopperBlockEntityAccessor) be).getCooldownTime();
        if (prev > 1 || curr <= 1) return;

        ((HopperBlockEntityAccessor) be).setCooldownTime(HopperMod.hopperSpeed);
        int extra = HopperMod.hopperAmount - 1;
        if (extra <= 0) return;

        transferring.set(true);
        try {
            int moved = 0;
            while (moved < extra) {
                ((HopperBlockEntityAccessor) be).setCooldownTime(0);
                HopperBlockEntity.pushItemsTick(world, be.getBlockPos(), be.getBlockState(), be);
                if (((HopperBlockEntityAccessor) be).getCooldownTime() > 0) {
                    moved++;
                    ((HopperBlockEntityAccessor) be).setCooldownTime(HopperMod.hopperSpeed);
                } else {
                    ((HopperBlockEntityAccessor) be).setCooldownTime(HopperMod.hopperSpeed);
                    break;
                }
            }
        } finally {
            transferring.set(false);
        }

        ((HopperBlockEntityAccessor) be).setCooldownTime(HopperMod.hopperSpeed);
    }
}