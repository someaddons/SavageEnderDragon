package com.dragonfight.mixin.dragon;

import com.dragonfight.DragonfightMod;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SoundEngine.class, priority = 10000)
public abstract class SoundEngineMixin
{
    @Shadow
    protected abstract float getVolume(@Nullable final SoundSource p_120259_);

    /**
     * Fix https://bugs.mojang.com/browse/MC?fixVersion=Minecraft%2014w26a until 1.21.9
     *
     * @param volume
     * @param p_235259_
     * @return
     */
    @Overwrite()
    private float calculateVolume(float volume, SoundSource p_235259_)
    {
        return Mth.clamp(volume, 0.0F, 1.0F) * this.getVolume(p_235259_);
    }

    @ModifyExpressionValue(
        method = "play",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundEngine;calculateVolume(FLnet/minecraft/sounds/SoundSource;)F"
        )
    )
    private float dragonfight$reduceThunderVolume(float volume, SoundInstance sound)
    {
        if (sound.getLocation().equals(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation()) || sound.getLocation().equals(SoundEvents.LIGHTNING_BOLT_IMPACT.getLocation()))
        {
            return (float) (volume * DragonfightMod.config.getCommonConfig().lightningSoundVolumeModifier);
        }

        return volume;
    }
}
