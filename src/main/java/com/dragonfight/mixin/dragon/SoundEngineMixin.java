package com.dragonfight.mixin.dragon;

import com.dragonfight.DragonfightMod;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SoundEngine.class, priority = 100)
public abstract class SoundEngineMixin
{
    @ModifyExpressionValue(
        method = "play",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundEngine;calculateVolume(FLnet/minecraft/sounds/SoundSource;)F"
        )
    )
    private float dragonfight$reduceThunderVolume(float volume, SoundInstance sound)
    {
        if (sound.getIdentifier().equals(SoundEvents.LIGHTNING_BOLT_THUNDER.location()) || sound.getIdentifier().equals(SoundEvents.LIGHTNING_BOLT_IMPACT.location()))
        {
            return (float) (volume * DragonfightMod.config.getCommonConfig().lightningSoundVolumeModifier);
        }

        return volume;
    }
}
