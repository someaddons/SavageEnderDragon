package com.dragonfight.fight;

import com.dragonfight.DragonfightMod;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.DyingPhase;
import net.minecraft.entity.boss.dragon.phase.IPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

import static net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE;

/**
 * Custom manager for handling additional dragon difficulty
 */
public class DragonFightManagerCustom
{
    private static final int      CRYSTAL_RESPAWN_TIME    = 7000;
    private static final int      LIGHTNING_DESTROY_RANGE = 10 * 10;
    private static final int      ADD_TIMER               = 1600;
    private static       BlockPos crystalRespawnPos       = null;
    private static       int      crystalRespawnTimer     = 0;

    private static int timeSinceLastLanding = 0;

    /**
     * ^^ Add counters
     */
    private static       boolean              spawnAdds    = false;
    private final static BlockPos             spawnPos     = new BlockPos(0, 68, 0);
    private static       int                  spawnCounter = 0;
    private static       List<EndermanEntity> meleeAdds    = new ArrayList<>();

    private static int advancingLightningCurrent = 0;
    private static int advancingLightningStop    = 0;

    private static int advancingExplosionCurrent = 0;
    private static int advancingExplosionStop    = 0;

    private static EnderDragonEntity dragonEntity = null;

    private static boolean isFightRunning = true;

    public static AttributeModifier AA_GRAVITY_MOD = new AttributeModifier("fall", 5.0, AttributeModifier.Operation.ADDITION);

    public static void onCrystalDeath(final EnderCrystalEntity enderCrystalEntity, final DamageSource damageSource)
    {
        AreaEffectCloudEntity areaeffectcloudentity =
          new AreaEffectCloudEntity(enderCrystalEntity.level, enderCrystalEntity.getX(), enderCrystalEntity.getY(), enderCrystalEntity.getZ());

        if (dragonEntity != null)
        {
            areaeffectcloudentity.setOwner(dragonEntity);
        }

        notifyPlayer(enderCrystalEntity.level, "Crystal died from:" + damageSource);
        // Spawn ground area effect making the player walk away
        areaeffectcloudentity.setParticle(ParticleTypes.DRAGON_BREATH);
        areaeffectcloudentity.setRadius(3.0F);
        areaeffectcloudentity.setDuration(CRYSTAL_RESPAWN_TIME - 200 * getDifficulty());
        areaeffectcloudentity.setRadiusPerTick((5.0F - areaeffectcloudentity.getRadius()) / (float) areaeffectcloudentity.getDuration());
        areaeffectcloudentity.addEffect(new EffectInstance(Effects.HARM, 100, Math.min(2, getDifficulty() / 4)));
        areaeffectcloudentity.addEffect(new EffectInstance(Effects.BLINDNESS, 100, 1));
        enderCrystalEntity.level.addFreshEntity(areaeffectcloudentity);

        // On ranged crystal kill
        if (damageSource.getEntity() instanceof PlayerEntity && damageSource.getEntity().blockPosition().distSqr(enderCrystalEntity.blockPosition()) > LIGHTNING_DESTROY_RANGE)
        {
            // Hit player destroying the crystals from range with lightning
            LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(enderCrystalEntity.level);
            lightningboltentity.moveTo(damageSource.getEntity().getX(), damageSource.getEntity().getY(), damageSource.getEntity().getZ());
            lightningboltentity.setVisualOnly(false);
            enderCrystalEntity.level.addFreshEntity(lightningboltentity);

            // Spawn phantoms aggrod to the player
            for (int i = 0; i < Math.min(1, getDifficulty() / 4); i++)
            {
                final PhantomEntity phantomEntity = EntityType.PHANTOM.create(enderCrystalEntity.level);
                phantomEntity.setTarget((LivingEntity) damageSource.getEntity());
                phantomEntity.moveTo(damageSource.getEntity().getX(), damageSource.getEntity().getY() + 5, damageSource.getEntity().getZ());
                enderCrystalEntity.level.addFreshEntity(phantomEntity);
            }
        }
        else
        {
            // Melee kill reduces dragon HP
            if (dragonEntity != null && dragonEntity.getHealth() > 100)
            {
                dragonEntity.setHealth(dragonEntity.getHealth() * 0.9f);
                float f = (DragonfightMod.rand.nextFloat() - 0.5F) * 8.0F;
                float f1 = (DragonfightMod.rand.nextFloat() - 0.5F) * 4.0F;
                float f2 = (DragonfightMod.rand.nextFloat() - 0.5F) * 8.0F;
                dragonEntity.level.addParticle(ParticleTypes.EXPLOSION_EMITTER,
                  dragonEntity.getX() + (double) f,
                  dragonEntity.getY() + 2.0D + (double) f1,
                  dragonEntity.getZ() + (double) f2,
                  0.0D,
                  0.0D,
                  0.0D);
            }
        }
    }

    private static Map<UUID, Integer> flyingPlayers = new HashMap<>();

    public static void onWorldTick(final World world)
    {
        final DragonFightManager manager = ((ServerWorld) world).dragonFight();
        if (manager == null || manager.dragonEvent.getPlayers().isEmpty() || dragonEntity == null)
        {
            reset();
            return;
        }

        if (dragonEntity.getHealth() < dragonEntity.getMaxHealth() && dragonEntity.isAlive())
        {
            if (!isFightRunning && !manager.dragonEvent.getPlayers().isEmpty())
            {
                // Cleans entities on fight start
                List<MonsterEntity> monsterEntities = world.getEntitiesOfClass(MonsterEntity.class, dragonEntity.getBoundingBox().inflate(150));
                for (final MonsterEntity entity : monsterEntities)
                {
                    if (!(entity instanceof INPC) && !entity.isPersistenceRequired())
                    {
                        entity.remove();
                    }
                }

                isFightRunning = true;
            }
        }
        else
        {
            if (isFightRunning)
            {
                reset();
            }
            isFightRunning = false;
        }

        if (!isFightRunning)
        {
            return;
        }

        // Fix dragon flying forever on death
        if (dragonEntity.getPhaseManager().getCurrentPhase() instanceof DyingPhase && dragonEntity.getPhaseManager().getCurrentPhase().getFlyTargetLocation() != null)
        {
            if (dragonEntity.getPhaseManager().getCurrentPhase().getFlyTargetLocation().distanceToSqr(dragonEntity.blockPosition().getX(),
              dragonEntity.blockPosition().getY(), dragonEntity.getZ()) < 10)
            {
                dragonEntity.setHealth(0);
            }
        }

        timeSinceLastLanding++;
        // Fix landing
        if (timeSinceLastLanding > 120 * 20 && dragonEntity != null)
        {
            timeSinceLastLanding = 0;
            dragonEntity.getPhaseManager().setPhase(PhaseType.LANDING_APPROACH);
            notifyPlayer(world, "Forcing landing phase");
        }

        if (crystalRespawnPos != null)
        {
            if (--crystalRespawnTimer > 0)
            {
                if (crystalRespawnTimer == 200)
                {
                    // Spawns pre-respawn lightning
                    spawnLightningAtCircle(crystalRespawnPos, 4, world);
                }
            }
            else
            {
                notifyPlayer(world, "Respawning crystal at" + crystalRespawnPos);
                respawnCrystalAt(crystalRespawnPos, world);
            }
        }


        if (dragonEntity.getHealth() > dragonEntity.getMaxHealth() * 0.9)
        {
            return;
        }

        for (final PlayerEntity player : manager.dragonEvent.getPlayers())
        {
            int time = flyingPlayers.computeIfAbsent(player.getUUID(), s -> 0);

            if (isFlying(player))
            {
                if (time == 300)
                {
                    // Kill player
                    player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get()).addTransientModifier(AA_GRAVITY_MOD);
                    flyingPlayers.put(player.getUUID(), ++time);
                }
                else if (time > 400)
                {
                    player.hurt(DamageSource.FALL, player.getMaxHealth() * 0.9f);
                    player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get()).removeModifier(AA_GRAVITY_MOD);
                    flyingPlayers.put(player.getUUID(), 0);
                }
                else
                {
                    // Remove if falling didnt happen properly after 5s
                    if (time == 100)
                    {
                        player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get()).removeModifier(AA_GRAVITY_MOD);
                    }

                    flyingPlayers.put(player.getUUID(), ++time);
                }
            }
            else
            {
                if (time > 300)
                {
                    player.hurt(DamageSource.FALL, player.getMaxHealth() * 0.9f);
                    player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get()).removeModifier(AA_GRAVITY_MOD);
                }
                flyingPlayers.put(player.getUUID(), 0);
            }
        }

        if (advancingLightningCurrent > 0 && world.getGameTime() % 100 == 0)
        {
            advancingLightningCurrent += 3;
            spawnLightningAtCircle(spawnPos, advancingLightningCurrent, world);

            if (advancingLightningCurrent > advancingLightningStop)
            {
                advancingLightningCurrent = 0;
                advancingLightningStop = 0;
            }
        }

        if (dragonEntity != null && advancingExplosionCurrent == 0 && advancingLightningCurrent == 0 && (dragonEntity.getHealth() / dragonEntity.getMaxHealth()) < 0.15d)
        {
            advancingExplosionCurrent = 8;
            advancingExplosionStop = 50;
        }

        if (advancingExplosionCurrent > 0 && world.getGameTime() % 200 == 0)
        {
            advancingExplosionCurrent += 3;
            explodeInCircleAround(spawnPos, advancingExplosionCurrent, world);

            if (advancingExplosionCurrent > advancingExplosionStop)
            {
                advancingExplosionCurrent = 0;
                advancingExplosionStop = 0;
            }
        }

        if (spawnAdds && spawnCounter++ > (ADD_TIMER - Math.max(400, getDifficulty() * 50)))
        {
            notifyPlayer(world, "Spawning melee add");
            spawnMeleeAdds(world);
            spawnCounter = 0;
        }
    }

    /**
     * Check if a player is considered flying
     *
     * @param player
     * @return
     */
    private static boolean isFlying(final PlayerEntity player)
    {
        return player != null && (player.hasImpulse || !player.isOnGround()) && player.fallDistance <= 0.1f && player.level.isEmptyBlock(player.blockPosition().below(2));
    }

    /**
     * Reset saved counters
     */
    private static void reset()
    {
        crystalRespawnPos = null;
        spawnAdds = false;
        spawnCounter = 0;
        for (final EndermanEntity endermanEntity : meleeAdds)
        {
            endermanEntity.remove();
        }
        meleeAdds.clear();
    }

    /**
     * Spawn aggroed enderman as melee adds
     *
     * @param world
     */
    private static void spawnMeleeAdds(final World world)
    {
        meleeAdds.removeIf(endermanEntity -> endermanEntity.removed);

        if (meleeAdds.size() >= getDifficulty())
        {
            return;
        }

        final EndermanEntity endermanEntity = EntityType.ENDERMAN.create(world);
        endermanEntity.moveTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        world.addFreshEntity(endermanEntity);

        final List<PlayerEntity> closesPlayers = world.getNearbyPlayers(EntityPredicate.DEFAULT, endermanEntity, endermanEntity.getBoundingBox().inflate(20));
        if (!closesPlayers.isEmpty())
        {
            final PlayerEntity closestPlayer = closesPlayers.get(DragonfightMod.rand.nextInt(closesPlayers.size()));
            endermanEntity.setTarget(closestPlayer);
        }
        else
        {
            final List<PlayerEntity> farPlayers = world.getNearbyPlayers(EntityPredicate.DEFAULT, endermanEntity, endermanEntity.getBoundingBox().inflate(60, 120, 60));
            if (!farPlayers.isEmpty())
            {
                final PlayerEntity closestPlayer = farPlayers.get(DragonfightMod.rand.nextInt(farPlayers.size()));
                endermanEntity.setTarget(closestPlayer);
            }
        }
    }

    /**
     * Respawns a crystal at the given pos
     *
     * @param pos   pos to respawn at
     * @param world world to respawn in
     */
    private static void respawnCrystalAt(final BlockPos pos, final World world)
    {
        if (world.getEntitiesOfClass(EnderCrystalEntity.class, new AxisAlignedBB(pos).inflate(2)).isEmpty())
        {
            // Respawn crystal
            final EnderCrystalEntity crystal = EntityType.END_CRYSTAL.create(world);
            crystal.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            world.addFreshEntity(crystal);

            for (int i = 0; i < getDifficulty() / 2; i++)
            {
                // Spawn blaze on respawn
                final BlazeEntity blaze = EntityType.BLAZE.create(world);
                blaze.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                world.addFreshEntity(blaze);

                blaze.setTarget(world.getNearestPlayer(blaze, 100));
            }

            float f = (DragonfightMod.rand.nextFloat() - 0.5F) * 8.0F;
            float f1 = (DragonfightMod.rand.nextFloat() - 0.5F) * 4.0F;
            float f2 = (DragonfightMod.rand.nextFloat() - 0.5F) * 8.0F;
            world.addParticle(ParticleTypes.EXPLOSION_EMITTER, crystal.getX() + (double) f, crystal.getY() + 2.0D + (double) f1, crystal.getZ() + (double) f2, 0.0D, 0.0D, 0.0D);
        }

        crystalRespawnPos = null;
    }

    /**
     * Called when the dragon heals
     *
     * @param dragonEntity
     */
    public static void onDragonHeal(final EnderDragonEntity dragonEntity)
    {
        dragonEntity.setHealth(Math.min(dragonEntity.getMaxHealth(), dragonEntity.getHealth() + (getDifficulty() / 5f)));
    }

    /**
     * Called when attacking a player
     *
     * @param damage
     * @return
     */
    public static float onAttackPlayer(final float damage)
    {
        return damage + getDifficulty() / 2;
    }

    public static void onPhaseChange(
      final PhaseType<?> newPhase,
      final PhaseType<? extends IPhase> oldphase,
      final EnderDragonEntity dragon)
    {
        // Avoid doing anything when we're reading a new entity, as nbt read does save the phases
        if (dragonEntity != dragon)
        {
            dragonEntity = dragon;
            return;
        }

        if (dragon == null || !(dragon.level instanceof ServerWorld) || !dragonEntity.isAlive())
        {
            return;
        }

        final DragonFightManager manager = ((ServerWorld) dragon.level).dragonFight();
        if (manager == null || manager.dragonEvent.getPlayers().isEmpty())
        {
            return;
        }

        notifyPlayer(dragon.level, "Next phase:" + newPhase.toString());

        if (newPhase == PhaseType.TAKEOFF)
        {
            // Start spawning endermen
            spawnAdds = true;

            checkCrystalsToRespawn(dragon.level);
            if ((dragon.getHealth() / dragon.getMaxHealth()) < 0.25d && dragon.getDragonFight() != null)
            {
                dragon.level.playLocalSound(dragon.getX(),
                  dragon.getY(),
                  dragon.getZ(),
                  SoundEvents.ENDER_DRAGON_GROWL,
                  dragon.getSoundSource(),
                  2.5F,
                  0.8F + DragonfightMod.rand.nextFloat() * 0.3F,
                  false);
                for (final PlayerEntity playerEntity : dragon.getDragonFight().dragonEvent.getPlayers())
                {
                    playerEntity.addEffect(new EffectInstance(Effects.WITHER, 100, getDifficulty() / 3));
                    if ((dragon.getHealth() / dragon.getMaxHealth()) < 0.10d)
                    {
                        playerEntity.addEffect(new EffectInstance(Effects.LEVITATION, 200, 1));
                    }
                }
            }
        }
        if ((newPhase == PhaseType.LANDING_APPROACH) || (newPhase == PhaseType.DYING))
        {
            // Stop spawning
            timeSinceLastLanding = 0;
            spawnAdds = false;
        }
        if (oldphase == PhaseType.LANDING && newPhase == PhaseType.SITTING_SCANNING)
        {
            timeSinceLastLanding = 0;

            final double healthpercent = (dragon.getHealth() / dragon.getMaxHealth());
            if (healthpercent < 0.5d)
            {
                advancingLightningCurrent = 6;
                advancingLightningStop = 50;
            }
            else
            {
                spawnLightningAtCircle(spawnPos, DragonfightMod.rand.nextInt(16) + 8, dragon.level);
            }
        }
    }

    private static void checkCrystalsToRespawn(final World world)
    {
        if (crystalRespawnPos != null)
        {
            return;
        }

        final List<EndSpikeFeature.EndSpike> spikes = EndSpikeFeature.getSpikesForLevel((ServerWorld) world);
        Collections.shuffle(spikes, DragonfightMod.rand);
        for (EndSpikeFeature.EndSpike spike : spikes)
        {
            final BlockPos pos = new BlockPos(spike.getCenterX(), spike.getHeight(), spike.getCenterZ());

            if (world.getEntitiesOfClass(EnderCrystalEntity.class, spike.getTopBoundingBox()).isEmpty())
            {
                crystalRespawnPos = pos;
                crystalRespawnTimer = Math.max(200, CRYSTAL_RESPAWN_TIME - 400 * getDifficulty());
                notifyPlayer(world, "Adding respawn at :" + crystalRespawnPos + " in:" + crystalRespawnTimer);
                break;
            }
        }
    }

    /**
     * Spawns a circular lightning hit
     *
     * @param midPoint
     * @param radius
     * @param world
     */
    private static void spawnLightningAtCircle(final BlockPos midPoint, final int radius, final World world)
    {
        Set<BlockPos> lightningPositions = getCircularPositionsAround(midPoint, radius, 5);
        for (final BlockPos lightningPos : lightningPositions)
        {
            notifyPlayer(world,
              "spawning plightning at!" + new BlockPos(lightningPos.getX(),
                world.getHeightmapPos(WORLD_SURFACE, lightningPos).getY(),
                lightningPos.getZ()));

            final int yLevel = world.getHeightmapPos(WORLD_SURFACE, lightningPos).getY();

            // Dont hit too varied height differences
            if (Math.abs(midPoint.getY() - yLevel) > 20)
            {
                continue;
            }

            LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(world);
            lightningboltentity.moveTo(lightningPos.getX(), yLevel, lightningPos.getZ());
            lightningboltentity.setVisualOnly(false);
            world.addFreshEntity(lightningboltentity);
        }
    }

    /**
     * Spawns a circular lightning hit
     *
     * @param midPoint
     * @param radius
     * @param world
     */
    private static void explodeInCircleAround(final BlockPos midPoint, final int radius, final World world)
    {
        Set<BlockPos> explodePos = getCircularPositionsAround(midPoint, radius, 15);
        for (final BlockPos lightningPos : explodePos)
        {
            notifyPlayer(world,
              "spawning explosion at!" + new BlockPos(lightningPos.getX(),
                world.getHeightmapPos(WORLD_SURFACE, lightningPos).getY(),
                lightningPos.getZ()));

            final int yLevel = world.getHeightmapPos(WORLD_SURFACE, lightningPos).getY();

            // Dont hit too varied height differences
            if (Math.abs(midPoint.getY() - yLevel) > 20)
            {
                continue;
            }

            world.explode(dragonEntity,
              lightningPos.getX(),
              lightningPos.getY(),
              lightningPos.getZ(),
              1 + getDifficulty() / 4,
              false,
              Explosion.Mode.NONE);
        }
    }

    private static Set<BlockPos> getCircularPositionsAround(final BlockPos start, final int radius, final int precision)
    {
        Set<BlockPos> positions = new HashSet<>();

        for (int i = 0; i < 360; i += precision)
        {
            int x = (int) Math.round(radius * Math.cos(Math.toRadians(i)));
            int z = (int) Math.round(radius * Math.sin(Math.toRadians(i)));

            positions.add(start.offset(x, 0, z));
        }

        return positions;
    }

    /**
     * Notify OP's of the fights state for debugging
     *
     * @param world
     * @param message
     */
    public static void notifyPlayer(final World world, final String message)
    {
        if (DragonfightMod.config.getCommonConfig().printDragonPhases.get())
        {
            for (final PlayerEntity player : ((ServerWorld) world).players())
            {
                if (world.getServer() != null && ((ServerWorld) world).getServer().getProfilePermissions(player.getGameProfile()) > 0)
                {
                    player.sendMessage(new StringTextComponent(message), player.getUUID());
                }
            }
        }
    }

    /**
     * Get the total difficulty number
     *
     * @return
     */
    private static int getDifficulty()
    {
        int difficulty = DragonfightMod.config.getCommonConfig().dragonDifficulty.get();

        if (dragonEntity != null)
        {
            difficulty += dragonEntity.level.getDifficulty().getId();
            if (dragonEntity.getDragonFight() != null)
            {
                difficulty += dragonEntity.getDragonFight().dragonEvent.getPlayers().size();
            }
        }

        return difficulty;
    }
}
