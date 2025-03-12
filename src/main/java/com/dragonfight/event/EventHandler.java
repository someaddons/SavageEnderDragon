package com.dragonfight.event;

import com.dragonfight.DragonfightMod;
import com.dragonfight.fight.DragonFightManagerCustom;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handler to catch server tick events
 */
public class EventHandler
{
    @SubscribeEvent
    public static void onWorldTick(final LevelTickEvent.Post event)
    {
        if (!event.getLevel().isClientSide && event.getLevel().dimension() == Level.END)
        {
            DragonFightManagerCustom.onWorldTick(event.getLevel());
        }
    }

    @SubscribeEvent
    public static void onEnterWorld(final EntityJoinLevelEvent event)
    {
        if (event.getEntity() instanceof EnderDragon)
        {
            final float pct = ((EnderDragon) event.getEntity()).getHealth() / ((EnderDragon) event.getEntity()).getMaxHealth();
            ((EnderDragon) event.getEntity()).getAttribute(Attributes.MAX_HEALTH)
              .setBaseValue(Math.max(400 + 50 * DragonfightMod.config.getCommonConfig().dragonDifficulty, ((EnderDragon) event.getEntity()).getMaxHealth()));
            ((EnderDragon) event.getEntity()).setHealth(((EnderDragon) event.getEntity()).getMaxHealth() * pct);
        }
    }

    @SubscribeEvent
    public static void onLivingSpawn(final FinalizeSpawnEvent event)
    {
        /**
         * Disable entity spawn for the dragon fight
         */
        if (event.getLevel() instanceof ServerLevel && ((ServerLevel) event.getLevel()).dimension() == Level.END && DragonFightManagerCustom.isFightRunning)
        {
            if (BlockPos.ZERO.distToCenterSqr(event.getX(), 64.0d, event.getZ()) < 300 * 300 && DragonfightMod.config.getCommonConfig().disableDragonAreaSpawns)
            {
                event.setSpawnCancelled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerTick(final PlayerTickEvent.Post playerTickEvent)
    {
        final Integer flyTime = DragonFightManagerCustom.flyingPlayers.get(playerTickEvent.getEntity().getUUID());
        if (flyTime != null && !playerTickEvent.getEntity().isCreative())
        {
            playerTickEvent.getEntity().getAbilities().flying = false;
        }
    }

    @SubscribeEvent
    public static void onExpDrop(final LivingExperienceDropEvent event)
    {
        if (event.getEntity() instanceof EnderDragon)
        {
            event.setDroppedExperience((int) (event.getDroppedExperience() * DragonfightMod.config.getCommonConfig().dragonXPModifier));
        }
    }

    @SubscribeEvent
    public static void onHurt(final LivingDamageEvent.Pre event)
    {
        if (event.getSource().getEntity() instanceof EnderDragon)
        {
            event.setNewDamage(DragonFightManagerCustom.onAttackPlayer(event.getNewDamage()));
        }
    }
}
