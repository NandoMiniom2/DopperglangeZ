package com.doppelgangerz.mixin;

import com.doppelgangerz.replay.ReplayManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Detecta colocacoes de blocos bem-sucedidas pelo jogador (Secoes 4 e 8).
 */
@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "place", at = @At("RETURN"))
    private void doppelgangerz$onPlace(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        ActionResult result = cir.getReturnValue();
        if (result != ActionResult.SUCCESS && result != ActionResult.CONSUME) return;

        PlayerEntity player = context.getPlayer();
        if (player == null || player.world.isClient) return;

        BlockState placed = context.getWorld().getBlockState(context.getBlockPos());
        ReplayManager.onBlockPlaced(player, context.getBlockPos(), placed);
    }
}
