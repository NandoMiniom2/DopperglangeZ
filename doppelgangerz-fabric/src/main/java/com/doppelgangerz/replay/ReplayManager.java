package com.doppelgangerz.replay;

import com.doppelgangerz.DoppelgangerZ;
import com.doppelgangerz.entity.DoppelgangerEntity;
import com.doppelgangerz.registry.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gerencia a gravacao das acoes do jogador e o disparo da criacao do
 * Doppelganger. Implementa as Secoes 4, 5, 7, 8 e 9 (nucleo) da
 * especificacao.
 */
public class ReplayManager {

    private static final Map<UUID, PlayerReplayData> DATA = new HashMap<>();
    private static final Map<UUID, Long> PENDING_SPAWN_TICK = new HashMap<>();
    private static int sampleCounter = 0;

    public static void register() {
        // ponto de extensao futuro (ex: persistir replay em disco por save)
    }

    public static PlayerReplayData getOrCreate(UUID playerId) {
        return DATA.computeIfAbsent(playerId, id -> new PlayerReplayData());
    }

    /** Chamado pelo mixin quando um bloco e colocado com sucesso por um jogador. */
    public static void onBlockPlaced(PlayerEntity player, BlockPos pos, BlockState state) {
        if (!(player instanceof ServerPlayerEntity) || player.world.isClient) return;
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        PlayerReplayData data = getOrCreate(player.getUuid());
        if (data.doppelgangerSpawned) return;

        long serverTick = serverPlayer.getServer().getTicks();
        if (data.recordingStartTick < 0) {
            data.recordingStartTick = serverTick;
        }
        if (!data.firstBlockDetected) {
            data.firstBlockDetected = true;
            DoppelgangerZ.LOGGER.info("[DoppelgangerZ] Primeiro bloco detectado para {}", player.getEntityName());
        }

        data.addEvent(ReplayEvent.placeBlock(serverTick - data.recordingStartTick,
                        player.getX(), player.getY(), player.getZ(), player.yaw, player.pitch, pos, state),
                DoppelgangerZ.CONFIG.maxRecordedEvents);

        data.blocksPlacedSinceStart++;

        if (!data.doppelgangerSpawned
                && !PENDING_SPAWN_TICK.containsKey(player.getUuid())
                && data.blocksPlacedSinceStart >= DoppelgangerZ.CONFIG.blocksToTriggerDoppelganger) {
            PENDING_SPAWN_TICK.put(player.getUuid(), serverTick + DoppelgangerZ.CONFIG.activationDelayTicks);
            DoppelgangerZ.LOGGER.info("[DoppelgangerZ] Limite de construcao atingido para {}. Doppelganger sera ativado em breve.",
                    player.getEntityName());
        }
    }

    /** Chamado pelo mixin quando um bloco e quebrado com sucesso por um jogador. */
    public static void onBlockBroken(PlayerEntity player, BlockPos pos, BlockState state) {
        if (!(player instanceof ServerPlayerEntity) || player.world.isClient) return;
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        PlayerReplayData data = getOrCreate(player.getUuid());
        if (data.recordingStartTick < 0 || data.doppelgangerSpawned) return;

        long serverTick = serverPlayer.getServer().getTicks();
        data.addEvent(ReplayEvent.breakBlock(serverTick - data.recordingStartTick,
                        player.getX(), player.getY(), player.getZ(), player.yaw, player.pitch, pos, state),
                DoppelgangerZ.CONFIG.maxRecordedEvents);
    }

    /** Amostragem periodica de movimento + verificacao de disparo do spawn. */
    public static void onServerTick(MinecraftServer server) {
        sampleCounter++;
        boolean sample = sampleCounter % Math.max(1, DoppelgangerZ.CONFIG.minTicksBetweenMoveSamples) == 0;
        long serverTick = server.getTicks();

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            PlayerReplayData data = DATA.get(player.getUuid());
            if (data == null || data.recordingStartTick < 0 || data.doppelgangerSpawned) continue;

            if (sample) {
                data.addEvent(ReplayEvent.move(serverTick - data.recordingStartTick,
                                player.getX(), player.getY(), player.getZ(), player.yaw, player.pitch,
                                player.isSprinting(), player.isSneaking()),
                        DoppelgangerZ.CONFIG.maxRecordedEvents);
            }

            Long spawnAt = PENDING_SPAWN_TICK.get(player.getUuid());
            if (spawnAt != null && serverTick >= spawnAt) {
                PENDING_SPAWN_TICK.remove(player.getUuid());
                spawnDoppelganger(player, data);
            }
        }
    }

    private static void spawnDoppelganger(ServerPlayerEntity player, PlayerReplayData data) {
        if (data.doppelgangerSpawned || data.events.isEmpty()) return;
        ServerWorld world = player.getServerWorld();

        BlockPos spawnPos = findSpawnPositionNear(world, player.getBlockPos());
        DoppelgangerEntity doppelganger = new DoppelgangerEntity(ModEntities.DOPPELGANGER, world);
        doppelganger.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                player.yaw, 0F);
        doppelganger.copyAppearanceFrom(player);
        doppelganger.setOriginPoint(spawnPos);
        doppelganger.setReplayScript(new ArrayList<>(data.events));

        world.spawnEntity(doppelganger);
        data.doppelgangerSpawned = true;

        DoppelgangerZ.LOGGER.info("[DoppelgangerZ] Doppelganger de {} nasceu em {}", player.getEntityName(), spawnPos);
    }

    private static BlockPos findSpawnPositionNear(ServerWorld world, BlockPos center) {
        for (int attempt = 0; attempt < 12; attempt++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            int distance = 14 + world.random.nextInt(10);
            int x = center.getX() + (int) (Math.cos(angle) * distance);
            int z = center.getZ() + (int) (Math.sin(angle) * distance);
            BlockPos top = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
            if (!world.getBlockState(top.down()).isAir()) {
                return top;
            }
        }
        return center.up(1);
    }
}
