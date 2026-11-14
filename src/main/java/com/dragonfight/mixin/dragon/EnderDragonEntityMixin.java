package com.dragonfight.mixin.dragon;

import com.dragonfight.DragonfightMod;
import com.dragonfight.fight.DragonFightManagerCustom;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragon.class)
public class EnderDragonEntityMixin
{
    final EnderDragon self = (EnderDragon) (Object) this;

    @Inject(method = "checkCrystals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;setHealth(F)V"))
    private void onDragonHeal(final CallbackInfo ci)
    {
        DragonFightManagerCustom.onDragonHeal(self);
    }

    @Inject(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/boss/enderdragon/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "HEAD"), cancellable = true)
    private void ignoreBedDamage(
        final ServerLevel level,
        final EnderDragonPart part,
        final DamageSource damageSource,
        final float damage,
        final CallbackInfoReturnable<Boolean> cir)
    {
        if (damageSource.is(DamageTypes.BAD_RESPAWN_POINT) || damageSource.getMsgId().contains("explosion") && DragonfightMod.config.getCommonConfig().explosionImmunity)
        {
            cir.setReturnValue(false);
        }
    }
}
