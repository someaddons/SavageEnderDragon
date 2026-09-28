package com.dragonfight.mixin.dragon;

import com.dragonfight.DragonfightMod;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SoundEngine.class, priority = 100)
public abstract class SoundEngineMixin
{
    @Shadow
    protected abstract float calculateVolume(final float p_235258_, final SoundSource p_235259_);

    /**
     * Fix https://bugs.mojang.com/browse/MC?fixVersion=Minecraft%2014w26a until 1.21.9
     *
     * @param volume
     * @return
     */
    @ModifyVariable(method = "calculateVolume(FLnet/minecraft/sounds/SoundSource;)F", at = @At("HEAD"), argsOnly = true)
    private float adjustVolume(float volume)
    {
        return Mth.clamp(volume, 0.0F, 1.0F);
    }

    @Redirect(
        method = "play",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundEngine;calculateVolume(FLnet/minecraft/sounds/SoundSource;)F"
        )
    )
    private float dragonfight$reduceThunderVolume(final SoundEngine engine, float volume, final SoundSource soundSource, final SoundInstance sound)
    {
        if (sound.getLocation().equals(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation()) || sound.getLocation().equals(SoundEvents.LIGHTNING_BOLT_IMPACT.getLocation()))
        {
            return (float) (calculateVolume(volume, soundSource) * DragonfightMod.config.getCommonConfig().lightningSoundVolumeModifier);
        }

        return calculateVolume(volume, soundSource);
    }
}
