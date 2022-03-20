package com.dragonfight.event;

import com.dragonfight.DragonfightMod;
import com.dragonfight.fight.DragonFightManagerCustom;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handler to catch server tick events
 */
public class EventHandler
{
    @SubscribeEvent
    public static void onWorldTick(final TickEvent.WorldTickEvent event)
    {
        if (!event.world.isClientSide && event.world.dimension() == Level.END)
        {
            DragonFightManagerCustom.onWorldTick(event.world);
        }
    }

    @SubscribeEvent
    public static void onEnterWorld(final EntityJoinWorldEvent event)
    {
        if (event.getEntity() instanceof EnderDragon)
        {
            final float pct = ((EnderDragon) event.getEntity()).getHealth() / ((EnderDragon) event.getEntity()).getMaxHealth();
            ((EnderDragon) event.getEntity()).getAttribute(Attributes.MAX_HEALTH)
              .setBaseValue(Math.max(400 + 50 * DragonfightMod.config.getCommonConfig().dragonDifficulty.get(), ((EnderDragon) event.getEntity()).getMaxHealth()));
            ((EnderDragon) event.getEntity()).setHealth(((EnderDragon) event.getEntity()).getMaxHealth() * pct);
        }
    }

    @SubscribeEvent
    public static void onLivingSpawn(final LivingSpawnEvent.CheckSpawn event)
    {
        /**
         * Disable entity spawn for the dragon fight
         */
        if (event.getWorld() instanceof ServerLevel && ((ServerLevel) event.getWorld()).dimension() == Level.END && DragonFightManagerCustom.isFightRunning)
        {
            if (BlockPos.ZERO.distSqr(event.getX(), 64.0d, event.getZ(), false) < 300 * 300)
            {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerTick(final TickEvent.PlayerTickEvent playerTickEvent)
    {
        final Integer flyTime = DragonFightManagerCustom.flyingPlayers.get(playerTickEvent.player.getUUID());
        if (flyTime != null && !playerTickEvent.player.isCreative())
        {
            playerTickEvent.player.getAbilities().flying = false;
        }
    }
}
