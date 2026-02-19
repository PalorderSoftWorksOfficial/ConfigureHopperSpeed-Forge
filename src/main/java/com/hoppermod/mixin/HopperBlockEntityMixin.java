package com.hoppermod.mixin;

import com.hoppermod.HopperMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HopperBlockEntity.class, priority = 900)
public abstract class HopperBlockEntityMixin {

    @Shadow
    private int transferCooldown;

    // Flag to prevent our extra-transfer inject from triggering itself recursively
    private static final ThreadLocal<Boolean> transferring = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Integer> prevCooldown = new ThreadLocal<>();

    @Inject(method = "serverTick", at = @At("HEAD"))
    private static void onHead(World world, BlockPos pos, BlockState state,
                               HopperBlockEntity be, CallbackInfo ci) {
        if (transferring.get()) return;
        prevCooldown.set(((HopperBlockEntityMixin)(Object) be).transferCooldown);
    }

    @Inject(method = "serverTick", at = @At("RETURN"))
    private static void onReturn(World world, BlockPos pos, BlockState state,
                                 HopperBlockEntity be, CallbackInfo ci) {
        if (transferring.get()) return;

        Integer prev = prevCooldown.get();
        if (prev == null) return;

        HopperBlockEntityMixin self = (HopperBlockEntityMixin)(Object) be;
        int curr = self.transferCooldown;

        // Transfer happened: cooldown was <=1 and got reset to >1
        if (prev > 1 || curr <= 1) return;

        // 1. SPEED: set our custom cooldown
        self.transferCooldown = HopperMod.hopperSpeed;

        // 2. AMOUNT: move exactly (hopperAmount - 1) more items manually
        int extra = HopperMod.hopperAmount - 1;
        if (extra <= 0) return;

        // The hopper IS an Inventory (5 slots). We move items from hopper slots
        // directly into the output, or pull from input into hopper.
        // We do this by calling serverTick with our flag set so we don't recurse,
        // but we limit it precisely to 'extra' successful transfers.
        transferring.set(true);
        try {
            int moved = 0;
            while (moved < extra) {
                // Save cooldown before extra tick
                int before = self.transferCooldown;
                self.transferCooldown = 0; // force it to try a transfer
                HopperBlockEntity.serverTick(world, pos, state, be);
                int after = self.transferCooldown;

                if (after > 0) {
                    // Transfer happened
                    moved++;
                    self.transferCooldown = HopperMod.hopperSpeed;
                } else {
                    // Nothing to transfer anymore - stop
                    self.transferCooldown = HopperMod.hopperSpeed;
                    break;
                }
            }
        } finally {
            transferring.set(false);
        }

        self.transferCooldown = HopperMod.hopperSpeed;
    }
}