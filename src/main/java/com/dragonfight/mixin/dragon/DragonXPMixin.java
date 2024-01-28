package com.dragonfight.mixin.dragon;

import com.dragonfight.DragonfightMod;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnderDragon.class)
public class DragonXPMixin
{
    @Redirect(method = "tickDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(F)I"))
    private int onDeathXp(final float f)
    {
        return Mth.floor(f * DragonfightMod.config.getCommonConfig().dragonXPModifier);
    }
}
