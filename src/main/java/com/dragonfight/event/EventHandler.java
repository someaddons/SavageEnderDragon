package com.dragonfight.event;

import com.dragonfight.fight.DragonFightManagerCustom;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Handler to catch server tick events
 */
public class EventHandler
{
    public static void onWorldTick(final ServerLevel level)
    {
        if (!level.isClientSide && level.dimension() == Level.END)
        {
            DragonFightManagerCustom.onWorldTick(level);
        }
    }

    public static void onLivingSpawn(final ServerLevel serverLevel, final BlockPos blockPos, final CallbackInfo ci)
    {
        /**
         * Disable entity spawn for the dragon fight
         */
        if (serverLevel.dimension() == Level.END && DragonFightManagerCustom.isFightRunning)
        {
            if (BlockPos.ZERO.distToCenterSqr(blockPos.getX(), 64.0d, blockPos.getZ()) < 300 * 300)
            {
                ci.cancel();
            }
        }
    }

    public static void onPlayerTick(final Player player)
    {
        final Integer flyTime = DragonFightManagerCustom.flyingPlayers.get(player.getUUID());
        if (flyTime != null && !player.isCreative())
        {
            player.getAbilities().flying = false;
        }
    }
}
