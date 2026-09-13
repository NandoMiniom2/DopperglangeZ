package com.doppelgangerz.entity;

import com.doppelgangerz.DoppelgangerZ;
import com.doppelgangerz.entity.ai.ReplayGoal;
import com.doppelgangerz.replay.ReplayEvent;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class DoppelgangerEntity extends PathAwareEntity {

    private GameProfile copiedProfile;
    private BlockPos originPoint;
    private final List<ReplayEvent> replayScript = new ArrayList<>();
    private int replayIndex = 0;

    public DoppelgangerEntity(EntityType<? extends DoppelgangerEntity> type, World world) {
        super(type, world);
        this.setCanPickUpLoot(false);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ReplayGoal(this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
    }

    public void copyAppearanceFrom(PlayerEntity player) {
        this.copiedProfile = player.getGameProfile();
        this.setCustomName(player.getName());
        this.setCustomNameVisible(false);
    }

    public GameProfile getCopiedProfile() {
        return copiedProfile;
    }

    public void setOriginPoint(BlockPos pos) {
        this.originPoint = pos;
    }

    public BlockPos getOriginPoint() {
        return originPoint == null ? this.getBlockPos() : originPoint;
    }

    public void setReplayScript(List<ReplayEvent> events) {
        this.replayScript.clear();
        this.replayScript.addAll(events);
        this.replayIndex = 0;
    }

    public List<ReplayEvent> getReplayScript() {
        return replayScript;
    }

    public int getReplayIndex() {
        return replayIndex;
    }

    public void advanceReplay() {
        replayIndex++;
    }

    public boolean hasReplayFinished() {
        return replayScript.isEmpty() || replayIndex >= replayScript.size();
    }
}
