package com.dragonfight.mixin.dragon;

import com.dragonfight.fight.IDragonfightAccessor;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EndDragonFight.class)
public class EndDragonFightMixin implements IDragonfightAccessor
{
    @Shadow
    @Final
    private ServerBossEvent dragonEvent;

    @Override
    public ServerBossEvent getDragonEvent()
    {
        return dragonEvent;
    }
}
