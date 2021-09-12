package com.dragonfight.mixin.dragon;

import net.minecraft.entity.effect.LightningBoltEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LightningBoltEntity.class)
public class LightningBoldSoundMixin
{
    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 10000.0F))
    private float onAttackPlayers(float damage)
    {
        return 1000.0F;
    }
}
