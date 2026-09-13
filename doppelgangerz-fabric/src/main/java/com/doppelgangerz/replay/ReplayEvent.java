package com.doppelgangerz.replay;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

/**
 * Uma unica acao gravada do jogador (Secao 8 da especificacao).
 *
 * Para manter a memoria sob controle (Secao 1/8), o replay usa apenas
 * amostras de movimento (keyframes) + eventos discretos de quebra/colocacao
 * de bloco - nao grava cada micro-alteracao de mouse.
 */
public class ReplayEvent {

    public enum Type {
        MOVE,
        BREAK_BLOCK,
        PLACE_BLOCK
    }

    public final Type type;
    public final long tickOffset;
    public final double x, y, z;
    public final float yaw, pitch;
    public final boolean sprinting;
    public final boolean sneaking;
    public final BlockPos blockPos;
    public final BlockState blockState;

    private ReplayEvent(Type type, long tickOffset, double x, double y, double z,
                         float yaw, float pitch, boolean sprinting, boolean sneaking,
                         BlockPos blockPos, BlockState blockState) {
        this.type = type;
        this.tickOffset = tickOffset;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.sprinting = sprinting;
        this.sneaking = sneaking;
        this.blockPos = blockPos;
        this.blockState = blockState;
    }

    public static ReplayEvent move(long tick, double x, double y, double z, float yaw, float pitch,
                                    boolean sprinting, boolean sneaking) {
        return new ReplayEvent(Type.MOVE, tick, x, y, z, yaw, pitch, sprinting, sneaking, null, null);
    }

    public static ReplayEvent breakBlock(long tick, double x, double y, double z, float yaw, float pitch,
                                          BlockPos pos, BlockState state) {
        return new ReplayEvent(Type.BREAK_BLOCK, tick, x, y, z, yaw, pitch, false, false, pos, state);
    }

    public static ReplayEvent placeBlock(long tick, double x, double y, double z, float yaw, float pitch,
                                          BlockPos pos, BlockState state) {
        return new ReplayEvent(Type.PLACE_BLOCK, tick, x, y, z, yaw, pitch, false, false, pos, state);
    }
}
