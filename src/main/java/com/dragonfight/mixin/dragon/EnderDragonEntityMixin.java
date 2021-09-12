package com.dragonfight.mixin.dragon;

import com.dragonfight.fight.DragonFightManagerCustom;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.util.BedExplosionDamageSource;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragonEntity.class)
public class EnderDragonEntityMixin
{
    @Shadow
    public boolean           inWall;
    final  EnderDragonEntity self = (EnderDragonEntity) (Object) this;

    @Inject(method = "checkCrystals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;setHealth(F)V"))
    private void onDragonHeal(final CallbackInfo ci)
    {
        DragonFightManagerCustom.onDragonHeal(self);
    }

    @Inject(method = "hurt(Lnet/minecraft/entity/boss/dragon/EnderDragonPartEntity;Lnet/minecraft/util/DamageSource;F)Z", at = @At(value = "HEAD"), cancellable = true)
    private void ignoreBedDamage(final EnderDragonPartEntity p_213403_1_, final DamageSource damageSource, final float p_213403_3_, final CallbackInfoReturnable<Boolean> cir)
    {
        if (damageSource instanceof BedExplosionDamageSource || damageSource.getMsgId().contains("explosion"))
        {
            cir.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "hurt(Ljava/util/List;)V", constant = @Constant(floatValue = 10.0F))
    private float onAttackPlayers(float damage)
    {
        return DragonFightManagerCustom.onAttackPlayer(damage);
    }

    @ModifyConstant(method = "knockBack", constant = @Constant(floatValue = 5.0F))
    private float onKnockbackPlayers(float damage)
    {
        return DragonFightManagerCustom.onAttackPlayer(damage);
    }
}
