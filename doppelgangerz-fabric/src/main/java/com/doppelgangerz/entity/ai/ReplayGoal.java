package com.doppelgangerz.entity.ai;

import com.doppelgangerz.DoppelgangerZ;
import com.doppelgangerz.entity.DoppelgangerEntity;
import com.doppelgangerz.replay.ReplayEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

/**
 * Faz o Doppelganger reproduzir fisicamente as acoes gravadas: anda ate cada
 * ponto, olha na direcao registrada e executa a quebra/colocacao de bloco
 * correspondente. Teleporte NUNCA e usado como comportamento normal (Secao
 * 9) - so existe como recuperacao de erro apos ficar preso por muito tempo.
 *
 * Contem os limites de seguranca da Secao 2: numero maximo de blocos
 * modificados por replay, raio maximo a partir do ponto de origem do
 * Doppelganger e protecao de blocos criticos (ex: bedrock).
 */
public class ReplayGoal extends Goal {

    private static final double ARRIVAL_DISTANCE_SQ = 1.1D * 1.1D;
    private static final double STUCK_RECOVERY_DISTANCE_SQ = 24 * 24;
    private static final int STUCK_TICKS_BEFORE_RECOVERY = 20 * 15; // 15s

    private final DoppelgangerEntity doppelganger;
    private int blocksModified = 0;
    private int stuckTicks = 0;

    public ReplayGoal(DoppelgangerEntity doppelganger) {
        this.doppelganger = doppelganger;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return !doppelganger.hasReplayFinished();
    }

    @Override
    public boolean shouldContinue() {
        return !doppelganger.hasReplayFinished();
    }

    @Override
    public void tick() {
        if (doppelganger.hasReplayFinished()) return;

        ReplayEvent event = doppelganger.getReplayScript().get(doppelganger.getReplayIndex());

        double dx = event.x - doppelganger.getX();
        double dy = event.y - doppelganger.getY();
        double dz = event.z - doppelganger.getZ();
        double distSq = dx * dx + dy * dy + dz * dz;

        if (distSq > STUCK_RECOVERY_DISTANCE_SQ) {
            stuckTicks++;
        } else {
            stuckTicks = 0;
        }

        // Recuperacao de erro (Secao 9): so reposiciona se ficou preso muito
        // tempo e longe demais do proximo ponto - nunca como comportamento normal.
        if (stuckTicks > STUCK_TICKS_BEFORE_RECOVERY) {
            doppelganger.refreshPositionAndAngles(event.x, event.y, event.z, event.yaw, event.pitch);
            stuckTicks = 0;
            return;
        }

        if (doppelganger.getNavigation().isIdle()) {
            double speed = event.sprinting ? DoppelgangerZ.CONFIG.doppelgangerSprintSpeed : DoppelgangerZ.CONFIG.doppelgangerWalkSpeed;
            doppelganger.getNavigation().startMovingTo(event.x, event.y, event.z, speed);
        }

        // Gira suavemente em direcao a rotacao gravada (nao instantaneo).
        doppelganger.yaw += MathHelper.wrapDegrees(event.yaw - doppelganger.yaw) * 0.3F;
        doppelganger.pitch += MathHelper.wrapDegrees(event.pitch - doppelganger.pitch) * 0.3F;

        if (distSq <= ARRIVAL_DISTANCE_SQ) {
            executeEventAction(event);
            doppelganger.advanceReplay();
            doppelganger.getNavigation().stop();
        }
    }

    private void executeEventAction(ReplayEvent event) {
        if (event.type == ReplayEvent.Type.MOVE || event.blockPos == null) return;

        BlockPos pos = event.blockPos;
        if (!withinSafetyRadius(pos)) return;
        if (blocksModified >= DoppelgangerZ.CONFIG.safetyMaxBlocksModifiedPerReplay) return;

        if (event.type == ReplayEvent.Type.BREAK_BLOCK && DoppelgangerZ.CONFIG.safetyAllowDoppelgangerToBreakBlocks) {
            BlockState current = doppelganger.world.getBlockState(pos);
            if (isSafeToModify(current) && event.blockState != null && current.getBlock() == event.blockState.getBlock()) {
                doppelganger.swingHand(Hand.MAIN_HAND);
                doppelganger.world.breakBlock(pos, false);
                blocksModified++;
            }
        } else if (event.type == ReplayEvent.Type.PLACE_BLOCK && DoppelgangerZ.CONFIG.safetyAllowDoppelgangerToPlaceBlocks) {
            BlockState current = doppelganger.world.getBlockState(pos);
            if (current.isAir() && event.blockState != null) {
                doppelganger.swingHand(Hand.MAIN_HAND);
                doppelganger.world.setBlockState(pos, event.blockState, 3);
                blocksModified++;
            }
        }
    }

    /** Secao 2: nunca permite que a reproducao modifique blocos fora de um raio controlado. */
    private boolean withinSafetyRadius(BlockPos pos) {
        BlockPos origin = doppelganger.getOriginPoint();
        return origin.isWithinDistance(pos, DoppelgangerZ.CONFIG.safetyMaxRadiusFromSpawnPoint);
    }

    /** Nunca permite modificar blocos criticos do mundo. */
    private boolean isSafeToModify(BlockState state) {
        return state.getBlock() != Blocks.BEDROCK && !state.isAir();
    }
}
