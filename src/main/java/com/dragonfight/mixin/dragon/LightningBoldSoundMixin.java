package com.dragonfight.mixin.dragon;

import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LightningBolt.class)
public class LightningBoldSoundMixin
{
    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 10000.0F))
    private float silentLightningB(float damage)
    {
        return 100F;
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 2.0F))
    private float silentLightning(float damage)
    {
        return 0.005F;
    }
}
