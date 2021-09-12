package com.dragonfight.event;

import com.dragonfight.DragonfightMod;
import com.dragonfight.fight.DragonFightManagerCustom;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handler to catch server tick events
 */
public class EventHandler
{
    @SubscribeEvent
    public static void onWorldTick(final TickEvent.WorldTickEvent event)
    {
        if (!event.world.isClientSide && event.world.dimension() == World.END)
        {
            DragonFightManagerCustom.onWorldTick(event.world);
        }
    }

    @SubscribeEvent
    public static void onEnterWorld(final EntityJoinWorldEvent event)
    {
        if (event.getEntity() instanceof EnderDragonEntity)
        {
            final float pct = ((EnderDragonEntity) event.getEntity()).getHealth() / ((EnderDragonEntity) event.getEntity()).getMaxHealth();
            ((EnderDragonEntity) event.getEntity()).getAttribute(Attributes.MAX_HEALTH)
              .setBaseValue(Math.max(400 + 50 * DragonfightMod.config.getCommonConfig().dragonDifficulty.get(), ((EnderDragonEntity) event.getEntity()).getMaxHealth()));
            ((EnderDragonEntity) event.getEntity()).setHealth(((EnderDragonEntity) event.getEntity()).getMaxHealth() * pct);
        }
    }

    @SubscribeEvent
    public static void onLivingSpawn(final LivingSpawnEvent.CheckSpawn event)
    {
        /**
         * Disable entity spawn for the dragon fight
         */
        if (event.getWorld() instanceof ServerWorld && ((ServerWorld) event.getWorld()).dimension() == World.END)
        {
            if (BlockPos.ZERO.distSqr(event.getX(), 64.0d, event.getZ(), false) < 300 * 300)
            {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
