package com.dragonfight.mixin.dragon;

import com.dragonfight.fight.DragonFightManagerCustom;
import net.minecraft.world.damagesource.BadRespawnPointDamage;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragon.class)
public class EnderDragonEntityMixin
{
    @Shadow
    public boolean     inWall;
    final  EnderDragon self = (EnderDragon) (Object) this;

    @Inject(method = "checkCrystals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;setHealth(F)V"))
    private void onDragonHeal(final CallbackInfo ci)
    {
        DragonFightManagerCustom.onDragonHeal(self);
    }

    @Inject(method = "hurt(Lnet/minecraft/world/entity/boss/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "HEAD"), cancellable = true)
    private void ignoreBedDamage(final EnderDragonPart p_213403_1_, final DamageSource damageSource, final float p_213403_3_, final CallbackInfoReturnable<Boolean> cir)
    {
        if (damageSource instanceof BadRespawnPointDamage || damageSource.getMsgId().contains("explosion"))
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
