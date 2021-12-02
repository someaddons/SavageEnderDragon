package com.dragonfight.mixin.dragon;

import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LightningBolt.class)
public class LightningBoldSoundMixin
{
    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 10000.0F))
    private float onAttackPlayers(float damage)
    {
        return 1000.0F;
    }
}
