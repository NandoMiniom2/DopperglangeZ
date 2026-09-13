package com.doppelgangerz.mixin;

import com.doppelgangerz.replay.ReplayManager;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Detecta blocos quebrados com sucesso pelo jogador (Secao 8).
 */
@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {

    @Shadow public ServerPlayerEntity player;

    private BlockState doppelgangerz$stateBeforeBreak;

    @Inject(method = "tryBreakBlock", at = @At("HEAD"))
    private void doppelgangerz$captureBefore(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        doppelgangerz$stateBeforeBreak = this.player.world.getBlockState(pos);
    }

    @Inject(method = "tryBreakBlock", at = @At("RETURN"))
    private void doppelgangerz$onBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(cir.getReturnValue()) && doppelgangerz$stateBeforeBreak != null) {
            ReplayManager.onBlockBroken(this.player, pos, doppelgangerz$stateBeforeBreak);
        }
        doppelgangerz$stateBeforeBreak = null;
    }
}
